package mcjty.rftoolspower.setup;

import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import mcjty.rftoolspower.blocks.BakedModelLoader;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.items.ModItems;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
//        OBJLoader.INSTANCE.addDomain(RFTools.MODID);
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        McJtyLibClient.preInit(e);
//        ClientCommandHandler.registerCommands();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
//        ModBlocks.initClientPost();
//        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
//        KeyBindings.init();
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModItems.initClient();
        ModBlocks.initClient();
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
//        ModBlocks.initItemModels();
    }
}
