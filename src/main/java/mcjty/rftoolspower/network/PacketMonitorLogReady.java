package mcjty.rftoolspower.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.InformationScreenTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMonitorLogReady {

    private BlockPos pos;
    private EnergyTools.EnergyLevel power;
    private long rfPerTickInserted;
    private long rfPerTickExtracted;
    private long roughMaxRfPerTick;

    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeLong(rfPerTickExtracted);
        buf.writeLong(rfPerTickInserted);
        buf.writeLong(roughMaxRfPerTick);
        if (power != null) {
            buf.writeBoolean(true);
            buf.writeLong(power.getEnergy());
            buf.writeLong(power.getMaxEnergy());
        } else {
            buf.writeBoolean(false);
        }
    }

    public PacketMonitorLogReady() {
    }

    public PacketMonitorLogReady(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        rfPerTickExtracted = buf.readLong();
        rfPerTickInserted = buf.readLong();
        roughMaxRfPerTick = buf.readLong();
        if (buf.readBoolean()) {
            power = new EnergyTools.EnergyLevel(buf.readLong(), buf.readLong());
        } else {
            power = null;
        }
    }

    public PacketMonitorLogReady(BlockPos pos, EnergyTools.EnergyLevel power, long rfPerTickInserted, long rfPerTickExtracted,
                                 long roughMaxRfPerTick) {
        this.pos = pos;
        this.power = power;
        this.rfPerTickExtracted = rfPerTickExtracted;
        this.rfPerTickInserted = rfPerTickInserted;
        this.roughMaxRfPerTick = roughMaxRfPerTick;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = RFToolsPower.proxy.getClientWorld().getTileEntity(pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity info = (InformationScreenTileEntity) te;
                info.setClientPower(power, rfPerTickInserted, rfPerTickExtracted, roughMaxRfPerTick);
            }
        });
        ctx.setPacketHandled(true);
    }
}
