package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class BlazingAgitatorRenderer extends TileEntityRenderer<BlazingAgitatorTileEntity> {

    public BlazingAgitatorRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlazingAgitatorTileEntity te, float v, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            matrixStack.push();

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

            for (int x = 0 ; x < 3 ; x++) {
                for (int y = 0 ; y < 3 ; y++) {
                    ItemStack stack = h.getStackInSlot(y * 3 + x);
                    if (!stack.isEmpty()) {
                        matrixStack.push();
                        matrixStack.scale(.3f, .3f, .3f);
                        matrixStack.translate(x * 1.2f + 0.4f, 1.5f, y * 1.2f + 0.4f);
                        itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                        matrixStack.pop();
                    }
                }
            }

            matrixStack.pop();
        });
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(BlazingSetup.TYPE_BLAZING_AGITATOR.get(), BlazingAgitatorRenderer::new);
    }

}
