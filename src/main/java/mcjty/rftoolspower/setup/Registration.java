package mcjty.rftoolspower.setup;


import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.InformationScreenBlock;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.blocks.PowerCellBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new InformationScreenBlock());
        event.getRegistry().register(new PowerCellBlock("cell1"));
        event.getRegistry().register(new PowerCellBlock("cell2"));
        event.getRegistry().register(new PowerCellBlock("cell3"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsPower.setup.getTab());
        event.getRegistry().register(new BlockItem(ModBlocks.INFORMATION_SCREEN, properties).setRegistryName(InformationScreenBlock.REGNAME));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL1, properties).setRegistryName("cell1"));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL2, properties).setRegistryName("cell2"));
        event.getRegistry().register(new BlockItem(ModBlocks.CELL3, properties).setRegistryName("cell3"));
    }

    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        // @todo
    }
}
