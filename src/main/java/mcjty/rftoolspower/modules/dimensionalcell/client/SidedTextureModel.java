package mcjty.rftoolspower.modules.dimensionalcell.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity.Mode;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock.*;


public class SidedTextureModel implements IModelGeometry<SidedTextureModel> {
    public static void register() {
        ModelLoaderRegistry.registerLoader(new ResourceLocation("rftoolspower", "sided_texture_model"), new Loader());
    }

    private final BlockModel baseModel;
    private final Map<AllSidesState, BlockModel> allModels = Maps.newHashMap();

    public SidedTextureModel(BlockModel baseModel) {
        this.baseModel = baseModel;
        Mode[] states = Mode.values();
        for (Mode east : states) {
            for (Mode west : states) {
                for (Mode north : states) {
                    for (Mode south : states) {
                        for (Mode up : states) {
                            for (Mode down : states) {
                                AllSidesState state = AllSidesState.of(east, west, north, south, up, down);
                                ImmutableMap.Builder<String, Either<Material, String>> builder = ImmutableMap.builder();
                                builder.put("east", Either.right(east.getOverlayName()));
                                builder.put("west", Either.right(west.getOverlayName()));
                                builder.put("north", Either.right(north.getOverlayName()));
                                builder.put("south", Either.right(south.getOverlayName()));
                                builder.put("up", Either.right(up.getOverlayName()));
                                builder.put("down", Either.right(down.getOverlayName()));
                                BlockModel child = new BlockModel(new ResourceLocation("rftoolspower", "generated/dummy"),
                                        Collections.emptyList(), builder.build(), true, null, ItemCameraTransforms.DEFAULT, Collections.emptyList()
                                );
//                                BlockModel child = new BlockModel(new ResourceLocation("rftoolspower", "block/" + state.toString()),
//                                        Collections.emptyList(), builder.build(), true, null, ItemCameraTransforms.DEFAULT, Collections.emptyList()
//                                );
                                child.parent = baseModel;
                                allModels.put(state, child);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<net.minecraft.client.renderer.model.Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new BakedModel(
                Maps.transformEntries(allModels, (k, v) -> v.bakeModel(bakery, v, spriteGetter, modelTransform, modelLocation, v.func_230176_c_().func_230178_a_())),
                owner.isSideLit(), owner.isShadedInGui(), spriteGetter.apply(owner.resolveTexture("particle")), overrides
        );
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
        Set<Material> allMaterials = Sets.newHashSet();
        for (BlockModel values : allModels.values()) {
            allMaterials.addAll(values.getTextures(modelGetter, missingTextureErrors));
        }
        return allMaterials;
    }

    public static class AllSidesState {
        public static AllSidesState of(Mode east, Mode west, Mode north, Mode south, Mode up, Mode down) {
            return new AllSidesState(east, west, north, south, up, down);
        }

        public final Mode east;
        public final Mode west;
        public final Mode north;
        public final Mode south;
        public final Mode up;
        public final Mode down;

        private AllSidesState(Mode east, Mode west, Mode north, Mode south, Mode up, Mode down) {
            this.east = east;
            this.west = west;
            this.north = north;
            this.south = south;
            this.up = up;
            this.down = down;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AllSidesState that = (AllSidesState) o;
            return east == that.east &&
                    west == that.west &&
                    north == that.north &&
                    south == that.south &&
                    up == that.up &&
                    down == that.down;
        }

        @Override
        public int hashCode() {
            return Objects.hash(east, west, north, south, up, down);
        }

        // @todo test
//        @Override
//        public String toString() {
//            return "side_states" +
//                    "/east_" + east.getOverlayName() +
//                    "/west_" + west.getOverlayName() +
//                    "/north_" + north.getOverlayName() +
//                    "/south_" + south.getOverlayName() +
//                    "/up_" + up.getOverlayName() +
//                    "/down_" + down.getOverlayName();
//        }
    }

    public static class BakedModel implements IDynamicBakedModel {
        private final Map<AllSidesState, IBakedModel> allModels;
        private final boolean isSideLit;
        private final boolean isAmbientOcclusion;
        private final TextureAtlasSprite particle;
        private final ItemOverrideList overrides;

        public BakedModel(Map<AllSidesState, IBakedModel> allModels, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemOverrideList overrides) {
            this.allModels = allModels;
            this.isSideLit = isSideLit;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.particle = particle;
            this.overrides = overrides;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
            AllSidesState states = AllSidesState.of(state.get(EAST), state.get(WEST), state.get(NORTH), state.get(SOUTH), state.get(UP), state.get(DOWN));
            return allModels.get(states).getQuads(state, side, rand, extraData);
        }

        @Override
        public boolean isAmbientOcclusion() {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean func_230044_c_() {
            return isSideLit;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return particle;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return overrides;
        }
    }

    public static class Loader implements IModelLoader<SidedTextureModel> {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public SidedTextureModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            BlockModel baseModel = deserializationContext.deserialize(JSONUtils.getJsonObject(modelContents, "base_model"), BlockModel.class);

            return new SidedTextureModel(baseModel);
        }
    }
}
