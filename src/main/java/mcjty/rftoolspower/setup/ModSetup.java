package mcjty.rftoolspower.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.config.ConfigSetup;
import mcjty.rftoolspower.items.ModItems;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("rftoolspower", () -> new ItemStack(Items.DIAMOND));
    }   // @todo

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsPowerMessages.registerMessages("rftoolspower");

        ModItems.init();
        ModBlocks.init();
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
//        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }

    protected void setupConfig() {
        ConfigSetup.init();
    }
}
