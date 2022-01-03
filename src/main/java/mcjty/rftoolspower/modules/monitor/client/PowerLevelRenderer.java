package mcjty.rftoolspower.modules.monitor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.ClientRegistry;

public class PowerLevelRenderer extends BlockEntityRenderer<PowerLevelTileEntity> {

    public static final ResourceLocation[] DIGITS = new ResourceLocation[] {
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_0"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_1"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_2"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_3"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_4"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_5"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_6"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_7"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_8"),
            new ResourceLocation(RFToolsPower.MODID, "block/monitor/powerlevel_9")
    };

    public PowerLevelRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PowerLevelTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.pushPose();
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());

        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        Block block = state.getBlock();
        if (!(block instanceof LogicSlabBlock)) {
            return;
        }

        LogicFacing logicFacing = state.getValue(LogicSlabBlock.LOGIC_FACING);
        Direction facing = logicFacing.getSide();

        RenderHelper.adjustTransformToDirection(matrixStack, facing);

        int level = te.getLogicSupport().getPowerOutput();
        if (level > 9) {
            level = 9;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DIGITS[level]);
        Matrix4f matrix = matrixStack.last().pose();

        ModelBuilder.FaceRotation rotation = ModelBuilder.FaceRotation.values()[logicFacing.getRotationStep()];
        RenderHelper.renderNorthSouthQuad(builder, matrix, sprite, rotation, .73f);

        matrixStack.popPose();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(MonitorModule.TYPE_POWER_LEVEL.get(), PowerLevelRenderer::new);
    }

}
