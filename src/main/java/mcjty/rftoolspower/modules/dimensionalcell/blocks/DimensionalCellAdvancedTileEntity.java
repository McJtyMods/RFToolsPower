package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalCellAdvancedTileEntity extends DimensionalCellTileEntity {

    public DimensionalCellAdvancedTileEntity(BlockPos pos, BlockState state) {
        super(DimensionalCellModule.TYPE_DIMENSIONAL_CELL_ADVANCED.get(), pos, state);
    }
}
