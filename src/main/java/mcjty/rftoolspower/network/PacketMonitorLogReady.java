package mcjty.rftoolspower.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.InformationScreenTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMonitorLogReady implements IMessage {

    private BlockPos pos;
    private EnergyTools.EnergyLevelMulti power;
    private long rfPerTickInserted;
    private long rfPerTickExtracted;
    private long roughMaxRfPerTick;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        rfPerTickExtracted = buf.readLong();
        rfPerTickInserted = buf.readLong();
        roughMaxRfPerTick = buf.readLong();
        if (buf.readBoolean()) {
            power = new EnergyTools.EnergyLevelMulti(buf.readLong(), buf.readLong());
        } else {
            power = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeLong(rfPerTickExtracted);
        buf.writeLong(rfPerTickInserted);
        buf.writeLong(roughMaxRfPerTick);
        if (power != null) {
            buf.writeBoolean(true);
            buf.writeLong(power.getEnergy());
            buf.writeLong(power.getMaxEnergy());
        } else {
            buf.writeBoolean(false);
        }
    }

    public PacketMonitorLogReady() {
    }

    public PacketMonitorLogReady(BlockPos pos, EnergyTools.EnergyLevelMulti power, long rfPerTickInserted, long rfPerTickExtracted,
                                 long roughMaxRfPerTick) {
        this.pos = pos;
        this.power = power;
        this.rfPerTickExtracted = rfPerTickExtracted;
        this.rfPerTickInserted = rfPerTickInserted;
        this.roughMaxRfPerTick = roughMaxRfPerTick;
    }

    public static class Handler implements IMessageHandler<PacketMonitorLogReady, IMessage> {
        @Override
        public IMessage onMessage(PacketMonitorLogReady message, MessageContext ctx) {
            RFToolsPower.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketMonitorLogReady message, MessageContext ctx) {
            TileEntity te = RFToolsPower.proxy.getClientWorld().getTileEntity(message.pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity info = (InformationScreenTileEntity) te;
                info.setClientPower(message.power, message.rfPerTickInserted, message.rfPerTickExtracted, message.roughMaxRfPerTick);
            }
        }
    }
}
