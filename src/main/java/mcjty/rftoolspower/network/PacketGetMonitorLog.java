package mcjty.rftoolspower.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.blocks.InformationScreenTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

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

    public PacketGetMonitorLog(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetMonitorLog(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
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
