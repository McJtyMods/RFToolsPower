package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.*;
import mcjty.rftoolspower.modules.dimensionalcell.client.GuiDimensionalCell;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class DimensionalCellModule implements IModule {

    public static final DeferredBlock<DimensionalCellBlock> DIMENSIONAL_CELL = BLOCKS.register("dimensionalcell", () -> new DimensionalCellBlock(DimensionalCellType.NORMAL, DimensionalCellNormalTileEntity::new));
    public static final DeferredItem<Item> DIMENSIONAL_CELL_ITEM = ITEMS.register("dimensionalcell", tab(() -> new BlockItem(DIMENSIONAL_CELL.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL = TILES.register("dimensionalcell", () -> BlockEntityType.Builder.of(DimensionalCellNormalTileEntity::new, DIMENSIONAL_CELL.get()).build(null));

    public static final DeferredBlock<DimensionalCellBlock> DIMENSIONAL_CELL_ADVANCED = BLOCKS.register("dimensionalcell_advanced", () -> new DimensionalCellBlock(DimensionalCellType.ADVANCED, DimensionalCellAdvancedTileEntity::new));
    public static final DeferredItem<Item> DIMENSIONAL_CELL_ADVANCED_ITEM = ITEMS.register("dimensionalcell_advanced", tab(() -> new BlockItem(DIMENSIONAL_CELL_ADVANCED.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_ADVANCED = TILES.register("dimensionalcell_advanced", () -> BlockEntityType.Builder.of(DimensionalCellAdvancedTileEntity::new, DIMENSIONAL_CELL_ADVANCED.get()).build(null));

    public static final DeferredBlock<DimensionalCellBlock> DIMENSIONAL_CELL_SIMPLE = BLOCKS.register("dimensionalcell_simple", () -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new));
    public static final DeferredItem<Item> DIMENSIONAL_CELL_SIMPLE_ITEM = ITEMS.register("dimensionalcell_simple", tab(() -> new BlockItem(DIMENSIONAL_CELL_SIMPLE.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_SIMPLE = TILES.register("dimensionalcell_simple", () -> BlockEntityType.Builder.of(DimensionalCellSimpleTileEntity::new, DIMENSIONAL_CELL_SIMPLE.get()).build(null));

    public static final DeferredBlock<DimensionalCellBlock> DIMENSIONAL_CELL_CREATIVE = BLOCKS.register("dimensionalcell_creative", () -> new DimensionalCellBlock(DimensionalCellType.CREATIVE, DimensionalCellCreativeTileEntity::new));
    public static final DeferredItem<Item> DIMENSIONAL_CELL_CREATIVE_ITEM = ITEMS.register("dimensionalcell_creative", tab(() -> new BlockItem(DIMENSIONAL_CELL_CREATIVE.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_CREATIVE = TILES.register("dimensionalcell_creative", () -> BlockEntityType.Builder.of(DimensionalCellCreativeTileEntity::new, DIMENSIONAL_CELL_CREATIVE.get()).build(null));

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIMENSIONAL_CELL = CONTAINERS.register("dimensionalcell", GenericContainer::createContainerType);

    public static final DeferredItem<PowerCellCardItem> POWERCELL_CARD = ITEMS.register("powercell_card", tab(PowerCellCardItem::new));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiDimensionalCell.register();
            PowerCellCardItem.initOverrides(POWERCELL_CARD.get());
        });
    }

    @Override
    public void initConfig(IEventBus bus) {
        DimensionalCellConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(DIMENSIONAL_CELL)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_DIMENSIONAL_CELL)
                        .blockState(p -> DataGenHelper.createDimensionalCellModel(p, DIMENSIONAL_CELL.get(), ""))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('P', Items.PRISMARINE_SHARD)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "PFP", "ReR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_ADVANCED)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_DIMENSIONAL_CELL_ADVANCED)
                        .blockState(p -> DataGenHelper.createDimensionalCellModel(p, DIMENSIONAL_CELL_ADVANCED.get(), "advanced"))
                        .shaped(builder -> builder
                                        .define('K', DIMENSIONAL_CELL.get())
                                        .define('*', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "R*R", "*K*", "R*R"),
                Dob.blockBuilder(DIMENSIONAL_CELL_SIMPLE)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_DIMENSIONAL_CELL_SIMPLE)
                        .blockState(p -> DataGenHelper.createDimensionalCellModel(p, DIMENSIONAL_CELL_SIMPLE.get(), "simple"))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('q', Items.QUARTZ)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "qFq", "RdR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_CREATIVE)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_DIMENSIONAL_CELL_CREATIVE)
                        .blockState(p -> DataGenHelper.createDimensionalCellModel(p, DIMENSIONAL_CELL_CREATIVE.get(), "creative")),
                Dob.itemBuilder(POWERCELL_CARD)
                        .shaped(builder -> builder
                                        .define('g', Items.GOLD_NUGGET)
                                        .unlockedBy("paper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER, Items.REDSTONE)),
                                "rgr", "gpg", "rgr")
        );
    }
}
