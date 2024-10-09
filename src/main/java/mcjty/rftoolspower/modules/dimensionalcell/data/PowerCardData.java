package mcjty.rftoolspower.modules.dimensionalcell.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PowerCardData(int id) {

    public static final PowerCardData EMPTY = new PowerCardData(-1);

    public static final Codec<PowerCardData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(PowerCardData::id)
    ).apply(instance, PowerCardData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PowerCardData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PowerCardData::id,
            PowerCardData::new
    );
}
