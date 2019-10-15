package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.api.smartwrench.SmartWrenchMode;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.rftoolsbase.items.SmartWrenchItem;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellNetwork;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

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

    private LazyOptional<IModuleSupport> moduleSupportHandler = LazyOptional.of(() -> new DefaultModuleSupport(DimensionalCellContainer.SLOT_CARD) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return false;
            // @todo 1.14 return (itemStack.getItem() instanceof PowerCellCardItem);
        }
    });

    private static long lastTime = 0;

    public DimensionalCellBlock(String name, Supplier<TileEntity> supplier) {
        super(name, new BlockBuilder()
                .tileEntitySupplier(supplier)
                .infusable());
    }

    // @todo 1.14
//    @Override
//    public void initModel() {
//        // If a block and item model have the same name, the block model wins even when rendering the item,
//        // due to MinecraftForge/MinecraftForge#4898. Since we have powercell blocks and items with different
//        // models, append "_item" to powercell items.
//        ResourceLocation blockRegistryName = getRegistryName();
//        ResourceLocation itemRegistryName = new ResourceLocation(blockRegistryName.getResourceDomain(), blockRegistryName.getResourcePath() + "_item");
//        McJtyLib.proxy.initCustomItemModel(Item.getItemFromBlock(this), 0, new ModelResourceLocation(itemRegistryName, "inventory"));
//    }

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public Collection<String> getTagsToPreserve() {
        return Collections.emptyList(); // @todo 1.14
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag advanced) {
        super.addInformation(itemStack, world, list, advanced);

        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Energy: " + tagCompound.getInt("energy")));
        }

        // @todo move to lang file
        if (McJtyLib.proxy.isShiftKeyDown()) {
            int totpower = DimensionalCellConfiguration.rfPerNormalCell.get() * getPowerFactor() / DimensionalCellConfiguration.simpleFactor.get();
            list.add(new StringTextComponent(TextFormatting.WHITE + "This block can store power (" + totpower + " RF)"));
            list.add(new StringTextComponent(TextFormatting.WHITE + "Optionally in a big multi dimensional structure"));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Infusing bonus: reduced long distance power"));
            list.add(new StringTextComponent(TextFormatting.YELLOW + "extraction cost and increased RF/tick output"));
        } else {
            list.add(new StringTextComponent(TextFormatting.WHITE + RFToolsPower.SHIFT_MESSAGE));
        }
    }

    private boolean isAdvanced() {
        return isAdvanced(this);
    }

    private boolean isSimple() {
        return isSimple(this);
    }

    public static boolean isAdvanced(Block block) {
        return block == DimensionalCellSetup.advancedDimensionalCellBlock || block == DimensionalCellSetup.creativeDimensionalCellBlock;
    }

    public static boolean isSimple(Block block) {
        return block == DimensionalCellSetup.simpleDimensionalCellBlock;
    }

    public static boolean isCreative(Block block) {
        return block == DimensionalCellSetup.creativeDimensionalCellBlock;
    }

    private int getPowerFactor() {
        if (isSimple()) {
            return 1;
        }
        return isAdvanced() ? (DimensionalCellConfiguration.advancedFactor.get() * DimensionalCellConfiguration.simpleFactor.get()) : DimensionalCellConfiguration.simpleFactor.get();
    }

    // @todo 1.14
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
                    int energy = stack.getTag().getInt("energy");
                    DimensionalCellNetwork dimensionalCellNetwork = DimensionalCellNetwork.getChannels();
                    DimensionalCellNetwork.Network network = dimensionalCellNetwork.getChannel(networkId);
                    network.receiveEnergy(energy);
                    Block block = world.getBlockState(pos).getBlock();
                    network.add(world, dimensionalCellTileEntity.getGlobalPos(), isAdvanced(block), isSimple(block));
                    dimensionalCellNetwork.save();
                }
            }
        } else if (!stack.hasTag() && !world.isRemote) {
            DimensionalCellTileEntity dimensionalCellTileEntity = (DimensionalCellTileEntity) world.getTileEntity(pos);
            if (dimensionalCellTileEntity != null && isCreative(this)) {
                dimensionalCellTileEntity.setAllOutput();
            }
        }

        if (placer instanceof PlayerEntity) {
            // @todo achievements
//            Achievements.trigger((EntityPlayer) placer, Achievements.storeThePower);
        }
    }

    // @todo 1.14
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess blockAccess, BlockPos pos, IBlockState state, int fortune) {
//        World world = (World) blockAccess;
//        super.getDrops(drops, world, pos, state, fortune);
//        if (!world.isRemote) {
//            TileEntity te = world.getTileEntity(pos);
//            if (te instanceof PowerCellTileEntity) {
//                PowerCellNetwork.Network network = ((PowerCellTileEntity) te).getNetwork();
//                if (network != null) {
//                    int energy = network.getEnergySingleBlock(isAdvanced(), isSimple());
//                    if (!drops.isEmpty()) {
//                        NBTTagCompound tagCompound = drops.get(0).getTagCompound();
//                        if (tagCompound == null) {
//                            tagCompound = new NBTTagCompound();
//                            drops.get(0).setTagCompound(tagCompound);
//                        }
//                        tagCompound.setInteger("energy", energy);
//                    }
//                }
//            }
//        }
//    }


    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity cellTileEntity = (DimensionalCellTileEntity) te;
                DimensionalCellNetwork.Network network = cellTileEntity.getNetwork();
                if (network != null) {
                    int a = network.extractEnergySingleBlock(isAdvanced(), isSimple());
                    Block block = world.getBlockState(pos).getBlock();
                    network.remove(world, cellTileEntity.getGlobalPos(), DimensionalCellBlock.isAdvanced(block), DimensionalCellBlock.isSimple(block));
                    DimensionalCellNetwork.getChannels().save();
                    cellTileEntity.setNetworkId(-1);
                }
            }
        }
        super.onExplosionDestroy(world, pos, explosion);
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof DimensionalCellTileEntity) {
                DimensionalCellTileEntity cellTileEntity = (DimensionalCellTileEntity) te;
                DimensionalCellNetwork.Network network = cellTileEntity.getNetwork();
                if (network != null) {
                    int a = network.extractEnergySingleBlock(isAdvanced(), isSimple());
                    Block block = world.getBlockState(pos).getBlock();
                    network.remove(world, cellTileEntity.getGlobalPos(), DimensionalCellBlock.isAdvanced(block), DimensionalCellBlock.isSimple(block));
                    DimensionalCellNetwork.getChannels().save();
                }
            }
        }
        super.onReplaced(state, world, pos, newstate, isMoving);
    }

    // @todo 1.14
