package mcjty.rftoolspower.proxy;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.lib.varia.WrenchChecker;
import mcjty.rftoolspower.ForgeEventHandlers;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.config.ConfigSetup;
import mcjty.rftoolspower.items.ModItems;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        setupModCompat();

        RFToolsPowerMessages.registerMessages("rftoolspower");

        ConfigSetup.init();
        ModItems.init();
        ModBlocks.init();
    }

    private void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.theoneprobe.TheOneProbeSupport");
    }

    @Override
    public void createTabs() {
        createTab("RFToolsPower", new ItemStack(ModBlocks.cell1Block));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
        WrenchChecker.init();
    }
}
