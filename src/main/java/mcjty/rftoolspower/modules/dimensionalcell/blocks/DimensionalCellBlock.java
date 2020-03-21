package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.rftoolsbase.modules.various.items.SmartWrenchItem;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


public class DimensionalCellBlock extends BaseBlock implements INBTPreservingIngredient {

    public static final EnumProperty<DimensionalCellTileEntity.Mode> NORTH = EnumProperty.create("north", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> SOUTH = EnumProperty.create("south", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> WEST = EnumProperty.create("west", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> EAST = EnumProperty.create("east", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> UP = EnumProperty.create("up", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> DOWN = EnumProperty.create("down", DimensionalCellTileEntity.Mode.class);

    private final DimensionalCellType type;
    private final static VoxelShape RENDER_SHAPE = VoxelShapes.create(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    @Override
    public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
        return false;
    }

    public DimensionalCellType getType() {
        return type;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return RENDER_SHAPE;
    }

    public static DimensionalCellType getType(Block block) {
        if (block instanceof DimensionalCellBlock) {
            return ((DimensionalCellBlock) block).getType();
        } else {
            return DimensionalCellType.UNKNOWN;
        }
    }

    public DimensionalCellBlock(DimensionalCellType type, Supplier<TileEntity> supplier) {
        super(new BlockBuilder()
                .tileEntitySupplier(supplier)
                .infusable()
                .info("message.rftoolspower.dimensional_cell")
                .infoParameter(stack -> stack.getTag() == null ? "<empty>" : String.valueOf(getEnergy(stack)))
                .infoExtended("message.rftoolspower.dimensional_cell_shift")
                .infoExtendedParameter(stack -> stack.getTag() == null ? "<empty>" : String.valueOf(getEnergy(stack)))
                .infoExtendedParameter(stack -> String.valueOf(DimensionalCellConfiguration.rfPerNormalCell.get() * getPowerFactor(type) / DimensionalCellConfiguration.simpleFactor.get()))
        );
        this.type = type;
    }


    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Collections.singleton("Info");
    }

    private static int getPowerFactor(DimensionalCellType type) {
        if (type.isSimple()) {
            return 1;
        }
        return type.isAdvanced() ? (DimensionalCellConfiguration.advancedFactor.get() * DimensionalCellConfiguration.simpleFactor.get()) : DimensionalCellConfiguration.simpleFactor.get();
    }

// @todo 1.14
//    private static long lastTime = 0;
//    @Override
//    @Optional.Method(modid = "theoneprobe")
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
//        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
//        TileEntity te = world.getTileEntity(data.getPos());
//        if (te instanceof PowerCellTileEntity) {
//            PowerCellTileEntity powerCellTileEntity = (PowerCellTileEntity) te;
//            int id = powerCellTileEntity.getNetworkId();
//            if (mode == ProbeMode.EXTENDED) {
//                if (id != -1) {
//                    probeInfo.text(TextFormatting.GREEN + "ID: " + new DecimalFormat("#.##").format(id));
//                } else {
//                    probeInfo.text(TextFormatting.GREEN + "Local storage!");
//                }
//            }
//
//            float costFactor = powerCellTileEntity.getCostFactor();
//            int rfPerTick = powerCellTileEntity.getRfPerTickPerSide();
//
//            probeInfo.text(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t");
//            PowerCellTileEntity.Mode powermode = powerCellTileEntity.getMode(data.getSideHit());
//            if (powermode == PowerCellTileEntity.Mode.MODE_INPUT) {
//                probeInfo.text(TextFormatting.YELLOW + "Side: input");
//            } else if (powermode == PowerCellTileEntity.Mode.MODE_OUTPUT) {
//                int cost = (int) ((costFactor - 1.0f) * 1000.0f);
//                probeInfo.text(TextFormatting.YELLOW + "Side: output (cost " + cost / 10 + "." + cost % 10 + "%)");
//            }
//            if (mode == ProbeMode.EXTENDED) {
//                int rfPerTickIn = powerCellTileEntity.getLastRfPerTickIn();
//                int rfPerTickOut = powerCellTileEntity.getLastRfPerTickOut();
//                probeInfo.text(TextFormatting.GREEN + "In:  " + rfPerTickIn + "RF/t");
//                probeInfo.text(TextFormatting.GREEN + "Out: " + rfPerTickOut + "RF/t");
//            }
//        }
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    @Optional.Method(modid = "waila")
//    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        super.getWailaBody(itemStack, currenttip, accessor, config);
//        TileEntity tileEntity = accessor.getTileEntity();
//        if (tileEntity instanceof PowerCellTileEntity) {
//            PowerCellTileEntity powercell = (PowerCellTileEntity) tileEntity;
//            int id = powercell.getNetworkId();
//            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
//                if (id != -1) {
//                    currenttip.add(TextFormatting.GREEN + "ID: " + new DecimalFormat("#.##").format(id));
//                } else {
//                    currenttip.add(TextFormatting.GREEN + "Local storage!");
//                }
//            }
//            if (System.currentTimeMillis() - lastTime > 250) {
//                lastTime = System.currentTimeMillis();
//                powercell.requestDataFromServer(RFTools.MODID, PowerCellTileEntity.CMD_GET_INFO, TypedMap.EMPTY);
//            }
//            long total = (PowerCellTileEntity.tooltipBlocks - PowerCellTileEntity.tooltipAdvancedBlocks - (long) PowerCellTileEntity.tooltipSimpleBlocks) * PowerCellConfiguration.rfPerNormalCell.get();
//            total += (long) PowerCellTileEntity.tooltipAdvancedBlocks * PowerCellConfiguration.rfPerNormalCell.get() * advancedFactor.get();
//            total += (long) PowerCellTileEntity.tooltipSimpleBlocks * PowerCellConfiguration.rfPerNormalCell.get() / PowerCellConfiguration.simpleFactor.get();
//            if (total > Integer.MAX_VALUE) {
//                total = Integer.MAX_VALUE;
//            }
//
//            currenttip.add(TextFormatting.GREEN + "Energy: " + PowerCellTileEntity.tooltipEnergy + "/" + total + " RF (" +
//                    PowerCellTileEntity.tooltipRfPerTick + " RF/t)");
//            PowerCellTileEntity.Mode mode = powercell.getMode(accessor.getSide());
//            if (mode == PowerCellTileEntity.Mode.MODE_INPUT) {
//                currenttip.add(TextFormatting.YELLOW + "Side: input");
//            } else if (mode == PowerCellTileEntity.Mode.MODE_OUTPUT) {
//                int cost = (int) ((PowerCellTileEntity.tooltipCostFactor - 1.0f) * 1000.0f);
//                currenttip.add(TextFormatting.YELLOW + "Side: output (cost " + cost / 10 + "." + cost % 10 + "%)");
//            }
//        }
//        return currenttip;
//    }


    @Override
    protected boolean wrenchSneakSelect(World world, BlockPos pos, PlayerEntity player) {
        if (!world.isRemote) {
            SmartWrenchMode currentMode = SmartWrenchItem.getCurrentMode(player.getHeldItem(Hand.MAIN_HAND));
            if (currentMode == SmartWrenchMode.MODE_SELECT) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof DimensionalCellTileEntity) {
                    DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) te;
                    DimensionalCellTileEntity.dumpNetwork(player, dimensionalCellTileEntity);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, Direction side, PlayerEntity player) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) te;
                dimensionalCellTileEntity.toggleMode(side);
            }
        }
        return true;
    }

