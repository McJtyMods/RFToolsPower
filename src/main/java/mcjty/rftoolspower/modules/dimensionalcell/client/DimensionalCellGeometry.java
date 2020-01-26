package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.client.DimensionCellBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class DimensionalCellGeometry implements IModelGeometry {

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        return new DimensionCellBakedModel();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function modelGetter, Set missingTextureErrors) {
        return Arrays.asList(
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcell")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcelladvanced")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellcreative")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellsimple")),

                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellin")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellinadvanced")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellincreative")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellinsimple")),

                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellout")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcelloutadvanced")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcelloutcreative")),
                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcelloutsimple")),

                new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(RFToolsPower.MODID, "block/dimensionalcellwindows"))
                );
    }
}
