package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalCellSimpleTileEntity extends DimensionalCellTileEntity {

    public DimensionalCellSimpleTileEntity(BlockPos pos, BlockState state) {
        super(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.be().get(), pos, state);
    }
}
