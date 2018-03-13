package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.util.IStringSerializable;

public enum SideType implements IStringSerializable {
    INVISIBLE(null, null),
    BOTH_OUTPUT(RFToolsPower.MODID + ":blocks/cellboth_output", RFToolsPower.MODID + ":blocks/cellhoriz_output"),
    BOTH_INPUT(RFToolsPower.MODID + ":blocks/cellboth_input", RFToolsPower.MODID + ":blocks/cellhoriz_input"),
    BOTH_NONE(RFToolsPower.MODID + ":blocks/cellboth_none", RFToolsPower.MODID + ":blocks/cellhoriz_none"),
    UPPER_OUTPUT(RFToolsPower.MODID + ":blocks/cellupper_output", RFToolsPower.MODID + ":blocks/cellhoriz_output"),
    UPPER_INPUT(RFToolsPower.MODID + ":blocks/cellupper_input", RFToolsPower.MODID + ":blocks/cellhoriz_input"),
    UPPER_NONE(RFToolsPower.MODID + ":blocks/cellupper_none", RFToolsPower.MODID + ":blocks/cellhoriz_none"),
    MIDDLE_OUTPUT(RFToolsPower.MODID + ":blocks/cellmiddle_output", RFToolsPower.MODID + ":blocks/cellhoriz_output"),
    MIDDLE_INPUT(RFToolsPower.MODID + ":blocks/cellmiddle_input", RFToolsPower.MODID + ":blocks/cellhoriz_input"),
    MIDDLE_NONE(RFToolsPower.MODID + ":blocks/cellmiddle_none", RFToolsPower.MODID + ":blocks/cellhoriz_none"),
    LOWER_OUTPUT(RFToolsPower.MODID + ":blocks/celllower_output", RFToolsPower.MODID + ":blocks/cellhoriz_output"),
    LOWER_INPUT(RFToolsPower.MODID + ":blocks/celllower_input", RFToolsPower.MODID + ":blocks/cellhoriz_input"),
    LOWER_NONE(RFToolsPower.MODID + ":blocks/celllower_none", RFToolsPower.MODID + ":blocks/cellhoriz_none");

    private final String sideTexture;
    private final String upDownTexture;

    SideType(String sideTexture, String upDownTexture) {
        this.sideTexture = sideTexture;
        this.upDownTexture = upDownTexture;
    }

    public String getSideTexture() {
        return sideTexture;
    }

    public String getUpDownTexture() {
        return upDownTexture;
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
