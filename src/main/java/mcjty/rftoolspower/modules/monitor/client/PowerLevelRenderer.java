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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import mcjty.lib.datagen.model.BlockModelBuilder;

public class PowerLevelRenderer implements BlockEntityRenderer<PowerLevelTileEntity, PowerLevelRenderer.State> {

    public static final Identifier[] DIGITS = new Identifier[] {
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_0"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_2"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_3"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_4"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_5"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_6"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_7"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_8"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/monitor/powerlevel_9")
    };

    public PowerLevelRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        LogicFacing facing;
        TextureAtlasSprite sprite;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(PowerLevelTileEntity te, State state, float partialTicks,
            net.minecraft.world.phys.Vec3 camera, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, camera, overlay);
        state.facing = te.getBlockState().getValue(LogicSlabBlock.LOGIC_FACING);
        int digit = Math.clamp(te.getLogicSupport().getPowerOutput(), 0, 9);
        state.sprite = mcjty.lib.client.AbstractDynamicBakedModel.getTexture(DIGITS[digit]);
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector,
            net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        pose.pushPose();
        RenderHelper.adjustTransformToDirection(pose, state.facing.getSide());
        var rotation = RenderHelper.FaceRotation.values()[state.facing.getRotationStep()];
        collector.submitCustomGeometry(pose, net.minecraft.client.renderer.rendertype.RenderTypes.cutoutMovingBlock(), (captured, builder) -> {
            PoseStack copy = new PoseStack();
            copy.last().pose().set(captured.pose());
            RenderHelper.renderNorthSouthQuad(copy, builder, state.sprite, rotation, .73f);
        });
        pose.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(MonitorModule.POWER_LEVEL.be().get(), PowerLevelRenderer::new);
    }

}
