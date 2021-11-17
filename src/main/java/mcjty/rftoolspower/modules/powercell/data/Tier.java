package mcjty.rftoolspower.modules.powercell.data;

import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
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
                return PowerCellModule.TYPE_CELL1.get();
            case TIER2:
                return PowerCellModule.TYPE_CELL2.get();
            case TIER3:
                return PowerCellModule.TYPE_CELL3.get();
        }
        throw new IllegalStateException("Unknown powercell tier!");
    }

    @Nonnull
    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public String getSuffix() {
        return suffix;
    }
}
