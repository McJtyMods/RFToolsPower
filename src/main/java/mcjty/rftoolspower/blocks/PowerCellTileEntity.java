package mcjty.rftoolspower.blocks;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mcjty.lib.entity.GenericTileEntity;
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
public abstract class PowerCellTileEntity extends GenericTileEntity implements IEnergyProvider, IEnergyReceiver, ITickable {

    private PowercellNetwork network = null;

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

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return 0;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getEnergyStored(EnumFacing from) {
        return 0;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return 0;
    }

    @Optional.Method(modid = "redstoneflux")
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return false;
    }

    private int receiveEnergyFacing(EnumFacing facing, int maxReceive, boolean simulate) {
        return 0;
    }

    private int getEnergyStoredAsInt() {
        return 0;
    }

    private int getMaxEnergyStoredAsInt() {
        return 0;
    }

    @Override
    public void update() {

    }

    public PowercellNetwork getNetwork() {
        if (network == null) {
            // This block has no network. Create one and distribute to all connected powercells
            network = new PowercellNetwork();
            buildNetwork(network, pos);
        }
        return network;
    }

    public void setNetwork(PowercellNetwork network) {
        this.network = network;
    }

    private void buildNetwork(PowercellNetwork network, BlockPos pos) {
        if (network.contains(pos)) {
            return;
        }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            network.add(pos);
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
