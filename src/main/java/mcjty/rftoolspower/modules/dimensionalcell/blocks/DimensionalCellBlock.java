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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DimensionalCellBlock extends BaseBlock implements INBTPreservingIngredient {

    public static final EnumProperty<DimensionalCellTileEntity.Mode> NORTH = EnumProperty.create("north", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> SOUTH = EnumProperty.create("south", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> WEST = EnumProperty.create("west", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> EAST = EnumProperty.create("east", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> UP = EnumProperty.create("up", DimensionalCellTileEntity.Mode.class);
    public static final EnumProperty<DimensionalCellTileEntity.Mode> DOWN = EnumProperty.create("down", DimensionalCellTileEntity.Mode.class);

    private final DimensionalCellType type;
    private static final VoxelShape RENDER_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    public DimensionalCellType getType() {
        return type;
    }

    @Override
    @Nonnull
    public VoxelShape getOcclusionShape(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return RENDER_SHAPE;
    }

    public static DimensionalCellType getType(Block block) {
        if (block instanceof DimensionalCellBlock) {
            return ((DimensionalCellBlock) block).getType();
        } else {
            return DimensionalCellType.UNKNOWN;
        }
    }

    public DimensionalCellBlock(DimensionalCellType type, BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
        super(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of(Material.METAL)
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
    protected boolean wrenchSneakSelect(Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            SmartWrenchMode currentMode = SmartWrenchItem.getCurrentMode(player.getItemInHand(InteractionHand.MAIN_HAND));
            if (currentMode == SmartWrenchMode.MODE_SELECT) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof DimensionalCellTileEntity) {
                    DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) te;
                    DimensionalCellTileEntity.dumpNetwork(player, dimensionalCellTileEntity);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) te;
                dimensionalCellTileEntity.toggleMode(side);
            }
        }
        return true;
    }

    private static int getEnergy(ItemStack stack) {
        return NBTTools.getInfoNBT(stack, CompoundTag::getInt, "energy", 0);
    }

    private static void setEnergy(ItemStack stack, int energy) {
        NBTTools.setInfoNBT(stack, CompoundTag::putInt, "energy", energy);
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
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
                    //                        world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
//@todo
//                        dimensionalCellTileEntity.updateState();
                }
            }
        } else if (!stack.hasTag() && !world.isClientSide) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getBlockEntity(pos);
            if (dimensionalCellTileEntity != null && type.isCreative()) {
                dimensionalCellTileEntity.setAllOutput();
            }
        }

        if (placer instanceof Player) {
            // @todo achievements
//            Achievements.trigger((EntityPlayer) placer, Achievements.storeThePower);
        }
    }

    // @todo 1.14 is this the right way? Perhaps not
    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public List<ItemStack> getDrops(@Nonnull BlockState state, LootContext.Builder builder) {
        System.out.println("DimensionalCellBlock.getDrops");
        Level world = builder.getLevel();
        Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(new BlockPos(pos.x, pos.y, pos.z));
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
    public void wasExploded(Level world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (!world.isClientSide) {
            BlockEntity te = world.getBlockEntity(pos);
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
    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isClientSide) {
            if (state.getBlock() != newstate.getBlock()) {
                BlockEntity te = world.getBlockEntity(pos);
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
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }
}
