package mcjty.rftoolspower.config;

import mcjty.lib.varia.Logging;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigSetup {
    public static final String CATEGORY_GENERAL = "general";

    public static int TIER1_MAXRF = 500000; // TODO make these longs
    public static int TIER2_MAXRF = 4000000;
    public static int TIER3_MAXRF = 20000000;

    public static int TIER1_RFPERTICK = 250;
    public static int TIER2_RFPERTICK = 1000;
    public static int TIER3_RFPERTICK = 4000;

    public static float RFPERTICK_SCALE = .25f;

    public static int NETWORK_MAX = 9*9*9;
    public static Configuration mainConfig;

    public static void init() {
        mainConfig = new Configuration(new File(RFToolsPower.setup.getModConfigDir().getPath() + File.separator + "rftools", "rftoolspower.cfg"));
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General settings");
            TIER1_MAXRF = cfg.getInt("tier1MaxRF", CATEGORY_GENERAL, TIER1_MAXRF, 1, 2000000000, "Maximum RF a single tier1 cell can hold");
            TIER2_MAXRF = cfg.getInt("tier2MaxRF", CATEGORY_GENERAL, TIER2_MAXRF, 1, 2000000000, "Maximum RF a single tier2 cell can hold");
            TIER3_MAXRF = cfg.getInt("tier3MaxRF", CATEGORY_GENERAL, TIER3_MAXRF, 1, 2000000000, "Maximum RF a single tier3 cell can hold");

            TIER1_RFPERTICK = cfg.getInt("tier1MaxRFPerTick", CATEGORY_GENERAL, TIER1_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier1 cell");
            TIER2_RFPERTICK = cfg.getInt("tier2MaxRFPerTick", CATEGORY_GENERAL, TIER2_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier2 cell");
            TIER3_RFPERTICK = cfg.getInt("tier3MaxRFPerTick", CATEGORY_GENERAL, TIER3_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier3 cell");

            RFPERTICK_SCALE = cfg.getFloat("rfPerTickScale", CATEGORY_GENERAL, RFPERTICK_SCALE, 0.0f, 100.0f, "How much extra RF/tick every cell gets per cell in the network. 0 means constant RF/t. 1 means linear with amount of cells");

            NETWORK_MAX = cfg.getInt("networkMax", CATEGORY_GENERAL, NETWORK_MAX, 1, 2000000000, "Maximum number of blocks in a single multiblock network");

        } catch (Exception e1) {
            Logging.getLogger().log(Level.ERROR, "Problem loading config file!", e1);
        }
    }

    public static void postInit() {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
    }
}