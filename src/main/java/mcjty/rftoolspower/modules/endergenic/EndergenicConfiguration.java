package mcjty.rftoolspower.modules.endergenic;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EndergenicConfiguration {
    public static final String CATEGORY_ENDERGENIC = "endergenic";

    public static ModConfigSpec.IntValue MAXENERGY;

    // This value indicates the chance (with 0 being no chance and 1000 being 100% chance) that an
    // endergenic pearl is lost while holding it.
    public static ModConfigSpec.IntValue chanceLost;
    // This value indicates how much RF is being consumed every tick to try to keep the endergenic pearl.
    public static ModConfigSpec.IntValue rfToHoldPearl;
    // This value indicates how much RF will be kept in the internal buffer (not given to conduits and machines next to it) as
    // a reserve to be able to hold pearls.
    public static ModConfigSpec.IntValue ENDERGENIC_KEEPRF;
    // This value indicates how much RF/tick this block can send out to neighbours
    public static ModConfigSpec.IntValue ENDERGENIC_SENDPERTICK;
    public static ModConfigSpec.IntValue goodParticleCount;
    public static ModConfigSpec.IntValue badParticleCount;

    public static ModConfigSpec.DoubleValue powergenFactor;

    public static void setup(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the endergenic generator").push(CATEGORY_ENDERGENIC);
        CLIENT_BUILDER.comment("Settings for the endergenic generator").push(CATEGORY_ENDERGENIC);

        MAXENERGY = SERVER_BUILDER
                .comment("Maximum amount of power the endergenic can store")
                .defineInRange("endergenicMaxPower", 5000000, 0, Integer.MAX_VALUE);
        chanceLost = SERVER_BUILDER
                .comment("The chance (in 1/10 percent, so 1000 = 100%) that an endergenic pearl is lost while trying to hold it")
                .defineInRange("endergenicChanceLost", 5, 0, 1000);
        rfToHoldPearl = SERVER_BUILDER
                .comment("The amount of RF that is consumed every tick to hold the endergenic pearl")
                .defineInRange("endergenicRfHolding", 500, 0, Integer.MAX_VALUE);
        ENDERGENIC_KEEPRF = SERVER_BUILDER
                .comment("The amount of RF that every endergenic will keep itself (so that it can hold pearls)")
                .defineInRange("endergenicKeepRf", 2000, 0, Integer.MAX_VALUE);
        ENDERGENIC_SENDPERTICK = SERVER_BUILDER
                .comment("The amount of RF per tick that this generator can give from its internal buffer to adjacent blocks")
                .defineInRange("endergenicSendPerTick", 20000, 0, Integer.MAX_VALUE);
        goodParticleCount = SERVER_BUILDER
                .comment("The amount of particles to spawn whenever energy is generated (use 0 to disable)")
                .defineInRange("endergenicGoodParticles", 10, 0, 1000);
        badParticleCount = SERVER_BUILDER
                .comment("The amount of particles to spawn whenever a pearl is lost (use 0 to disable)")
                .defineInRange("endergenicBadParticles", 10, 0, 1000);
        powergenFactor = SERVER_BUILDER
                .comment("Multiplier for power generation")
                .defineInRange("powergenFactor", 2.0, 0, 1000000000.0);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
