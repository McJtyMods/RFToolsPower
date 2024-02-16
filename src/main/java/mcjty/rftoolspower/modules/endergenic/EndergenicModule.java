package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.ClientTools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.modules.endergenic.client.*;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

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

    public EndergenicModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientTools.onTextureStitch(FMLJavaModLoadingContext.get().getModEventBus(), ClientSetup::onTextureStitch);
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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(ENDER_MONITOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_ENDER_MONITOR)
                        .blockState(p -> p.logicSlabBlock(ENDER_MONITOR.get(), "ender_monitor", p.modLoc("block/endergenic/ender_monitor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " o ", "rAr", "TrT"),
                Dob.blockBuilder(PEARL_INJECTOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_PEARL_INJECTOR)
                        .blockState(p -> p.orientedBlock(PEARL_INJECTOR.get(), p.frontBasedModel("pearl_injector", p.modLoc("block/endergenic/pearl_injector"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('C', Tags.Items.CHESTS)
                                        .define('H', Blocks.HOPPER)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                " C ", "rFr", " H "),
                Dob.blockBuilder(ENDERGENIC)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_ENDERGENIC)
                        .blockState(p -> p.singleTextureBlockC(ENDERGENIC.get(), "endergenic", ModelProvider.BLOCK_FOLDER + "/endergenic/endergenic", builder -> builder.renderType("translucent")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('Z', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "dod", "ZFZ", "dod")
        );
    }
}
