package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.List;

public enum SideType implements IStringSerializable {
    INVISIBLE(false, false),
    OUTPUT(false, true),
    INPUT(true, false),
    NONE(false, false);

    public static final List<SideType> VALUES = Arrays.asList(values());

    private final boolean input;
    private final boolean output;

    SideType(boolean input, boolean output) {
        this.input = input;
        this.output = output;
    }

    public String getSideTexture(boolean upper, boolean lower, int tier) {
        String s = RFToolsPower.MODID + ":block/cell";
        if (upper && lower) {
            s += "middle_t";
        } else if (upper) {
            s += "lower_t";
        } else if (lower) {
            s += "upper_t";
        } else {
            s += "both_t";
        }
        s += tier;
        return s;
    }

    public String getUpDownTexture() {
        return RFToolsPower.MODID + ":block/cellhoriz_t1";
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
