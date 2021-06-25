package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolsbase.modules.various.items.SmartWrenchItem;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.*;


import net.minecraft.block.AbstractBlock;

public class DimensionalCellBlock extends BaseBlock implements INBTPreservingIngredient {

    public static final EnumProperty<DimensionalCellTileEntity.Mode> NORTH = EnumProperty.create("north", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> SOUTH = EnumProperty.create("south", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> WEST = EnumProperty.create("west", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> EAST = EnumProperty.create("east", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> UP = EnumProperty.create("up", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> DOWN = EnumProperty.create("down", DimensionalCellTileEntity.Mode.class);

    private final DimensionalCellType type;
    private final static VoxelShape RENDER_SHAPE = VoxelShapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    public DimensionalCellType getType() {
        return type;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader reader, BlockPos pos) {
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
                .properties(AbstractBlock.Properties.of(Material.METAL)
                        .strength(2.0f)
                        .sound(SoundType.METAL)
                        .isRedstoneConductor((state, world, pos) -> false)
                )
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .tileEntitySupplier(supplier)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powerstorage/dimensionalcell"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> {
                            String energy = stack.getTag() == null ? "0" : String.valueOf(getEnergy(stack));
                            String max = String.valueOf(DimensionalCellConfiguration.rfPerNormalCell.get() * getPowerFactor(type) / DimensionalCellConfiguration.simpleFactor.get());
                            return energy + " (max " + max + " RF/FE)";
                        }))
        );
        this.type = type;
    }


    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Collections.singleton("BlockEntityTag");
    }

    private static int getPowerFactor(DimensionalCellType type) {
        if (type.isSimple()) {
            return 1;
        }
        return type.isAdvanced() ? (DimensionalCellConfiguration.advancedFactor.get() * DimensionalCellConfiguration.simpleFactor.get()) : DimensionalCellConfiguration.simpleFactor.get();
    }

    @Override
    protected boolean wrenchSneakSelect(World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClientSide) {
            SmartWrenchMode currentMode = SmartWrenchItem.getCurrentMode(player.getItemInHand(Hand.MAIN_HAND));
            if (currentMode == SmartWrenchMode.MODE_SELECT) {
                TileEntity te = world.getBlockEntity(pos);
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
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) te;
                dimensionalCellTileEntity.toggleMode(side);
            }
        }
        return true;
    }

    private static int getEnergy(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "energy", 0);
    }

    private static void setEnergy(ItemStack stack, int energy) {
        NBTTools.setInfoNBT(stack, CompoundNBT::putInt, "energy", energy);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTag() && !world.isClientSide) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getBlockEntity(pos);
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
                    if (!world.isClientSide) {
//                        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
//@todo
//                        dimensionalCellTileEntity.updateState();
                    }
                }
            }
        } else if (!stack.hasTag() && !world.isClientSide) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getBlockEntity(pos);
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
        World world = builder.getLevel();
        Vector3d pos = builder.getOptionalParameter(LootParameters.ORIGIN);
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(new BlockPos(pos.x, pos.y, pos.z));
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
    public void wasExploded(World world, BlockPos pos, Explosion explosion) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
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
        super.wasExploded(world, pos, explosion);
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            if (state.getBlock() != newstate.getBlock()) {
                TileEntity te = world.getBlockEntity(pos);
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
        super.onRemove(state, world, pos, newstate, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }
}
