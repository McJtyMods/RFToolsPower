package mcjty.rftoolspower.modules.dimensionalcell;

import net.minecraftforge.common.ForgeConfigSpec;

public class DimensionalCellConfiguration {

    public static final String CATEGORY_DIMENSIONALCELL = "dimensionalcell";
    public static ForgeConfigSpec.IntValue rfPerNormalCell;
    public static ForgeConfigSpec.IntValue advancedFactor;
    public static ForgeConfigSpec.IntValue simpleFactor;
    public static ForgeConfigSpec.IntValue rfPerTick;

    public static ForgeConfigSpec.DoubleValue powerCellCostFactor;
    public static ForgeConfigSpec.DoubleValue powerCellDistanceCap;
    public static ForgeConfigSpec.DoubleValue powerCellMinDistance;
    public static ForgeConfigSpec.DoubleValue powerCellRFToolsDimensionAdvantage;

    public static ForgeConfigSpec.IntValue CHARGEITEMPERTICK;

    public static void setup(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Settings for the powercell").push(CATEGORY_DIMENSIONALCELL);
        CLIENT_BUILDER.comment("Settings for the powercell").push(CATEGORY_DIMENSIONALCELL);

        rfPerTick = COMMON_BUILDER
                .comment("Base amount of RF/tick that can be extracted/inserted in this block")
                .defineInRange("rfPerTick", 5000, 0, Integer.MAX_VALUE);
        rfPerNormalCell = COMMON_BUILDER
                .comment("Maximum RF storage that a single cell can hold")
                .defineInRange("rfPerNormalCell", 1000000, 0, Integer.MAX_VALUE);

        advancedFactor = COMMON_BUILDER
                .comment("How much better is the advanced cell with RF and RF/t")
                .defineInRange("advancedFactor", 4, 0, Integer.MAX_VALUE);
        simpleFactor = COMMON_BUILDER
                .comment("How much worse is the simple cell with RF and RF/t")
                .defineInRange("simpleFactor", 4, 0, Integer.MAX_VALUE);

        powerCellCostFactor = COMMON_BUILDER
                .comment("The maximum cost factor for extracting energy out of a powercell for blocks in other dimensions or farther away then 10000 blocks")
                .defineInRange("powerCellCostFactor", 1.10, 0, 1000000000.0);
        powerCellDistanceCap = COMMON_BUILDER
                .comment("At this distance the cost factor will be maximum. This value is also used when power is extracted from cells in different dimensions")
                .defineInRange("powerCellDistanceCap", 10000.0, 0, 1000000000.0);
        powerCellMinDistance = COMMON_BUILDER
                .comment("As soon as powercells are not connected this value will be taken as the minimum distance to base the cost factor from")
                .defineInRange("powerCellMinDistance", 100.0, 0, 1000000000.0);
        powerCellRFToolsDimensionAdvantage = COMMON_BUILDER
                .comment("A multiplier for the distance if RFTools dimensions are involved. If both sides are RFTools dimensions then this multiplier is done twice")
                .defineInRange("powerCellRFToolsDimensionAdvantage", 0.5, 0, 1000000000.0);

        CHARGEITEMPERTICK = COMMON_BUILDER
                .comment("RF per tick that the powrcell can charge items with")
                .defineInRange("powercellChargePerTick", 30000, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
