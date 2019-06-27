package mcjty.rftoolspower.blocks;

import com.google.common.collect.ImmutableSet;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class GenericCellModel implements IUnbakedModel {


    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function spriteGetter, ISprite sprite, VertexFormat format) {
        return new GenericCellBakedModel(format);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return ImmutableSet.of(
                new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t1"),
                new ResourceLocation(RFToolsPower.MODID, "block/celllower_t1"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t1"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t1"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellhoriz_t1"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t2"),
                new ResourceLocation(RFToolsPower.MODID, "block/celllower_t2"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t2"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t2"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellboth_t3"),
                new ResourceLocation(RFToolsPower.MODID, "block/celllower_t3"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellmiddle_t3"),
                new ResourceLocation(RFToolsPower.MODID, "block/cellupper_t3"),
                new ResourceLocation(RFToolsPower.MODID, "block/inputmask"),
                new ResourceLocation(RFToolsPower.MODID, "block/outputmask")
                );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
