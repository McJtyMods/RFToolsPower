package mcjty.rftoolspower.modules.powercell.blocks;

import mcjty.lib.api.power.IBigPower;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.rftoolspower.modules.powercell.data.PowerCellNetwork;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static mcjty.rftoolspower.modules.powercell.data.SideType.NONE;

public class PowerCellTileEntity extends GenericTileEntity implements ITickableTileEntity, IBigPower {

    public static final String REGNAME1 = "cell1";
    public static final String REGNAME2 = "cell2";
    public static final String REGNAME3 = "cell3";

    private PowerCellNetwork network = null;
    private long localEnergy = 0;
    private final Tier tier;

    public static final ModelProperty<SideType> NORTH = new ModelProperty<>();
    public static final ModelProperty<SideType> SOUTH = new ModelProperty<>();
    public static final ModelProperty<SideType> WEST = new ModelProperty<>();
    public static final ModelProperty<SideType> EAST = new ModelProperty<>();
    public static final ModelProperty<SideType> UP = new ModelProperty<>();
    public static final ModelProperty<SideType> DOWN = new ModelProperty<>();
    public static final ModelProperty<Tier> TIER = new ModelProperty<>();


    private LazyOptional<NullHandler> nullStorage = LazyOptional.of(this::createNullHandler);
    private LazyOptional<SidedHandler>[] sidedStorages;

    public PowerCellTileEntity(Tier tier) {
        super(tier.getType());
        this.tier = tier;
        sidedStorages = new LazyOptional[OrientationTools.DIRECTION_VALUES.length];
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            sidedStorages[direction.ordinal()] = LazyOptional.of(() -> createSidedHandler(direction));
        }
    }

    private SideType modes[] = new SideType[] {
            NONE, NONE, NONE,
            NONE, NONE, NONE };
    private int outputCount = 0;        // Caches the number of sides that have outputs

    public SideType getMode(Direction side) {
        return modes[side.ordinal()];
    }

    public void toggleMode(Direction side) {
        switch (modes[side.ordinal()]) {
            case NONE:
                modes[side.ordinal()] = SideType.INPUT;
                break;
            case INPUT:
                modes[side.ordinal()] = SideType.OUTPUT;
                break;
            case OUTPUT:
                modes[side.ordinal()] = SideType.NONE;
                break;
        }
        updateOutputCount();
        markDirtyClient();
    }

    private void updateOutputCount() {
        outputCount = 0;
        for (SideType mode : modes) {
            if (mode == SideType.OUTPUT) {
                outputCount++;
            }
        }
    }

    public Tier getTier() {
        return tier;
    }

    // @todo temporary until long values work in forge!
    public static long safeCast(Object o) {
        if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Integer) {
            return (long) (Integer) o;
        } else {
            return 0;
        }
    }

    public long getLocalMaxEnergy() {
        switch (tier) {
            case TIER1:
                return safeCast(PowerCellConfig.TIER1_MAXRF.get());
            case TIER2:
                return safeCast(PowerCellConfig.TIER2_MAXRF.get());
            case TIER3:
                return safeCast(PowerCellConfig.TIER3_MAXRF.get());
        }
        return 0;
    }

    public long getRfPerTickPerSide() {
        switch (tier) {
            case TIER1:
                return PowerCellConfig.TIER1_RFPERTICK.get();
            case TIER2:
                return PowerCellConfig.TIER2_RFPERTICK.get();
            case TIER3:
                return PowerCellConfig.TIER3_RFPERTICK.get();
        }
        return 0;
    }

    public long getRfPerTickReal() {
        if (PowerCellConfig.RFPERTICK_SCALE.get() > 0) {
            return (long) (getRfPerTickPerSide() + (getNetwork().getPositions().size()-1) * getRfPerTickPerSide() * PowerCellConfig.RFPERTICK_SCALE.get());
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
        if (modes[from.ordinal()] != SideType.INPUT) {
            return 0;
        }

        PowerCellNetwork network = getNetwork();
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
                PowerCellNetwork network = getNetwork();
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
        PowerCellNetwork network = getNetwork();
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
            if (modes[face.ordinal()] == SideType.OUTPUT) {
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

    public PowerCellNetwork getNetwork() {
        if (network == null) {
            // This block has no network. Create one and distribute to all connected powercells
            buildNetwork(new PowerCellNetwork(), pos);
        }
        return network;
    }

    public void setNetwork(PowerCellNetwork network) {
        this.network = network;
    }

    @Override
    public void onReplaced(World world, BlockPos pos, BlockState state) {
        if (getNetwork() != null) {
            dismantleNetwork(getNetwork());
        }
        BlockState stateUp = world.getBlockState(pos.up());
        if (stateUp.getBlock() instanceof PowerCellBlock) {
            world.notifyBlockUpdate(pos.up(), stateUp, stateUp, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
        BlockState stateDown = world.getBlockState(pos.down());
        if (stateDown.getBlock() instanceof PowerCellBlock) {
            world.notifyBlockUpdate(pos.down(), stateDown, stateDown, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void dismantleNetwork(PowerCellNetwork network) {
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

    private void buildNetwork(PowerCellNetwork network, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;

            if (network.contains(pos)) {
                if (powercell.network != network) {
                    if (!alreadyReportedBad.contains(pos)) {
                        System.out.println("Bad network at pos = " + pos + " (dimension " + world.getDimension().getType() + ")");
                        alreadyReportedBad.add(pos);
                    }
                }
                return;
            }

            if (powercell.network == network) {
                if (!alreadyReportedUnexpected.contains(pos)) {
                    System.out.println("Unexpected network at pos = " + pos + " (dimension " + world.getDimension().getType() + ")");
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
        SideType[] old = new SideType[] { modes[0], modes[1], modes[2], modes[3], modes[4], modes[5] };
        super.onDataPacket(net, packet);
        for (int i = 0 ; i < 6 ; i++) {
            if (old[i] != modes[i]) {
                ModelDataManager.requestModelDataRefresh(this);
                BlockState state = world.getBlockState(pos);
                getWorld().notifyBlockUpdate(getPos(), state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
                return;
            }
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(NORTH, getMode(Direction.NORTH))
                .withInitial(SOUTH, getMode(Direction.SOUTH))
                .withInitial(WEST, getMode(Direction.WEST))
                .withInitial(EAST, getMode(Direction.EAST))
                .withInitial(UP, getMode(Direction.UP))
                .withInitial(DOWN, getMode(Direction.DOWN))
                .withInitial(TIER, tier)
                .build();
    }


    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        CompoundNBT info = tagCompound.getCompound("Info");
        String mode = info.getString("mode");
        if (mode.length() >= 6) {
            for (int i = 0 ; i < 6 ; i++) {
                modes[i] = SideType.VALUES[Integer.parseInt(mode.substring(i, i+1))];
            }
        }
        updateOutputCount();
        localEnergy = info.getLong("energy");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        String mode = "";
        for (int i = 0 ; i < 6 ; i++) {
            mode += modes[i].ordinal();
        }
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putString("mode", mode);
        info.putLong("energy", localEnergy);
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
                return sidedStorages[facing.ordinal()].cast();
            }
        }
        return super.getCapability(capability, facing);
    }

    class SidedHandler implements IEnergyStorage {

        private final Direction facing;

        public SidedHandler(Direction facing) {
            this.facing = facing;
        }

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
        return new SidedHandler(facing);
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
