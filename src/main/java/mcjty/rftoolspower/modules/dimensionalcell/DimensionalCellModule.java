package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.Registration;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.*;
import mcjty.rftoolspower.modules.dimensionalcell.client.GuiDimensionalCell;
import mcjty.rftoolspower.modules.dimensionalcell.data.DimensionalCellData;
import mcjty.rftoolspower.modules.dimensionalcell.data.PowerCardData;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;

import java.util.function.Supplier;

import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class DimensionalCellModule implements IModule {

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellNormalTileEntity> DIMENSIONAL_CELL = RBLOCKS.registerBlock("dimensionalcell",
            DimensionalCellNormalTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.NORMAL, DimensionalCellNormalTileEntity::new, properties),
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellNormalTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellAdvancedTileEntity> DIMENSIONAL_CELL_ADVANCED = RBLOCKS.registerBlock("dimensionalcell_advanced",
            DimensionalCellAdvancedTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.ADVANCED, DimensionalCellAdvancedTileEntity::new, properties),
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellAdvancedTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellSimpleTileEntity> DIMENSIONAL_CELL_SIMPLE = RBLOCKS.registerBlock("dimensionalcell_simple",
            DimensionalCellSimpleTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new, properties),
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellSimpleTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellCreativeTileEntity> DIMENSIONAL_CELL_CREATIVE = RBLOCKS.registerBlock("dimensionalcell_creative",
            DimensionalCellCreativeTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.CREATIVE, DimensionalCellCreativeTileEntity::new, properties),
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellCreativeTileEntity::new);

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIMENSIONAL_CELL = CONTAINERS.register("dimensionalcell", GenericContainer::createContainerType);

    public static final DeferredItem<PowerCellCardItem> POWERCELL_CARD = ITEMS.register("powercell_card", tab(PowerCellCardItem::new));

    public static final AttachmentType<DimensionalCellData> DIMENSIONAL_CELL_DATA = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
            net.minecraft.resources.Identifier.fromNamespaceAndPath("rftoolspower", "dimensional_cell_data"), builder -> builder.initializer(() -> new DimensionalCellData(0, 0, 0, -1)).persistent(DimensionalCellData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DimensionalCellData>> ITEM_DIMENSIONAL_CELL_DATA = COMPONENTS.registerComponentType(
            "dimensional_cell_data",
            builder -> builder
                    .persistent(DimensionalCellData.CODEC)
                    .networkSynchronized(DimensionalCellData.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PowerCardData>> ITEM_POWERCARD_DATA = COMPONENTS.registerComponentType(
            "powercard_data",
            builder -> builder
                    .persistent(PowerCardData.CODEC)
                    .networkSynchronized(PowerCardData.STREAM_CODEC));


    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerMenuScreens();

    }

    public void registerMenuScreens() {
        GuiDimensionalCell.register();
    }

    @Override
    public void initConfig() {
        DimensionalCellConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(DIMENSIONAL_CELL)
                        .blockTags(java.util.List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('P', Items.PRISMARINE_SHARD)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "PFP", "ReR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_ADVANCED)
                        .blockTags(java.util.List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('K', DIMENSIONAL_CELL.block().get())
                                        .define('*', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "R*R", "*K*", "R*R"),
                Dob.blockBuilder(DIMENSIONAL_CELL_SIMPLE)
                        .blockTags(java.util.List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('q', Items.QUARTZ)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "qFq", "RdR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_CREATIVE)
                        .blockTags(java.util.List.of(net.minecraft.tags.BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get()),
                Dob.itemBuilder(POWERCELL_CARD)
                        .shaped(builder -> builder
                                        .define('g', Items.GOLD_NUGGET)
                                        .unlockedBy("paper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER, Items.REDSTONE)),
                                "rgr", "gpg", "rgr")
        );
    }
}
