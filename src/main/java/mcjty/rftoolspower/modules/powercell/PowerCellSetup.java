package mcjty.rftoolspower.modules.powercell;

import mcjty.lib.blocks.BaseBlockItem;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.items.PowerCoreItem;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class PowerCellSetup {

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static PowerCellBlock CELL1;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME2)
    public static PowerCellBlock CELL2;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME3)
    public static PowerCellBlock CELL3;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME1)
    public static TileEntityType<?> TYPE_CELL1;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME2)
    public static TileEntityType<?> TYPE_CELL2;

    @ObjectHolder(RFToolsPower.MODID + ":" + PowerCellTileEntity.REGNAME3)
    public static TileEntityType<?> TYPE_CELL3;


    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new PowerCellBlock(Tier.TIER1));
        event.getRegistry().register(new PowerCellBlock(Tier.TIER2));
        event.getRegistry().register(new PowerCellBlock(Tier.TIER3));
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsPower.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(PowerCellSetup.CELL1, properties));
        event.getRegistry().register(new BaseBlockItem(PowerCellSetup.CELL2, properties));
        event.getRegistry().register(new BaseBlockItem(PowerCellSetup.CELL3, properties));

        event.getRegistry().register(new PowerCoreItem("1"));
        event.getRegistry().register(new PowerCoreItem("2"));
        event.getRegistry().register(new PowerCoreItem("3"));
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER1), PowerCellSetup.CELL1).build(null).setRegistryName(PowerCellSetup.CELL1.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER2), PowerCellSetup.CELL2).build(null).setRegistryName(PowerCellSetup.CELL2.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER3), PowerCellSetup.CELL3).build(null).setRegistryName(PowerCellSetup.CELL3.getRegistryName()));
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    }
}
