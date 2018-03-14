package mcjty.rftoolspower.blocks;

import mcjty.lib.gui.HudRenderHelper;
import mcjty.rftoolspower.network.PacketGetMonitorLog;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class InformationScreenRenderer extends TileEntitySpecialRenderer<InformationScreenTileEntity> {

    @Override
    public void render(InformationScreenTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderHud(te, x, y, z);
    }

    public static void renderHud(InformationScreenTileEntity hudSupport, double x, double y, double z) {
        renderHud(hudSupport, x, y, z, 0.3f, false);
    }

    public static void renderHud(InformationScreenTileEntity support, double x, double y, double z, float scale, boolean faceVert) {
        List<String> log = support.getClientLog();
        long t = System.currentTimeMillis();
        if (t - support.getLastUpdateTime() > 250) {
            RFToolsPowerMessages.INSTANCE.sendToServer(new PacketGetMonitorLog(support.getPos()));
            support.setLastUpdateTime(t);
        }
        EnumFacing orientation = support.getBlockOrientation();
        HudRenderHelper.HudPlacement hudPlacement = HudRenderHelper.HudPlacement.HUD_FRONT;
        HudRenderHelper.HudOrientation hudOrientation = orientation == null ? HudRenderHelper.HudOrientation.HUD_TOPLAYER_HORIZ : HudRenderHelper.HudOrientation.HUD_SOUTH;
        HudRenderHelper.renderHud(log, hudPlacement, hudOrientation, orientation, x - orientation.getFrontOffsetX() * .95, y, z - orientation.getFrontOffsetZ() * .95, 1.0f + scale);
    }

}
