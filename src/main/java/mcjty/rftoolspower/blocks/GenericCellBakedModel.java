package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;


public class GenericCellBakedModel implements IBakedModel {

    public static final ModelResourceLocation modelCell = new ModelResourceLocation(RFToolsPower.MODID + ":" + "cell1");

    private static Map<String, TextureAtlasSprite> sideSpriteMap = new HashMap<>();
    private static Map<String, TextureAtlasSprite> topSpriteMap = new HashMap<>();

    private VertexFormat format;

    private static TextureAtlasSprite inputMask;
    private static TextureAtlasSprite outputMask;

    private static TextureAtlasSprite getInputMask() {
        if (inputMask == null) {
            inputMask = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RFToolsPower.MODID + ":blocks/inputmask");
        }
        return inputMask;
    }

    private static TextureAtlasSprite getOutputMask() {
        if (outputMask == null) {
            outputMask = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RFToolsPower.MODID + ":blocks/outputmask");
        }
        return outputMask;
    }

    private static TextureAtlasSprite getSideTexture(SideType type, int tier) {
        String key = type.getName() + tier;
        if (!sideSpriteMap.containsKey(key)) {
            sideSpriteMap.put(key, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(type.getSideTexture()+tier));
        }
        return sideSpriteMap.get(key);
    }

    private static TextureAtlasSprite getTopTexture(SideType type, int tier) {
        String key = type.getName();
        if (!topSpriteMap.containsKey(key)) {
            topSpriteMap.put(key, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(type.getUpDownTexture()));
        }
        return topSpriteMap.get(key);
    }

    public GenericCellBakedModel(VertexFormat format) {
        this.format = format;
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal,
                           double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float color) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {

                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, color, color, color, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, float hilight) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite, hilight);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16, sprite, hilight);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16, sprite, hilight);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0, sprite, hilight);
        return builder.build();
    }

    private static Vec3d v(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (state == null || side != null) {
            return Collections.emptyList();
        }

        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        SideType north = extendedBlockState.getValue(PowerCellBlock.NORTH);
        SideType south = extendedBlockState.getValue(PowerCellBlock.SOUTH);
        SideType west = extendedBlockState.getValue(PowerCellBlock.WEST);
        SideType east = extendedBlockState.getValue(PowerCellBlock.EAST);
        SideType up = extendedBlockState.getValue(PowerCellBlock.UP);
        SideType down = extendedBlockState.getValue(PowerCellBlock.DOWN);
        Tier tier = extendedBlockState.getValue(PowerCellBlock.TIER);
        int t = tier.ordinal()+1;

        List<BakedQuad> quads = new ArrayList<>();

        float hilight = 1.0f;

        float o = .25f;

        if (up != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), getTopTexture(up, t), hilight));
            if (up.isInput()) {
                quads.add(createQuad(v(o, 1.02, o), v(o, 1.02, 1-o), v(1-o, 1.02, 1-o), v(1-o, 1.02, o), getInputMask(), hilight));
            }
            if (up.isOutput()) {
                quads.add(createQuad(v(o, 1.02, o), v(o, 1.02, 1-o), v(1-o, 1.02, 1-o), v(1-o, 1.02, o), getOutputMask(), hilight));
            }
        }
        if (down != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), getTopTexture(down, t), hilight));
            if (down.isInput()) {
                quads.add(createQuad(v(o, -.02, o), v(1-o, -.02, o), v(1-o, -.02, 1-o), v(o, -.02, 1-o), getInputMask(), hilight));
            }
            if (down.isOutput()) {
                quads.add(createQuad(v(o, -.02, o), v(1-o, -.02, o), v(1-o, -.02, 1-o), v(o, -.02, 1-o), getOutputMask(), hilight));
            }
        }
        if (east != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 1), v(1, 0, 1), v(1, 0, 0), v(1, 1, 0), getSideTexture(east, t), hilight));
            if (east.isInput()) {
                quads.add(createQuad(v(1.02, 1-o, 1-o), v(1.02, o, 1-o), v(1.02, o, o), v(1.02, 1-o, o), getInputMask(), hilight));
            }
            if (east.isOutput()) {
                quads.add(createQuad(v(1.02, 1-o, 1-o), v(1.02, o, 1-o), v(1.02, o, o), v(1.02, 1-o, o), getOutputMask(), hilight));
            }
        }
        if (west != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0, 1, 1), getSideTexture(west, t), hilight));
            if (west.isInput()) {
                quads.add(createQuad(v(-.02, 1-o, o), v(-.02, o, o), v(-.02, o, 1-o), v(-.02, 1-o, 1-o), getInputMask(), hilight));
            }
            if (west.isOutput()) {
                quads.add(createQuad(v(-.02, 1-o, o), v(-.02, o, o), v(-.02, o, 1-o), v(-.02, 1-o, 1-o), getOutputMask(), hilight));
            }
        }
        if (north != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), v(0, 1, 0), getSideTexture(north, t), hilight));
            if (north.isInput()) {
                quads.add(createQuad(v(1-o, 1-o, -.02), v(1-o, o, -.02), v(o, o, -.02), v(o, 1-o, -.02), getInputMask(), hilight));
            }
            if (north.isOutput()) {
                quads.add(createQuad(v(1-o, 1-o, -.02), v(1-o, o, -.02), v(o, o, -.02), v(o, 1-o, -.02), getOutputMask(), hilight));
            }
        }
        if (south != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 1), v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), getSideTexture(south, t), hilight));
            if (south.isInput()) {
                quads.add(createQuad(v(o, 1-o, 1.02), v(o, o, 1.02), v(1-o, o, 1.02), v(1-o, 1-o, 1.02), getInputMask(), hilight));
            }
            if (south.isOutput()) {
                quads.add(createQuad(v(o, 1-o, 1.02), v(o, o, 1.02), v(1-o, o, 1.02), v(1-o, 1-o, 1.02), getOutputMask(), hilight));
            }
        }

        return quads;
    }


    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getSideTexture(SideType.BOTH_NONE, 1);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}
