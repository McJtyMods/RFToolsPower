package mcjty.rftoolspower;

import mcjty.lib.base.ModBase;
import mcjty.lib.setup.IProxy;
import mcjty.rftoolspower.config.Config;
import mcjty.rftoolspower.setup.ClientProxy;
import mcjty.rftoolspower.setup.ModSetup;
import mcjty.rftoolspower.setup.ServerProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(RFToolsPower.MODID)
public class RFToolsPower implements ModBase {
    public static final String MODID = "rftoolspower";

    @SuppressWarnings("PublicField")
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsPower instance;

    public RFToolsPower() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("rftoolspower-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("rftoolspower-common.toml"));
    }

    public void init(final FMLCommonSetupEvent event) {
        setup.init(event);
        proxy.init(event);
    }


    @Override
    public String getModId() {
        return MODID;
    }

    public static final String SHIFT_MESSAGE = "<Press Shift>";


    @Override
    public void openManual(PlayerEntity player, int bookIndex, String page) {
    }
}
