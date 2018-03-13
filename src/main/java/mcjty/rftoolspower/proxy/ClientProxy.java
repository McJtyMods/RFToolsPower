package mcjty.rftoolspower.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.lib.McJtyLibClient;
import mcjty.rftools.blocks.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Callable;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
//        OBJLoader.INSTANCE.addDomain(RFTools.MODID);
//        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
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
//        ModItems.initClient();
        ModBlocks.initClient();
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> sounds) {
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(callableToSchedule);
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(runnableToSchedule);
    }
}
