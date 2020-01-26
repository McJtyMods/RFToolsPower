package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DimensionCellBakedModel extends AbstractDynamicBakedModel {

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(RFToolsPower.MODID, "block/model"));
        return model.getQuads(state, side, rand, extraData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(RFToolsPower.MODID, "block/model"));
        return model.getParticleTexture();
    }
}
