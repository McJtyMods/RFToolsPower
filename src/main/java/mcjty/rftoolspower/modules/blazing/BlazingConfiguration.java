package mcjty.rftoolspower.modules.blazing;

import net.neoforged.neoforge.common.ForgeConfigSpec;

public class BlazingConfiguration {
    public static final String CATEGORY_BLAZING = "blazing";

    public static ForgeConfigSpec.IntValue GENERATOR_MAXENERGY;
    public static ForgeConfigSpec.IntValue GENERATOR_SENDPERTICK;

    public static ForgeConfigSpec.IntValue AGITATOR_MAXENERGY;
    public static ForgeConfigSpec.IntValue AGITATOR_ENERGY_INPUT_PERTICK;
    public static ForgeConfigSpec.IntValue AGITATOR_USE_PER_TICK;

    public static ForgeConfigSpec.IntValue INFUSER_MAXENERGY;
    public static ForgeConfigSpec.IntValue INFUSER_ENERGY_INPUT_PERTICK;
    public static ForgeConfigSpec.IntValue INFUSER_USE_PER_TICK;

    public static void setup(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the blazing generator").push(CATEGORY_BLAZING);
        CLIENT_BUILDER.comment("Settings for the blazing generator").push(CATEGORY_BLAZING);

        GENERATOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the blazing generator can store")
                .defineInRange("blazingGeneratorMaxPower", 1000000, 0, Integer.MAX_VALUE);
        GENERATOR_SENDPERTICK = SERVER_BUILDER
                .comment("Maximum amount of power the blazing generator give to adjacent machines per side and per tick")
                .defineInRange("blazingGeneratorSendPerTick", 20000, 0, Integer.MAX_VALUE);
        AGITATOR_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the blazing agitator can store")
                .defineInRange("blazingAgitatorMaxPower", 50000, 0, Integer.MAX_VALUE);
        AGITATOR_ENERGY_INPUT_PERTICK = SERVER_BUILDER
                .comment("Amount of RF per tick input (per side) for the agitator")
                .defineInRange("blazingAgitatorRFPerTick", 100, 0, Integer.MAX_VALUE);
        AGITATOR_USE_PER_TICK = SERVER_BUILDER
                .comment("Amount of RF per tick the agitator uses while operating")
                .defineInRange("blazingAgitatorUsePerTick", 10, 0, Integer.MAX_VALUE);
        INFUSER_MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the blazing infuser can store")
                .defineInRange("blazingInfuserMaxPower", 50000, 0, Integer.MAX_VALUE);
        INFUSER_ENERGY_INPUT_PERTICK = SERVER_BUILDER
                .comment("Amount of RF per tick input (per side) for the infuser")
                .defineInRange("blazingInfuserRFPerTick", 100, 0, Integer.MAX_VALUE);
        INFUSER_USE_PER_TICK = SERVER_BUILDER
                .comment("Amount of RF per tick the infuser uses while operating")
                .defineInRange("blazingInfuserUsePerTick", 10, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
