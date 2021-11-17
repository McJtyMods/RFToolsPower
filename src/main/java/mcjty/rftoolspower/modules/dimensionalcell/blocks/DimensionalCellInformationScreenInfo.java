package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.api.infoscreen.IInformationScreenInfo;
import mcjty.rftoolspower.modules.powercell.client.PowerCellInformationRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

import static mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo.ENERGY;
import static mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo.MAXENERGY;
import static mcjty.rftoolspower.modules.powercell.blocks.PowerCellInformationScreenInfo.*;

public class DimensionalCellInformationScreenInfo implements IInformationScreenInfo {

    public static final int MODE_POWER_EXTENDED = 2;

    private final DimensionalCellTileEntity cell;
    private long lastExtracted = 0;
    private long lastInserted = 0;
    private long rfExtractPerTick = 0;      // Also used client side and synced with packet
    private long rfInsertedPerTick = 0;

    public DimensionalCellInformationScreenInfo(DimensionalCellTileEntity cell) {
        this.cell = cell;
    }

    @Override
    public int[] getSupportedModes() {
        return new int[]{MODE_POWER, MODE_POWER_GRAPHICAL, MODE_POWER_EXTENDED};
    }

    @Override
    public void tick() {
        long newExtracted = cell.getTotalExtracted();
        long newInserted = cell.getTotalInserted();
        rfExtractPerTick = (newExtracted - lastExtracted) / 10;
        rfInsertedPerTick = (newInserted - lastInserted) / 10;
        lastExtracted = newExtracted;
        lastInserted = newInserted;
    }

    private long calculateRoughMaxRfPerTick() {
        return cell.getRfPerTickPerSide() * 2L;
    }

    @Nonnull
    @Override
    public TypedMap getInfo(int mode) {
        long energy = cell.getEnergy();
        long maxEnergy = cell.getMaxEnergyStored();
        return TypedMap.builder()
                .put(ENERGY, energy)
                .put(MAXENERGY, maxEnergy)
                .put(RFEXTRACT_PERTICK, rfExtractPerTick)
                .put(RFINSERT_PERTICK, rfInsertedPerTick)
                .put(ROUGH_MAXRF_PERTICK, calculateRoughMaxRfPerTick())
                .build();
    }

    @Override
    public void render(int mode, MatrixStack matrixStack, IRenderTypeBuffer buffer, @Nonnull TypedMap data, Direction orientation, double scale) {
        if (mode == MODE_POWER) {
            PowerCellInformationRenderer.renderDefault(matrixStack, buffer, data, orientation, scale);
        } else {
            PowerCellInformationRenderer.renderGraphical(matrixStack, buffer, mode, data, orientation, scale);
        }
    }
}
