package mcjty.rftoolspower.modules.powercell.client;

import com.google.common.collect.Lists;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.List;

public class ClientSetup {

    public static List<ResourceLocation> onTextureStitch() {
        return List.of(
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t1"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellhoriz_t1"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t1"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t1"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t1"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t2"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t2"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t2"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t2"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellboth_t3"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/celllower_t3"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellmiddle_t3"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/cellupper_t3"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/inputmask"),
            new ResourceLocation(RFToolsPower.MODID, "block/powercell/outputmask")
        );
    }

    public static void onModelBake(ModelEvent.BakingCompleted event) {
        PowerCellBakedModel model = new PowerCellBakedModel();
        Lists.newArrayList("cell1", "cell2", "cell3").stream()
                .forEach(name -> {
                    ResourceLocation rl = new ResourceLocation(RFToolsPower.MODID, name);
                    event.getModels().put(new ModelResourceLocation(rl, ""), model);
                    event.getModels().put(new ModelResourceLocation(rl, "lower=false,upper=false"), model);
                    event.getModels().put(new ModelResourceLocation(rl, "lower=false,upper=true"), model);
                    event.getModels().put(new ModelResourceLocation(rl, "lower=true,upper=false"), model);
                    event.getModels().put(new ModelResourceLocation(rl, "lower=true,upper=true"), model);
                });
    }
}
