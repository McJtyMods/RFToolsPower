package mcjty.rftoolspower.modules.dimensionalcell.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DimensionalCellData(int energy, long totalInserted, long totalExtracted, int networkId) {

    public static final Codec<DimensionalCellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("energy").forGetter(DimensionalCellData::energy),
            Codec.LONG.fieldOf("totalInserted").forGetter(DimensionalCellData::totalInserted),
            Codec.LONG.fieldOf("totalExtracted").forGetter(DimensionalCellData::totalExtracted),
            Codec.INT.fieldOf("networkId").forGetter(DimensionalCellData::networkId)
    ).apply(instance, DimensionalCellData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionalCellData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DimensionalCellData::energy,
            ByteBufCodecs.VAR_LONG, DimensionalCellData::totalInserted,
            ByteBufCodecs.VAR_LONG, DimensionalCellData::totalExtracted,
            ByteBufCodecs.INT, DimensionalCellData::networkId,
            DimensionalCellData::new
    );
}
