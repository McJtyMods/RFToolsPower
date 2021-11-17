package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.data.DataGenerator;

import javax.annotation.Nonnull;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(MonitorModule.POWER_MONITOR.get(), createStandardTable("power_monitor", MonitorModule.POWER_MONITOR.get()));
        lootTables.put(MonitorModule.POWER_LEVEL.get(), createSimpleTable("power_level", MonitorModule.POWER_LEVEL.get()));
        lootTables.put(CoalGeneratorModule.COALGENERATOR.get(), createStandardTable("coalgenerator", CoalGeneratorModule.COALGENERATOR.get()));
        lootTables.put(PowerCellModule.CELL1.get(), createStandardTable("cell1", PowerCellModule.CELL1.get()));
        lootTables.put(PowerCellModule.CELL2.get(), createStandardTable("cell2", PowerCellModule.CELL2.get()));
        lootTables.put(PowerCellModule.CELL3.get(), createStandardTable("cell3", PowerCellModule.CELL3.get()));
        lootTables.put(DimensionalCellModule.DIMENSIONAL_CELL.get(), createStandardTable("dimensionalcell", DimensionalCellModule.DIMENSIONAL_CELL.get()));
        lootTables.put(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get(), createStandardTable("dimensionalcell_advanced", DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get()));
        lootTables.put(DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.get(), createStandardTable("dimensionalcell_creative", DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.get()));
        lootTables.put(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get(), createStandardTable("dimensionalcell_simple", DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get()));
        lootTables.put(EndergenicModule.ENDERGENIC.get(), createStandardTable("endergenic", EndergenicModule.ENDERGENIC.get()));
        lootTables.put(EndergenicModule.PEARL_INJECTOR.get(), createStandardTable("pearl_injector", EndergenicModule.PEARL_INJECTOR.get()));
        lootTables.put(EndergenicModule.ENDER_MONITOR.get(), createStandardTable("ender_monitor", EndergenicModule.ENDER_MONITOR.get()));
        lootTables.put(BlazingModule.BLAZING_GENERATOR.get(), createStandardTable("blazing_generator", BlazingModule.BLAZING_GENERATOR.get()));
        lootTables.put(BlazingModule.BLAZING_AGITATOR.get(), createStandardTable("blazing_agitator", BlazingModule.BLAZING_AGITATOR.get()));
        lootTables.put(BlazingModule.BLAZING_INFUSER.get(), createStandardTable("blazing_infuser", BlazingModule.BLAZING_INFUSER.get()));
    }

    @Nonnull
    @Override
    public String getName() {
        return "RFToolsPower LootTables";
    }
}