//    @Override
//    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
//        TileEntity tileEntity = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
//        if (tileEntity instanceof PowerCellTileEntity) {
//            PowerCellTileEntity te = (PowerCellTileEntity) tileEntity;
//            PowerCellTileEntity.Mode north = te.getMode(EnumFacing.NORTH);
//            PowerCellTileEntity.Mode south = te.getMode(EnumFacing.SOUTH);
//            PowerCellTileEntity.Mode west = te.getMode(EnumFacing.WEST);
//            PowerCellTileEntity.Mode east = te.getMode(EnumFacing.EAST);
//            PowerCellTileEntity.Mode up = te.getMode(EnumFacing.UP);
//            PowerCellTileEntity.Mode down = te.getMode(EnumFacing.DOWN);
//            return state.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east).withProperty(UP, up).withProperty(DOWN, down);
//        }
//        return state.withProperty(NORTH, PowerCellTileEntity.Mode.MODE_NONE)
//                .withProperty(SOUTH, PowerCellTileEntity.Mode.MODE_NONE)
//                .withProperty(WEST, PowerCellTileEntity.Mode.MODE_NONE)
//                .withProperty(EAST, PowerCellTileEntity.Mode.MODE_NONE)
//                .withProperty(UP, PowerCellTileEntity.Mode.MODE_NONE)
//                .withProperty(DOWN, PowerCellTileEntity.Mode.MODE_NONE);
//    }


    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        // @todo 1.14, add this to the blockstate?
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }


    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }
    // @todo 1.14
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
//        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            return itemHandler.cast();
//        }
//        if (cap == CapabilityEnergy.ENERGY) {
//            return energyHandler.cast();
//        }
//        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
//            return screenHandler.cast();
//        }
//        if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
//            return infusableHandler.cast();
//        }
//        if (cap == CapabilityModuleSupport.MODULE_CAPABILITY) {
//            return moduleSupportHandler.cast();
//        }
//        return super.getCapability(cap, facing);
//    }

}
