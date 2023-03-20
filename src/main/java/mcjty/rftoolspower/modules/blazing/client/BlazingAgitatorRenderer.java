package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class BlazingAgitatorRenderer implements BlockEntityRenderer<BlazingAgitatorTileEntity> {

    public BlazingAgitatorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlazingAgitatorTileEntity te, float v, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            matrixStack.pushPose();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            for (int x = 0 ; x < 3 ; x++) {
                for (int y = 0 ; y < 3 ; y++) {
                    ItemStack stack = h.getStackInSlot(y * 3 + x);
                    if (!stack.isEmpty()) {
                        matrixStack.pushPose();
                        matrixStack.scale(.3f, .3f, .3f);
                        matrixStack.translate(x * 0.9f + 0.75f, 2.1f, y * 0.9f + 0.75f);
                        float rotationSpeed = te.getRotationSpeed(x, y);
                        float angle = te.getCurrentAngle(x, y);
                        angle += rotationSpeed * 9;
                        if (angle > 360) {
                            angle -= 360;
                        }
                        te.setCurrentAngle(x, y, angle);
                        RenderHelper.rotateYP(matrixStack, angle);
                        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, RenderHelper.MAX_BRIGHTNESS, combinedOverlay, matrixStack, buffer, Minecraft.getInstance().level, 0); // @todo 1.18 last parameter?
                        matrixStack.popPose();
                    }
                }
            }

            matrixStack.popPose();
        });
    }

    public static void register() {
        BlockEntityRenderers.register(BlazingModule.TYPE_BLAZING_AGITATOR.get(), BlazingAgitatorRenderer::new);
    }

}
