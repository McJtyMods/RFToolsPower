package mcjty.rftoolspower.modules.powercell.data;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum SideType implements StringRepresentable {
    NONE(false, false),
    OUTPUT(false, true),
    INPUT(true, false);

    public static final SideType[] VALUES = values();

    private final boolean input;
    private final boolean output;

    SideType(boolean input, boolean output) {
        this.input = input;
        this.output = output;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
    }

    @Nonnull
    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
