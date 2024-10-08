package mcjty.rftoolspower.modules.blazing.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record AgitatorData(List<Boolean> locked) {

    public static final Codec<AgitatorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.BOOL).fieldOf("locked").forGetter(AgitatorData::locked)
    ).apply(instance, AgitatorData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AgitatorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), AgitatorData::locked,
            AgitatorData::new
    );

    public boolean isLocked(int idx) {
        return (idx >= 0 && idx < locked.size()) ? locked.get(idx) : false;
    }

    public void setLocked(int idx, boolean value) {
        while (idx >= locked.size()) {
            locked.add(false);
        }
        locked.set(idx, value);
    }
}
