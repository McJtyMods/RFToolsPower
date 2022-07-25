package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.modules.endergenic.client.*;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicModule implements IModule {

    public static final RegistryObject<LogicSlabBlock> ENDER_MONITOR = BLOCKS.register("ender_monitor", EnderMonitorTileEntity::createBlock);
    public static final RegistryObject<Item> ENDER_MONITOR_ITEM = ITEMS.register("ender_monitor", () -> new BlockItem(ENDER_MONITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_ENDER_MONITOR = TILES.register("ender_monitor", () -> BlockEntityType.Builder.of(EnderMonitorTileEntity::new, ENDER_MONITOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> PEARL_INJECTOR = BLOCKS.register("pearl_injector", PearlInjectorTileEntity::createBlock);
    public static final RegistryObject<Item> PEARL_INJECTOR_ITEM = ITEMS.register("pearl_injector", () -> new BlockItem(PEARL_INJECTOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_PEARL_INJECTOR = TILES.register("pearl_injector", () -> BlockEntityType.Builder.of(PearlInjectorTileEntity::new, PEARL_INJECTOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> ENDERGENIC = BLOCKS.register("endergenic", EndergenicTileEntity::createBlock);
    public static final RegistryObject<Item> ENDERGENIC_ITEM = ITEMS.register("endergenic", () -> new BlockItem(ENDERGENIC.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<EndergenicTileEntity>> TYPE_ENDERGENIC = TILES.register("endergenic", () -> BlockEntityType.Builder.of(EndergenicTileEntity::new, ENDERGENIC.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    public EndergenicModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiEndergenic.register();
            GuiEnderMonitor.register();
            GuiPearlInjector.register();
            ClientCommandHandler.registerCommands();
        });

        EndergenicRenderer.register();
    }

    @Override
    public void initConfig() {
        EndergenicConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

}
