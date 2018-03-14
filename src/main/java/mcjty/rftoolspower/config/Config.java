package mcjty.rftoolspower.config;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static int TIER1_MAXRF = 500000;
    public static int TIER2_MAXRF = 4000000;
    public static int TIER3_MAXRF = 20000000;

    public static int TIER1_RFPERTICK = 500;
    public static int TIER2_RFPERTICK = 5000;
    public static int TIER3_RFPERTICK = 50000;

    public static int NETWORK_MAX = 9*9*9;

    public static void init(Configuration cfg) {
        TIER1_MAXRF = cfg.getInt("tier1MaxRF", CATEGORY_GENERAL, TIER1_MAXRF, 1, 2000000000, "Maximum RF a single tier1 cell can hold");
        TIER2_MAXRF = cfg.getInt("tier2MaxRF", CATEGORY_GENERAL, TIER2_MAXRF, 1, 2000000000, "Maximum RF a single tier2 cell can hold");
        TIER3_MAXRF = cfg.getInt("tier3MaxRF", CATEGORY_GENERAL, TIER3_MAXRF, 1, 2000000000, "Maximum RF a single tier3 cell can hold");

        TIER1_RFPERTICK = cfg.getInt("tier1MaxRFPerTick", CATEGORY_GENERAL, TIER1_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier1 cell");
        TIER2_RFPERTICK = cfg.getInt("tier2MaxRFPerTick", CATEGORY_GENERAL, TIER2_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier2 cell");
        TIER3_RFPERTICK = cfg.getInt("tier3MaxRFPerTick", CATEGORY_GENERAL, TIER3_RFPERTICK, 1, 2000000000, "Maximum RF/tick per side for a tier3 cell");

        NETWORK_MAX = cfg.getInt("networkMax", CATEGORY_GENERAL, NETWORK_MAX, 1, 2000000000, "Maximum number of blocks in a single multiblock network");

    }
}