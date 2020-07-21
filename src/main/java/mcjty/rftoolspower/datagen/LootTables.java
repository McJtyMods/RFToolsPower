package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.endergenic.EndergenicSetup;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.monitor.MonitorSetup;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(MonitorSetup.POWER_MONITOR.get(), createStandardTable("power_monitor", MonitorSetup.POWER_MONITOR.get()));
        lootTables.put(MonitorSetup.POWER_LEVEL.get(), createSimpleTable("power_level", MonitorSetup.POWER_LEVEL.get()));
        lootTables.put(CoalGeneratorSetup.COALGENERATOR.get(), createStandardTable("coalgenerator", CoalGeneratorSetup.COALGENERATOR.get()));
        lootTables.put(PowerCellSetup.CELL1.get(), createStandardTable("cell1", PowerCellSetup.CELL1.get()));
        lootTables.put(PowerCellSetup.CELL2.get(), createStandardTable("cell2", PowerCellSetup.CELL2.get()));
        lootTables.put(PowerCellSetup.CELL3.get(), createStandardTable("cell3", PowerCellSetup.CELL3.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL.get(), createStandardTable("dimensionalcell", DimensionalCellSetup.DIMENSIONAL_CELL.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get(), createStandardTable("dimensionalcell_advanced", DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get(), createStandardTable("dimensionalcell_creative", DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get(), createStandardTable("dimensionalcell_simple", DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get()));
        lootTables.put(EndergenicSetup.ENDERGENIC.get(), createStandardTable("endergenic", EndergenicSetup.ENDERGENIC.get()));
        lootTables.put(EndergenicSetup.PEARL_INJECTOR.get(), createStandardTable("pearl_injector", EndergenicSetup.PEARL_INJECTOR.get()));
        lootTables.put(EndergenicSetup.ENDER_MONITOR.get(), createStandardTable("ender_monitor", EndergenicSetup.ENDER_MONITOR.get()));
        lootTables.put(BlazingSetup.BLAZING_GENERATOR.get(), createStandardTable("blazing_generator", BlazingSetup.BLAZING_GENERATOR.get()));
        lootTables.put(BlazingSetup.BLAZING_AGITATOR.get(), createStandardTable("blazing_agitator", BlazingSetup.BLAZING_AGITATOR.get()));
        lootTables.put(BlazingSetup.BLAZING_INFUSER.get(), createStandardTable("blazing_infuser", BlazingSetup.BLAZING_INFUSER.get()));
    }

    @Override
    public String getName() {
        return "RFToolsPower LootTables";
    }
}
