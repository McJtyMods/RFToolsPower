package mcjty.rftoolspower.network;

import mcjty.lib.network.Arguments;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import javax.annotation.Nonnull;

public class RFToolsPowerMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
//        net.registerMessage(mcjty.rftools.network.PacketGetPlayers.Handler.class, PacketGetPlayers.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
//        net.registerMessage(PacketPlayersReady.Handler.class, PacketPlayersReady.class, PacketHandler.nextPacketID(), Side.CLIENT);

//        PacketHandler.register(PacketHandler.nextPacketID(), PowerCellInfoPacketServer.class, PowerCellInfoPacketClient.class);
    }

    public static void sendToServer(String command, @Nonnull Arguments.Builder argumentBuilder) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        INSTANCE.sendToServer(new PacketSendServerCommand(RFToolsPower.MODID, command, Arguments.EMPTY));
    }

    public static void sendToClient(EntityPlayer player, String command, @Nonnull Arguments.Builder argumentBuilder) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, argumentBuilder.build()), (EntityPlayerMP) player);
    }

    public static void sendToClient(EntityPlayer player, String command) {
        INSTANCE.sendTo(new PacketSendClientCommand(RFToolsPower.MODID, command, Arguments.EMPTY), (EntityPlayerMP) player);
    }
}
