package mcjty.rftoolspower.datagen;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsPower.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BlockModelBuilder dimCellFrame = models().getBuilder("block/dimcell/main");
        dimCellFrame.renderType("translucent");
        createFrame(dimCellFrame, "#window", 1f);
        innerCube(dimCellFrame, "#window", 1f, 1f, 1f, 15f, 15f, 15f);
        dimCellFrame.texture("window", modLoc("block/dimcell/dimensionalcellwindows"));

        createDimensionalCellModel(DimensionalCellModule.DIMENSIONAL_CELL.get(), "", dimCellFrame);
        createDimensionalCellModel(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get(), "simple", dimCellFrame);
        createDimensionalCellModel(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get(), "advanced", dimCellFrame);
        createDimensionalCellModel(DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.get(), "creative", dimCellFrame);

        ModelFile[][] models = new ModelFile[][] {
                getLogicSlabModels("power_monitor0", modLoc("block/monitor/power_monitor0")),
                getLogicSlabModels("power_monitor1", modLoc("block/monitor/power_monitor1")),
                getLogicSlabModels("power_monitor2", modLoc("block/monitor/power_monitor2")),
                getLogicSlabModels("power_monitor3", modLoc("block/monitor/power_monitor3")),
                getLogicSlabModels("power_monitor4", modLoc("block/monitor/power_monitor4")),
                getLogicSlabModels("power_monitor5", modLoc("block/monitor/power_monitor5"))
        };
        variantBlock(MonitorModule.POWER_MONITOR.get(),
                state -> models[state.getValue(PowerMonitorTileEntity.LEVEL)][state.getValue(LogicSlabBlock.LOGIC_FACING).getRotationStep()],
                state -> getXRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()),
                state -> getYRotation(state.getValue(LogicSlabBlock.LOGIC_FACING).getSide()));

        logicSlabBlock(MonitorModule.POWER_LEVEL.get(), "power_level", modLoc("block/monitor/power_level"));
        logicSlabBlock(EndergenicModule.ENDER_MONITOR.get(), "ender_monitor", modLoc("block/endergenic/ender_monitor"));

        orientedBlock(EndergenicModule.PEARL_INJECTOR.get(), frontBasedModel("pearl_injector", modLoc("block/endergenic/pearl_injector")));
        singleTextureBlockC(EndergenicModule.ENDERGENIC.get(), "endergenic", ModelProvider.BLOCK_FOLDER + "/endergenic/endergenic", builder -> builder.renderType("translucent"));

        registerBlazingGenerator();
        simpleBlock(BlazingModule.BLAZING_AGITATOR.get(), models().slab("blazing_agitator",
                modLoc("block/blazing/blazing_agitator_side"),
                new ResourceLocation("rftoolsbase", "block/base/machinebottom"),
                new ResourceLocation("rftoolsbase", "block/base/machinetop")));
        orientedBlock(BlazingModule.BLAZING_INFUSER.get(), frontBasedModel("blazing_infuser", modLoc("block/blazing/blazing_infuser")));
    }

    private void registerBlazingGenerator() {
        ModelFile modelIdle = frontBasedModel("blazing_generator_idle", modLoc("block/blazing/blazing_generator_idle"));
        ModelFile modelBusy = frontBasedModel("blazing_generator_busy", modLoc("block/blazing/blazing_generator_busy"));
        VariantBlockStateBuilder builder = getVariantBuilder(BlazingModule.BLAZING_GENERATOR.get());
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(BlazingGeneratorTileEntity.WORKING, false)
                    .modelForState().modelFile(modelIdle), direction);
            applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(BlazingGeneratorTileEntity.WORKING, true)
                    .modelForState().modelFile(modelBusy), direction);
        }
    }

    private void createDimensionalCellModel(Block block, String suffix, BlockModelBuilder dimCellFrame) {
        BlockModelBuilder singleNone = models().getBuilder("block/dimcell/singlenone" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimcell/dimensionalcell" + suffix))
                .renderType("translucent");
        BlockModelBuilder singleIn = models().getBuilder("block/dimcell/singlein" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimcell/dimensionalcellin" + suffix))
                .renderType("translucent");
        BlockModelBuilder singleOut = models().getBuilder("block/dimcell/singleout" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("single", modLoc("block/dimcell/dimensionalcellout" + suffix))
                .renderType("translucent");

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
