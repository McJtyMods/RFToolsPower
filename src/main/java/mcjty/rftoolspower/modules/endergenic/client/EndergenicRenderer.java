package mcjty.rftoolspower.modules.endergenic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.modules.hud.client.HudRenderer;
import mcjty.rftoolsbase.modules.various.items.SmartWrenchItem;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class EndergenicRenderer implements BlockEntityRenderer<EndergenicTileEntity, EndergenicRenderer.State> {

    public static final Identifier HALO = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/endergenic/floatingpearl");
    public static final Identifier WHITEFLASH = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/endergenic/whiteflash");
    public static final Identifier BLACKFLASH = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/endergenic/redflash");

    public static final Identifier REDGLOW = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/endergenic/redglow");
    public static final Identifier BLUEGLOW = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/endergenic/blueglow");

    public static final RenderSettings FLASH_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .alpha(128)
//            .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
            .renderType(CustomRenderTypes.translucent())
            .build();

    public EndergenicRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        Identifier glow;
        int good, bad;
        float pulse;
        boolean showHud, above;
        net.minecraft.core.Direction facing;
        java.util.List<String> log = java.util.List.of();
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(EndergenicTileEntity te, State state, float partialTicks,
            Vec3 camera, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, camera, overlay);
        var info = RFToolsBase.instance.clientInfo;
        state.glow = te.getBlockPos().equals(info.getSelectedTE()) ? REDGLOW : te.getBlockPos().equals(info.getDestinationTE()) ? BLUEGLOW : null;
        state.good = te.getGoodCounter();
        state.bad = te.getBadCounter();
        float pulse = (System.currentTimeMillis() % 1000) / 1000.0f;
        state.pulse = pulse > .5f ? 1 - pulse : pulse;
        var player = Minecraft.getInstance().player;
        state.showHud = player != null && (player.getMainHandItem().getItem() instanceof SmartWrenchItem || player.getOffhandItem().getItem() instanceof SmartWrenchItem);
        state.facing = te.getBlockOrientation();
        state.above = te.isBlockAboveAir();
        state.log = java.util.List.copyOf(te.getClientLog());
        if (state.showHud && System.currentTimeMillis() - te.getLastUpdateTime() > 250) {
            mcjty.lib.network.Networking.sendToServer(mcjty.lib.network.PacketGetListFromServer.create(te.getHudPos(), mcjty.rftoolsbase.modules.hud.Hud.COMMAND_GETHUDLOG));
            te.setLastUpdateTime(System.currentTimeMillis());
        }
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        if (state.glow != null) RenderGlowEffect.renderGlow(pose, collector, state.glow);
        billboard(pose, collector, camera, .2f + state.pulse * .3f, HALO, RenderSettings.builder().build());
        if (state.good > 0) billboard(pose, collector, camera, .8f * state.good / 10, WHITEFLASH, FLASH_SETTINGS);
        if (state.bad > 0) billboard(pose, collector, camera, .8f * state.bad / 20, BLACKFLASH, FLASH_SETTINGS);
        if (state.showHud) mcjty.lib.client.HudRenderHelper.renderHud(pose, collector, state.log,
                state.above ? mcjty.lib.client.HudRenderHelper.HudPlacement.HUD_ABOVE : mcjty.lib.client.HudRenderHelper.HudPlacement.HUD_ABOVE_FRONT,
                state.facing == null ? mcjty.lib.client.HudRenderHelper.HudOrientation.HUD_TOPLAYER_HORIZ : mcjty.lib.client.HudRenderHelper.HudOrientation.HUD_SOUTH,
                state.facing, 0, 0, 0, 1);
    }

    private static void billboard(PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.CameraRenderState camera, float size, Identifier texture, RenderSettings settings) {
        var sprite = mcjty.lib.client.AbstractDynamicBakedModel.getTexture(texture);
        pose.pushPose();
        pose.translate(.5f, .5f, .5f);
        pose.mulPose(camera.orientation);
        collector.submitCustomGeometry(pose, settings.renderType(), (captured, vertex) -> {
            var matrix = captured.pose();
            vertex.addVertex(matrix, -size, -size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU0(), sprite.getV1()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, size, -size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU1(), sprite.getV1()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, size, size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU1(), sprite.getV0()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
            vertex.addVertex(matrix, -size, size, 0).setColor(settings.r(), settings.g(), settings.b(), settings.a()).setUv(sprite.getU0(), sprite.getV0()).setOverlay(0).setLight(settings.brightness()).setNormal(0, 0, 1);
        });
        pose.popPose();
    }

    @Override public boolean shouldRenderOffScreen() { return true; }


    public static void register() {
        BlockEntityRenderers.register(EndergenicModule.ENDERGENIC.be().get(), EndergenicRenderer::new);
    }
}
