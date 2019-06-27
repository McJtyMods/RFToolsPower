package mcjty.rftoolspower.setup;


import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.*;
import mcjty.rftoolspower.items.PowerCoreItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsPower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new InformationScreenBlock());
        event.getRegistry().register(new PowerCellBlock(Tier.TIER1));
        event.getRegistry().register(new PowerCellBlock(Tier.TIER2));
        event.getRegistry().register(new PowerCellBlock(Tier.TIER3));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsPower.setup.getTab());
        event.getRegistry().register(new BlockItem(ModBlocks.INFORMATION_SCREEN, properties).setRegistryName(InformationScreenBlock.REGNAME));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL1, properties).setRegistryName("cell1"));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL2, properties).setRegistryName("cell2"));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL3, properties).setRegistryName("cell3"));

        event.getRegistry().register(new PowerCoreItem("1"));
        event.getRegistry().register(new PowerCoreItem("2"));
        event.getRegistry().register(new PowerCoreItem("3"));
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER1), ModBlocks.CELL1).build(null).setRegistryName(ModBlocks.CELL1.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER2), ModBlocks.CELL2).build(null).setRegistryName(ModBlocks.CELL2.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER3), ModBlocks.CELL3).build(null).setRegistryName(ModBlocks.CELL3.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(InformationScreenTileEntity::new, ModBlocks.INFORMATION_SCREEN).build(null).setRegistryName(ModBlocks.INFORMATION_SCREEN.getRegistryName()));
    }
}
