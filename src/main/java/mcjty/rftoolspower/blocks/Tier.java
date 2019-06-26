package mcjty.rftoolspower.blocks;

import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.List;

public enum Tier implements IStringSerializable {
    TIER1,
    TIER2,
    TIER3;

    public static final List<Tier> VALUES = Arrays.asList(values());

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
