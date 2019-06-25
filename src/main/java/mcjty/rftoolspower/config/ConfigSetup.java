package mcjty.rftoolspower.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import mcjty.lib.varia.Logging;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigSetup {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec.IntValue TIER1_MAXRF; // TODO make these longs
    public static ForgeConfigSpec.IntValue TIER2_MAXRF;
    public static ForgeConfigSpec.IntValue TIER3_MAXRF;

    public static ForgeConfigSpec.IntValue TIER1_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER2_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER3_RFPERTICK;

    public static ForgeConfigSpec.DoubleValue RFPERTICK_SCALE;

    public static ForgeConfigSpec.IntValue NETWORK_MAX;

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    static {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        TIER1_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier1 cell can hold")
                .defineInRange("tier1MaxRF", 500000, 1, 2000000000);
        TIER2_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier2 cell can hold")
                .defineInRange("tier2MaxRF", 4000000, 1, 2000000000);
        TIER3_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier3 cell can hold")
                .defineInRange("tier3MaxRF", 20000000, 1, 2000000000);

        TIER1_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier1 cell")
                .defineInRange("tier1MaxRFPerTick", 250, 1, 2000000000);
        TIER2_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier2 cell")
                .defineInRange("tier2MaxRFPerTick", 1000, 1, 2000000000);
        TIER3_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier3 cell")
                .defineInRange("tier3MaxRFPerTick", 4000, 1, 2000000000);

        RFPERTICK_SCALE = SERVER_BUILDER
                .comment("How much extra RF/tick every cell gets per cell in the network. 0 means constant RF/t. 1 means linear with amount of cells")
                .defineInRange("rfPerTickScale", .25f, 0.0f, 100.0f);

        NETWORK_MAX = SERVER_BUILDER
                .comment("Maximum number of blocks in a single multiblock network")
                .defineInRange("networkMax", 9*9*9, 1, 2000000000);


        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static ConfigSpec SERVER_CONFIG;
    public static ConfigSpec CLIENT_CONFIG;

//    public static Configuration mainConfig;

    public static void init() {
        mainConfig = new Configuration(new File(RFToolsPower.setup.getModConfigDir().getPath() + File.separator + "rftools", "rftoolspower.cfg"));
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            SERVER_CONFIG = SERVER_BUILDER.build(mainConfig);
            CLIENT_CONFIG = CLIENT_BUILDER.build(mainConfig);
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