package mcjty.rftoolspower.modules.informationscreen;

import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenBlock;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static mcjty.rftoolspower.RFToolsPower.MODID;

public class InformationScreenSetup {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> INFORMATION_SCREEN = BLOCKS.register("information_screen", InformationScreenBlock::new);
    public static final RegistryObject<Item> INFORMATION_SCREEN_ITEM = ITEMS.register("information_screen", () -> new BlockItem(INFORMATION_SCREEN.get(), RFToolsPower.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_INFORMATION_SCREEN = TILES.register("information_screen", () -> TileEntityType.Builder.create(InformationScreenTileEntity::new, INFORMATION_SCREEN.get()).build(null));
}
