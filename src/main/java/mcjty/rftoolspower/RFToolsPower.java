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
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

@Mod(RFToolsPower.MODID)
public class RFToolsPower {

    public static final String MODID = "rftoolspower";

    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();
    private final Modules modules = new Modules();

    public static RFToolsPower instance;

    public RFToolsPower(ModContainer mod, IEventBus bus, Dist dist) {
        instance = this;
        setupModules(bus, dist);

        Config.register(mod, bus, modules);
        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);
        // @todo 1.21 add capability registrar

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getLookupProvider());
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new BlazingModule(bus));
        modules.register(new DimensionalCellModule(bus));
        modules.register(new EndergenicModule(bus, dist));
        modules.register(new CoalGeneratorModule(bus));
        modules.register(new MonitorModule(bus, dist));
        modules.register(new PowerCellModule(bus, dist));
    }
}
