package mcjty.rftoolspower.modules.endergenic;

import java.util.List;
import java.util.function.Supplier;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.datagen.model.ModelProvider;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsbase.tools.ModelDataGen;
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
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import mcjty.lib.setup.DeferredHolder;
import static mcjty.lib.datagen.DataGen.has;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicModule implements IModule {

    public static final RBlock<LogicSlabBlock, BlockItem, EnderMonitorTileEntity> ENDER_MONITOR = RBLOCKS.registerBlock("ender_monitor",
            EnderMonitorTileEntity.class,
            EnderMonitorTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            EnderMonitorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, PearlInjectorTileEntity> PEARL_INJECTOR = RBLOCKS.registerBlock("pearl_injector",
            PearlInjectorTileEntity.class,
            PearlInjectorTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            PearlInjectorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, EndergenicTileEntity> ENDERGENIC = RBLOCKS.registerBlock("endergenic",
            EndergenicTileEntity.class,
            EndergenicTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            EndergenicTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    public static final AttachmentType<EnderMonitorData> ENDER_MONITOR_DATA = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
            net.minecraft.resources.Identifier.fromNamespaceAndPath("rftoolspower", "ender_monitor_data"), builder -> builder.initializer(() -> new EnderMonitorData(EnderMonitorMode.MODE_LOSTPEARL)).persistent(EnderMonitorData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnderMonitorData>> ITEM_ENDER_MONITOR_DATA = COMPONENTS.registerComponentType(
            "ender_monitor_data",
            builder -> builder
                    .persistent(EnderMonitorData.CODEC)
                    .networkSynchronized(EnderMonitorData.STREAM_CODEC));

    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerMenuScreens();

            ClientCommandHandler.registerCommands();

        EndergenicRenderer.register();
    }

    public void registerMenuScreens() {
        GuiEnderMonitor.register();
        GuiEndergenic.register();
        GuiPearlInjector.register();
    }

    @Override
    public void initConfig() {
        EndergenicConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(ENDER_MONITOR)
                        .blockTags(List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.logicSlabBlock(ENDER_MONITOR.block().get(), "ender_monitor", p.modLoc("block/endergenic/ender_monitor")))
                        .shaped(builder -> builder
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " o ", "rAr", "TrT"),
                Dob.blockBuilder(PEARL_INJECTOR)
                        .blockTags(List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> p.orientedBlock(PEARL_INJECTOR.block().get(), p.frontBasedModel("pearl_injector", p.modLoc("block/endergenic/pearl_injector"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('C', net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.parse("c:chests")))
                                        .define('H', Blocks.HOPPER)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                " C ", "rFr", " H "),
                Dob.blockBuilder(ENDERGENIC)
                        .blockTags(List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get())
                        .blockState(p -> p.singleTextureBlockC(ENDERGENIC.block().get(), "endergenic", ModelProvider.BLOCK_FOLDER + "/endergenic/endergenic", builder -> ModelDataGen.renderType(builder, "translucent")))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('Z', VariousModule.INFUSED_ENDERPEARL.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "dod", "ZFZ", "dod")
        );
    }
}
