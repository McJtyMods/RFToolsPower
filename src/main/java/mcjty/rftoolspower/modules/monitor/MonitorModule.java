package mcjty.rftoolspower.modules.monitor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.datagen.model.ModelFile;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.modules.monitor.client.GuiPowerMonitor;
import mcjty.rftoolspower.modules.monitor.client.PowerLevelRenderer;
import mcjty.rftoolspower.modules.monitor.data.PowerMonitorData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import mcjty.lib.setup.DeferredHolder;
import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.setup.Registration.*;

public class MonitorModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, PowerMonitorTileEntity> POWER_MONITOR = RBLOCKS.registerBlock("power_monitor",
            PowerMonitorTileEntity.class,
            PowerMonitorTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            PowerMonitorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_POWER_MONITOR = CONTAINERS.register("power_monitor", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, PowerLevelTileEntity> POWER_LEVEL = RBLOCKS.registerBlock("power_level",
            PowerLevelTileEntity.class,
            PowerLevelTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            PowerLevelTileEntity::new);

    public static final AttachmentType<PowerMonitorData> POWER_MONITOR_DATA = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath("rftoolspower", "power_monitor_data"), builder -> builder.initializer(() -> new PowerMonitorData(0, (byte) 0, (byte) 0)).persistent(PowerMonitorData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PowerMonitorData>> ITEM_POWER_MONITOR_DATA = COMPONENTS.registerComponentType(
            "power_monitor_data",
            builder -> builder
                    .persistent(PowerMonitorData.CODEC)
                    .networkSynchronized(PowerMonitorData.STREAM_CODEC));

    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerMenuScreens();
        PowerLevelRenderer.register();
    }

    public void registerMenuScreens() {
        GuiPowerMonitor.register();
    }

    @Override
    public void initConfig() {

    }

    public static List<Identifier> onTextureStitch() {
        return Arrays.asList(PowerLevelRenderer.DIGITS);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(POWER_MONITOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> {
                            ModelFile[][] models = new ModelFile[][]{
                                    p.getLogicSlabModels("power_monitor0", p.modLoc("block/monitor/power_monitor0")),
                                    p.getLogicSlabModels("power_monitor1", p.modLoc("block/monitor/power_monitor1")),
                                    p.getLogicSlabModels("power_monitor2", p.modLoc("block/monitor/power_monitor2")),
                                    p.getLogicSlabModels("power_monitor3", p.modLoc("block/monitor/power_monitor3")),
                                    p.getLogicSlabModels("power_monitor4", p.modLoc("block/monitor/power_monitor4")),
                                    p.getLogicSlabModels("power_monitor5", p.modLoc("block/monitor/power_monitor5"))
                            };
                            p.variantBlock(POWER_MONITOR.block().get(),
                                    state -> models[state.getValue(PowerMonitorTileEntity.LEVEL)][state.getValue(LogicSlabBlock.LOGIC_FACING).getRotationStep()],
                                    state -> p.getXRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()),
                                    state -> p.getYRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()));
                        })
                        .shaped(builder -> builder
                                        .define('P', Items.COMPARATOR)
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " R "),
                Dob.blockBuilder(POWER_LEVEL)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockState(p -> p.logicSlabBlock(POWER_LEVEL.block().get(), "power_level", p.modLoc("block/monitor/power_level")))
                        .shaped(builder -> builder
                                        .define('P', net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, Identifier.parse("c:glass_panes")))
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " R ")
        );
    }
}
