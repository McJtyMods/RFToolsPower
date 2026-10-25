package mcjty.rftoolspower.setup;

public class ClientSetup implements net.fabricmc.api.ClientModInitializer {
    @Override public void onInitializeClient() {
        mcjty.rftoolspower.RFToolsPower.instance.modules.initClient();
        mcjty.rftoolspower.modules.dimensionalcell.client.LinkedPowerCardProperty.register();
    }
}
