package mcjty.rftoolspower.modules.powercell.client;

import com.google.common.collect.Lists;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }

        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellhoriz_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t1"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t2"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t3"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/inputmask"));
        event.addSprite(new ResourceLocation(RFToolsPower.MODID, "block/powercell/outputmask"));
    }

    public static void onModelBake(ModelBakeEvent event) {
        PowerCellBakedModel model = new PowerCellBakedModel();
        Lists.newArrayList("cell1", "cell2", "cell3").stream()
                .forEach(name -> {
                    ResourceLocation rl = new ResourceLocation(RFToolsPower.MODID, name);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, ""), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=false,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=false,upper=true"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=true,upper=false"), model);
                    event.getModelRegistry().put(new ModelResourceLocation(rl, "lower=true,upper=true"), model);
                });
    }
}
