package mcjty.rftoolspower.setup;


import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.BakedModelLoader;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsPower.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
//        ModBlocks.initClient();
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

//    @SubscribeEvent
//    public static void onModelBake(ModelBakeEvent event) {
//        event.getModelRegistry().put(new ModelResourceLocation(ModBlocks.CELL1.getRegistryName(), ""), new GenericCellBakedModel(DefaultVertexFormats.POSITION_TEX_COLOR));
//    }
}
