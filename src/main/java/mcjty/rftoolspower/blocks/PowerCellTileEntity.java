package mcjty.rftoolspower.blocks;

import mcjty.lib.api.power.IBigPower;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.config.ConfigSetup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static mcjty.rftoolspower.blocks.PowerCellTileEntity.Mode.MODE_NONE;
import static mcjty.rftoolspower.blocks.PowerCellTileEntity.Mode.MODE_OUTPUT;

public abstract class PowerCellTileEntity extends GenericTileEntity implements ITickableTileEntity, IBigPower {

    private PowercellNetwork network = null;
    private long localEnergy = 0;

    private LazyOptional<NullHandler> nullStorage = LazyOptional.of(() -> createNullHandler());
    private LazyOptional<SidedHandler>[] sidedStorages;

    // Forge energy
    private IEnergyStorage[] sidedHandlers = new IEnergyStorage[6];
    private IEnergyStorage nullHandler;


    public PowerCellTileEntity(TileEntityType<?> type) {
        super(type);
        sidedStorages = new LazyOptional[OrientationTools.DIRECTION_VALUES.length];
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            sidedStorages[direction.ordinal()] = LazyOptional.of(() -> createSidedHandler(direction));
        }
    }

    public enum Mode implements IStringSerializable {
        MODE_NONE("none"),
        MODE_INPUT("input"),   // Blue
        MODE_OUTPUT("output"); // Yellow

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return name;
        }
    }

    private PowerCellTileEntity.Mode modes[] = new PowerCellTileEntity.Mode[] {
            MODE_NONE, MODE_NONE, MODE_NONE,
            MODE_NONE, MODE_NONE, MODE_NONE };
    private int outputCount = 0;        // Caches the number of sides that have outputs

    public Mode getMode(Direction side) {
        return modes[side.ordinal()];
    }

    public void toggleMode(Direction side) {
        switch (modes[side.ordinal()]) {
            case MODE_NONE:
                modes[side.ordinal()] = Mode.MODE_INPUT;
                break;
            case MODE_INPUT:
                modes[side.ordinal()] = Mode.MODE_OUTPUT;
                break;
            case MODE_OUTPUT:
                modes[side.ordinal()] = Mode.MODE_NONE;
                break;
        }
        updateOutputCount();
        markDirtyClient();
    }

    private void updateOutputCount() {
        outputCount = 0;
        for (Mode mode : modes) {
            if (mode == MODE_OUTPUT) {
                outputCount++;
            }
        }
    }

    abstract Tier getTier();

    abstract long getLocalMaxEnergy();

    abstract long getRfPerTickPerSide();

    public long getRfPerTickReal() {
        if (ConfigSetup.RFPERTICK_SCALE.get() > 0) {
            return (long) (getRfPerTickPerSide() + (getNetwork().getPositions().size()-1) * getRfPerTickPerSide() * ConfigSetup.RFPERTICK_SCALE.get());
        } else {
            return getRfPerTickPerSide();
        }
    }

    @Override
    public long getStoredPower() {
        return getNetwork().getEnergy();
    }

    @Override
    public long getCapacity() {
        return getNetwork().getMaxEnergy();
    }

    private long receiveEnergyFacing(Direction from, long maxReceive, boolean simulate) {
        if (modes[from.ordinal()] != Mode.MODE_INPUT) {
            return 0;
        }

        PowercellNetwork network = getNetwork();
        if (network == null || !network.isValid()) {
            return 0;
        }

        maxReceive = Math.min(maxReceive, getRfPerTickReal());
        long received = receiveEnergyLocal(maxReceive, simulate);
        if (received > 0) {
            if (!simulate) {
                network.addEnergy(received);
            }
            maxReceive -= received;
        }
        long totReceived = received;
        if (maxReceive > 0) {
            for (Long l : network.getPositions()) {
                BlockPos p = BlockPos.fromLong(l);
                TileEntity te = world.getTileEntity(p);
                if (te instanceof PowerCellTileEntity) {
                    PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                    received = powercell.receiveEnergyLocal(maxReceive, simulate);
                    if (received > 0) {
                        if (!simulate) {
                            network.addEnergy(received);
                        }
                        maxReceive -= received;
                        totReceived += received;
                    }
                }
            }
        }
        return totReceived;
    }

    private long receiveEnergyLocal(long maxReceive, boolean simulate) {
        long maxInsert = Math.min(getLocalMaxEnergy() - localEnergy, maxReceive);
        if (maxInsert > 0) {
            if (!simulate) {
                localEnergy += maxInsert;
                markDirtyQuick();
            }
        }
        return maxInsert;
    }



    private int getEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getEnergy()); // TODO replace with EnergyTools one
    }

    private int getMaxEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getMaxEnergy()); // TODO replace with EnergyTools one
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (outputCount > 0) {
                PowercellNetwork network = getNetwork();
                if (network != null && network.isValid()) {
                    long energyStored = network.getEnergy();
                    if (energyStored <= 0) {
                        return;
                    }
                    sendOutEnergy(energyStored);
                }
//                validateNetwork();
            }
        }
    }

    public void redistributeNetwork() {
        PowercellNetwork network = getNetwork();
        if (network == null || network.getPositions().isEmpty()) {
            return;
        }
        final long[] energy = {0, 0, 0};  // For each tier
        final int[] count = {0, 0, 0};
        network.getPositions().stream().forEach(l -> {
            BlockPos p = BlockPos.fromLong(l);
            TileEntity te = world.getTileEntity(p);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                int t = powercell.getTier().ordinal();
                energy[t] += powercell.getLocalEnergy();
                count[t]++;
            }
        });
        for (Tier tier : Tier.values()) {
            if (count[tier.ordinal()] > 0) {
                long energyPerBlock = energy[tier.ordinal()] / count[tier.ordinal()];
                final long[] energyToSet = {energyPerBlock + energy[tier.ordinal()] % count[tier.ordinal()]};   // First block gets more (remainder)
                network.getPositions().stream().forEach(l -> {
                    BlockPos p = BlockPos.fromLong(l);
                    TileEntity te = world.getTileEntity(p);
                    if (te instanceof PowerCellTileEntity) {
                        PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                        if (powercell.getTier() == tier) {
                            powercell.setLocalEnergy(energyToSet[0]);
                            powercell.markDirtyQuick();
                            energyToSet[0] = energyPerBlock;
                        }
                    }
                });
            }
        }
    }

    private void validateNetwork() {
        final long[] energy = {0};
        final long[] maxEnergy = {0};
        getNetwork().getPositions().stream().forEach(l -> {
            BlockPos p = BlockPos.fromLong(l);
            TileEntity te = world.getTileEntity(p);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                energy[0] += powercell.getLocalEnergy();
                maxEnergy[0] += powercell.getLocalMaxEnergy();
                if (powercell.network != this.network) {
                    System.out.println("##### Network doesn't match at: " + p);
                }
            } else {
                System.out.println("##### Not a powercell: " + p);
            }
        });
        if (network.getEnergy() != energy[0]) {
            System.out.println("##### Energy mismatch! Got " + energy[0] + ", expected " + network.getEnergy());
        }
        if (network.getMaxEnergy() != maxEnergy[0]) {
            System.out.println("##### Max energy mismatch! Got " + maxEnergy[0] + ", expected " + network.getMaxEnergy());
        }
    }

    private void sendOutEnergy(long energyStored) {
        final long[] stored = {energyStored};
        final long[] energyExtracted = {0};

        for (Direction face : Direction.values()) {
            if (modes[face.ordinal()] == Mode.MODE_OUTPUT) {
                BlockPos pos = getPos().offset(face);
                TileEntity te = getWorld().getTileEntity(pos);
                Direction opposite = face.getOpposite();
                if (te != null) {
                    // @todo tesla
                    te.getCapability(CapabilityEnergy.ENERGY, opposite).ifPresent(e -> {
                        if (!(te instanceof PowerCellTileEntity)) {
                            long rfPerTick = getRfPerTickReal();
                            long rfToGive = Math.min(rfPerTick, stored[0]);

                            long received = EnergyTools.receiveEnergy(te, opposite, rfToGive);

                            stored[0] -= received;
                            energyExtracted[0] += received;
                        }
                    });
                    if (stored[0] <= 0) {
                        break;
                    }
                }
            }
        }

        if (energyExtracted[0] > 0) {
            network.extractEnergy(energyExtracted[0]);
            extractEnergyFromNetwork(energyExtracted[0]);
        }
    }

    private void extractEnergyFromNetwork(long energyExtracted) {
        long toExtractLocal = Math.min(energyExtracted, localEnergy);
        if (toExtractLocal > 0) {
            // First extract locally
            localEnergy -= toExtractLocal;
            energyExtracted -= toExtractLocal;
            markDirtyQuick();
        }

        // If we still have energy to extract go find another block in the network
        if (energyExtracted > 0) {
            for (Long l : network.getPositions()) {
                BlockPos p = BlockPos.fromLong(l);
                TileEntity te = world.getTileEntity(p);
                if (te instanceof PowerCellTileEntity) {
                    PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                    toExtractLocal = Math.min(energyExtracted, powercell.localEnergy);
                    if (toExtractLocal > 0) {
                        powercell.localEnergy -= toExtractLocal;
                        energyExtracted -= toExtractLocal;
                        powercell.markDirtyQuick();
                    }
                    if (energyExtracted <= 0) {
                        break;      // We're done
                    }
                }
            }
        }
    }

    public long getLocalEnergy() {
        return localEnergy;
    }

    public void setLocalEnergy(long localEnergy) {
        this.localEnergy = localEnergy;
    }

    public PowercellNetwork getNetwork() {
        if (network == null) {
            // This block has no network. Create one and distribute to all connected powercells
            buildNetwork(new PowercellNetwork(), pos);
        }
        return network;
    }

    public void setNetwork(PowercellNetwork network) {
        this.network = network;
    }

    public void dismantleNetwork(PowercellNetwork network) {
        network.getPositions().stream().map(BlockPos::fromLong).forEach(pos -> {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                powercell.setNetwork(null);
            }
        });
    }

    private static Set<BlockPos> alreadyReportedBad = new HashSet<>();
    private static Set<BlockPos> alreadyReportedUnexpected = new HashSet<>();

    private void buildNetwork(PowercellNetwork network, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;

            if (network.contains(pos)) {
                if (powercell.network != network) {
                    if (!alreadyReportedBad.contains(pos)) {
                        System.out.println("Bad network at pos = " + pos + " (dimension " + world.provider.getDimension() + ")");
                        alreadyReportedBad.add(pos);
                    }
                }
                return;
            }

            if (powercell.network == network) {
                if (!alreadyReportedUnexpected.contains(pos)) {
                    System.out.println("Unexpected network at pos = " + pos + " (dimension " + world.provider.getDimension() + ")");
                    alreadyReportedUnexpected.add(pos);
                }
                return;
            }

            if (powercell.network != null) {
                // Connected to some other network. First break that down
                dismantleNetwork(powercell.network);
            }

            powercell.setNetwork(network);
            network.add(pos);
            network.setEnergy(network.getEnergy() + powercell.getLocalEnergy());
            network.setMaxEnergy(network.getMaxEnergy() + powercell.getLocalMaxEnergy());

            for (Direction facing : OrientationTools.DIRECTION_VALUES) {
                buildNetwork(network, pos.offset(facing));
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        Mode[] old = new Mode[] { modes[0], modes[1], modes[2], modes[3], modes[4], modes[5] };
        super.onDataPacket(net, packet);
        for (int i = 0 ; i < 6 ; i++) {
            if (old[i] != modes[i]) {
                world.markForRerender(getPos());
                return;
            }
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        modes[0] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m0")];
        modes[1] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m1")];
        modes[2] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m2")];
        modes[3] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m3")];
        modes[4] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m4")];
        modes[5] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m5")];
        updateOutputCount();
        localEnergy = tagCompound.getLong("local");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putByte("m0", (byte) modes[0].ordinal());
        tagCompound.putByte("m1", (byte) modes[1].ordinal());
        tagCompound.putByte("m2", (byte) modes[2].ordinal());
        tagCompound.putByte("m3", (byte) modes[3].ordinal());
        tagCompound.putByte("m4", (byte) modes[4].ordinal());
        tagCompound.putByte("m5", (byte) modes[5].ordinal());
        tagCompound.putLong("local", localEnergy);
        return super.write(tagCompound);
    }


    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        // @todo tesla support
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null) {
                return nullStorage.cast();
            } else {
                if (sidedHandlers[facing.ordinal()] == null) {
                    createSidedHandler(facing);
                }
                return (T) sidedHandlers[facing.ordinal()];
            }
        }
        return super.getCapability(capability, facing);
    }

    class SidedHandler implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int)PowerCellTileEntity.this.receiveEnergyFacing(facing, maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return PowerCellTileEntity.this.getEnergyStoredAsInt();
        }

        @Override
        public int getMaxEnergyStored() {
            return PowerCellTileEntity.this.getMaxEnergyStoredAsInt();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    private SidedHandler createSidedHandler(Direction facing) {
        return new SidedHandler();
    }

    class NullHandler implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return PowerCellTileEntity.this.getEnergyStoredAsInt();
        }

        @Override
        public int getMaxEnergyStored() {
            return PowerCellTileEntity.this.getMaxEnergyStoredAsInt();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

    private NullHandler createNullHandler() {
        return new NullHandler();
    }
}
