package mcjty.rftoolspower.modules.endergenic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderGlowEffect;
import mcjty.lib.client.RenderHelper;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicSetup;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class EndergenicRenderer extends TileEntityRenderer<EndergenicTileEntity> {

    public static final ResourceLocation HALO = new ResourceLocation(RFToolsPower.MODID, "textures/block/endergenic/floatingpearl.png");
    public static final ResourceLocation WHITEFLASH = new ResourceLocation(RFToolsPower.MODID, "textures/block/endergenic/whiteflash.png");
    public static final ResourceLocation BLACKFLASH = new ResourceLocation(RFToolsPower.MODID, "textures/block/endergenic/redflash.png");

    public static final ResourceLocation REDGLOW = new ResourceLocation(RFToolsPower.MODID, "textures/block/endergenic/redglow.png");
    public static final ResourceLocation BLUEGLOW = new ResourceLocation(RFToolsPower.MODID, "textures/block/endergenic/blueglow.png");

    public EndergenicRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(EndergenicTileEntity tileEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Tessellator tessellator = Tessellator.getInstance();
        BlockPos coord = tileEntity.getPos();
        if (coord.equals(RFToolsBase.instance.clientInfo.getSelectedTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, 0, 0, 0, REDGLOW);
        } else if (coord.equals(RFToolsBase.instance.clientInfo.getDestinationTE())) {
            RenderGlowEffect.renderGlow(matrixStackIn, bufferIn, 0, 0, 0, BLUEGLOW);
        }

//        GlStateManager.depthMask(false);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
//        GlStateManager.disableAlpha();
//
//        GlStateManager.pushMatrix();
//        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
//        this.bindTexture(halo);
        float s = (System.currentTimeMillis() % 1000) / 1000.0f;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        RenderHelper.renderBillboardQuadBright(matrixStackIn, bufferIn, 0.2f + s * 0.3f, HALO);// + random.nextFloat() * .05f);
//
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//
//        if (tileEntity.getGoodCounter() > 0) {
//            this.bindTexture(whiteflash);
//            RenderHelper.renderBillboardQuadBright(0.8f * (tileEntity.getGoodCounter() / 10.0f));
//        }
//        if (tileEntity.getBadCounter() > 0) {
//            this.bindTexture(blackflash);
//            RenderHelper.renderBillboardQuadBright(0.8f * (tileEntity.getBadCounter() / 20.0f));
//        }
//
//        GlStateManager.popMatrix();
//
//        ItemStack mainHand = Minecraft.getMinecraft().player.getHeldItemMainhand();
//        ItemStack offHand = Minecraft.getMinecraft().player.getHeldItemOffhand();
//        boolean showOverlay = (!mainHand.isEmpty() && mainHand.getItem() instanceof SmartWrenchItem) ||
//                (!offHand.isEmpty() && offHand.getItem() instanceof SmartWrenchItem);
//        if (showOverlay) {
//            HudRenderer.renderHud(tileEntity, x, y, z);
//        }
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(EndergenicSetup.TYPE_ENDERGENIC.get(), EndergenicRenderer::new);
    }
}
