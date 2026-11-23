package mcjty.rftoolspower.modules.endergenic.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EndergenicRenderer implements BlockEntityRenderer<EndergenicTileEntity> {

    public static final ResourceLocation HALO = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/floatingpearl");
    public static final ResourceLocation WHITEFLASH = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/whiteflash");
    public static final ResourceLocation BLACKFLASH = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/redflash");

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/redglow");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/blueglow");

    private static final RenderSettings PEARL_SETTINGS = RenderSettings.builder()
            .renderType(RenderType.cutout())
            .build();

    public static final RenderSettings FLASH_SETTINGS = RenderSettings.builder()
            .alpha(128)
            .renderType(FlashRenderType.INSTANCE)
            .build();

    public EndergenicRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EndergenicTileEntity tileEntity, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos coord = tileEntity.getBlockPos();
        if (coord.equals(RFToolsBase.instance.clientInfo.getSelectedTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, REDGLOW);
        } else if (coord.equals(RFToolsBase.instance.clientInfo.getDestinationTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, BLUEGLOW);
        }

        float s = (System.currentTimeMillis() % 1000) / 1000.0f;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.2f + s * 0.3f, HALO, PEARL_SETTINGS);

        if (tileEntity.getGoodCounter() > 0) {
            RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.8f * (tileEntity.getGoodCounter() / 10.0f), WHITEFLASH, FLASH_SETTINGS);
        }
        if (tileEntity.getBadCounter() > 0) {
            RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.8f * (tileEntity.getBadCounter() / 20.0f), BLACKFLASH, FLASH_SETTINGS);
        }

        ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
        ItemStack offHand = Minecraft.getInstance().player.getOffhandItem();
        boolean showOverlay = (!mainHand.isEmpty() && mainHand.getItem() instanceof SmartWrenchItem) ||
                (!offHand.isEmpty() && offHand.getItem() instanceof SmartWrenchItem);
        if (showOverlay) {
            HudRenderer.renderHud(matrixStackIn, bufferIn, tileEntity);
        }
    }

    public static void register() {
        BlockEntityRenderers.register(EndergenicModule.TYPE_ENDERGENIC.get(), EndergenicRenderer::new);
    }

    private static final class FlashRenderType extends RenderType {

        // Avoid masking the translucent glass behind transparent parts of the flash.
        private static final RenderType INSTANCE = create("endergenic_flash", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                        .setTextureState(BLOCK_SHEET_MIPPED)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setOutputState(TRANSLUCENT_TARGET)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

        private FlashRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int size, boolean affectsCrumbling,
                                boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, size, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }
}
