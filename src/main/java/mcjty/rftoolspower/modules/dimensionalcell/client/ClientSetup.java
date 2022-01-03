package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_CREATIVE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get(), RenderType.translucent());
    }
}