    private static int getEnergy(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        if (tag.contains("BlockEntityTag")) {
            tag = tag.getCompound("BlockEntityTag");
            if (tag.contains("Info")) {
                return tag.getCompound("Info").getInt("energy");
            }
        }
        return 0;
    }

    private static void setEnergy(ItemStack stack, int energy) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains("BlockEntityTag")) {
            tag.put("BlockEntityTag", new CompoundNBT());
        }
        tag = tag.getCompound("BlockEntityTag");
        if (!tag.contains("Info")) {
            tag.put("Info", new CompoundNBT());
        }
        tag.getCompound("Info").putInt("energy", energy);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTag() && !world.isRemote) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getTileEntity(pos);
            if (dimensionalCellTileEntity != null) {
                int networkId = dimensionalCellTileEntity.getNetworkId();
                if (networkId == -1) {
                    // No network, energy is already restored to the local block
                } else {
                    int energy = getEnergy(stack);
                    DimensionalCellNetwork dimensionalCellNetwork = DimensionalCellNetwork.get(world);
                    DimensionalCellNetwork.Network network = dimensionalCellNetwork.getChannel(networkId);
                    network.receiveEnergy(energy);
                    Block block = world.getBlockState(pos).getBlock();
                    network.add(world, dimensionalCellTileEntity.getGlobalPos(), getType(block));
                    dimensionalCellNetwork.save();
                    if (!world.isRemote) {
//                        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
//@todo
//                        dimensionalCellTileEntity.updateState();
                    }
                }
            }
        } else if (!stack.hasTag() && !world.isRemote) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getTileEntity(pos);
            if (dimensionalCellTileEntity != null && type.isCreative()) {
                dimensionalCellTileEntity.setAllOutput();
            }
        }

        if (placer instanceof PlayerEntity) {
            // @todo achievements
//            Achievements.trigger((EntityPlayer) placer, Achievements.storeThePower);
        }
    }

    // @todo 1.14 is this the right way? Perhaps not
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        System.out.println("DimensionalCellBlock.getDrops");
        World world = builder.getWorld();
        BlockPos pos = builder.get(LootParameters.POSITION);
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellNetwork.Network network = ((DimensionalCellTileEntity) te).getNetwork();
                if (network != null) {
                    int energy = network.getEnergySingleBlock(getType());
                    System.out.println("getDrops: energy = " + energy);
                    if (!drops.isEmpty()) {
                        setEnergy(drops.get(0), energy);
                    }
                }
            }
        }
        return drops;
    }


    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity cellTileEntity = (DimensionalCellTileEntity) te;
                DimensionalCellNetwork.Network network = cellTileEntity.getNetwork();
                if (network != null) {
                    int a = network.extractEnergySingleBlock(type);
                    Block block = world.getBlockState(pos).getBlock();
                    network.remove(world, cellTileEntity.getGlobalPos(), DimensionalCellBlock.getType(block));
                    DimensionalCellNetwork.get(world).save();
                    cellTileEntity.setNetworkId(-1);
                }
            }
        }
        super.onExplosionDestroy(world, pos, explosion);
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isRemote) {
            if (state.getBlock() != newstate.getBlock()) {
                System.out.println("DimensionalCellBlock.onReplaced");
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof DimensionalCellTileEntity) {
                    DimensionalCellTileEntity cellTileEntity = (DimensionalCellTileEntity) te;
                    DimensionalCellNetwork.Network network = cellTileEntity.getNetwork();
                    if (network != null) {
                        int a = network.extractEnergySingleBlock(type);
                        System.out.println("a = " + a);
                        Block block = world.getBlockState(pos).getBlock();
                        network.remove(world, cellTileEntity.getGlobalPos(), DimensionalCellBlock.getType(block));
                        DimensionalCellNetwork.get(world).save();
                    }
                }
            }
        }
        super.onReplaced(state, world, pos, newstate, isMoving);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }
}
