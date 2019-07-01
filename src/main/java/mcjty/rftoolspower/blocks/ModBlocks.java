package mcjty.rftoolspower.blocks;

import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.generator.CoalGeneratorTileEntity;
import mcjty.rftoolspower.blocks.informationscreen.InformationScreenBlock;
import mcjty.rftoolspower.blocks.informationscreen.InformationScreenTileEntity;
import mcjty.rftoolspower.blocks.powercell.PowerCellBlock;
import mcjty.rftoolspower.blocks.powercell.PowerCellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public final class ModBlocks {

    // ----- Blocks -----

    // @todo dummy object just to have the powercell textures. Remove if a better solution is implemented in 1.14
    @ObjectHolder(RFToolsPower.MODID + ":celltextures")
    public static Block CELL_TEXTURES;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static PowerCellBlock CELL1;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static PowerCellBlock CELL2;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static PowerCellBlock CELL3;

    @ObjectHolder(RFToolsPower.MODID + ":" + InformationScreenTileEntity.REGNAME)
    public static InformationScreenBlock INFORMATION_SCREEN;

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static Block COALGENERATOR;

    // ----- Tile entities -----

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static TileEntityType<?> TYPE_CELL1;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static TileEntityType<?> TYPE_CELL2;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static TileEntityType<?> TYPE_CELL3;

    @ObjectHolder(RFToolsPower.MODID + ":" + InformationScreenTileEntity.REGNAME)
    public static TileEntityType<?> TYPE_INFORMATION_SCREEN;

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static TileEntityType<?> TYPE_COALGENERATOR;

    // ----- Containers -----

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static ContainerType<GenericContainer> CONTAINER_COALGENERATOR;

}
