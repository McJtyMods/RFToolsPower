package mcjty.rftoolspower.setup;

import mcjty.lib.setup.DefaultClientProxy;
import mcjty.rftoolspower.blocks.BakedModelLoader;
import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());

        ModBlocks.initClient();
    }
}
