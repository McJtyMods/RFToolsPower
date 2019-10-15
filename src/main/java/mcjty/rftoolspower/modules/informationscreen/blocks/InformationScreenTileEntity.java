package mcjty.rftoolspower.modules.informationscreen.blocks;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.PowerCellNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

import static mcjty.rftoolspower.modules.informationscreen.InformationScreenSetup.TYPE_INFORMATION_SCREEN;

public class InformationScreenTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final String REGNAME = "information_screen";

    private int mode = 0;
    private int cnt = 0;
    private long lastExtracted = 0;
    private long lastInserted = 0;
    private long rfExtractPerTick = 0;      // Also used client side and synced with packet
    private long rfInsertedPerTick = 0;
    private long lastHudTime = 0;

    // Client side information
    private EnergyTools.EnergyLevel clientPower;
    private long roughMaxRfPerTick = 0;     // This number indicates a high RF/tick estimate used for rendering


    public InformationScreenTileEntity() {
        super(TYPE_INFORMATION_SCREEN);
    }

    public Direction getBlockOrientation() {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof InformationScreenBlock) {
            return OrientationTools.getOrientationHoriz(state);
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            cnt--;
            if (cnt <= 0) {
                cnt = 10;
                BlockPos offset = getPos().offset(getBlockOrientation().getOpposite());
                TileEntity te = world.getTileEntity(offset);
                if (te instanceof PowerCellTileEntity) {
                    PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                    PowerCellNetwork network = powercell.getNetwork();
                    if (network != null) {
                        long newExtracted = network.getExtracted();
                        long newInserted = network.getInserted();
                        rfExtractPerTick = (newExtracted - lastExtracted) / 10;
                        rfInsertedPerTick = (newInserted - lastInserted) / 10;
                        lastExtracted = newExtracted;
                        lastInserted = newInserted;
                    }
                } else {
                    rfExtractPerTick = -1;
                    rfInsertedPerTick = -1;
                }
            }
        }
    }

    public void toggleMode() {
        mode++;
        if (mode > 2) {
            mode = 0;
        }
        markDirtyClient();
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        mode = tagCompound.getByte("mode");
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putByte("mode", (byte) mode);
        return super.write(tagCompound);
    }

    public void setClientPower(EnergyTools.EnergyLevel power, long rfInsertedPerTick, long rfExtractPerTick, long roughMaxRfPerTick) {
        this.clientPower = power;
        this.rfInsertedPerTick = rfInsertedPerTick;
        this.rfExtractPerTick = rfExtractPerTick;
        this.roughMaxRfPerTick = roughMaxRfPerTick;
    }

    public EnergyTools.EnergyLevel getClientPower() {
        return clientPower;
    }

    public long calculateRoughMaxRfPerTick() {
        BlockPos offset = getPos().offset(getBlockOrientation().getOpposite());
        TileEntity te = world.getTileEntity(offset);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
            return (long) powercell.getRfPerTickPerSide() * 2;
        }
        return -1;
    }

    public long getRoughMaxRfPerTick() {
        return roughMaxRfPerTick;
    }

    public long getLastUpdateTime() {
        return lastHudTime;
    }

    public void setLastUpdateTime(long t) {
        lastHudTime = t;
    }

    public long getRfExtractPerTick() {
        return rfExtractPerTick;
    }

    public long getRfInsertedPerTick() {
        return rfInsertedPerTick;
    }

    public EnergyTools.EnergyLevel getPower() {
        BlockPos offset = getPos().offset(getBlockOrientation().getOpposite());
        TileEntity te = world.getTileEntity(offset);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
            PowerCellNetwork network = powercell.getNetwork();
            if (network != null) {
                long energy = network.getEnergy();
                long maxEnergy = network.getMaxEnergy();
                return new EnergyTools.EnergyLevel(energy, maxEnergy);
            }
        } else if (EnergyTools.isEnergyTE(te, getBlockOrientation())) {
            return EnergyTools.getEnergyLevelMulti(te, getBlockOrientation());
        }
        return null;
    }



}
