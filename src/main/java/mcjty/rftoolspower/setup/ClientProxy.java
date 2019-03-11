package mcjty.rftoolspower.setup;

import mcjty.lib.setup.DefaultClientProxy;
import mcjty.rftoolspower.blocks.BakedModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
    }
}
