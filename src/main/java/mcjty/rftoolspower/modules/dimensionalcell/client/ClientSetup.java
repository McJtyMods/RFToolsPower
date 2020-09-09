package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        RenderTypeLookup.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get(), RenderType.getTranslucent());
    }
}
