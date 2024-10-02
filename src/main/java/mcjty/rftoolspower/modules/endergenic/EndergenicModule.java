package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.modules.endergenic.client.EndergenicRenderer;
import mcjty.rftoolspower.modules.endergenic.client.GuiEnderMonitor;
import mcjty.rftoolspower.modules.endergenic.client.GuiEndergenic;
import mcjty.rftoolspower.modules.endergenic.client.GuiPearlInjector;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicModule implements IModule {

    public static final DeferredBlock<LogicSlabBlock> ENDER_MONITOR = BLOCKS.register("ender_monitor", EnderMonitorTileEntity::createBlock);
    public static final DeferredItem<Item> ENDER_MONITOR_ITEM = ITEMS.register("ender_monitor", tab(() -> new BlockItem(ENDER_MONITOR.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_ENDER_MONITOR = TILES.register("ender_monitor", () -> BlockEntityType.Builder.of(EnderMonitorTileEntity::new, ENDER_MONITOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> PEARL_INJECTOR = BLOCKS.register("pearl_injector", PearlInjectorTileEntity::createBlock);
    public static final DeferredItem<Item> PEARL_INJECTOR_ITEM = ITEMS.register("pearl_injector", tab(() -> new BlockItem(PEARL_INJECTOR.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_PEARL_INJECTOR = TILES.register("pearl_injector", () -> BlockEntityType.Builder.of(PearlInjectorTileEntity::new, PEARL_INJECTOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final DeferredBlock<BaseBlock> ENDERGENIC = BLOCKS.register("endergenic", EndergenicTileEntity::createBlock);
    public static final DeferredItem<Item> ENDERGENIC_ITEM = ITEMS.register("endergenic", tab(() -> new BlockItem(ENDERGENIC.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<EndergenicTileEntity>> TYPE_ENDERGENIC = TILES.register("endergenic", () -> BlockEntityType.Builder.of(EndergenicTileEntity::new, ENDERGENIC.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    public EndergenicModule(IEventBus bus, Dist dist) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientCommandHandler.registerCommands();
        });

        EndergenicRenderer.register();
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiEnderMonitor.register(event);
        GuiEndergenic.register(event);
        GuiPearlInjector.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {
        EndergenicConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(ENDER_MONITOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.logicSlabBlock(ENDER_MONITOR.get(), "ender_monitor", p.modLoc("block/endergenic/ender_monitor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " o ", "rAr", "TrT"),
                Dob.blockBuilder(PEARL_INJECTOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.orientedBlock(PEARL_INJECTOR.get(), p.frontBasedModel("pearl_injector", p.modLoc("block/endergenic/pearl_injector"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('C', Tags.Items.CHESTS)
                                        .define('H', Blocks.HOPPER)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                " C ", "rFr", " H "),
                Dob.blockBuilder(ENDERGENIC)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.singleTextureBlockC(ENDERGENIC.get(), "endergenic", ModelProvider.BLOCK_FOLDER + "/endergenic/endergenic", builder -> builder.renderType("translucent")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('Z', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "dod", "ZFZ", "dod")
        );
    }
}
