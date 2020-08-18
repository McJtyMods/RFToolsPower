package mcjty.rftoolspower.modules.powercell.data;

import net.minecraft.util.IStringSerializable;

public enum SideType implements IStringSerializable {
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

    @Override
    public String getString() {
        return name().toLowerCase();
    }
}
