package mcjty.rftoolspower.modules.powercell.client;

import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.client.HudRenderHelper;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellInformationScreenInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static mcjty.rftoolsbase.modules.informationscreen.client.DefaultPowerInformationRenderer.*;

public class PowerCellInformationRenderer {

    public static void renderGraphical(int mode, TypedMap data, Direction orientation, double x, double y, double z, double scale) {
        if (data == null || data.size() == 0) {
            return;
        }

        long energy = data.getOptional(DefaultPowerInformationScreenInfo.ENERGY).orElse(0L);
        long maxEnergy = data.getOptional(DefaultPowerInformationScreenInfo.MAXENERGY).orElse(0L);
        long rfExtractPerTick = data.getOptional(PowerCellInformationScreenInfo.RFEXTRACT_PERTICK).orElse(0L);
        long rfInsertPerTick = data.getOptional(PowerCellInformationScreenInfo.RFINSERT_PERTICK).orElse(0L);
        long roughMaxRfPerTick = data.getOptional(PowerCellInformationScreenInfo.ROUGH_MAXRF_PERTICK).orElse(0L);

        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        GlStateManager.rotatef(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.0F, -0.2500F, -0.4375F + .9f);

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();

        if (maxEnergy > 0) {
            GlStateManager.translatef(-0.5F, 0.5F, 0.07F);
            float f3 = 0.0075F;
            GlStateManager.scaled(f3 * scale, -f3 * scale, f3);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            long pct = energy * 100 / maxEnergy;

            for (int i = 0; i < 100; i += 5) {
                int col = i < pct ? getPercentageColor(i) : 0xff111111;
                RenderHelper.drawFlatBox(16, (int) (100 - i * .8 - 13), mode == 1 ? 88 : 44, (int) (100 - i * .8 + 3 - 13), col, col);
            }

            if (mode == 2) {
                if (roughMaxRfPerTick > 0) {
                    long pctInserted = Math.max(0, Math.min(100L, rfInsertPerTick * 100 / roughMaxRfPerTick));
                    RenderHelper.drawFlatBox(60, 20, 90, 50, 0xffffffff, 0xff000000 + (int) (pctInserted * 2.5f));
                    long pctExtracted = Math.max(0, Math.min(100L, rfExtractPerTick * 100 / roughMaxRfPerTick));
                    int mask = (int) (pctExtracted * 2.5f);
                    RenderHelper.drawFlatBox(60, 54, 90, 84, 0xffffffff, 0xff000000 + (mask << 16) + (mask << 8));
                }
            }

        }
        Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();

//        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.popMatrix();
    }

    public static void renderDefault(TypedMap data, Direction orientation, double x, double y, double z, double scale) {
        List<String> log = getLog(data);
        HudRenderHelper.HudPlacement hudPlacement = HudRenderHelper.HudPlacement.HUD_FRONT;
        HudRenderHelper.HudOrientation hudOrientation = HudRenderHelper.HudOrientation.HUD_SOUTH;
        // @todo 1.15
//        HudRenderHelper.renderHud(log, hudPlacement, hudOrientation, orientation, x - orientation.getXOffset() * .95, y, z - orientation.getZOffset() * .95, (float) (1.0f + scale));
    }

    private static List<String> getLog(TypedMap data) {
        List<String> list = new ArrayList<>();
        list.add("");

        if (data != null && data.size() > 0) {
            long energy = data.get(DefaultPowerInformationScreenInfo.ENERGY);
            long maxEnergy = data.get(DefaultPowerInformationScreenInfo.MAXENERGY);
            long rfExtractPerTick = data.get(PowerCellInformationScreenInfo.RFEXTRACT_PERTICK);
            long rfInsertPerTick = data.get(PowerCellInformationScreenInfo.RFINSERT_PERTICK);
            list.add(TextFormatting.BLUE + " RF: " + TextFormatting.WHITE + formatPower(energy));
            list.add(TextFormatting.BLUE + " Max: " + TextFormatting.WHITE + formatPower(maxEnergy));
            if (rfExtractPerTick != -1) {
                list.add(TextFormatting.BLUE + " Ext/t: " + TextFormatting.WHITE + formatPower(rfExtractPerTick));
                list.add(TextFormatting.BLUE + " Ins/t: " + TextFormatting.WHITE + formatPower(rfInsertPerTick));
            }
        } else {
            list.add(TextFormatting.RED + " Not a powercell");
            list.add(TextFormatting.RED + " or anything that");
            list.add(TextFormatting.RED + " supports power");
        }
        return list;
    }
}
