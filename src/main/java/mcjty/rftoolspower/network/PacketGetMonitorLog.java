package mcjty.rftoolspower.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.blocks.InformationScreenTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetMonitorLog implements IMessage {

    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetMonitorLog() {
    }

    public PacketGetMonitorLog(BlockPos pos) {
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetMonitorLog, IMessage> {

        @Override
        public IMessage onMessage(PacketGetMonitorLog message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetMonitorLog message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            TileEntity te = player.getEntityWorld().getTileEntity(message.pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity info = (InformationScreenTileEntity) te;
                EnergyTools.EnergyLevel power = info.getPower();

                RFToolsPowerMessages.INSTANCE.sendTo(new PacketMonitorLogReady(message.pos, power, info.getRfInsertedPerTick(), info.getRfExtractPerTick(),
                        info.calculateRoughMaxRfPerTick()), player);
            }
        }
    }
}
