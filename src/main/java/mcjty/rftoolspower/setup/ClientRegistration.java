package mcjty.rftoolspower.setup;


import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initClient();
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

//    @SubscribeEvent
//    public static void onModelBake(ModelBakeEvent event) {
//        event.getModelRegistry().put(new ModelResourceLocation(ModBlocks.CELL1.getRegistryName(), ""), new GenericCellBakedModel());
//    }
}
