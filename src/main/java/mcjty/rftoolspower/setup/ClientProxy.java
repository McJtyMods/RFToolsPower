package mcjty.rftoolspower.setup;

import mcjty.lib.setup.DefaultClientProxy;
import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        ModBlocks.initClient();
    }
}
