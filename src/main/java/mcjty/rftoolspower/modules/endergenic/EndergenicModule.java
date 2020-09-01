package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.modules.endergenic.client.EndergenicRenderer;
import mcjty.rftoolspower.modules.endergenic.client.GuiEnderMonitor;
import mcjty.rftoolspower.modules.endergenic.client.GuiEndergenic;
import mcjty.rftoolspower.modules.endergenic.client.GuiPearlInjector;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicModule implements IModule {

    public static final RegistryObject<LogicSlabBlock> ENDER_MONITOR = BLOCKS.register("ender_monitor", EnderMonitorTileEntity::createBlock);
    public static final RegistryObject<Item> ENDER_MONITOR_ITEM = ITEMS.register("ender_monitor", () -> new BlockItem(ENDER_MONITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_ENDER_MONITOR = TILES.register("ender_monitor", () -> TileEntityType.Builder.create(EnderMonitorTileEntity::new, ENDER_MONITOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> PEARL_INJECTOR = BLOCKS.register("pearl_injector", PearlInjectorTileEntity::createBlock);
    public static final RegistryObject<Item> PEARL_INJECTOR_ITEM = ITEMS.register("pearl_injector", () -> new BlockItem(PEARL_INJECTOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_PEARL_INJECTOR = TILES.register("pearl_injector", () -> TileEntityType.Builder.create(PearlInjectorTileEntity::new, PEARL_INJECTOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> ENDERGENIC = BLOCKS.register("endergenic", EndergenicTileEntity::createBlock);
    public static final RegistryObject<Item> ENDERGENIC_ITEM = ITEMS.register("endergenic", () -> new BlockItem(ENDERGENIC.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<EndergenicTileEntity>> TYPE_ENDERGENIC = TILES.register("endergenic", () -> TileEntityType.Builder.create(EndergenicTileEntity::new, ENDERGENIC.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    public EndergenicModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(EndergenicModule::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GenericGuiContainer.register(CONTAINER_ENDERGENIC.get(), GuiEndergenic::new);
            GenericGuiContainer.register(CONTAINER_ENDER_MONITOR.get(), GuiEnderMonitor::new);
            GenericGuiContainer.register(CONTAINER_PEARL_INJECTOR.get(), GuiPearlInjector::new);
            ClientCommandHandler.registerCommands();
        });

        RenderTypeLookup.setRenderLayer(ENDERGENIC.get(), RenderType.getTranslucent());

        EndergenicRenderer.register();
    }

    @Override
    public void initConfig() {
        EndergenicConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }

        event.addSprite(EndergenicRenderer.HALO);
        event.addSprite(EndergenicRenderer.BLACKFLASH);
        event.addSprite(EndergenicRenderer.WHITEFLASH);
        event.addSprite(EndergenicRenderer.BLUEGLOW);
        event.addSprite(EndergenicRenderer.REDGLOW);
    }

}
