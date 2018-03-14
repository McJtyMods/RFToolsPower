package mcjty.rftoolspower.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModBlocks {

    public static PowerCellBlock cell1Block;
    public static PowerCellBlock cell2Block;
    public static PowerCellBlock cell3Block;
    public static InformationScreenBlock informationScreenBlock;

    public static void init() {
        cell1Block = new PowerCellBlock("cell1", PowerCellTileEntity1.class);
        cell2Block = new PowerCellBlock("cell2", PowerCellTileEntity2.class);
        cell3Block = new PowerCellBlock("cell3", PowerCellTileEntity3.class);
        informationScreenBlock = new InformationScreenBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        cell1Block.initModel();
        cell2Block.initModel();
        cell3Block.initModel();
        informationScreenBlock.initModel();
    }
}
