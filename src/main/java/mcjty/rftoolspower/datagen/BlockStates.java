package mcjty.rftoolspower.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsPower.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockModelBuilder dimCellFrame = models().getBuilder("block/dimcell/main");
        createFrame(dimCellFrame, "#window", 1f);
        innerCube(dimCellFrame, "#window", 1f, 1f, 1f, 15f, 15f, 15f);
        dimCellFrame.texture("window", modLoc("block/dimensionalcellwindows"));

        createDimensionalCellModel(DimensionalCellSetup.DIMENSIONAL_CELL.get(), "", dimCellFrame);
        createDimensionalCellModel(DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get(), "simple", dimCellFrame);
        createDimensionalCellModel(DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get(), "advanced", dimCellFrame);
        createDimensionalCellModel(DimensionalCellSetup.DIMENSIONAL_CELL_CREATIVE.get(), "creative", dimCellFrame);
    }

    private void createDimensionalCellModel(Block block, String suffix, BlockModelBuilder dimCellFrame) {
        BlockModelBuilder singleNone = models().getBuilder("block/dimcell/singlenone" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimensionalcell" + suffix));
        BlockModelBuilder singleIn = models().getBuilder("block/dimcell/singlein" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimensionalcellin" + suffix));
        BlockModelBuilder singleOut = models().getBuilder("block/dimcell/singleout" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimensionalcellout" + suffix));

        MultiPartBlockStateBuilder bld = getMultipartBuilder(block);

        bld.part().modelFile(dimCellFrame).addModel();

        BlockModelBuilder[] models = new BlockModelBuilder[] { singleNone, singleIn, singleOut };
        for (DimensionalCellTileEntity.Mode mode : DimensionalCellTileEntity.Mode.values()) {
            bld.part().modelFile(models[mode.ordinal()]).addModel().condition(DimensionalCellBlock.DOWN, mode);
            bld.part().modelFile(models[mode.ordinal()]).rotationX(180).addModel().condition(DimensionalCellBlock.UP, mode);
            bld.part().modelFile(models[mode.ordinal()]).rotationX(90).addModel().condition(DimensionalCellBlock.SOUTH, mode);
            bld.part().modelFile(models[mode.ordinal()]).rotationX(270).addModel().condition(DimensionalCellBlock.NORTH, mode);
            bld.part().modelFile(models[mode.ordinal()]).rotationY(90).rotationX(90).addModel().condition(DimensionalCellBlock.WEST, mode);
            bld.part().modelFile(models[mode.ordinal()]).rotationY(270).rotationX(90).addModel().condition(DimensionalCellBlock.EAST, mode);
        }
    }
}
