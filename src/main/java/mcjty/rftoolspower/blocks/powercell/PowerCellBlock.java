package mcjty.rftoolspower.blocks.powercell;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.blocks.ModBlocks;
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
                .tileEntitySupplier(() -> new PowerCellTileEntity(tier)));
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable IBlockReader player, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, player, list, flag);

        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            list.add(new StringTextComponent(TextFormatting.BLUE + "Energy: " + TextFormatting.YELLOW + tagCompound.getLong("energy")));
        }

        if (McJtyLib.proxy.isShiftKeyDown()) {
            long totpower = 0;
            if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL1)) {
                totpower = PowerCellConfig.TIER1_MAXRF.get();
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL2)) {
                totpower = PowerCellConfig.TIER2_MAXRF.get();
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.CELL3)) {
                totpower = PowerCellConfig.TIER3_MAXRF.get();
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

//    @Override
//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, BlockState blockState, IProbeHitData data) {
//        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
//        TileEntity te = world.getTileEntity(data.getPos());
//        if (te instanceof PowerCellTileEntity) {
//            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
//            long rfPerTick = powercell.getRfPerTickReal();
//
//            if (powercell.getNetwork().isValid()) {
//                probeInfo.text(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t");
//                PowerCellTileEntity.Mode powermode = powercell.getMode(data.getSideHit());
//                if (powermode == PowerCellTileEntity.Mode.MODE_INPUT) {
//                    probeInfo.text(TextFormatting.YELLOW + "Side: input");
//                } else if (powermode == PowerCellTileEntity.Mode.MODE_OUTPUT) {
//                    probeInfo.text(TextFormatting.YELLOW + "Side: output");
//                }
//            } else {
//                probeInfo.text(TextStyleClass.ERROR + "Too many blocks in network (max " + ConfigSetup.NETWORK_MAX + ")!");
//            }
//
//            int networkId = powercell.getNetwork().getNetworkId();
//            if (mode == ProbeMode.DEBUG) {
//                probeInfo.text(TextStyleClass.LABEL + "Network ID: " + TextStyleClass.INFO + networkId);
//            }
//            if (mode == ProbeMode.EXTENDED) {
//                probeInfo.text(TextStyleClass.LABEL + "Local Energy: " + TextStyleClass.INFO + powercell.getLocalEnergy());
//            }
//        }
//    }

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
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                if (powercell.getNetwork() != null) {
                    powercell.dismantleNetwork(powercell.getNetwork());
                }
            }
        }
        super.onReplaced(state, world, pos, newstate, isMoving);
        if (!world.isRemote) {
            BlockState stateUp = world.getBlockState(pos.up());
            if (stateUp.getBlock() instanceof PowerCellBlock) {
                world.notifyBlockUpdate(pos.up(), stateUp, stateUp, 3);
            }
            BlockState stateDown = world.getBlockState(pos.down());
            if (stateDown.getBlock() instanceof PowerCellBlock) {
                world.notifyBlockUpdate(pos.down(), stateDown, stateDown, 3);
            }
        }
    }


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

}
