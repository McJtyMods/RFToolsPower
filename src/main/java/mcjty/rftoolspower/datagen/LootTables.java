package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.informationscreen.InformationScreenSetup;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(CoalGeneratorSetup.COALGENERATOR.get(), createStandardTable("coalgenerator", CoalGeneratorSetup.COALGENERATOR.get()));
        lootTables.put(PowerCellSetup.CELL1.get(), createStandardTable("cell1", PowerCellSetup.CELL1.get()));
        lootTables.put(PowerCellSetup.CELL2.get(), createStandardTable("cell2", PowerCellSetup.CELL2.get()));
        lootTables.put(PowerCellSetup.CELL3.get(), createStandardTable("cell3", PowerCellSetup.CELL3.get()));
        lootTables.put(InformationScreenSetup.INFORMATION_SCREEN.get(), createSimpleTable("informationscreen", InformationScreenSetup.INFORMATION_SCREEN.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL.get(), createStandardTable("dimensionalcell", DimensionalCellSetup.DIMENSIONAL_CELL.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get(), createStandardTable("dimensionalcell_advanced", DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get(), createStandardTable("dimensionalcell_creative", DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get()));
        lootTables.put(DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get(), createStandardTable("dimensionalcell_simple", DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get()));
    }

    @Override
    public String getName() {
        return "RFToolsPower LootTables";
    }
}
