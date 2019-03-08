package mcjty.rftoolspower.network;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import javax.annotation.Nonnull;

public class RFToolsPowerMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(RFToolsPower.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
        net.registerMessageServer(id(), PacketGetMonitorLog.class, PacketGetMonitorLog::toBytes, PacketGetMonitorLog::new, PacketGetMonitorLog::handle);

        // Client side
        net.registerMessageServer(id(), PacketMonitorLogReady.class, PacketMonitorLogReady::toBytes, PacketMonitorLogReady::new, PacketMonitorLogReady::handle);

//        PacketHandler.register(PacketHandler.nextPacketID(), PowerCellInfoPacketServer.class, PowerCellInfoPacketClient.class);
    }

    private static int id() {
        return PacketHandler.nextPacketID();
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(EntityPlayer player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, argumentBuilder.build()), (EntityPlayerMP) player);
    }

    public static void sendToClient(EntityPlayer player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, TypedMap.EMPTY), (EntityPlayerMP) player);
    }
}
