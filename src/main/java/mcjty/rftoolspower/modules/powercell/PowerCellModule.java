package mcjty.rftoolspower.modules.powercell;

import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.client.ClientSetup;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import mcjty.rftoolspower.modules.powercell.items.PowerCoreItem;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class PowerCellModule implements IModule {

    public static final RegistryObject<Block> CELL1 = BLOCKS.register("cell1", () -> new PowerCellBlock(Tier.TIER1));
    public static final RegistryObject<Item> CELL1_ITEM = ITEMS.register("cell1", () -> new BlockItem(CELL1.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CELL1 = TILES.register("cell1", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER1, pos, state), CELL1.get()).build(null));

    public static final RegistryObject<Block> CELL2 = BLOCKS.register("cell2", () -> new PowerCellBlock(Tier.TIER2));
    public static final RegistryObject<Item> CELL2_ITEM = ITEMS.register("cell2", () -> new BlockItem(CELL2.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CELL2 = TILES.register("cell2", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER2, pos, state), CELL2.get()).build(null));

    public static final RegistryObject<Block> CELL3 = BLOCKS.register("cell3", () -> new PowerCellBlock(Tier.TIER3));
    public static final RegistryObject<Item> CELL3_ITEM = ITEMS.register("cell3", () -> new BlockItem(CELL3.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_CELL3 = TILES.register("cell3", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER3, pos, state), CELL3.get()).build(null));

    public static final RegistryObject<Item> POWER_CORE1 = ITEMS.register("power_core1", PowerCoreItem::new);
    public static final RegistryObject<Item> POWER_CORE2 = ITEMS.register("power_core2", PowerCoreItem::new);
    public static final RegistryObject<Item> POWER_CORE3 = ITEMS.register("power_core3", PowerCoreItem::new);

    public PowerCellModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModelBake);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) { }

    @Override
    public void initConfig() {
        PowerCellConfig.setup(Config.SERVER_BUILDER);
    }

}
