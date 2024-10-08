package mcjty.rftoolspower;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import mcjty.rftoolsbase.api.infoscreen.CapabilityInformationScreenInfo;
import mcjty.rftoolsbase.api.infoscreen.IInformationScreenInfo;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.ModSetup;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

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
        bus.addListener(setup.getBlockCapabilityRegistrar(Registration.RBLOCKS));
        bus.addListener(this::onRegisterCapabilities);

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

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY, new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable IMachineInformation getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction) {
                if (be instanceof EndergenicTileEntity te) {
                    return te.getInfoHandler();
                }
                return null;
            }
        });
        event.registerBlock(CapabilityInformationScreenInfo.INFORMATION_SCREEN_INFO_CAPABILITY, new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable IInformationScreenInfo getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction) {
                if (be instanceof PowerCellTileEntity te) {
                    return te.getInfoScreenInfo();
                }
                return null;
            }
        });
        event.registerBlock(Capabilities.EnergyStorage.BLOCK, new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable IEnergyStorage getCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity be, Direction direction) {
                if (be instanceof PowerCellTileEntity te) {
                    return te.getEnergyStorage(direction);
                }
                return null;
            }
        });
    }
}
