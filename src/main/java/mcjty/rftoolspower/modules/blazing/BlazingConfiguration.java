package mcjty.rftoolspower.modules.blazing;

import net.minecraftforge.common.ForgeConfigSpec;

public class BlazingConfiguration {
    public static final String CATEGORY_BLAZING = "blazing";

    public static ForgeConfigSpec.IntValue GENERATOR_MAXENERGY;
    public static ForgeConfigSpec.IntValue AGITATOR_MAXENERGY;
    public static ForgeConfigSpec.IntValue AGITATOR_ENERGY_INPUT_PERTICK;
    public static ForgeConfigSpec.IntValue AGITATOR_USE_PER_TICK;

    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the blazing generator").push(CATEGORY_BLAZING);
        CLIENT_BUILDER.comment("Settings for the blazing generator").push(CATEGORY_BLAZING);

        GENERATOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the blazing generator can store")
                .defineInRange("blazingGeneratorMaxPower", 1000000, 0, Integer.MAX_VALUE);
        AGITATOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the blazing agitator can store")
                .defineInRange("blazingAgitatorMaxPower", 50000, 0, Integer.MAX_VALUE);
        AGITATOR_ENERGY_INPUT_PERTICK = SERVER_BUILDER
                .comment("Amount of RF per tick input (per side) for the agitator")
                .defineInRange("blazingAgitatorRFPerTick", 100, 0, Integer.MAX_VALUE);
        AGITATOR_USE_PER_TICK = SERVER_BUILDER
                .comment("Amount of RF per tick the agitator uses while operating")
                .defineInRange("blazingAgitatorUsePerTick", 10, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
