package mcjty.rftoolspower;

import mcjty.lib.base.ModBase;
import mcjty.lib.proxy.IProxy;
import mcjty.rftoolspower.setup.ModSetup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = RFToolsPower.MODID, name = "RFToolsPower",
        dependencies =
                        "required-after:mcjtylib_ng@[" + RFToolsPower.MIN_MCJTYLIB_VER + ",);" +
                        "after:forge@[" + RFToolsPower.MIN_FORGE_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = RFToolsPower.VERSION)
public class RFToolsPower implements ModBase {
    public static final String MODID = "rftoolspower";
    public static final String VERSION = "1.1.2";
    public static final String MIN_FORGE_VER = "14.22.0.2464";
    public static final String MIN_MCJTYLIB_VER = "3.1.0";

    @SidedProxy(clientSide = "mcjty.rftoolspower.setup.ClientProxy", serverSide = "mcjty.rftoolspower.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance("rftoolspower")
    public static RFToolsPower instance;

    @Override
    public String getModId() {
        return MODID;
    }

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    /**
     * Set our custom inventory Gui index to the next available Gui index
     */
//    public static final int GUI_MANUAL_MAIN = modGuiIndex++;

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        setup.preInit(e);
        proxy.preInit(e);
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }


    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
//        TeleportDestinations.clearInstance();
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
//        GuiRFToolsManual.locatePage = page;
//        player.openGui(RFTools.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }
}
