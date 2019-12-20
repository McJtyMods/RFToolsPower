package mcjty.rftoolspower.setup;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.generator.CoalGeneratorConfig;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    static {
        setupGeneralConfig();

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralConfig() {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        PowerCellConfig.setup(COMMON_BUILDER);
        CoalGeneratorConfig.setup(COMMON_BUILDER);
        DimensionalCellConfiguration.setup(COMMON_BUILDER, CLIENT_BUILDER);

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