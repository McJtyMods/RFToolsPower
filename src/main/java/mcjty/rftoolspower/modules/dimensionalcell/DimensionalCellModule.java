package mcjty.rftoolspower.modules.dimensionalcell;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Supplier;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class DimensionalCellModule implements IModule {

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellNormalTileEntity> DIMENSIONAL_CELL = RBLOCKS.registerBlock("dimensionalcell",
            DimensionalCellNormalTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.NORMAL, DimensionalCellNormalTileEntity::new, properties),
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellNormalTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellAdvancedTileEntity> DIMENSIONAL_CELL_ADVANCED = RBLOCKS.registerBlock("dimensionalcell_advanced",
            DimensionalCellAdvancedTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.ADVANCED, DimensionalCellAdvancedTileEntity::new, properties),
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellAdvancedTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellSimpleTileEntity> DIMENSIONAL_CELL_SIMPLE = RBLOCKS.registerBlock("dimensionalcell_simple",
            DimensionalCellSimpleTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new, properties),
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellSimpleTileEntity::new);

    public static final RBlock<DimensionalCellBlock, BlockItem, DimensionalCellCreativeTileEntity> DIMENSIONAL_CELL_CREATIVE = RBLOCKS.registerBlock("dimensionalcell_creative",
            DimensionalCellCreativeTileEntity.class,
            properties -> new DimensionalCellBlock(DimensionalCellType.CREATIVE, DimensionalCellCreativeTileEntity::new, properties),
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            DimensionalCellCreativeTileEntity::new);

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_DIMENSIONAL_CELL = CONTAINERS.register("dimensionalcell", GenericContainer::createContainerType);

    public static final DeferredItem<PowerCellCardItem> POWERCELL_CARD = ITEMS.register("powercell_card", tab(PowerCellCardItem::new));

    public static final Supplier<AttachmentType<DimensionalCellData>> DIMENSIONAL_CELL_DATA = ATTACHMENT_TYPES.register(
            "dimensional_cell_data", () -> AttachmentType.builder(() -> new DimensionalCellData(0, 0, 0, -1))
                    .serialize(MapCodec.assumeMapUnsafe(DimensionalCellData.CODEC))
                    .build());
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

    public DimensionalCellModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiDimensionalCell.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {
        DimensionalCellConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(DIMENSIONAL_CELL)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('P', Items.PRISMARINE_SHARD)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "PFP", "ReR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_ADVANCED)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('K', DIMENSIONAL_CELL.block().get())
                                        .define('*', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "R*R", "*K*", "R*R"),
                Dob.blockBuilder(DIMENSIONAL_CELL_SIMPLE)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get())
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('q', Items.QUARTZ)
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                                "RdR", "qFq", "RdR"),
                Dob.blockBuilder(DIMENSIONAL_CELL_CREATIVE)
                        .blockTags(List.of(BlockTags.BLOCKS_MOTION_NO_LEAVES))
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), DimensionalCellModule.ITEM_DIMENSIONAL_CELL_DATA.get()),
                Dob.itemBuilder(POWERCELL_CARD)
                        .shaped(builder -> builder
                                        .define('g', Items.GOLD_NUGGET)
                                        .unlockedBy("paper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PAPER, Items.REDSTONE)),
                                "rgr", "gpg", "rgr")
        );
    }
}
