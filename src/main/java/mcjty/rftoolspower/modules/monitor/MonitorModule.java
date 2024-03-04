package mcjty.rftoolspower.modules.monitor;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.modules.monitor.client.GuiPowerMonitor;
import mcjty.rftoolspower.modules.monitor.client.PowerLevelRenderer;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class MonitorModule implements IModule {

    public static final DeferredBlock<LogicSlabBlock> POWER_MONITOR = BLOCKS.register("power_monitor", PowerMonitorTileEntity::createBlock);
    public static final DeferredItem<Item> POWER_MONITOR_ITEM = ITEMS.register("power_monitor", tab(() -> new BlockItem(POWER_MONITOR.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_POWER_MONITOR = TILES.register("power_monitor", () -> BlockEntityType.Builder.of(PowerMonitorTileEntity::new, POWER_MONITOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_POWER_MONITOR = CONTAINERS.register("power_monitor", GenericContainer::createContainerType);

    public static final DeferredBlock<LogicSlabBlock> POWER_LEVEL = BLOCKS.register("power_level", PowerLevelTileEntity::createBlock);
    public static final DeferredItem<Item> POWER_LEVEL_ITEM = ITEMS.register("power_level", tab(() -> new BlockItem(POWER_LEVEL.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<PowerLevelTileEntity>> TYPE_POWER_LEVEL = TILES.register("power_level", () -> BlockEntityType.Builder.of(PowerLevelTileEntity::new, POWER_LEVEL.get()).build(null));

    public MonitorModule(IEventBus bus, Dist dist) {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiPowerMonitor.register();
        });

        PowerLevelRenderer.register();
    }

    @Override
    public void initConfig(IEventBus bus) {

    }

    public static List<ResourceLocation> onTextureStitch() {
        return Arrays.asList(PowerLevelRenderer.DIGITS);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(POWER_MONITOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_POWER_MONITOR)
                        .blockState(p -> {
                            ModelFile[][] models = new ModelFile[][]{
                                    p.getLogicSlabModels("power_monitor0", p.modLoc("block/monitor/power_monitor0")),
                                    p.getLogicSlabModels("power_monitor1", p.modLoc("block/monitor/power_monitor1")),
                                    p.getLogicSlabModels("power_monitor2", p.modLoc("block/monitor/power_monitor2")),
                                    p.getLogicSlabModels("power_monitor3", p.modLoc("block/monitor/power_monitor3")),
                                    p.getLogicSlabModels("power_monitor4", p.modLoc("block/monitor/power_monitor4")),
                                    p.getLogicSlabModels("power_monitor5", p.modLoc("block/monitor/power_monitor5"))
                            };
                            p.variantBlock(POWER_MONITOR.get(),
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
                        .blockState(p -> p.logicSlabBlock(POWER_LEVEL.get(), "power_level", p.modLoc("block/monitor/power_level")))
                        .shaped(builder -> builder
                                        .define('P', Tags.Items.GLASS_PANES)
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " R ")
        );
    }
}
