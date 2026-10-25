package mcjty.rftoolspower.setup;

import mcjty.lib.modules.Modules;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static void register(Modules modules) {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        modules.initConfig();

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

        fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry.INSTANCE.register("rftoolspower", ModConfig.Type.CLIENT, CLIENT_CONFIG);
        fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry.INSTANCE.register("rftoolspower", ModConfig.Type.SERVER, SERVER_CONFIG);
    }
}
