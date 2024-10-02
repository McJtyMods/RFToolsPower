package mcjty.rftoolspower.modules.monitor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

public class PowerLevelRenderer implements BlockEntityRenderer<PowerLevelTileEntity> {

    public static final ResourceLocation[] DIGITS = new ResourceLocation[] {
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_0"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_1"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_2"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_3"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_4"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_5"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_6"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_7"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_8"),
            ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_9")
    };

    public PowerLevelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PowerLevelTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());

        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        Block block = state.getBlock();
        if (!(block instanceof LogicSlabBlock)) {
            return;
        }

        matrixStack.pushPose();
        LogicFacing logicFacing = state.getValue(LogicSlabBlock.LOGIC_FACING);
        Direction facing = logicFacing.getSide();

        RenderHelper.adjustTransformToDirection(matrixStack, facing);

        int level = te.getLogicSupport().getPowerOutput();
        if (level > 9) {
            level = 9;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DIGITS[level]);

        ModelBuilder.FaceRotation rotation = ModelBuilder.FaceRotation.values()[logicFacing.getRotationStep()];
        RenderHelper.renderNorthSouthQuad(matrixStack, builder, sprite, rotation, .73f);

        matrixStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(MonitorModule.TYPE_POWER_LEVEL.get(), PowerLevelRenderer::new);
    }

}
