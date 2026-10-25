package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.ItemStack;
import mcjty.lib.container.IItemHandler;

import javax.annotation.Nonnull;

public class BlazingAgitatorRenderer implements BlockEntityRenderer<BlazingAgitatorTileEntity, BlazingAgitatorRenderer.State> {

    public BlazingAgitatorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        final ItemStack[] items = new ItemStack[9];
        final float[] angles = new float[9];
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(BlazingAgitatorTileEntity te, State state, float partialTicks,
            net.minecraft.world.phys.Vec3 camera, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, camera, overlay);
        var items = mcjty.lib.varia.CapabilityTools.itemHandler(net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(te.getLevel(), te.getBlockPos(), null));
        for (int i = 0; i < 9; i++) {
            state.items[i] = items == null ? ItemStack.EMPTY : items.getStackInSlot(i).copy();
            state.angles[i] = (te.getCurrentAngle(i % 3, i / 3) + te.getRotationSpeed(i % 3, i / 3) * 9) % 360;
            te.setCurrentAngle(i % 3, i / 3, state.angles[i]);
        }
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        for (int i = 0; i < 9; i++) {
            if (state.items[i].isEmpty()) continue;
            pose.pushPose();
            pose.scale(.3f, .3f, .3f);
            pose.translate((i % 3) * .9f + .75f, 2.1f, (i / 3) * .9f + .75f);
            RenderHelper.rotateYP(pose, state.angles[i]);
            RenderHelper.renderStaticFixed(pose, collector, state.items[i], RenderHelper.MAX_BRIGHTNESS,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
    }

    public static void register() {
        BlockEntityRenderers.register(BlazingModule.BLAZING_AGITATOR.be().get(), BlazingAgitatorRenderer::new);
    }

}
