package mcjty.rftoolspower;

import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.varia.Logging;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
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
    public static final String VERSION = "0.1.0";
    public static final String MIN_FORGE_VER = "14.22.0.2464";
    public static final String MIN_MCJTYLIB_VER = "2.6.6";

    @SidedProxy(clientSide = "mcjty.rftoolspower.proxy.ClientProxy", serverSide = "mcjty.rftoolspower.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("rftoolspower")
    public static RFToolsPower instance;

    public static boolean redstoneflux = false;

    /**
     * This is used to keep track of GUIs that we make
     */
    private static int modGuiIndex = 0;

    @Override
    public String getModId() {
        return MODID;
    }

    public static CreativeTabs tabRfToolsPower = new CreativeTabs("RFToolsPower") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.cell1Block);
        }
    };

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
        proxy.preInit(e);
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);

        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.theoneprobe.TheOneProbeSupport");

        if (Loader.isModLoaded("redstoneflux")) {
            redstoneflux = true;
            Logging.log("RFTools Detected RedstoneFlux: enabling support");
        }
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
//        TeleportDestinations.clearInstance();
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
//        GuiRFToolsManual.locatePage = page;
//        player.openGui(RFTools.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }
}
