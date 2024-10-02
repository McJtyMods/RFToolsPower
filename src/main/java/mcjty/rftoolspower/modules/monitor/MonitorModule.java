package mcjty.rftoolspower.modules.monitor;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.modules.monitor.client.GuiPowerMonitor;
import mcjty.rftoolspower.modules.monitor.client.PowerLevelRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.setup.Registration.*;

public class MonitorModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, PowerMonitorTileEntity> POWER_MONITOR = RBLOCKS.registerBlock("power_monitor",
            PowerMonitorTileEntity.class,
            PowerMonitorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            PowerMonitorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_POWER_MONITOR = CONTAINERS.register("power_monitor", GenericContainer::createContainerType);

    public static final RBlock<LogicSlabBlock, BlockItem, PowerLevelTileEntity> POWER_LEVEL = RBLOCKS.registerBlock("power_level",
            PowerLevelTileEntity.class,
            PowerLevelTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            PowerLevelTileEntity::new
    );

    public MonitorModule(IEventBus bus, Dist dist) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        PowerLevelRenderer.register();
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiPowerMonitor.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {

    }

    public static List<ResourceLocation> onTextureStitch() {
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
                                        .define('P', Tags.Items.GLASS_PANES)
                                        .define('A', VariousModule.MACHINE_BASE.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                                " P ", "rAr", " R ")
        );
    }
}
