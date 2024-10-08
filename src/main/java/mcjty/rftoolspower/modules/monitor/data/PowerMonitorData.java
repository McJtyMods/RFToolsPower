package mcjty.rftoolspower.modules.monitor.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PowerMonitorData(int rflevel, byte minimum, byte maximum) {

    public static final Codec<PowerMonitorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("rflevel").forGetter(PowerMonitorData::rflevel),
            Codec.BYTE.fieldOf("minimum").forGetter(PowerMonitorData::minimum),
            Codec.BYTE.fieldOf("maximum").forGetter(PowerMonitorData::maximum)
    ).apply(instance, PowerMonitorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PowerMonitorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PowerMonitorData::rflevel,
            ByteBufCodecs.BYTE, PowerMonitorData::minimum,
            ByteBufCodecs.BYTE, PowerMonitorData::maximum,
            PowerMonitorData::new
    );

    public PowerMonitorData withRflevel(int rflevel) {
        return new PowerMonitorData(rflevel, this.minimum, this.maximum);
    }

    public PowerMonitorData withMinimum(byte minimum) {
        return new PowerMonitorData(this.rflevel, minimum, this.maximum);
    }

    public PowerMonitorData withMaximum(byte maximum) {
        return new PowerMonitorData(this.rflevel, this.minimum, maximum);
    }
}
