package mcjty.rftoolspower.blocks;

import net.minecraft.util.IStringSerializable;

public enum Tier implements IStringSerializable {
    TIER1,
    TIER2,
    TIER3;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
