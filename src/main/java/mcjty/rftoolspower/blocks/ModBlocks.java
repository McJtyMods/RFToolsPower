package mcjty.rftoolspower.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModBlocks {

    public static PowerCellBlock cell1Block;

    public static void init() {
        cell1Block = new PowerCellBlock("cell1");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        cell1Block.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        cell1Block.initItemModel();
    }
}
