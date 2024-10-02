package mcjty.rftoolspower.modules.powercell.data;

import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public enum Tier implements StringRepresentable {
    TIER1("1"),
    TIER2("2"),
    TIER3("3");

    private final String suffix;

    public static final List<Tier> VALUES = Arrays.asList(values());

    Tier(String suffix) {
        this.suffix = suffix;
    }

    public BlockEntityType<?> getType() {
        switch (this) {
            case TIER1:
                return PowerCellModule.CELL1.be().get();
            case TIER2:
                return PowerCellModule.CELL2.be().get();
            case TIER3:
                return PowerCellModule.CELL3.be().get();
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
