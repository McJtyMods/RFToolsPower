package mcjty.rftoolspower.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.blocks.InformationScreenTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGetMonitorLog {

    private BlockPos pos;

    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetMonitorLog() {
    }

    public PacketGetMonitorLog(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    public PacketGetMonitorLog(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            TileEntity te = player.getEntityWorld().getTileEntity(pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity info = (InformationScreenTileEntity) te;
                EnergyTools.EnergyLevel power = info.getPower();

                RFToolsPowerMessages.INSTANCE.sendTo(new PacketMonitorLogReady(pos, power, info.getRfInsertedPerTick(), info.getRfExtractPerTick(),
                        info.calculateRoughMaxRfPerTick()), player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
