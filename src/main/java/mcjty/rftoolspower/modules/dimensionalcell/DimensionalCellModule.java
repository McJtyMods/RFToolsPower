package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.*;
import mcjty.rftoolspower.modules.dimensionalcell.client.GuiDimensionalCell;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class DimensionalCellModule implements IModule {

    public static final RegistryObject<DimensionalCellBlock> DIMENSIONAL_CELL = BLOCKS.register("dimensionalcell", () -> new DimensionalCellBlock(DimensionalCellType.NORMAL, DimensionalCellNormalTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_ITEM = ITEMS.register("dimensionalcell", () -> new BlockItem(DIMENSIONAL_CELL.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL = TILES.register("dimensionalcell", () -> BlockEntityType.Builder.of(DimensionalCellNormalTileEntity::new, DIMENSIONAL_CELL.get()).build(null));

    public static final RegistryObject<DimensionalCellBlock> DIMENSIONAL_CELL_ADVANCED = BLOCKS.register("dimensionalcell_advanced", () -> new DimensionalCellBlock(DimensionalCellType.ADVANCED, DimensionalCellAdvancedTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_ADVANCED_ITEM = ITEMS.register("dimensionalcell_advanced", () -> new BlockItem(DIMENSIONAL_CELL_ADVANCED.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_ADVANCED = TILES.register("dimensionalcell_advanced", () -> BlockEntityType.Builder.of(DimensionalCellAdvancedTileEntity::new, DIMENSIONAL_CELL_ADVANCED.get()).build(null));

    public static final RegistryObject<DimensionalCellBlock> DIMENSIONAL_CELL_SIMPLE = BLOCKS.register("dimensionalcell_simple", () -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_SIMPLE_ITEM = ITEMS.register("dimensionalcell_simple", () -> new BlockItem(DIMENSIONAL_CELL_SIMPLE.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_SIMPLE = TILES.register("dimensionalcell_simple", () -> BlockEntityType.Builder.of(DimensionalCellSimpleTileEntity::new, DIMENSIONAL_CELL_SIMPLE.get()).build(null));

    public static final RegistryObject<DimensionalCellBlock> DIMENSIONAL_CELL_CREATIVE = BLOCKS.register("dimensionalcell_creative", () -> new DimensionalCellBlock(DimensionalCellType.CREATIVE, DimensionalCellCreativeTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_CREATIVE_ITEM = ITEMS.register("dimensionalcell_creative", () -> new BlockItem(DIMENSIONAL_CELL_CREATIVE.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_DIMENSIONAL_CELL_CREATIVE = TILES.register("dimensionalcell_creative", () -> BlockEntityType.Builder.of(DimensionalCellCreativeTileEntity::new, DIMENSIONAL_CELL_CREATIVE.get()).build(null));

    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_DIMENSIONAL_CELL = CONTAINERS.register("dimensionalcell", GenericContainer::createContainerType);

    public static final RegistryObject<PowerCellCardItem> POWERCELL_CARD = ITEMS.register("powercell_card", PowerCellCardItem::new);

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
    public void initConfig() {
        DimensionalCellConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
