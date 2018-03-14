package mcjty.rftoolspower.network;

import mcjty.lib.network.PacketRequestServerList;
import mcjty.lib.network.PacketRequestServerListHandler;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.typed.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

public class PacketGetMonitorLog extends PacketRequestServerList<String> {

    public static String CMD_GETLOG = "getLog";
    public static String CLIENTCMD_GETLOG = "getLog";

    public PacketGetMonitorLog() {
    }

    public PacketGetMonitorLog(BlockPos pos) {
        super(RFToolsPower.MODID, pos, CMD_GETLOG);
    }

    public static class Handler extends PacketRequestServerListHandler<PacketGetMonitorLog, String> {

        public Handler() {
            super(Type.STRING);
        }

        @Override
        protected void sendToClient(BlockPos pos, @Nonnull List<String> list, MessageContext messageContext) {
            RFToolsPowerMessages.INSTANCE.sendTo(new PacketMonitorLogReady(pos, CLIENTCMD_GETLOG, list), messageContext.getServerHandler().player);
        }
    }
}
