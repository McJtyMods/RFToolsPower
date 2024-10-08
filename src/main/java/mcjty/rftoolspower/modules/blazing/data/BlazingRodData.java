package mcjty.rftoolspower.modules.blazing.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import static mcjty.rftoolspower.modules.blazing.items.BlazingRod.*;

public record BlazingRodData(int steps, float time, float quality, float duration) {

    public static final Codec<BlazingRodData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("steps").forGetter(BlazingRodData::steps),
            Codec.FLOAT.fieldOf("time").forGetter(BlazingRodData::time),
            Codec.FLOAT.fieldOf("quality").forGetter(BlazingRodData::quality),
            Codec.FLOAT.fieldOf("duration").forGetter(BlazingRodData::duration)
    ).apply(instance, BlazingRodData::new));

    public static final BlazingRodData DEFAULT = new BlazingRodData(MAX_INFUSION_STEPS, MAXTIME, START_QUALITY, START_DURATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, BlazingRodData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BlazingRodData::steps,
            ByteBufCodecs.FLOAT, BlazingRodData::time,
            ByteBufCodecs.FLOAT, BlazingRodData::quality,
            ByteBufCodecs.FLOAT, BlazingRodData::duration,
            BlazingRodData::new
    );

    public BlazingRodData withSteps(int steps) {
        return new BlazingRodData(steps, this.time, this.quality, this.duration);
    }

    public BlazingRodData withTime(float time) {
        return new BlazingRodData(this.steps, time, this.quality, this.duration);
    }

    public BlazingRodData withQuality(float quality) {
        return new BlazingRodData(this.steps, this.time, quality, this.duration);
    }

    public BlazingRodData withDuration(float duration) {
        return new BlazingRodData(this.steps, this.time, this.quality, duration);
    }
}
