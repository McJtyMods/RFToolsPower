package mcjty.rftoolspower.setup;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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

        PacketRequestDataFromServer.register(net, id());

        PacketHandler.registerStandardMessages(id(), net);
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

    public static void sendToClient(Player player, String command, @Nonnull TypedMap.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, argumentBuilder.build()), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToClient(Player player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, TypedMap.EMPTY), ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
