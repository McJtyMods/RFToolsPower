package mcjty.rftoolspower.modules.endergenic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EnderMonitorData(EnderMonitorMode mode) {

    public static final Codec<EnderMonitorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EnderMonitorMode.CODEC.fieldOf("mode").forGetter(EnderMonitorData::mode)
    ).apply(instance, EnderMonitorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnderMonitorData> STREAM_CODEC = StreamCodec.composite(
            EnderMonitorMode.STREAM_CODEC, EnderMonitorData::mode,
            EnderMonitorData::new
    );
}
