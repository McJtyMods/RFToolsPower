package mcjty.rftoolspower.modules.dimensionalcell;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import mcjty.lib.datagen.model.BlockModelBuilder;
import com.google.gson.*;
import mcjty.lib.datagen.model.ConfiguredModel;
import net.minecraft.data.*;
import net.minecraft.resources.Identifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DataGenHelper {

    public static void createDimensionalCellModel(Provider provider, Block block, String suffix) {

        BlockModelBuilder dimCellFrame = provider.models().getBuilder("block/dimcell/main");
        mcjty.rftoolsbase.tools.ModelDataGen.renderType(dimCellFrame, "translucent");
        provider.createFrame(dimCellFrame, "#window", 1f);
        provider.innerCube(dimCellFrame, "#window", 1f, 1f, 1f, 15f, 15f, 15f);
        dimCellFrame.texture("particle", "#window");
        dimCellFrame.texture("window", provider.modLoc("block/dimcell/dimensionalcellwindows"));

        BlockModelBuilder singleNone = provider.models().getBuilder("block/dimcell/singlenone" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("particle", "#single")
                .texture("single", provider.modLoc("block/dimcell/dimensionalcell" + suffix));
        BlockModelBuilder singleIn = provider.models().getBuilder("block/dimcell/singlein" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("particle", "#single")
                .texture("single", provider.modLoc("block/dimcell/dimensionalcellin" + suffix));
        BlockModelBuilder singleOut = provider.models().getBuilder("block/dimcell/singleout" + suffix)
                .element().from(3, 3, 3).to(13, 13, 13).face(Direction.DOWN).texture("#single").end().end()
                .texture("particle", "#single")
                .texture("single", provider.modLoc("block/dimcell/dimensionalcellout" + suffix));

        var parts = new JsonArray();
        var frame = new JsonObject();
        frame.add("apply", ConfiguredModel.builder().modelFile(dimCellFrame).build()[0].toJson());
        parts.add(frame);
        BlockModelBuilder[] models = { singleNone, singleIn, singleOut };
        var directions = Direction.values();
        for (var direction : directions) {
            var property = switch (direction) {
                case DOWN -> DimensionalCellBlock.DOWN; case UP -> DimensionalCellBlock.UP;
                case NORTH -> DimensionalCellBlock.NORTH; case SOUTH -> DimensionalCellBlock.SOUTH;
                case WEST -> DimensionalCellBlock.WEST; case EAST -> DimensionalCellBlock.EAST;
            };
            for (var mode : DimensionalCellTileEntity.Mode.values()) {
                var part = new JsonObject();
                var when = new JsonObject();
                when.addProperty(property.getName(), property.getName(mode));
                part.add("when", when);
                int x = direction == Direction.UP ? 180 : direction == Direction.DOWN ? 0 : direction == Direction.NORTH ? 270 : 90;
                int y = direction == Direction.WEST ? 90 : direction == Direction.EAST ? 270 : 0;
                part.add("apply", ConfiguredModel.builder().modelFile(models[mode.ordinal()]).rotationX(x).rotationY(y).build()[0].toJson());
                parts.add(part);
            }
        }
        var json = new JsonObject();
        json.add("multipart", parts);
        provider.multipart.put(block, json);
    }

    /** Own provider because the published library exposes variant builders only. */
    public static class Provider extends BaseBlockStateProvider {
        private final Map<Block, JsonObject> multipart = new LinkedHashMap<>();
        public Provider(net.fabricmc.fabric.api.datagen.v1.FabricPackOutput output) { super(output, "rftoolspower"); }
        @Override protected void registerStatesAndModels() {
            multipart.clear();
            createDimensionalCellModel(this, DimensionalCellModule.DIMENSIONAL_CELL.block().get(), "");
            createDimensionalCellModel(this, DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.block().get(), "advanced");
            createDimensionalCellModel(this, DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.block().get(), "simple");
            createDimensionalCellModel(this, DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.block().get(), "creative");
        }
        @Override public String getName() { return "RFToolsPower dimensional cell models"; }
        @Override public CompletableFuture<?> run(CachedOutput cache) {
            var generatedModels = super.run(cache);
            var paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
            var tasks = new ArrayList<CompletableFuture<?>>();
            tasks.add(generatedModels);
            multipart.forEach((block, json) -> tasks.add(DataProvider.saveStable(cache, json, paths.json(mcjty.lib.varia.Tools.getId(block)))));
            return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
        }
    }
}
