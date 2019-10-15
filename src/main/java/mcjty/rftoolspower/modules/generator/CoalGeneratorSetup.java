package mcjty.rftoolspower.modules.generator;

import mcjty.lib.blocks.BaseBlockItem;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

public class CoalGeneratorSetup {

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static Block COALGENERATOR;

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static TileEntityType<?> TYPE_COALGENERATOR;

    @ObjectHolder(RFToolsPower.MODID + ":" + CoalGeneratorTileEntity.REGNAME)
    public static ContainerType<GenericContainer> CONTAINER_COALGENERATOR;

    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(CoalGeneratorTileEntity.createBlock());
        }
    }

    public static void registerItems(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(RFToolsPower.setup.getTab());
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(new BaseBlockItem(COALGENERATOR, properties));
        }
    }

    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(TileEntityType.Builder.create(CoalGeneratorTileEntity::new, COALGENERATOR).build(null).setRegistryName(COALGENERATOR.getRegistryName()));
        }
    }

    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        if (CoalGeneratorConfig.ENABLED.get()) {
            event.getRegistry().register(GenericContainer.createContainerType("coalgenerator"));
        }
    }
}
