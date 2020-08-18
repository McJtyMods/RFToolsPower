package mcjty.rftoolspower.modules.powercell.data;

import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
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
                return PowerCellSetup.TYPE_CELL1.get();
            case TIER2:
                return PowerCellSetup.TYPE_CELL2.get();
            case TIER3:
                return PowerCellSetup.TYPE_CELL3.get();
        }
        throw new IllegalStateException("Unknown powercell tier!");
    }

    @Override
    public String getString() {
        return name().toLowerCase();
    }

    public String getSuffix() {
        return suffix;
    }
}
