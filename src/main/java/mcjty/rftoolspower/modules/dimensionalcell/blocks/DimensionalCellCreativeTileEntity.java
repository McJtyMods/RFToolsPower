package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalCellCreativeTileEntity extends DimensionalCellTileEntity {

    public DimensionalCellCreativeTileEntity(BlockPos pos, BlockState state) {
        super(DimensionalCellModule.TYPE_DIMENSIONAL_CELL_CREATIVE.get(), pos, state);
    }
}
