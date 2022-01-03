package mcjty.rftoolspower.modules.powercell.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import mcjty.lib.client.HudRenderHelper;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolsbase.modules.informationscreen.blocks.DefaultPowerInformationScreenInfo;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellInformationScreenInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.ChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static mcjty.rftoolsbase.modules.informationscreen.client.DefaultPowerInformationRenderer.*;

public class PowerCellInformationRenderer {

    // @todo 1.15 port rendering
    public static void renderGraphical(PoseStack matrixStack, MultiBufferSource buffer, int mode, TypedMap data, Direction orientation, double scale) {
        if (data == null || data.size() == 0) {
            return;
        }

        long energy = data.getOptional(DefaultPowerInformationScreenInfo.ENERGY).orElse(0L);
        long maxEnergy = data.getOptional(DefaultPowerInformationScreenInfo.MAXENERGY).orElse(0L);
        long rfExtractPerTick = data.getOptional(PowerCellInformationScreenInfo.RFEXTRACT_PERTICK).orElse(0L);
        long rfInsertPerTick = data.getOptional(PowerCellInformationScreenInfo.RFINSERT_PERTICK).orElse(0L);
        long roughMaxRfPerTick = data.getOptional(PowerCellInformationScreenInfo.ROUGH_MAXRF_PERTICK).orElse(0L);

        GlStateManager._pushMatrix();
        GlStateManager._translatef(0.5F, 0.75F, 0.5F);
        GlStateManager._rotatef(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F);
        GlStateManager._translatef(0.0F, -0.2500F, -0.4375F + .9f);

        com.mojang.blaze3d.platform.Lighting.turnOff();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        GlStateManager._disableBlend();
        GlStateManager._disableLighting();

        if (maxEnergy > 0) {
            GlStateManager._translatef(-0.5F, 0.5F, 0.07F);
            float f3 = 0.0075F;
            GlStateManager._scaled(f3 * scale, -f3 * scale, f3);
            GlStateManager._normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);

            long pct = energy * 100 / maxEnergy;

            for (int i = 0; i < 100; i += 5) {
                int col = i < pct ? getPercentageColor(i) : 0xff111111;
                RenderHelper.drawFlatBox(matrixStack, 16, (int) (100 - i * .8 - 13), mode == 1 ? 88 : 44, (int) (100 - i * .8 + 3 - 13), col, col);
            }

            if (mode == 2) {
                if (roughMaxRfPerTick > 0) {
                    long pctInserted = Math.max(0, Math.min(100L, rfInsertPerTick * 100 / roughMaxRfPerTick));
                    RenderHelper.drawFlatBox(matrixStack, 60, 20, 90, 50, 0xffffffff, 0xff000000 + (int) (pctInserted * 2.5f));
                    long pctExtracted = Math.max(0, Math.min(100L, rfExtractPerTick * 100 / roughMaxRfPerTick));
                    int mask = (int) (pctExtracted * 2.5f);
                    RenderHelper.drawFlatBox(matrixStack, 60, 54, 90, 84, 0xffffffff, 0xff000000 + (mask << 16) + (mask << 8));
                }
            }

        }
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();

//        RenderHelper.enableStandardItemLighting();
        GlStateManager._enableLighting();
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager._popMatrix();
    }

    public static void renderDefault(PoseStack matrixStack, MultiBufferSource buffer, TypedMap data, Direction orientation, double scale) {
        List<String> log = getLog(data);
        HudRenderHelper.HudPlacement hudPlacement = HudRenderHelper.HudPlacement.HUD_FRONT;
        HudRenderHelper.HudOrientation hudOrientation = HudRenderHelper.HudOrientation.HUD_SOUTH;
        HudRenderHelper.renderHud(matrixStack, buffer, log, hudPlacement, hudOrientation, orientation, - orientation.getStepX() * .95, 0, - orientation.getStepZ() * .95, (float) (1.0f + scale));
    }

    private static List<String> getLog(TypedMap data) {
        List<String> list = new ArrayList<>();
        list.add("");

        if (data != null && data.size() > 0) {
            long energy = data.get(DefaultPowerInformationScreenInfo.ENERGY);
            long maxEnergy = data.get(DefaultPowerInformationScreenInfo.MAXENERGY);
            long rfExtractPerTick = data.get(PowerCellInformationScreenInfo.RFEXTRACT_PERTICK);
            long rfInsertPerTick = data.get(PowerCellInformationScreenInfo.RFINSERT_PERTICK);
            list.add(ChatFormatting.BLUE + " RF: " + ChatFormatting.WHITE + formatPower(energy));
            list.add(ChatFormatting.BLUE + " Max: " + ChatFormatting.WHITE + formatPower(maxEnergy));
            if (rfExtractPerTick != -1) {
                list.add(ChatFormatting.BLUE + " Ext/t: " + ChatFormatting.WHITE + formatPower(rfExtractPerTick));
                list.add(ChatFormatting.BLUE + " Ins/t: " + ChatFormatting.WHITE + formatPower(rfInsertPerTick));
            }
        } else {
            list.add(ChatFormatting.RED + " Not a powercell");
            list.add(ChatFormatting.RED + " or anything that");
            list.add(ChatFormatting.RED + " supports power");
        }
        return list;
    }
}
