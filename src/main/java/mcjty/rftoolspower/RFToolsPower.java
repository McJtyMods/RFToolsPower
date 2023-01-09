package mcjty.rftoolspower;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.ModSetup;
import mcjty.rftoolspower.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RFToolsPower.MODID)
public class RFToolsPower {

    public static final String MODID = "rftoolspower";

    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();
    private final Modules modules = new Modules();

    public static RFToolsPower instance;

    public RFToolsPower() {
        instance = this;
        setupModules();

        Config.register(modules);
        Registration.register();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(modules::initClient);
        });
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }


    private void setupModules() {
        modules.register(new BlazingModule());
        modules.register(new DimensionalCellModule());
        modules.register(new EndergenicModule());
        modules.register(new CoalGeneratorModule());
        modules.register(new MonitorModule());
        modules.register(new PowerCellModule());
    }
}
