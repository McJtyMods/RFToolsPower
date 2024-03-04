package mcjty.rftoolspower.modules.generator;

import net.neoforged.neoforge.common.ModConfigSpec;

// ----- Coal generator ------
public class CoalGeneratorConfig {

    public static final String SUB_CATEGORY_COALGENERATOR = "coalgenerator";

    public static ModConfigSpec.IntValue MAXENERGY; // TODO change these to longs once Configuration supports them
    public static ModConfigSpec.IntValue SENDPERTICK;
    public static ModConfigSpec.IntValue CHARGEITEMPERTICK;
    public static ModConfigSpec.IntValue RFPERTICK;
    public static ModConfigSpec.IntValue TICKSPERCOAL;

    public static void setup(ModConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Coal generator settings").push(SUB_CATEGORY_COALGENERATOR);

        RFPERTICK = SERVER_BUILDER
                .comment("Amount of RF generated per tick")
                .defineInRange("generatePerTick", 60, 0, Integer.MAX_VALUE);
        TICKSPERCOAL = SERVER_BUILDER
                .comment("Amount of ticks generated per coal")
                .defineInRange("ticksPerCoal", 600, 0, Integer.MAX_VALUE);
        MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the generator can hold")
                .defineInRange("generatorMaxRF", 500000, 0, Integer.MAX_VALUE);
        SENDPERTICK = SERVER_BUILDER
                .comment("RF per tick that the generator can send")
                .defineInRange("generatorRFPerTick", 2000, 0, Integer.MAX_VALUE);
        CHARGEITEMPERTICK = SERVER_BUILDER
                .comment("RF per tick that the generator can charge items with")
                .defineInRange("generatorChargePerTick", 1000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

}
