package mcjty.rftoolspower.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;


    public static ForgeConfigSpec.LongValue TIER1_MAXRF;
    public static ForgeConfigSpec.LongValue TIER2_MAXRF;
    public static ForgeConfigSpec.LongValue TIER3_MAXRF;

    public static ForgeConfigSpec.IntValue TIER1_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER2_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER3_RFPERTICK;

    public static ForgeConfigSpec.DoubleValue RFPERTICK_SCALE;

    public static ForgeConfigSpec.IntValue NETWORK_MAX;

    static {
        setupGeneralConfig();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralConfig() {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        TIER1_MAXRF = COMMON_BUILDER
                .comment("Maximum RF a single tier1 cell can hold")
                .defineInRange("tier1MaxRF", 500000L, 1, 2000000000);
        TIER2_MAXRF = COMMON_BUILDER
                .comment("Maximum RF a single tier2 cell can hold")
                .defineInRange("tier2MaxRF", 4000000L, 1, 2000000000);
        TIER3_MAXRF = COMMON_BUILDER
                .comment("Maximum RF a single tier3 cell can hold")
                .defineInRange("tier3MaxRF", 20000000L, 1, 2000000000);

        TIER1_RFPERTICK = COMMON_BUILDER
                .comment("Maximum RF/tick per side for a tier1 cell")
                .defineInRange("tier1MaxRFPerTick", 250, 1, 2000000000);
        TIER2_RFPERTICK = COMMON_BUILDER
                .comment("Maximum RF/tick per side for a tier2 cell")
                .defineInRange("tier2MaxRFPerTick", 1000, 1, 2000000000);
        TIER3_RFPERTICK = COMMON_BUILDER
                .comment("Maximum RF/tick per side for a tier3 cell")
                .defineInRange("tier3MaxRFPerTick", 4000, 1, 2000000000);

        RFPERTICK_SCALE = COMMON_BUILDER
                .comment("How much extra RF/tick every cell gets per cell in the network. 0 means constant RF/t. 1 means linear with amount of cells")
                .defineInRange("rfPerTickScale", .25f, 0.0f, 100.0f);

        NETWORK_MAX = COMMON_BUILDER
                .comment("Maximum number of blocks in a single multiblock network")
                .defineInRange("networkMax", 9*9*9, 1, 2000000000);


        COMMON_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}