package mcjty.rftoolspower.modules.dimensionalcell.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcjty.rftoolspower.modules.dimensionalcell.client.DimensionalCellGeometry;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class DimensionalCellLoader implements IModelLoader {

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new DimensionalCellGeometry();
    }
}
