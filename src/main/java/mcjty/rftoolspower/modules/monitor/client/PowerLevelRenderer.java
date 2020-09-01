package mcjty.rftoolspower.modules.monitor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.varia.LogicFacing;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class PowerLevelRenderer extends TileEntityRenderer<PowerLevelTileEntity> {

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

    public PowerLevelRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PowerLevelTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        IVertexBuilder builder = buffer.getBuffer(RenderType.getCutout());

        BlockState state = te.getWorld().getBlockState(te.getPos());
        Block block = state.getBlock();
        if (!(block instanceof LogicSlabBlock)) {
            return;
        }

        LogicFacing logicFacing = state.get(LogicSlabBlock.LOGIC_FACING);
        Direction facing = logicFacing.getSide();

        RenderHelper.adjustTransformToDirection(matrixStack, facing);

        int level = te.getPowerOutput();
        if (level > 9) {
            level = 9;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(DIGITS[level]);
        Matrix4f matrix = matrixStack.getLast().getMatrix();

        ModelBuilder.FaceRotation rotation = ModelBuilder.FaceRotation.values()[logicFacing.getRotationStep()];
        RenderHelper.renderNorthSouthQuad(builder, matrix, sprite, rotation, .73f);

        matrixStack.pop();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(MonitorModule.TYPE_POWER_LEVEL.get(), PowerLevelRenderer::new);
    }

}
