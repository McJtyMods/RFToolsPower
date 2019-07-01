package mcjty.rftoolspower.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.blocks.informationscreen.InformationScreenTileEntity;
import net.minecraft.entity.player.PlayerEntity;
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

                RFToolsPowerMessages.INSTANCE.sendTo(new PacketMonitorLogReady(pos, power, info.getRfInsertedPerTick(), info.getRfExtractPerTick(),
                        info.calculateRoughMaxRfPerTick()), McJtyLib.proxy.getNetworkManager(player), NetworkDirection.PLAY_TO_SERVER);
            }
        });
        ctx.setPacketHandled(true);
    }
}
