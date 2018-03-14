package mcjty.rftoolspower.blocks;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.network.PacketGetMonitorLog;
import mcjty.typed.Type;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InformationScreenTileEntity extends GenericTileEntity implements ITickable {

    private int mode = 0;
    private int cnt = 0;
    private long lastExtracted = 0;
    private long lastInserted = 0;
    private long rfExtractPerTick = 0;
    private long rfInsertedPerTick = 0;
    private long lastHudTime = 0;
    private List<String> clientHudLog = new ArrayList<>();

    public EnumFacing getBlockOrientation() {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof InformationScreenBlock) {
            return OrientationTools.getOrientationHoriz(state);
        } else {
            return null;
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            cnt--;
            if (cnt <= 0) {
                cnt = 10;
                BlockPos offset = getPos().offset(getBlockOrientation().getOpposite());
                TileEntity te = world.getTileEntity(offset);
                if (te instanceof PowerCellTileEntity) {
                    PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                    PowercellNetwork network = powercell.getNetwork();
                    long newExtracted = network.getExtracted();
                    long newInserted = network.getInserted();
                    rfExtractPerTick = (newExtracted - lastExtracted)/10;
                    rfInsertedPerTick = (newInserted - lastInserted)/10;
                    lastExtracted = newExtracted;
                    lastInserted = newInserted;
                }
            }
        }
    }

    public void toggleMode() {
        mode++;
        if (mode > 1) {
            mode = 0;
        }
        markDirtyClient();
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        mode = tagCompound.getByte("mode");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setByte("mode", (byte) mode);
    }

    public List<String> getClientLog() {
        return clientHudLog;
    }

    public long getLastUpdateTime() {
        return lastHudTime;
    }

    public void setLastUpdateTime(long t) {
        lastHudTime = t;
    }

    DecimalFormat format = new DecimalFormat("#.###");

    private String formatPower(long l) {
        if (l < 100000) {
            return Long.toString(l);
        } else if (l < 10000000) {
            Double d = l / 1000.0;
            return format.format(d)+"K";
        } else if (l < 10000000000L) {
            Double d = l / 1000000.0;
            return format.format(d)+"M";
        } else {
            Double d = l / 1000000000.0;
            return format.format(d)+"G";
        }
    }

    private String formatEnergyBar(long energy, long max) {
        if (max <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(" ");
        int bars = (int) (energy * 12 / max);
        if (bars > 0) {
            builder.append(TextFormatting.GREEN);
            for (int i = 0; i < Math.max(bars, 7); i++) {
                builder.append('#');
                bars--;
            }
            if (bars > 0) {
                builder.append(TextFormatting.YELLOW);
                for (int i = 0; i < Math.max(bars, 3); i++) {
                    builder.append('#');
                    bars--;
                }
                if (bars > 0) {
                    builder.append(TextFormatting.RED);
                    for (int i = 0; i < Math.max(bars, 2); i++) {
                        builder.append('#');
                        bars--;
                    }
                }
            }
        }
        return builder.toString();
    }

    public List<String> getLog() {
        BlockPos offset = getPos().offset(getBlockOrientation().getOpposite());
        TileEntity te = world.getTileEntity(offset);
        List<String> list = new ArrayList<>();
        list.add("");

        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
            long energy = powercell.getNetwork().getEnergy();
            long maxEnergy = powercell.getNetwork().getMaxEnergy();
            if (mode == 0) {
                list.add(TextFormatting.BLUE + " RF: " + TextFormatting.WHITE + formatPower(energy));
                list.add(TextFormatting.BLUE + " Max: " + TextFormatting.WHITE + formatPower(maxEnergy));
                list.add(TextFormatting.BLUE + " Extracted/t: " + TextFormatting.WHITE + formatPower(rfExtractPerTick));
                list.add(TextFormatting.BLUE + " Inserted/t: " + TextFormatting.WHITE + formatPower(rfInsertedPerTick));
            } else {
                list.add(formatEnergyBar(energy, maxEnergy));
            }
        } else if (EnergyTools.isEnergyTE(te)) {
            EnergyTools.EnergyLevelMulti info = EnergyTools.getEnergyLevelMulti(te);
            long energy = info.getEnergy();
            long maxEnergy = info.getMaxEnergy();
//            if (mode == 0) {
                list.add(TextFormatting.BLUE + " Power: " + TextFormatting.WHITE + formatPower(energy));
                list.add(TextFormatting.BLUE + " Max: " + TextFormatting.WHITE + formatPower(maxEnergy));
//            } else {
//                list.add(formatEnergyBar(energy, maxEnergy));
//            }
        } else {
            list.add(TextFormatting.RED + " Not a powercell");
            list.add(TextFormatting.RED + " or anything that");
            list.add(TextFormatting.RED + " supports power");
        }
        return list;
    }


    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (PacketGetMonitorLog.CMD_GETLOG.equals(command)) {
            return type.convert(getLog());
        }
        return rc;
    }


    @Override
    public <T> boolean execute(String command, List<T> list, Type<T> type) {
        boolean rc = super.execute(command, list, type);
        if (rc) {
            return true;
        }
        if (PacketGetMonitorLog.CLIENTCMD_GETLOG.equals(command)) {
            clientHudLog = Type.STRING.convert(list);
            return true;
        }
        return false;
    }

}
