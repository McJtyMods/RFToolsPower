package mcjty.rftoolspower.setup;


import com.google.common.collect.Lists;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.dimensionalcell.client.GuiDimensionalCell;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.modules.powercell.client.PowerCellBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RFToolsPower.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        GenericGuiContainer.register(CoalGeneratorSetup.CONTAINER_COALGENERATOR.get(), GuiCoalGenerator::new);
        GenericGuiContainer.register(DimensionalCellSetup.CONTAINER_DIMENSIONAL_CELL.get(), GuiDimensionalCell::new);

        RenderTypeLookup.setRenderLayer(DimensionalCellSetup.DIMENSIONAL_CELL.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get(), RenderType.getTranslucent());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }

        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellhoriz_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/celllower_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/celllower_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/celllower_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/inputmask"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/outputmask"));
    }

    @SubscribeEvent
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
