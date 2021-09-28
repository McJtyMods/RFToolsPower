package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class BlazingAgitatorRenderer extends TileEntityRenderer<BlazingAgitatorTileEntity> {

    public BlazingAgitatorRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlazingAgitatorTileEntity te, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            matrixStack.pushPose();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            long millis = System.currentTimeMillis();
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
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
                        itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, RenderHelper.MAX_BRIGHTNESS, combinedOverlay, matrixStack, buffer);
                        matrixStack.popPose();
                    }
                }
            }

            matrixStack.popPose();
        });
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(BlazingModule.TYPE_BLAZING_AGITATOR.get(), BlazingAgitatorRenderer::new);
    }

}
