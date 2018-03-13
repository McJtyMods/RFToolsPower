package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.util.IStringSerializable;

public enum SideType implements IStringSerializable {
    INVISIBLE(null, null, false, false),
    BOTH_OUTPUT(RFToolsPower.MODID + ":blocks/cellboth_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, true),
    BOTH_INPUT(RFToolsPower.MODID + ":blocks/cellboth_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", true, false),
    BOTH_NONE(RFToolsPower.MODID + ":blocks/cellboth_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, false),
    UPPER_OUTPUT(RFToolsPower.MODID + ":blocks/cellupper_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, true),
    UPPER_INPUT(RFToolsPower.MODID + ":blocks/cellupper_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", true, false),
    UPPER_NONE(RFToolsPower.MODID + ":blocks/cellupper_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, false),
    MIDDLE_OUTPUT(RFToolsPower.MODID + ":blocks/cellmiddle_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, true),
    MIDDLE_INPUT(RFToolsPower.MODID + ":blocks/cellmiddle_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", true, false),
    MIDDLE_NONE(RFToolsPower.MODID + ":blocks/cellmiddle_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, false),
    LOWER_OUTPUT(RFToolsPower.MODID + ":blocks/celllower_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, true),
    LOWER_INPUT(RFToolsPower.MODID + ":blocks/celllower_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", true, false),
    LOWER_NONE(RFToolsPower.MODID + ":blocks/celllower_t1", RFToolsPower.MODID + ":blocks/cellhoriz_t1", false, false);

    private final String sideTexture;
    private final String upDownTexture;
    private final boolean input;
    private final boolean output;

    SideType(String sideTexture, String upDownTexture, boolean input, boolean output) {
        this.sideTexture = sideTexture;
        this.upDownTexture = upDownTexture;
        this.input = input;
        this.output = output;
    }

    public String getSideTexture() {
        return sideTexture;
    }

    public String getUpDownTexture() {
        return upDownTexture;
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
