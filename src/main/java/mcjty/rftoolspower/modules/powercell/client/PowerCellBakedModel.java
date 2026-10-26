package mcjty.rftoolspower.modules.powercell.client;

import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;



public class PowerCellBakedModel extends AbstractDynamicBakedModel {

    public PowerCellBakedModel() { inputMask = null; outputMask = null; }

    public static final Identifier TEXTURE_INPUTMASK = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/inputmask");
    public static final Identifier TEXTURE_OUTPUTMASK = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/outputmask");

    private VertexFormat format;

    private static TextureAtlasSprite inputMask;
    private static TextureAtlasSprite outputMask;

    private static TextureAtlasSprite getInputMask() {
        if (inputMask == null) {
            inputMask = getTexture(TEXTURE_INPUTMASK);
        }
        return inputMask;
    }

    private static TextureAtlasSprite getOutputMask() {
        if (outputMask == null) {
            outputMask = getTexture(TEXTURE_OUTPUTMASK);
        }
        return outputMask;
    }

    private static TextureAtlasSprite getSideTexture(boolean upper, boolean lower, int tier) {
        String s = "block/powercell/cell";
        if (upper && lower) {
            s += "middle_t";
        } else if (upper) {
            s += "lower_t";
        } else if (lower) {
            s += "upper_t";
        } else {
            s += "both_t";
        }
        s += tier;
        return getTexture(Identifier.fromNamespaceAndPath(RFToolsPower.MODID, s));
    }

    private static TextureAtlasSprite getTopTexture() {
        String name = "block/powercell/cellhoriz_t1";
        return getTexture(Identifier.fromNamespaceAndPath(RFToolsPower.MODID, name));
    }

    @Override
    public void collectParts(net.minecraft.client.renderer.block.BlockAndTintGetter world, net.minecraft.core.BlockPos pos,
            BlockState state, RandomSource random, List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts) {
        var entity = world.getBlockEntity(pos);
        PowerCellTileEntity cell = entity instanceof PowerCellTileEntity found ? found : null;
        parts.add(new mcjty.rftoolsbase.client.QuadModelPart(buildQuads(state, cell), particleMaterial()));
    }

    private List<BakedQuad> buildQuads(BlockState state, PowerCellTileEntity cell) {
        boolean upper = state.getValue(PowerCellBlock.UPPER);
        boolean lower = state.getValue(PowerCellBlock.LOWER);
        SideType north = cell == null ? SideType.NONE : cell.getMode(Direction.NORTH);
        SideType south = cell == null ? SideType.NONE : cell.getMode(Direction.SOUTH);
        SideType west = cell == null ? SideType.NONE : cell.getMode(Direction.WEST);
        SideType east = cell == null ? SideType.NONE : cell.getMode(Direction.EAST);
        SideType up = cell == null ? SideType.NONE : cell.getMode(Direction.UP);
        SideType down = cell == null ? SideType.NONE : cell.getMode(Direction.DOWN);
        Tier tier = cell == null ? Tier.TIER1 : cell.getTier();
        int t = tier.ordinal()+1;

        List<BakedQuad> quads = new ArrayList<>();

        float hilight = 1.0f;

        float o = .25f;

//        if (up != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), getTopTexture(), hilight));
            if (up.isInput()) {
                quads.add(createQuad(v(o, 1.02, o), v(o, 1.02, 1-o), v(1-o, 1.02, 1-o), v(1-o, 1.02, o), getInputMask(), hilight));
            }
            if (up.isOutput()) {
                quads.add(createQuad(v(o, 1.02, o), v(o, 1.02, 1-o), v(1-o, 1.02, 1-o), v(1-o, 1.02, o), getOutputMask(), hilight));
            }
//        }
//        if (down != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), getTopTexture(), hilight));
            if (down.isInput()) {
                quads.add(createQuad(v(o, -.02, o), v(1-o, -.02, o), v(1-o, -.02, 1-o), v(o, -.02, 1-o), getInputMask(), hilight));
            }
            if (down.isOutput()) {
                quads.add(createQuad(v(o, -.02, o), v(1-o, -.02, o), v(1-o, -.02, 1-o), v(o, -.02, 1-o), getOutputMask(), hilight));
            }
//        }
//        if (east != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 1), v(1, 0, 1), v(1, 0, 0), v(1, 1, 0), getSideTexture(upper, lower, t), hilight));
            if (east.isInput()) {
                quads.add(createQuad(v(1.02, 1-o, 1-o), v(1.02, o, 1-o), v(1.02, o, o), v(1.02, 1-o, o), getInputMask(), hilight));
            }
            if (east.isOutput()) {
                quads.add(createQuad(v(1.02, 1-o, 1-o), v(1.02, o, 1-o), v(1.02, o, o), v(1.02, 1-o, o), getOutputMask(), hilight));
            }
//        }
//        if (west != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0, 1, 1), getSideTexture(upper, lower, t), hilight));
            if (west.isInput()) {
                quads.add(createQuad(v(-.02, 1-o, o), v(-.02, o, o), v(-.02, o, 1-o), v(-.02, 1-o, 1-o), getInputMask(), hilight));
            }
            if (west.isOutput()) {
                quads.add(createQuad(v(-.02, 1-o, o), v(-.02, o, o), v(-.02, o, 1-o), v(-.02, 1-o, 1-o), getOutputMask(), hilight));
            }
//        }
//        if (north != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), v(0, 1, 0), getSideTexture(upper, lower, t), hilight));
            if (north.isInput()) {
                quads.add(createQuad(v(1-o, 1-o, -.02), v(1-o, o, -.02), v(o, o, -.02), v(o, 1-o, -.02), getInputMask(), hilight));
            }
            if (north.isOutput()) {
                quads.add(createQuad(v(1-o, 1-o, -.02), v(1-o, o, -.02), v(o, o, -.02), v(o, 1-o, -.02), getOutputMask(), hilight));
            }
//        }
//        if (south != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 1), v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), getSideTexture(upper, lower, t), hilight));
            if (south.isInput()) {
                quads.add(createQuad(v(o, 1-o, 1.02), v(o, o, 1.02), v(1-o, o, 1.02), v(1-o, 1-o, 1.02), getInputMask(), hilight));
            }
            if (south.isOutput()) {
                quads.add(createQuad(v(o, 1-o, 1.02), v(o, o, 1.02), v(1-o, o, 1.02), v(1-o, 1-o, 1.02), getOutputMask(), hilight));
            }
//        }

        return quads;
    }

    @Nonnull
    @Override
    public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial() {
        return new net.minecraft.client.resources.model.sprite.Material.Baked(getSideTexture(false, false, 1), false);
    }
}
