package mcjty.rftoolspower;

import mcjty.lib.modules.Modules;
import mcjty.rftoolsbase.api.infoscreen.CapabilityInformationScreenInfo;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.ModSetup;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.world.item.Item;
import java.util.function.Supplier;

public class RFToolsPower implements net.fabricmc.api.ModInitializer {
    public static final String MODID = "rftoolspower";
    public static final ModSetup setup = new ModSetup();
    public static RFToolsPower instance;
    public final Modules modules = new Modules();
    @Override
    public void onInitialize() {
        instance = this;
        modules.register(new BlazingModule());
        modules.register(new DimensionalCellModule());
        modules.register(new EndergenicModule());
        modules.register(new CoalGeneratorModule());
        modules.register(new MonitorModule());
        modules.register(new PowerCellModule());
        Config.register(modules);
        Registration.register();
        setup.init();
        modules.init();
        setup.registerBlockCapabilities(Registration.RBLOCKS);
        registerCapabilities();
    }
    public static <T extends Item> java.util.function.Function<Item.Properties, T> tab(java.util.function.Function<Item.Properties, T> factory) {
        return properties -> instance.setup.tab(() -> factory.apply(properties)).get();
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void registerCapabilities() {
        CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof EndergenicTileEntity te) return te.getInfoHandler();
            if (be instanceof DimensionalCellTileEntity te) return te.getInfoHandler();
            return null;
        }, EndergenicModule.ENDERGENIC.block().get(), DimensionalCellModule.DIMENSIONAL_CELL.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.block().get());
        CapabilityInformationScreenInfo.INFORMATION_SCREEN_INFO_CAPABILITY.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof PowerCellTileEntity te) return te.getInfoScreenInfo();
            if (be instanceof DimensionalCellTileEntity te) return te.getInfoScreenInfo();
            return null;
        }, PowerCellModule.CELL1.block().get(), PowerCellModule.CELL2.block().get(), PowerCellModule.CELL3.block().get(), DimensionalCellModule.DIMENSIONAL_CELL.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.block().get());
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof PowerCellTileEntity te) return te.getEnergyHandler(side);
            if (be instanceof DimensionalCellTileEntity te) return te.getEnergyHandler(side);
            return null;
        }, PowerCellModule.CELL1.block().get(), PowerCellModule.CELL2.block().get(), PowerCellModule.CELL3.block().get(), DimensionalCellModule.DIMENSIONAL_CELL.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.block().get(), DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.block().get());
    }
}
