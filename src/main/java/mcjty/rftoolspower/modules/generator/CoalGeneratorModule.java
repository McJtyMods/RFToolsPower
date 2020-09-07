package mcjty.rftoolspower.modules.generator;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolspower.setup.Registration.*;

public class CoalGeneratorModule implements IModule {

    public static final RegistryObject<BaseBlock> COALGENERATOR = BLOCKS.register("coalgenerator", CoalGeneratorTileEntity::createBlock);
    public static final RegistryObject<Item> COALGENERATOR_ITEM = ITEMS.register("coalgenerator", () -> new BlockItem(COALGENERATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_COALGENERATOR = TILES.register("coalgenerator", () -> TileEntityType.Builder.create(CoalGeneratorTileEntity::new, COALGENERATOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_COALGENERATOR = CONTAINERS.register("coalgenerator", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GenericGuiContainer.register(CONTAINER_COALGENERATOR.get(), GuiCoalGenerator::new);
        });
    }

    @Override
    public void initConfig() {
        CoalGeneratorConfig.setup(Config.SERVER_BUILDER);
    }
}
