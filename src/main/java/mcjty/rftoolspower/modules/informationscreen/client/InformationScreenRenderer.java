package mcjty.rftoolspower.modules.informationscreen.client;

import mcjty.lib.client.HudRenderHelper;
import mcjty.rftoolspower.modules.informationscreen.blocks.InformationScreenTileEntity;
import mcjty.rftoolspower.modules.informationscreen.network.PacketGetMonitorLog;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

public class InformationScreenRenderer extends TileEntityRenderer<InformationScreenTileEntity> {

    @Override
    public void render(InformationScreenTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(te, x, y, z, partialTicks, destroyStage);
        renderHud(te, x, y, z);
    }

    public static void renderHud(InformationScreenTileEntity hudinfoscreen, double x, double y, double z) {
        renderHud(hudinfoscreen, x, y, z, 0.3f, false);
    }

    public static void renderHud(InformationScreenTileEntity infoscreen, double x, double y, double z, float scale, boolean faceVert) {
        long t = System.currentTimeMillis();
        if (t - infoscreen.getLastUpdateTime() > 250) {
            RFToolsPowerMessages.INSTANCE.sendToServer(new PacketGetMonitorLog(infoscreen.getPos()));
            infoscreen.setLastUpdateTime(t);
        }
        Direction orientation = infoscreen.getBlockOrientation();
        if (orientation == null) {
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("");
        list.add(TextFormatting.RED + " Right click to ");
        list.add(TextFormatting.RED + " update me! ");
        HudRenderHelper.HudPlacement hudPlacement = HudRenderHelper.HudPlacement.HUD_FRONT;
        HudRenderHelper.HudOrientation hudOrientation = HudRenderHelper.HudOrientation.HUD_SOUTH;
        HudRenderHelper.renderHud(list, hudPlacement, hudOrientation, orientation, x - orientation.getXOffset() * .95, y, z - orientation.getZOffset() * .95, 1.0f + scale);
    }

    private static float getHudAngle(Direction orientation) {
        float f3 = 0.0f;

        if (orientation != null) {
            switch (orientation) {
                case NORTH:
                    f3 = 180.0F;
                    break;
                case WEST:
                    f3 = 90.0F;
                    break;
                case EAST:
                    f3 = -90.0F;
                    break;
                default:
                    f3 = 0.0f;
            }
        }
        return f3;
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(InformationScreenTileEntity.class, new InformationScreenRenderer());
    }
}
