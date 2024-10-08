package mcjty.rftoolspower.modules.endergenic.data;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.NamedEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;
import java.util.Map;

public enum EnderMonitorMode implements NamedEnum<EnderMonitorMode>, StringRepresentable {
    MODE_LOSTPEARL("Lost Pearl"),
    MODE_PEARLFIRED("Pearl Fired"),
    MODE_PEARLARRIVED("Pearl Arrived"),
    ;

    public static final Codec<EnderMonitorMode> CODEC = StringRepresentable.fromEnum(EnderMonitorMode::values);
    public static final StreamCodec<FriendlyByteBuf, EnderMonitorMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(EnderMonitorMode.class);

    private static final Map<String,EnderMonitorMode> modeToMode = new HashMap<>();

    private final String description;

    EnderMonitorMode(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return description;
    }

    @Override
    public String[] getDescription() {
        return new String[] { description };
    }


    public static EnderMonitorMode getMode(String mode) {
        return modeToMode.get(mode);
    }



    static {
        for (EnderMonitorMode mode : values()) {
            modeToMode.put(mode.description, mode);
        }
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }
}
