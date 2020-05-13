package mcjty.rftoolspower;

import mcjty.lib.base.ModBase;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.ModSetup;
import mcjty.rftoolspower.setup.Registration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsPower.MODID)
public class RFToolsPower implements ModBase {

    public static final String MODID = "rftoolspower";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public static RFToolsPower instance;

    public RFToolsPower() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));
    }

    @Override
    public String getModId() {
        return MODID;
    }
}
