package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalCellNormalTileEntity extends DimensionalCellTileEntity {

    public DimensionalCellNormalTileEntity(BlockPos pos, BlockState state) {
        super(DimensionalCellModule.DIMENSIONAL_CELL.be().get(), pos, state);
    }
}
