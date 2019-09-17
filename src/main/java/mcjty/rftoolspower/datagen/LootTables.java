package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(ModBlocks.COALGENERATOR, createStandardTable("coalgenerator", ModBlocks.COALGENERATOR));
        lootTables.put(ModBlocks.CELL1, createStandardTable("cell1", ModBlocks.CELL1));
        lootTables.put(ModBlocks.CELL2, createStandardTable("cell2", ModBlocks.CELL2));
        lootTables.put(ModBlocks.CELL3, createStandardTable("cell3", ModBlocks.CELL3));
        lootTables.put(ModBlocks.INFORMATION_SCREEN, createSimpleTable("informationscreen", ModBlocks.INFORMATION_SCREEN));
    }

    @Override
    public String getName() {
        return "RFToolsPower LootTables";
    }
}
