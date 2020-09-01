package mcjty.rftoolspower.modules.powercell;

import com.google.common.collect.Lists;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.client.PowerCellBakedModel;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import mcjty.rftoolspower.modules.powercell.items.PowerCoreItem;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolspower.setup.Registration.*;

public class PowerCellModule implements IModule {

    public static final RegistryObject<Block> CELL1 = BLOCKS.register("cell1", () -> new PowerCellBlock(Tier.TIER1));
    public static final RegistryObject<Item> CELL1_ITEM = ITEMS.register("cell1", () -> new BlockItem(CELL1.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CELL1 = TILES.register("cell1", () -> TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER1), CELL1.get()).build(null));

    public static final RegistryObject<Block> CELL2 = BLOCKS.register("cell2", () -> new PowerCellBlock(Tier.TIER2));
    public static final RegistryObject<Item> CELL2_ITEM = ITEMS.register("cell2", () -> new BlockItem(CELL2.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CELL2 = TILES.register("cell2", () -> TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER2), CELL2.get()).build(null));

    public static final RegistryObject<Block> CELL3 = BLOCKS.register("cell3", () -> new PowerCellBlock(Tier.TIER3));
    public static final RegistryObject<Item> CELL3_ITEM = ITEMS.register("cell3", () -> new BlockItem(CELL3.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_CELL3 = TILES.register("cell3", () -> TileEntityType.Builder.create(() -> new PowerCellTileEntity(Tier.TIER3), CELL3.get()).build(null));

    public static final RegistryObject<Item> POWER_CORE1 = ITEMS.register("power_core1", PowerCoreItem::new);
    public static final RegistryObject<Item> POWER_CORE2 = ITEMS.register("power_core2", PowerCoreItem::new);
    public static final RegistryObject<Item> POWER_CORE3 = ITEMS.register("power_core3", PowerCoreItem::new);

    public PowerCellModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(PowerCellModule::onModelBake);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(PowerCellModule::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        PowerCellConfig.setup(Config.SERVER_BUILDER);
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }

        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellhoriz_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/inputmask"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/outputmask"));
    }

    public static void onModelBake(ModelBakeEvent event) {
        PowerCellBakedModel model = new PowerCellBakedModel();
        Lists.newArrayList("cell1", "cell2", "cell3").stream()
                .forEach(name -> {
                    ResourceLocation rl = new ResourceLocation(RFToolsPower.MODID, name);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, ""), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=false,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=false,upper=true"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=true,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=true,upper=true"), model);
                });
    }
}
