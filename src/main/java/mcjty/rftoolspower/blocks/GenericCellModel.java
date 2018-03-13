package mcjty.rftoolspower.blocks;

import com.google.common.collect.ImmutableSet;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class GenericCellModel implements IModel {

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new GenericCellBakedModel(format);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellboth_none"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/celllower_none"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellmiddle_none"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellupper_none"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellhoriz_none"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellboth_input"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/celllower_input"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellmiddle_input"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellupper_input"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellhoriz_input"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellboth_output"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/celllower_output"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellmiddle_output"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellupper_output"),
                new ResourceLocation(RFToolsPower.MODID, "blocks/cellhoriz_output")
                );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
