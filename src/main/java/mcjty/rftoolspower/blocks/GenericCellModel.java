package mcjty.rftoolspower.blocks;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.generic.GenericCableBakedModel;
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
        return new GenericCableBakedModel(format);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(
                new ResourceLocation(XNet.MODID, "blocks/cable0/connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/advanced_connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_three_netcable"),

                new ResourceLocation(XNet.MODID, "blocks/cable1/connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/advanced_connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_three_netcable"),

                new ResourceLocation(XNet.MODID, "blocks/cable2/connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/advanced_connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_three_netcable"),

                new ResourceLocation(XNet.MODID, "blocks/cable3/connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/advanced_connector"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_three_netcable"),

                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable4/normal_three_netcable"),

                new ResourceLocation(XNet.MODID, "blocks/connector_side")
                );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
