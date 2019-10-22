package mcjty.rftoolspower.modules.informationscreen.network;

import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenTileEntity;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGetMonitorLog {

    private BlockPos pos;

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public PacketGetMonitorLog(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }

    public PacketGetMonitorLog(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity player = ctx.getSender();
            TileEntity te = player.getEntityWorld().getTileEntity(pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity info = (InformationScreenTileEntity) te;
                EnergyTools.EnergyLevel power = info.getPower();

                PacketMonitorLogReady packet = new PacketMonitorLogReady(pos, power, info.getRfInsertedPerTick(), info.getRfExtractPerTick(),
                        info.calculateRoughMaxRfPerTick());
                RFToolsPowerMessages.INSTANCE.sendTo(packet, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.setPacketHandled(true);
    }
}
