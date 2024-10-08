package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
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
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorData;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicModule implements IModule {

    public static final RBlock<LogicSlabBlock, BlockItem, EnderMonitorTileEntity> ENDER_MONITOR = RBLOCKS.registerBlock("ender_monitor",
            EnderMonitorTileEntity.class,
            EnderMonitorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            EnderMonitorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, PearlInjectorTileEntity> PEARL_INJECTOR = RBLOCKS.registerBlock("pearl_injector",
            PearlInjectorTileEntity.class,
            PearlInjectorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            PearlInjectorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, EndergenicTileEntity> ENDERGENIC = RBLOCKS.registerBlock("endergenic",
            EndergenicTileEntity.class,
            EndergenicTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            EndergenicTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    public static final Supplier<AttachmentType<EnderMonitorData>> ENDER_MONITOR_DATA = ATTACHMENT_TYPES.register(
            "ender_monitor_data", () -> AttachmentType.builder(() -> new EnderMonitorData(EnderMonitorMode.MODE_LOSTPEARL))
                    .serialize(EnderMonitorData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnderMonitorData>> ITEM_ENDER_MONITOR_DATA = COMPONENTS.registerComponentType(
            "ender_monitor_data",
            builder -> builder
                    .persistent(EnderMonitorData.CODEC)
                    .networkSynchronized(EnderMonitorData.STREAM_CODEC));

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
                        .blockState(p -> p.logicSlabBlock(ENDER_MONITOR.block().get(), "ender_monitor", p.modLoc("block/endergenic/ender_monitor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " o ", "rAr", "TrT"),
                Dob.blockBuilder(PEARL_INJECTOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.orientedBlock(PEARL_INJECTOR.block().get(), p.frontBasedModel("pearl_injector", p.modLoc("block/endergenic/pearl_injector"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('C', Tags.Items.CHESTS)
                                        .define('H', Blocks.HOPPER)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                " C ", "rFr", " H "),
                Dob.blockBuilder(ENDERGENIC)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.singleTextureBlockC(ENDERGENIC.block().get(), "endergenic", ModelProvider.BLOCK_FOLDER + "/endergenic/endergenic", builder -> builder.renderType("translucent")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('Z', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "dod", "ZFZ", "dod")
        );
    }
}
