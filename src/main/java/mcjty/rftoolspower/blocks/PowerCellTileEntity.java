package mcjty.rftoolspower.blocks;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mcjty.lib.compat.RedstoneFluxCompatibility;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.api.IBigPower;
import mcjty.rftoolspower.config.Config;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import static mcjty.rftoolspower.blocks.PowerCellTileEntity.Mode.MODE_NONE;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux")
})
public abstract class PowerCellTileEntity extends GenericTileEntity implements IEnergyProvider, IEnergyReceiver, ITickable,
        IBigPower {

    private PowercellNetwork network = null;
    private int localEnergy = 0;

    public PowerCellTileEntity() {
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
        public String getName() {
            return name;
        }
    }

    private PowerCellTileEntity.Mode modes[] = new PowerCellTileEntity.Mode[] {
            MODE_NONE, MODE_NONE, MODE_NONE,
            MODE_NONE, MODE_NONE, MODE_NONE };

    public Mode getMode(EnumFacing side) {
        return modes[side.ordinal()];
    }

    public void toggleMode(EnumFacing side) {
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
        markDirtyClient();
    }

    abstract Tier getTier();

    abstract int getLocalMaxEnergy();

    abstract int getRfPerTickPerSide();

    public int getRfPerTickReal() {
        if (Config.RFPERTICK_SCALE > 0) {
            return (int) (getRfPerTickPerSide() + (getNetwork().getPositions().size()-1) * getRfPerTickPerSide() * Config.RFPERTICK_SCALE);
        } else {
            return getRfPerTickPerSide();
        }
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return receiveEnergyFacing(from, maxReceive, simulate);
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return getEnergyStoredAsInt();
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaxEnergyStoredAsInt();
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public long getBigEnergy() {
        return getNetwork().getEnergy();
    }

    @Override
    public long getBigMaxEnergy() {
        return getNetwork().getMaxEnergy();
    }

    private int receiveEnergyFacing(EnumFacing from, int maxReceive, boolean simulate) {
        if (modes[from.ordinal()] != Mode.MODE_INPUT) {
            return 0;
        }

        if (!getNetwork().isValid()) {
            return 0;
        }

        maxReceive = Math.min(maxReceive, getRfPerTickReal());
        int received = receiveEnergyLocal(maxReceive, simulate);
        PowercellNetwork network = getNetwork();
        if (received > 0) {
            if (!simulate) {
                network.addEnergy(received);
            }
            maxReceive -= received;
        }
        int totReceived = received;
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

    private int receiveEnergyLocal(int maxReceive, boolean simulate) {
        int maxInsert = Math.min(getLocalMaxEnergy() - localEnergy, maxReceive);
        if (maxInsert > 0) {
            if (!simulate) {
                localEnergy += maxInsert;
                markDirtyQuick();
            }
        }
        return maxInsert;
    }



    private int getEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getEnergy());
    }

    private int getMaxEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getMaxEnergy());
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (getNetwork().isValid()) {
                long energyStored = getNetwork().getEnergy();
                if (energyStored <= 0) {
                    return;
                }
                sendOutEnergy(energyStored);
            }
            validateNetwork();
        }
    }

    public void redistributeNetwork() {
        PowercellNetwork network = getNetwork();
        if (network.getPositions().isEmpty()) {
            return;
        }
        final long[] energy = {0};
        network.getPositions().stream().forEach(l -> {
            BlockPos p = BlockPos.fromLong(l);
            TileEntity te = world.getTileEntity(p);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                energy[0] += powercell.getLocalEnergy();
            }
        });
        int energyPerBlock = (int) (energy[0] / network.getPositions().size());
        final int[] energyToSet = {energyPerBlock + (int) (energy[0] % network.getPositions().size())};   // First block gets more (remainder)
        network.getPositions().stream().forEach(l -> {
            BlockPos p = BlockPos.fromLong(l);
            TileEntity te = world.getTileEntity(p);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                powercell.setLocalEnergy(energyToSet[0]);
                powercell.markDirtyQuick();
                energyToSet[0] = energyPerBlock;
            }
        });
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
        long energyExtracted = 0;

        for (EnumFacing face : EnumFacing.VALUES) {
            if (modes[face.ordinal()] == Mode.MODE_OUTPUT) {
                BlockPos pos = getPos().offset(face);
                TileEntity te = getWorld().getTileEntity(pos);
                EnumFacing opposite = face.getOpposite();
                if (EnergyTools.isEnergyTE(te) || (te != null && te.hasCapability(CapabilityEnergy.ENERGY, opposite))) {
                    if (!(te instanceof PowerCellTileEntity)) {
                        int rfPerTick = getRfPerTickReal();
                        int received;

                        int rfToGive = Math.min(rfPerTick, (int) (energyStored));

                        if (RFToolsPower.redstoneflux && RedstoneFluxCompatibility.isEnergyConnection(te)) {
                            if (RedstoneFluxCompatibility.canConnectEnergy(te, opposite)) {
                                received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                            } else {
                                received = 0;
                            }
                        } else {
                            // Forge unit
                            received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                        }

                        energyStored -= received;
                        energyExtracted += received;
                        if (energyStored <= 0) {
                            break;
                        }
                    }
                }
            }
        }

        if (energyExtracted > 0) {
            network.extractEnergy(energyExtracted);
            extractEnergyFromNetwork(energyExtracted);
        }
    }

    private void extractEnergyFromNetwork(long energyExtracted) {
        int toExtractLocal = (int) Math.min(energyExtracted, localEnergy);
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
                    toExtractLocal = (int) Math.min(energyExtracted, powercell.localEnergy);
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

    public int getLocalEnergy() {
        return localEnergy;
    }

    public void setLocalEnergy(int localEnergy) {
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

    private void buildNetwork(PowercellNetwork network, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;

            if (network.contains(pos)) {
                if (powercell.network != network) {
                    System.out.println("Bad network at pos = " + pos);
                }
                return;
            }

            if (powercell.network == network) {
                System.out.println("Unexpected network at pos = " + pos);
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

            for (EnumFacing facing : EnumFacing.VALUES) {
                buildNetwork(network, pos.offset(facing));
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        Mode[] old = new Mode[] { modes[0], modes[1], modes[2], modes[3], modes[4], modes[5] };
        super.onDataPacket(net, packet);
        for (int i = 0 ; i < 6 ; i++) {
            if (old[i] != modes[i]) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
                return;
            }
        }
    }


    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
//        energy = tagCompound.getInteger("energy");
        modes[0] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m0")];
        modes[1] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m1")];
        modes[2] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m2")];
        modes[3] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m3")];
        modes[4] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m4")];
        modes[5] = PowerCellTileEntity.Mode.values()[tagCompound.getByte("m5")];
        localEnergy = tagCompound.getInteger("local");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
//        tagCompound.setInteger("energy", energy);
        tagCompound.setByte("m0", (byte) modes[0].ordinal());
        tagCompound.setByte("m1", (byte) modes[1].ordinal());
        tagCompound.setByte("m2", (byte) modes[2].ordinal());
        tagCompound.setByte("m3", (byte) modes[3].ordinal());
        tagCompound.setByte("m4", (byte) modes[4].ordinal());
        tagCompound.setByte("m5", (byte) modes[5].ordinal());
        tagCompound.setInteger("local", localEnergy);
    }


    // Forge energy
    private IEnergyStorage[] sidedHandlers = new IEnergyStorage[6];
    private IEnergyStorage nullHandler;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null) {
                if (nullHandler == null) {
                    createNullHandler();
                }
                return (T) nullHandler;
            } else {
                if (sidedHandlers[facing.ordinal()] == null) {
                    createSidedHandler(facing);
                }
                return (T) sidedHandlers[facing.ordinal()];
            }
        }
        return super.getCapability(capability, facing);
    }

    private void createSidedHandler(EnumFacing facing) {
        sidedHandlers[facing.ordinal()] = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return PowerCellTileEntity.this.receiveEnergyFacing(facing, maxReceive, simulate);
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
        };
    }

    private void createNullHandler() {
        nullHandler = new IEnergyStorage() {
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
        };
    }
}
