package mcjty.rftoolspower.blocks;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.blocks.cables.NetCableBlock;
import mcjty.xnet.blocks.facade.FacadeBlock;
import mcjty.xnet.blocks.facade.FacadeModel;
import mcjty.xnet.blocks.generic.GenericCableModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.Set;

public class BakedModelLoader implements ICustomModelLoader {

    public static final GenericCableModel GENERIC_MODEL = new GenericCableModel();
    public static final FacadeModel FACADE_MODEL = new FacadeModel();

    private static final Set<String> NAMES = ImmutableSet.of(
            ConnectorBlock.CONNECTOR,
            NetCableBlock.NETCABLE,
            FacadeBlock.FACADE);

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(XNet.MODID)) {
            return false;
        }
        return NAMES.contains(modelLocation.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if (FacadeBlock.FACADE.equals(modelLocation.getResourcePath())) {
            return FACADE_MODEL;
        } else {
            return GENERIC_MODEL;
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
