package mcjty.rftoolspower.setup;


import com.google.common.collect.Lists;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.dimensionalcell.client.GuiDimensionalCell;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.modules.informationscreen.client.InformationScreenRenderer;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import mcjty.rftoolspower.modules.powercell.client.PowerCellBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RFToolsPower.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        InformationScreenRenderer.register();
        GenericGuiContainer.register(CoalGeneratorSetup.CONTAINER_COALGENERATOR, GuiCoalGenerator::new);
        GenericGuiContainer.register(DimensionalCellSetup.CONTAINER_DIMENSIONAL_CELL, GuiDimensionalCell::new);
        OBJLoader.INSTANCE.addDomain(RFToolsPower.MODID);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getBasePath().equals("textures")) {
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
        PowerCellBakedModel model = new PowerCellBakedModel(DefaultVertexFormats.BLOCK);
        Lists.newArrayList(PowerCellSetup.CELL1, PowerCellSetup.CELL2, PowerCellSetup.CELL3).stream()
                .forEach(block -> {
                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), ""), model);
                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=false,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=false,upper=true"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=true,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "lower=true,upper=true"), model);
                });
    }
}
