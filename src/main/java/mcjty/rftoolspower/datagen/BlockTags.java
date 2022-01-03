package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseBlockTagsProvider;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class BlockTags extends BaseBlockTagsProvider {

    public BlockTags(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, RFToolsPower.MODID, helper);
    }

    @Override
    protected void addTags() {
        ironPickaxe(
                BlazingModule.BLAZING_AGITATOR, BlazingModule.BLAZING_GENERATOR, BlazingModule.BLAZING_INFUSER,
                DimensionalCellModule.DIMENSIONAL_CELL, DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED, DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE,
                DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE,
                EndergenicModule.ENDERGENIC, EndergenicModule.PEARL_INJECTOR, EndergenicModule.ENDER_MONITOR,
                CoalGeneratorModule.COALGENERATOR,
                MonitorModule.POWER_MONITOR, MonitorModule.POWER_LEVEL,
                PowerCellModule.CELL1, PowerCellModule.CELL2, PowerCellModule.CELL3
        );
    }

    @Override
    @Nonnull
    public String getName() {
        return "RFToolsPower Tags";
    }
}
