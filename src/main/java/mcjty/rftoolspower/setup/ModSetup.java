package mcjty.rftoolspower.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.config.ConfigSetup;
import mcjty.rftoolspower.items.ModItems;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModSetup extends DefaultModSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        RFToolsPowerMessages.registerMessages("rftoolspower");

        ModItems.init();
        ModBlocks.init();
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }

    @Override
    protected void setupConfig() {
        ConfigSetup.init();
    }

    @Override
    public void createTabs() {
        createTab("RFToolsPower", () -> new ItemStack(ModBlocks.cell1Block));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
    }
}
