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
        lootTables.put(CoalGeneratorSetup.COALGENERATOR, createStandardTable("coalgenerator", CoalGeneratorSetup.COALGENERATOR));
        lootTables.put(PowerCellSetup.CELL1, createStandardTable("cell1", PowerCellSetup.CELL1));
        lootTables.put(PowerCellSetup.CELL2, createStandardTable("cell2", PowerCellSetup.CELL2));
        lootTables.put(PowerCellSetup.CELL3, createStandardTable("cell3", PowerCellSetup.CELL3));
        lootTables.put(InformationScreenSetup.INFORMATION_SCREEN, createSimpleTable("informationscreen", InformationScreenSetup.INFORMATION_SCREEN));
        lootTables.put(DimensionalCellSetup.dimensionalCellBlock, createSimpleTable("dimensionalcell", DimensionalCellSetup.dimensionalCellBlock));
        lootTables.put(DimensionalCellSetup.advancedDimensionalCellBlock, createSimpleTable("dimensionalcell_advanced", DimensionalCellSetup.advancedDimensionalCellBlock));
        lootTables.put(DimensionalCellSetup.creativeDimensionalCellBlock, createSimpleTable("dimensionalcell_creative", DimensionalCellSetup.creativeDimensionalCellBlock));
        lootTables.put(DimensionalCellSetup.simpleDimensionalCellBlock, createSimpleTable("dimensionalcell_simple", DimensionalCellSetup.simpleDimensionalCellBlock));
    }

    @Override
    public String getName() {
        return "RFToolsPower LootTables";
    }
}
