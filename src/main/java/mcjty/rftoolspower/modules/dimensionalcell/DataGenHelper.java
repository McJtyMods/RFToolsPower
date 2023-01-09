package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

public class DataGenHelper {

    public static void createDimensionalCellModel(BaseBlockStateProvider provider, Block block, String suffix) {

        BlockModelBuilder dimCellFrame = provider.models().getBuilder("block/dimcell/main");
        dimCellFrame.renderType("translucent");
        provider.createFrame(dimCellFrame, "#window", 1f);
        provider.innerCube(dimCellFrame, "#window", 1f, 1f, 1f, 15f, 15f, 15f);
        dimCellFrame.texture("window", provider.modLoc("block/dimcell/dimensionalcellwindows"));

        BlockModelBuilder singleNone = provider.models().getBuilder("block/dimcell/singlenone" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", provider.modLoc("block/dimcell/dimensionalcell" + suffix))
                .renderType("translucent");
        BlockModelBuilder singleIn = provider.models().getBuilder("block/dimcell/singlein" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", provider.modLoc("block/dimcell/dimensionalcellin" + suffix))
                .renderType("translucent");
        BlockModelBuilder singleOut = provider.models().getBuilder("block/dimcell/singleout" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", provider.modLoc("block/dimcell/dimensionalcellout" + suffix))
                .renderType("translucent");

        MultiPartBlockStateBuilder bld = provider.getMultipartBuilder(block);

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
