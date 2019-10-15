package mcjty.rftoolspower.setup;


import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.modules.generator.CoalGeneratorConfig;
import mcjty.rftoolspower.items.PowerCoreItem;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenBlock;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenTileEntity;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
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
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(CoalGeneratorTileEntity.createBlock());
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsPower.setup.getTab());
        event.getRegistry().register(new BaseBlockItem(ModBlocks.INFORMATION_SCREEN, properties));
        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL1, properties));
        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL2, properties));
        event.getRegistry().register(new BaseBlockItem(ModBlocks.CELL3, properties));

        event.getRegistry().register(new PowerCoreItem("1"));
        event.getRegistry().register(new PowerCoreItem("2"));
        event.getRegistry().register(new PowerCoreItem("3"));

        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(new BaseBlockItem(ModBlocks.COALGENERATOR, properties));
        }
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER1), ModBlocks.CELL1).build(null).setRegistryName(ModBlocks.CELL1.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER2), ModBlocks.CELL2).build(null).setRegistryName(ModBlocks.CELL2.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER3), ModBlocks.CELL3).build(null).setRegistryName(ModBlocks.CELL3.getRegistryName()));
        event.getRegistry().register(TileEntityType.Builder.create(InformationScreenTileEntity::new, ModBlocks.INFORMATION_SCREEN).build(null).setRegistryName(ModBlocks.INFORMATION_SCREEN.getRegistryName()));
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(TileEntityType.Builder.create(CoalGeneratorTileEntity::new, ModBlocks.COALGENERATOR).build(null).setRegistryName(ModBlocks.COALGENERATOR.getRegistryName()));
        }
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(GenericContainer.createContainerType("coalgenerator"));
        }
    }

}
