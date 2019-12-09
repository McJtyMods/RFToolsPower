package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.*;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static mcjty.rftoolspower.RFToolsPower.MODID;

public class DimensionalCellSetup {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> DIMENSIONAL_CELL = BLOCKS.register("dimensionalcell", () -> new DimensionalCellBlock(DimensionalCellType.NORMAL, DimensionalCellNormalTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_ITEM = ITEMS.register("dimensionalcell", () -> new BlockItem(DIMENSIONAL_CELL.get(), RFToolsPower.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_DIMENSIONAL_CELL = TILES.register("dimensionalcell", () -> TileEntityType.Builder.create(DimensionalCellNormalTileEntity::new, DIMENSIONAL_CELL.get()).build(null));

    public static final RegistryObject<Block> DIMENSIONAL_CELL_ADVANCED = BLOCKS.register("dimensionalcell_advanced", () -> new DimensionalCellBlock(DimensionalCellType.ADVANCED, DimensionalCellAdvancedTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_ADVANCED_ITEM = ITEMS.register("dimensionalcell_advanced", () -> new BlockItem(DIMENSIONAL_CELL_ADVANCED.get(), RFToolsPower.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_DIMENSIONAL_CELL_ADVANCED = TILES.register("dimensionalcell_advanced", () -> TileEntityType.Builder.create(DimensionalCellAdvancedTileEntity::new, DIMENSIONAL_CELL_ADVANCED.get()).build(null));

    public static final RegistryObject<Block> DIMENSIONAL_CELL_SIMPLE = BLOCKS.register("dimensionalcell_simple", () -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_SIMPLE_ITEM = ITEMS.register("dimensionalcell_simple", () -> new BlockItem(DIMENSIONAL_CELL_SIMPLE.get(), RFToolsPower.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_DIMENSIONAL_CELL_SIMPLE = TILES.register("dimensionalcell_simple", () -> TileEntityType.Builder.create(DimensionalCellSimpleTileEntity::new, DIMENSIONAL_CELL_SIMPLE.get()).build(null));

    public static final RegistryObject<Block> DIMENSIONAL_CELL_CREATIVE = BLOCKS.register("dimensionalcell_creative", () -> new DimensionalCellBlock(DimensionalCellType.SIMPLE, DimensionalCellSimpleTileEntity::new));
    public static final RegistryObject<Item> DIMENSIONAL_CELL_CREATIVE_ITEM = ITEMS.register("dimensionalcell_creative", () -> new BlockItem(DIMENSIONAL_CELL_CREATIVE.get(), RFToolsPower.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_DIMENSIONAL_CELL_CREATIVE = TILES.register("dimensionalcell_creative", () -> TileEntityType.Builder.create(DimensionalCellCreativeTileEntity::new, DIMENSIONAL_CELL_CREATIVE.get()).build(null));

    public static final RegistryObject<ContainerType<DimensionalCellContainer>> CONTAINER_DIMENSIONAL_CELL = CONTAINERS.register("dimensionalcell", GenericContainer::createContainerType);

    public static final RegistryObject<PowerCellCardItem> POWERCELL_CARD = ITEMS.register("powercell_card", PowerCellCardItem::new);
}
