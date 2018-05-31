package mcjty.rftoolspower.blocks;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class InformationScreenBlock extends GenericBlock<InformationScreenTileEntity, EmptyContainer> {

    public InformationScreenBlock() {
        super(RFToolsPower.instance, Material.IRON, InformationScreenTileEntity.class, EmptyContainer::new, "information_screen", false);
        setCreativeTab(RFToolsPower.tabRfToolsPower);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(InformationScreenTileEntity.class, new InformationScreenRenderer());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag flag) {
        super.addInformation(itemStack, player, list, flag);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "Place this block on a powercell");
            list.add(TextFormatting.WHITE + "to display information about that cell");
            list.add(TextFormatting.WHITE + "Right click with a wrench to change");
            list.add(TextFormatting.WHITE + "modes");
            list.add(TextFormatting.GRAY + "Bonus: this block can display");
            list.add(TextFormatting.GRAY + "power information about any block");
            list.add(TextFormatting.GRAY + "that supports RF/FE");
        } else {
            list.add(TextFormatting.WHITE + RFToolsPower.SHIFT_MESSAGE);
        }
    }

    public static final AxisAlignedBB BLOCK_NORTH = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.05F);
    public static final AxisAlignedBB BLOCK_SOUTH = new AxisAlignedBB(0.0F, 0.0F, 0.95F, 1.0F, 1.0F, 1.0F);
    public static final AxisAlignedBB BLOCK_WEST = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.05F, 1.0F, 1.0F);
    public static final AxisAlignedBB BLOCK_EAST = new AxisAlignedBB(0.95F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (!(blockState.getBlock() instanceof InformationScreenBlock)) {
            return BLOCK_NORTH;
        }
        EnumFacing side = OrientationTools.getOrientationHoriz(state);
        switch (side) {
            case NORTH:
                return BLOCK_SOUTH;
            case EAST:
                return BLOCK_WEST;
            case WEST:
                return BLOCK_EAST;
            default:
                return BLOCK_NORTH;
        }
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof InformationScreenTileEntity) {
                InformationScreenTileEntity monitor = (InformationScreenTileEntity) te;
                monitor.toggleMode();
            }
        }
        return true;
    }



    @Override
    public RotationType getRotationType() {
        return RotationType.HORIZROTATION;
    }


    @Override
    public int getGuiID() {
        return -1;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Indicate if a material is a normal solid opaque cube
     */
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    /**
     * @return true if the state occupies all of its 1x1x1 cube
     */
    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
