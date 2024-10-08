package mcjty.rftoolspower.modules.generator.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CoalGeneratorData(int burning) {

    public static final Codec<CoalGeneratorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("burning").forGetter(CoalGeneratorData::burning)
    ).apply(instance, CoalGeneratorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CoalGeneratorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CoalGeneratorData::burning,
            CoalGeneratorData::new
    );

    public CoalGeneratorData withBurning(int burning) {
        return new CoalGeneratorData(burning);
    }
}
