package mcjty.rftoolspower.network;

import mcjty.lib.network.*;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.informationscreen.network.PacketGetMonitorLog;
import mcjty.rftoolspower.modules.informationscreen.network.PacketMonitorLogReady;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

public class RFToolsPowerMessages {
    public static SimpleChannel INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RFToolsPower.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Server side
        PacketHandler.debugRegister("RFToolsPower", net, id(), PacketGetMonitorLog.class, PacketGetMonitorLog::toBytes, PacketGetMonitorLog::new, PacketGetMonitorLog::handle);

        // Client side
        PacketHandler.debugRegister("RFToolsPower", net, id(), PacketMonitorLogReady.class, PacketMonitorLogReady::toBytes, PacketMonitorLogReady::new, PacketMonitorLogReady::handle);

        PacketHandler.debugRegister("RFToolsPower", net, id(), PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new,
                new ChannelBoundHandler<>(net, PacketRequestDataFromServer::handle));

        PacketHandler.registerStandardMessages("RFToolsPower - standard", id(), net);
    }

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, TypedMap.EMPTY));
    }

    public static void sendToClient(PlayerEntity player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, argumentBuilder.build()), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToClient(PlayerEntity player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, TypedMap.EMPTY), ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
