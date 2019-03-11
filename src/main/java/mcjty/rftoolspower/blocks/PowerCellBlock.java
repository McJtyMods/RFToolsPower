package mcjty.rftoolspower.blocks;

import mcjty.lib.McJtyLib;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.config.ConfigSetup;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PowerCellBlock extends GenericBlock<PowerCellTileEntity, EmptyContainer> implements INBTPreservingIngredient {

    public static final UnlistedPropertySideType NORTH = new UnlistedPropertySideType("north");
    public static final UnlistedPropertySideType SOUTH = new UnlistedPropertySideType("south");
    public static final UnlistedPropertySideType WEST = new UnlistedPropertySideType("west");
    public static final UnlistedPropertySideType EAST = new UnlistedPropertySideType("east");
    public static final UnlistedPropertySideType UP = new UnlistedPropertySideType("up");
    public static final UnlistedPropertySideType DOWN = new UnlistedPropertySideType("down");
    public static final UnlistedPropertySideTier TIER = new UnlistedPropertySideTier("tier");

    public static PropertyBool UPPER = PropertyBool.create("upper");
    public static PropertyBool LOWER = PropertyBool.create("lower");

    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    public PowerCellBlock(String name, Class<? extends PowerCellTileEntity> clazz) {
        super(RFToolsPower.instance, Material.IRON, clazz, EmptyContainer::new, name, false);
        setCreativeTab(RFToolsPower.setup.getTab());
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    public void initModel() {
        ResourceLocation name = getRegistryName();
        McJtyLib.proxy.initCustomItemModel(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(name.getResourceDomain(), name.getResourcePath()+"item"), "inventory"));
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        McJtyLib.proxy.initStateMapper(this, GenericCellBakedModel.modelCell);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag flag) {
        super.addInformation(itemStack, player, list, flag);

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            list.add(TextFormatting.BLUE + "Energy: " + TextFormatting.YELLOW + tagCompound.getLong("energy"));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            long totpower = 0;
            if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.cell1Block)) {
                totpower = ConfigSetup.TIER1_MAXRF;
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.cell2Block)) {
                totpower = ConfigSetup.TIER2_MAXRF;
            } else if (itemStack.getItem() == Item.getItemFromBlock(ModBlocks.cell3Block)) {
                totpower = ConfigSetup.TIER3_MAXRF;
            }
            list.add(TextFormatting.WHITE + "This block can store power (" + totpower + " RF)");
            list.add(TextFormatting.WHITE + "and can be combined with other cells to form a");
            list.add(TextFormatting.WHITE + "big multiblock");
            list.add(TextFormatting.WHITE + "Right click with a wrench to toggle");
            list.add(TextFormatting.WHITE + "input/output mode for a side");
        } else {
            list.add(TextFormatting.WHITE + RFToolsPower.SHIFT_MESSAGE);
        }
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
            long rfPerTick = powercell.getRfPerTickReal();

            if (powercell.getNetwork().isValid()) {
                probeInfo.text(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t");
                PowerCellTileEntity.Mode powermode = powercell.getMode(data.getSideHit());
                if (powermode == PowerCellTileEntity.Mode.MODE_INPUT) {
                    probeInfo.text(TextFormatting.YELLOW + "Side: input");
                } else if (powermode == PowerCellTileEntity.Mode.MODE_OUTPUT) {
                    probeInfo.text(TextFormatting.YELLOW + "Side: output");
                }
            } else {
                probeInfo.text(TextStyleClass.ERROR + "Too many blocks in network (max " + ConfigSetup.NETWORK_MAX + ")!");
            }

            int networkId = powercell.getNetwork().getNetworkId();
            if (mode == ProbeMode.DEBUG) {
                probeInfo.text(TextStyleClass.LABEL + "Network ID: " + TextStyleClass.INFO + networkId);
            }
            if (mode == ProbeMode.EXTENDED) {
                probeInfo.text(TextStyleClass.LABEL + "Local Energy: " + TextStyleClass.INFO + powercell.getLocalEnergy());
            }
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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                long energy = stack.hasTagCompound() ? stack.getTagCompound().getLong("energy") : 0;
                powercell.setLocalEnergy(energy);
                powercell.getNetwork();   // Force a rebuild of the network
                powercell.markDirtyQuick();
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess access, BlockPos pos, IBlockState metadata, int fortune) {
        super.getDrops(drops, access, pos, metadata, fortune);
        TileEntity te = access.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity powercell = (PowerCellTileEntity) te;
            powercell.redistributeNetwork();
            long energy = powercell.getLocalEnergy();
            if (!drops.isEmpty()) {
                NBTTagCompound tagCompound = drops.get(0).getTagCompound();
                if (tagCompound == null) {
                    tagCompound = new NBTTagCompound();
                    drops.get(0).setTagCompound(tagCompound);
                }
                tagCompound.setLong("energy", energy);
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powercell = (PowerCellTileEntity) te;
                if (powercell.getNetwork() != null) {
                    powercell.dismantleNetwork(powercell.getNetwork());
                }
            }
        }
        super.breakBlock(world, pos, state);
        if (!world.isRemote) {
            IBlockState stateUp = world.getBlockState(pos.up());
            if (stateUp.getBlock() == ModBlocks.cell1Block) {
                world.notifyBlockUpdate(pos.up(), stateUp, stateUp, 3);
            }
            IBlockState stateDown = world.getBlockState(pos.down());
            if (stateDown.getBlock() == ModBlocks.cell1Block) {
                world.notifyBlockUpdate(pos.down(), stateDown, stateDown, 3);
            }
        }
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof PowerCellTileEntity) {
                PowerCellTileEntity powerCellTileEntity = (PowerCellTileEntity) te;
                powerCellTileEntity.toggleMode(side);
            }
        }
        return true;
    }



    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(UPPER, world.getBlockState(pos.up()).getBlock() == this)
                .withProperty(LOWER, world.getBlockState(pos.down()).getBlock() == this);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        IProperty[] listedProperties = new IProperty[]{UPPER, LOWER};
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{NORTH, SOUTH, WEST, EAST, UP, DOWN, TIER};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getStateInternal(state, world, pos);
    }

    public IBlockState getStateInternal(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        boolean upper = Boolean.TRUE.equals(state.getValue(UPPER));
        boolean lower = Boolean.TRUE.equals(state.getValue(LOWER));

        SideType north = getSideType(world, pos, EnumFacing.NORTH, upper, lower);
        SideType south = getSideType(world, pos, EnumFacing.SOUTH, upper, lower);
        SideType west = getSideType(world, pos, EnumFacing.WEST, upper, lower);
        SideType east = getSideType(world, pos, EnumFacing.EAST, upper, lower);
        SideType up = getSideType(world, pos, EnumFacing.UP, upper, lower);
        SideType down = getSideType(world, pos, EnumFacing.DOWN, upper, lower);

        Tier tier = Tier.TIER1;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            tier = ((PowerCellTileEntity) te).getTier();
        }

        return extendedBlockState
                .withProperty(NORTH, north)
                .withProperty(SOUTH, south)
                .withProperty(WEST, west)
                .withProperty(EAST, east)
                .withProperty(UP, up)
                .withProperty(DOWN, down)
                .withProperty(TIER, tier);
    }

    protected SideType getSideType(IBlockAccess world, BlockPos pos, EnumFacing facing, boolean upper, boolean lower) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PowerCellTileEntity) {
            PowerCellTileEntity.Mode mode = ((PowerCellTileEntity) te).getMode(facing);
            switch (mode) {
                case MODE_NONE:
                    if (upper && lower) {
                        return SideType.MIDDLE_NONE;
                    } else if (upper) {
                        return SideType.LOWER_NONE;
                    } else if (lower) {
                        return SideType.UPPER_NONE;
                    } else {
                        return SideType.BOTH_NONE;
                    }
                case MODE_INPUT:
                    if (upper && lower) {
                        return SideType.MIDDLE_INPUT;
                    } else if (upper) {
                        return SideType.LOWER_INPUT;
                    } else if (lower) {
                        return SideType.UPPER_INPUT;
                    } else {
                        return SideType.BOTH_INPUT;
                    }
                case MODE_OUTPUT:
                    if (upper && lower) {
                        return SideType.MIDDLE_OUTPUT;
                    } else if (upper) {
                        return SideType.LOWER_OUTPUT;
                    } else if (lower) {
                        return SideType.UPPER_OUTPUT;
                    } else {
                        return SideType.BOTH_OUTPUT;
                    }
                default:
                    return SideType.BOTH_NONE;
            }
        } else {
            if (upper && lower) {
                return SideType.MIDDLE_NONE;
            } else if (upper) {
                return SideType.LOWER_NONE;
            } else if (lower) {
                return SideType.UPPER_NONE;
            } else {
                return SideType.BOTH_NONE;
            }
        }

    }

}
