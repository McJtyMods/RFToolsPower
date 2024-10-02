package mcjty.rftoolspower.modules.powercell.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import static mcjty.lib.builder.TooltipBuilder.*;

public class PowerCellBlock extends BaseBlock implements INBTPreservingIngredient {

    public static final BooleanProperty UPPER = BooleanProperty.create("upper");
    public static final BooleanProperty LOWER = BooleanProperty.create("lower");

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    public PowerCellBlock(Tier tier) {
        super(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(),
                        parameter("info", stack -> {
                            long power = getEnergy(stack);
                            long totpower = 0;
                            if (stack.getItem() == PowerCellModule.CELL1.item().get()) {
                                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER1_MAXRF.get());
                            } else if (stack.getItem() == PowerCellModule.CELL2.item().get()) {
                                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER2_MAXRF.get());
                            } else if (stack.getItem() == PowerCellModule.CELL3.item().get()) {
                                totpower = PowerCellTileEntity.safeCast(PowerCellConfig.TIER3_MAXRF.get());
                            }
                            return power + " (max " + totpower + " RF/FE)";
                        }))
                .tileEntitySupplier((pos, state) -> new PowerCellTileEntity(tier, pos, state)));
    }

    private static long getEnergy(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getLong, "energy", 0L);
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {

            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PowerCellTileEntity powercell) {
                long energy = getEnergy(stack);
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
    protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof PowerCellTileEntity powercell) {
                powercell.toggleMode(side);
            }
        }
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return state.setValue(UPPER, world.getBlockState(pos.above()).getBlock() == this)
                .setValue(LOWER, world.getBlockState(pos.below()).getBlock() == this);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockPos facingPos) {
        if (facing == Direction.UP) {
            return stateIn.setValue(UPPER, facingState.getBlock() == this);
        }
        if (facing == Direction.DOWN) {
            return stateIn.setValue(LOWER, facingState.getBlock() == this);
        }
        return stateIn;
    }

    //    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        return state.with(UPPER, world.getBlockState(pos.up()).getBlock() == this)
//                .with(LOWER, world.getBlockState(pos.down()).getBlock() == this);
//    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UPPER, LOWER);
    }

    @Override
    @Nonnull
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Collections.singleton("BlockEntityTag");
    }
}
