package mcjty.rftoolspower.proxy;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WrenchChecker;
import mcjty.rftoolspower.ForgeEventHandlers;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.config.Config;
import mcjty.rftoolspower.items.ModItems;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
//        CommandHandler.registerCommands();

        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "rftools", "rftoolspower.cfg"));

        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(RFToolsPower.MODID, "rftoolspower");
        RFToolsPowerMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();

        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }

    @Override
    public void createTabs() {
        createTab("RFToolsPower", new ItemStack(ModBlocks.cell1Block));
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(Config.CATEGORY_GENERAL, "General settings");
            Config.init(cfg);
        } catch (Exception e1) {
            Logging.getLogger().log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.theoneprobe.TheOneProbeSupport");
//        NetworkRegistry.INSTANCE.registerGuiHandler(RFTools.instance, new GuiProxy());
//        MinecraftForge.EVENT_BUS.register(WorldTickHandler.instance);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        mainConfig = null;
        WrenchChecker.init();
    }
}
