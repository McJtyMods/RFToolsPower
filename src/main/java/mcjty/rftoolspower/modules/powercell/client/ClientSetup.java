package mcjty.rftoolspower.modules.powercell.client;

import com.google.common.collect.Lists;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class ClientSetup {

    public static List<ResourceLocation> onTextureStitch() {
        return List.of(
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellhoriz_t1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/inputmask"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/outputmask")
        );
    }

    public static void onModelBake(Map<ModelResourceLocation, BakedModel> map) {
        PowerCellBakedModel model = new PowerCellBakedModel();
        Lists.newArrayList("cell1", "cell2", "cell3").stream()
                .forEach(name -> {
                    ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, name);
                    map.put(new ModelResourceLocation(rl, ""), model);
                    map.put(new ModelResourceLocation(rl, "lower=false,upper=false"), model);
                    map.put(new ModelResourceLocation(rl, "lower=false,upper=true"), model);
                    map.put(new ModelResourceLocation(rl, "lower=true,upper=false"), model);
                    map.put(new ModelResourceLocation(rl, "lower=true,upper=true"), model);
                });
    }
}
