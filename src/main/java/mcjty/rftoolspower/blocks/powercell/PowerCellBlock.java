package mcjty.rftoolspower.blocks.powercell;

import com.google.common.collect.Lists;
import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.config.PowerCellConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class PowerCellBlock extends BaseBlock implements INBTPreservingIngredient {

    public static BooleanProperty UPPER = BooleanProperty.create("upper");
    public static BooleanProperty LOWER = BooleanProperty.create("lower");

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    public PowerCellBlock(Tier tier) {
        super("cell" + tier.getSuffix(), new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .tileEntitySupplier(() -> new PowerCellTileEntity(tier)));
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable IBlockReader player, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, player, list, flag);

        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            list.add(new StringTextComponent(TextFormatting.BLUE + "Energy: " + TextFormatting.YELLOW + tagCompound.getLong("Energy")));
        }

        if (McJtyLib.proxy.isShiftKeyDown()) {
            long totpower = 0;
            if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL1)) {
                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER1_MAXRF.get());
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL2)) {
                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER2_MAXRF.get());
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL3)) {
                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER3_MAXRF.get());
            }
            list.add(new StringTextComponent(TextFormatting.WHITE + "This block can store power (" + totpower + " RF)"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "and can be combined with other cells to form a"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "big multiblock"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "Right click with a wrench to toggle"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "input/output mode for a side"));
        } else {
            list.add(new StringTextComponent(TextFormatting.WHITE + RFToolsPower.SHIFT_MESSAGE));
        }
    }


//    private static long lastTime = 0;
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    @Optional.Method(modid = "waila")
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        super.getWailaBody(itemStack, currenttip, accessor, config);
//        TileEntity tileEntity = accessor.getTileEntity();
//        if (tileEntity instanceof PowerCellTileEntity) {
//            PowerCellTileEntity powerCellTileEntity = (PowerCellTileEntity) tileEntity;
//            if (System.currentTimeMillis() - lastTime > 250) {
//                lastTime = System.currentTimeMillis();
////                RFToolsMessages.INSTANCE.sendToServer(new PacketGetInfoFromServer(RFTools.MODID, new PowerCellInfoPacketServer(powerCellTileEntity)));
//            }
////            currenttip.add(TextFormatting.GREEN + "Energy: " + PowerCellInfoPacketClient.tooltipEnergy + "/" + total + " RF (" +
////                    PowerCellInfoPacketClient.tooltipRfPerTick + " RF/t)");
//        }
//        return currenttip;
//    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                long energy = stack.hasTag() ? stack.getTag().getLong("energy") : 0;
                powercell.setLocalEnergy(energy);
                powercell.getNetwork();   // Force a rebuild of the network
                powercell.markDirtyQuick();
            }
        }
    }

//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, BlockState metadata, int fortune) {
//        super.getDrops(drops, access, pos, metadata, fortune);
//        TileEntity te = access.getTileEntity(pos);
//        if (te instanceof PowerCellTileEntity) {
//            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
//            powercell.redistributeNetwork();
//            long energy = powercell.getLocalEnergy();
//            if (!drops.isEmpty()) {
//                CompoundNBT tagCompound = drops.get(0).getTagCompound();
//                if (tagCompound == null) {
//                    tagCompound = new CompoundNBT();
//                    drops.get(0).setTagCompound(tagCompound);
//                }
//                tagCompound.setLong("energy", energy);
//            }
//        }
//    }


    @Override
    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powerCellTileEntity = (PowerCellTileEntity) te;
                powerCellTileEntity.toggleMode(side);
            }
        }
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        return state.with(UPPER, world.getBlockState(pos.up()).getBlock() == this)
                .with(LOWER, world.getBlockState(pos.down()).getBlock() == this);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        if (facing == Direction.UP) {
            return stateIn.with(UPPER, facingState.getBlock() == this);
        }
        if (facing == Direction.DOWN) {
            return stateIn.with(LOWER, facingState.getBlock() == this);
        }
        return stateIn;
    }

    //    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        return state.with(UPPER, world.getBlockState(pos.up()).getBlock() == this)
//                .with(LOWER, world.getBlockState(pos.down()).getBlock() == this);
//    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(UPPER, LOWER);
    }

    @Override
    @Nonnull
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Lists.newArrayList("m0", "m1", "m2", "m3", "m4", "m5", "m6", "local");
    }
}
