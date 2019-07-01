package mcjty.rftoolspower.blocks.powercell;

import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.List;

public enum Tier implements IStringSerializable {
    TIER1("1"),
    TIER2("2"),
    TIER3("3");

    private final String suffix;

    public static final List<Tier> VALUES = Arrays.asList(values());

    Tier(String suffix) {
        this.suffix = suffix;
    }

    public TileEntityType<?> getType() {
        switch (this) {
            case TIER1:
                return ModBlocks.TYPE_CELL1;
            case TIER2:
                return ModBlocks.TYPE_CELL2;
            case TIER3:
                return ModBlocks.TYPE_CELL3;
        }
        throw new IllegalStateException("Unknown powercell tier!");
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    public String getSuffix() {
        return suffix;
    }
}
