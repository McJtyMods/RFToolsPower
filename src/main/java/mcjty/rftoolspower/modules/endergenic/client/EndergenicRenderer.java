package mcjty.rftoolspower.modules.endergenic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.modules.hud.client.HudRenderer;
import mcjty.rftoolsbase.modules.various.items.SmartWrenchItem;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicSetup;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class EndergenicRenderer extends TileEntityRenderer<EndergenicTileEntity> {

    public static final ResourceLocation HALO = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/floatingpearl");
    public static final ResourceLocation WHITEFLASH = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/whiteflash");
    public static final ResourceLocation BLACKFLASH = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/redflash");

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/redglow");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsPower.MODID, "block/endergenic/blueglow");

    public static final RenderSettings FLASH_SETTINGS = RenderSettings.builder()
            .color(255, 255, 255)
            .alpha(128)
            .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
            .build();

    public EndergenicRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(EndergenicTileEntity tileEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos coord = tileEntity.getPos();
        if (coord.equals(RFToolsBase.instance.clientInfo.getSelectedTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, REDGLOW);
        } else if (coord.equals(RFToolsBase.instance.clientInfo.getDestinationTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, BLUEGLOW);
        }

        float s = (System.currentTimeMillis() % 1000) / 1000.0f;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.2f + s * 0.3f, HALO);// + random.nextFloat() * .05f);

        if (tileEntity.getGoodCounter() > 0) {
            RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.8f * (tileEntity.getGoodCounter() / 10.0f), WHITEFLASH, FLASH_SETTINGS);
        }
        if (tileEntity.getBadCounter() > 0) {
            RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.8f * (tileEntity.getBadCounter() / 20.0f), BLACKFLASH, FLASH_SETTINGS);
        }

        ItemStack mainHand = Minecraft.getInstance().player.getHeldItemMainhand();
        ItemStack offHand = Minecraft.getInstance().player.getHeldItemOffhand();
        boolean showOverlay = (!mainHand.isEmpty() && mainHand.getItem() instanceof SmartWrenchItem) ||
                (!offHand.isEmpty() && offHand.getItem() instanceof SmartWrenchItem);
        if (showOverlay) {
            HudRenderer.renderHud(matrixStackIn, bufferIn, tileEntity);
        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(EndergenicSetup.TYPE_ENDERGENIC.get(), EndergenicRenderer::new);
    }
}
