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

    private static Map<SideType, TextureAtlasSprite> sideSpriteMap = new HashMap<>();
    private static Map<SideType, TextureAtlasSprite> topSpriteMap = new HashMap<>();

    private VertexFormat format;

    private static TextureAtlasSprite getSideTexture(SideType type) {
        if (!sideSpriteMap.containsKey(type)) {
            sideSpriteMap.put(type, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(type.getSideTexture()));
        }
        return sideSpriteMap.get(type);
    }

    private static TextureAtlasSprite getTopTexture(SideType type) {
        if (!topSpriteMap.containsKey(type)) {
            topSpriteMap.put(type, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(type.getUpDownTexture()));
        }
        return topSpriteMap.get(type);
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

        if (side != null) {
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

        List<BakedQuad> quads = new ArrayList<>();

        float hilight = 1.0f;

        if (up != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 1, 1), v(1, 1, 1), v(1, 1, 0), getTopTexture(up), hilight));
        }
        if (down != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 0, 0), v(1, 0, 0), v(1, 0, 1), v(0, 0, 1), getTopTexture(down), hilight));
        }
        if (east != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 1), v(1, 0, 1), v(1, 0, 0), v(1, 1, 0), getSideTexture(east), hilight));
        }
        if (west != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0, 1, 1), getSideTexture(west), hilight));
        }
        if (north != SideType.INVISIBLE) {
            quads.add(createQuad(v(1, 1, 0), v(1, 0, 0), v(0, 0, 0), v(0, 1, 0), getSideTexture(north), hilight));
        }
        if (south != SideType.INVISIBLE) {
            quads.add(createQuad(v(0, 1, 1), v(0, 0, 1), v(1, 0, 1), v(1, 1, 1), getSideTexture(south), hilight));
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
        return getSideTexture(SideType.BOTH_NONE);
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
