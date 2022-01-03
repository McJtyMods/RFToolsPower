package mcjty.rftoolspower.modules.powercell.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.api.infoscreen.IInformationScreenInfo;
import mcjty.rftoolspower.modules.powercell.client.PowerCellInformationRenderer;
import mcjty.rftoolspower.modules.powercell.data.PowerCellNetwork;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

import static mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo.ENERGY;
import static mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo.MAXENERGY;

public class PowerCellInformationScreenInfo implements IInformationScreenInfo {

    public static final int MODE_POWER_EXTENDED = 2;

    public static final Key<Long> RFEXTRACT_PERTICK = new Key<>("rfExtractPerTick", Type.LONG);
    public static final Key<Long> RFINSERT_PERTICK = new Key<>("rfInsertPerTick", Type.LONG);
    public static final Key<Long> ROUGH_MAXRF_PERTICK = new Key<>("roughMaxRfPerTick", Type.LONG);

    private final PowerCellTileEntity cell;
    private long lastExtracted = 0;
    private long lastInserted = 0;
    private long rfExtractPerTick = 0;      // Also used client side and synced with packet
    private long rfInsertedPerTick = 0;

    public PowerCellInformationScreenInfo(PowerCellTileEntity cell) {
        this.cell = cell;
    }

    @Override
    public int[] getSupportedModes() {
        return new int[]{MODE_POWER, MODE_POWER_GRAPHICAL, MODE_POWER_EXTENDED};
    }

    @Override
    public void tick() {
        PowerCellNetwork network = cell.getNetwork();
        if (network != null) {
            long newExtracted = network.getExtracted();
            long newInserted = network.getInserted();
            rfExtractPerTick = (newExtracted - lastExtracted) / 10;
            rfInsertedPerTick = (newInserted - lastInserted) / 10;
            lastExtracted = newExtracted;
            lastInserted = newInserted;
        } else {
            rfExtractPerTick = -1;
            rfInsertedPerTick = -1;
        }
    }

    private long calculateRoughMaxRfPerTick() {
        return cell.getRfPerTickPerSide() * 2;
    }

    @Nonnull
    @Override
    public TypedMap getInfo(int mode) {
        PowerCellNetwork network = cell.getNetwork();
        if (network != null) {
            long energy = network.getEnergy();
            long maxEnergy = network.getMaxEnergy();
            return TypedMap.builder()
                    .put(ENERGY, energy)
                    .put(MAXENERGY, maxEnergy)
                    .put(RFEXTRACT_PERTICK, rfExtractPerTick)
                    .put(RFINSERT_PERTICK, rfInsertedPerTick)
                    .put(ROUGH_MAXRF_PERTICK, calculateRoughMaxRfPerTick())
                    .build();
        } else {
            return TypedMap.EMPTY;
        }
    }

    @Override
    public void render(int mode, PoseStack matrixStack, MultiBufferSource buffer, @Nonnull TypedMap data, Direction orientation, double scale) {
        if (mode == MODE_POWER) {
            PowerCellInformationRenderer.renderDefault(matrixStack, buffer, data, orientation, scale);
        } else {
            PowerCellInformationRenderer.renderGraphical(matrixStack, buffer, mode, data, orientation, scale);
        }
    }
}
