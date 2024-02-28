package mcjty.rftoolspower.modules.powercell;

import net.neoforged.neoforge.common.ForgeConfigSpec;

// ----- Powercells ------
public class PowerCellConfig {

    public static final String SUB_CATEGORY_POWERCELL = "powercell";

    public static ForgeConfigSpec.LongValue TIER1_MAXRF;
    public static ForgeConfigSpec.LongValue TIER2_MAXRF;
    public static ForgeConfigSpec.LongValue TIER3_MAXRF;
    public static ForgeConfigSpec.IntValue TIER1_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER2_RFPERTICK;
    public static ForgeConfigSpec.IntValue TIER3_RFPERTICK;
    public static ForgeConfigSpec.DoubleValue RFPERTICK_SCALE;
    public static ForgeConfigSpec.IntValue NETWORK_MAX;

    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Powercell settings").push(SUB_CATEGORY_POWERCELL);

        PowerCellConfig.TIER1_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier1 cell can hold")
                .defineInRange("tier1MaxRF", 500000L, 1, 2000000000);
        PowerCellConfig.TIER2_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier2 cell can hold")
                .defineInRange("tier2MaxRF", 4000000L, 1, 2000000000);
        PowerCellConfig.TIER3_MAXRF = SERVER_BUILDER
                .comment("Maximum RF a single tier3 cell can hold")
                .defineInRange("tier3MaxRF", 20000000L, 1, 2000000000);

        PowerCellConfig.TIER1_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier1 cell")
                .defineInRange("tier1MaxRFPerTick", 250, 1, 2000000000);
        PowerCellConfig.TIER2_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier2 cell")
                .defineInRange("tier2MaxRFPerTick", 1000, 1, 2000000000);
        PowerCellConfig.TIER3_RFPERTICK = SERVER_BUILDER
                .comment("Maximum RF/tick per side for a tier3 cell")
                .defineInRange("tier3MaxRFPerTick", 4000, 1, 2000000000);

        PowerCellConfig.RFPERTICK_SCALE = SERVER_BUILDER
                .comment("How much extra RF/tick every cell gets per cell in the network. 0 means constant RF/t. 1 means linear with amount of cells")
                .defineInRange("rfPerTickScale", .25f, 0.0f, 100.0f);

        PowerCellConfig.NETWORK_MAX = SERVER_BUILDER
                .comment("Maximum number of blocks in a single multiblock network")
                .defineInRange("networkMax", 9*9*9, 1, 2000000000);

        SERVER_BUILDER.pop();
    }

}
