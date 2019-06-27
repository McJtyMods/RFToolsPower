package mcjty.rftoolspower.blocks;

import com.google.common.collect.ImmutableSet;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;

import java.util.Set;

public class BakedModelLoader implements ICustomModelLoader {

    public static final GenericCellModel GENERIC_MODEL = new GenericCellModel();

    private static final Set<String> NAMES = ImmutableSet.of("cell1", "cell2", "cell3");

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getNamespace().equals(RFToolsPower.MODID)) {
            return false;
        }
//        if (modelLocation instanceof ModelResourceLocation && ((ModelResourceLocation)modelLocation).getVariant().equals("inventory")) {
//            return false;
//        }
        return NAMES.contains(modelLocation.getPath());
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
        return GENERIC_MODEL;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
