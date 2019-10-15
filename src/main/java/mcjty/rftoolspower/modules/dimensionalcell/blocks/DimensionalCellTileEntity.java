package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.api.module.DefaultModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.api.smartwrench.ISmartWrenchSelector;
import mcjty.lib.bindings.DefaultAction;
import mcjty.lib.bindings.IAction;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellNetwork;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock.*;

public class DimensionalCellTileEntity extends GenericTileEntity implements ITickableTileEntity, ISmartWrenchSelector {

    public static final String CMD_GET_INFO = "getInfo";
    public static final Key<Integer> PARAM_ENERGY = new Key<>("energy", Type.INTEGER);
    public static final Key<Integer> PARAM_BLOCKS = new Key<>("block", Type.INTEGER);
    public static final Key<Integer> PARAM_SIMPLEBLOCKS = new Key<>("simpleblocks", Type.INTEGER);
    public static final Key<Integer> PARAM_ADVANCEDBLOCKS = new Key<>("advancedblocks", Type.INTEGER);
    public static final Key<Long> PARAM_TOTAL_INSERTED = new Key<>("totalinserted", Type.LONG);
    public static final Key<Long> PARAM_TOTAL_EXTRACTED = new Key<>("totalextracted", Type.LONG);
    public static final Key<Integer> PARAM_RFPERTICK = new Key<>("rfpertick", Type.INTEGER);
    public static final Key<Double> PARAM_COSTFACTOR = new Key<>("costfactor", Type.DOUBLE);

    public static final String ACTION_SETNONE = "setNone";
    public static final String ACTION_SETINPUT = "setInput";
    public static final String ACTION_SETOUTPUT = "setOutput";
    public static final String ACTION_CLEARSTATS = "clearStats";

    // Client side for tooltip purposes
    public static int tooltipEnergy = 0;
    public static int tooltipBlocks = 0;
    public static int tooltipSimpleBlocks = 0;
    public static int tooltipAdvancedBlocks = 0;
    public static long tooltipInserted = 0;
    public static long tooltipExtracted = 0;
    public static int tooltipRfPerTick = 0;
    public static float tooltipCostFactor = 0;

    @Override
    public IAction[] getActions() {
        return new IAction[]{
                new DefaultAction(ACTION_SETNONE, this::setAllNone),
                new DefaultAction(ACTION_SETINPUT, this::setAllInput),
                new DefaultAction(ACTION_SETOUTPUT, this::setAllOutput),
                new DefaultAction(ACTION_CLEARSTATS, () -> {
                    this.totalExtracted = 0;
                    this.totalInserted = 0;
                    this.markDirty();
                }),
        };
    }

    private LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(DimensionalCellTileEntity.this));
    private LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(this::createItemHandler);
    private LazyOptional<NullHandler> nullStorage = LazyOptional.of(() -> new NullHandler());
    private LazyOptional<IMachineInformation> infoHandler = LazyOptional.of(() -> createMachineInfo());
    private LazyOptional<SidedHandler>[] sidedStorages = new LazyOptional[]{
            LazyOptional.of(() -> new SidedHandler(Direction.DOWN)),
            LazyOptional.of(() -> new SidedHandler(Direction.UP)),
            LazyOptional.of(() -> new SidedHandler(Direction.NORTH)),
            LazyOptional.of(() -> new SidedHandler(Direction.SOUTH)),
            LazyOptional.of(() -> new SidedHandler(Direction.WEST)),
            LazyOptional.of(() -> new SidedHandler(Direction.EAST))
    };
    private LazyOptional<IModuleSupport> moduleSupportHandler = LazyOptional.of(() -> new DefaultModuleSupport(DimensionalCellContainer.SLOT_CARD) {
        @Override
        public boolean isModule(ItemStack itemStack) {
            return itemStack.getItem() instanceof PowerCellCardItem;
        }
    });

    private int networkId = -1;

    // Only used when this block is not part of a network
    private int energy = 0;

    // Total amount of energy extracted from this block (local or not)
    private long totalExtracted = 0;
    // Total amount of energy inserted in this block (local or not)
    private long totalInserted = 0;

    private int lastRfPerTickIn = 0;
    private int lastRfPerTickOut = 0;
    private int powerIn = 0;
    private int powerOut = 0;
    private long lastTime = 0;

    public enum Mode implements IStringSerializable {
        MODE_NONE("none"),
        MODE_INPUT("input"),   // Blue
        MODE_OUTPUT("output"); // Yellow

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private Mode modes[] = new Mode[]{Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE};

    public DimensionalCellTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public int getLastRfPerTickIn() {
        return lastRfPerTickIn;
    }

    public int getLastRfPerTickOut() {
        return lastRfPerTickOut;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
        markDirty();
    }

    @Nullable
    public DimensionalCellNetwork.Network getNetwork() {
        if (world.isRemote) {
            // Safety
            return null;
        }

        int networkId = getNetworkId();
        if (networkId == -1) {
            return null;
        }
        DimensionalCellNetwork generatorNetwork = DimensionalCellNetwork.getChannels();
        return generatorNetwork.getOrCreateNetwork(networkId);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        Mode[] old = new Mode[]{modes[0], modes[1], modes[2], modes[3], modes[4], modes[5]};
        super.onDataPacket(net, packet);
        for (int i = 0; i < 6; i++) {
            if (old[i] != modes[i]) {
                world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
                return;
            }
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        energy = info.getInt("energy");
        totalInserted = info.getLong("totIns");
        totalExtracted = info.getLong("totExt");
        networkId = info.getInt("networkId");
        modes[0] = Mode.values()[info.getByte("m0")];
        modes[1] = Mode.values()[info.getByte("m1")];
        modes[2] = Mode.values()[info.getByte("m2")];
        modes[3] = Mode.values()[info.getByte("m3")];
        modes[4] = Mode.values()[info.getByte("m4")];
        modes[5] = Mode.values()[info.getByte("m5")];
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("energy", energy);
        info.putLong("totIns", totalInserted);
        info.putLong("totExt", totalExtracted);
        info.putInt("networkId", networkId);
        info.putByte("m0", (byte) modes[0].ordinal());
        info.putByte("m1", (byte) modes[1].ordinal());
        info.putByte("m2", (byte) modes[2].ordinal());
        info.putByte("m3", (byte) modes[3].ordinal());
        info.putByte("m4", (byte) modes[4].ordinal());
        info.putByte("m5", (byte) modes[5].ordinal());
        return tagCompound;
    }

    public Mode getMode(Direction side) {
        return modes[side.ordinal()];
    }

    private void updateState() {
        Mode north = getMode(Direction.NORTH);
        Mode south = getMode(Direction.SOUTH);
        Mode west = getMode(Direction.WEST);
        Mode east = getMode(Direction.EAST);
        Mode up = getMode(Direction.UP);
        Mode down = getMode(Direction.DOWN);
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(NORTH, north).with(SOUTH, south).with(WEST, west).with(EAST, east).with(UP, up).with(DOWN, down),
                Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        markDirtyQuick();
    }

    public void toggleMode(Direction side) {
        switch (modes[side.ordinal()]) {
            case MODE_NONE:
                modes[side.ordinal()] = Mode.MODE_INPUT;
                break;
            case MODE_INPUT:
                modes[side.ordinal()] = Mode.MODE_OUTPUT;
                break;
            case MODE_OUTPUT:
                modes[side.ordinal()] = Mode.MODE_NONE;
                break;
        }
        updateState();
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            long time = world.getGameTime();
            if (lastTime == 0) {
                lastTime = time;
            } else if (time > lastTime + 40) {
                lastRfPerTickIn = (int) (powerIn / (time - lastTime));
                lastRfPerTickOut = (int) (powerOut / (time - lastTime));
                lastTime = time;
                powerIn = 0;
                powerOut = 0;
            }

            if (getDimensionalCellType().isCreative()) {
                // A creative powercell automatically generates 1000000 RF/tick
                int gain = 1000000;
                int networkId = getNetworkId();
                if (networkId == -1) {
                    receiveEnergyLocal(gain, false);
                } else {
                    receiveEnergyMulti(gain, false);
                }
            }

            int energyStored = getEnergyStored();
            if (energyStored <= 0) {
                return;
            }

            handleChargingItem();
            sendOutEnergy();
        }
    }

    private void handleChargingItem() {
        itemHandler.ifPresent(h -> {
            ItemStack stack = h.getStackInSlot(DimensionalCellContainer.SLOT_CHARGEITEM);
            if (stack.isEmpty()) {
                return;
            }

            int rfToGive = Math.min(DimensionalCellConfiguration.CHARGEITEMPERTICK.get(), getEnergyStored());
            int received = (int) EnergyTools.receiveEnergy(stack, rfToGive);
            if (received == 0) {
                return;
            }
            extractEnergyInternal(received, false, DimensionalCellConfiguration.CHARGEITEMPERTICK.get());
        });
    }

    private void sendOutEnergy() {
        int energyStored = getEnergyStored();

        for (Direction face : OrientationTools.DIRECTION_VALUES) {
            if (modes[face.ordinal()] == Mode.MODE_OUTPUT) {
                BlockPos pos = getPos().offset(face);
                TileEntity te = world.getTileEntity(pos);
                Direction opposite = face.getOpposite();
                if (EnergyTools.isEnergyTE(te, opposite)) {
                    // If the adjacent block is also a powercell then we only send energy if this cell is local or the other cell has a different id
                    if ((!(te instanceof DimensionalCellTileEntity)) || getNetworkId() == -1 || ((DimensionalCellTileEntity) te).getNetworkId() != getNetworkId()) {
                        float factor = getCostFactor();
                        int rfPerTick = getRfPerTickPerSide();
                        int rfToGive = Math.min(rfPerTick, (int) (energyStored / factor));

                        int received = (int) EnergyTools.receiveEnergy(te, opposite, rfToGive);

                        energyStored -= extractEnergyInternal(received, false, Integer.MAX_VALUE);
                        if (energyStored <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public float getCostFactor() {
        float infusedFactor = getInfusedFactor();

        float factor;
        if (getNetworkId() == -1) {
            factor = 1.0f; // Local energy
        } else {
            factor = getNetwork().calculateCostFactor(world, getGlobalPos());
            factor = (factor - 1) * (1 - infusedFactor / 2) + 1;
        }
        return factor;
    }

    private Float getInfusedFactor() {
        return infusableHandler.map(h -> h.getInfusedFactor()).orElse(0.0f);
    }

    public int getRfPerTickPerSide() {
        return (int) (DimensionalCellConfiguration.rfPerTick.get() * getPowerFactor() / DimensionalCellConfiguration.simpleFactor.get() * (getInfusedFactor() * .5 + 1));
    }

    private void handleCardRemoval() {
        if (!world.isRemote) {
            DimensionalCellNetwork.Network network = getNetwork();
            if (network != null) {
                energy = network.extractEnergySingleBlock(getDimensionalCellType());
                network.remove(world, getGlobalPos(), getDimensionalCellType());
                DimensionalCellNetwork.getChannels().save();
            }
        }
        networkId = -1;
        markDirty();
    }

    private void handleCardInsertion() {
        itemHandler.ifPresent(h -> {
            ItemStack stack = h.getStackInSlot(DimensionalCellContainer.SLOT_CARD);
            int id = PowerCellCardItem.getId(stack);
            if (!world.isRemote) {
                DimensionalCellNetwork channels = DimensionalCellNetwork.getChannels();
                if (id == -1) {
                    id = channels.newChannel();
                    PowerCellCardItem.setId(stack, id);
                }
                networkId = id;
                DimensionalCellNetwork.Network network = getNetwork();
                network.add(world, getGlobalPos(), getDimensionalCellType());
                network.receiveEnergy(energy);
                channels.save();
            } else {
                networkId = id;
            }
            markDirty();
        });
    }

    private DimensionalCellType getDimensionalCellType() {
        return DimensionalCellBlock.getType(world.getBlockState(pos).getBlock());
    }

    // Get the power factor relative to the simple powercell
    private int getPowerFactor() {
        if (getDimensionalCellType().isSimple()) {
            return 1;
        }
        return getDimensionalCellType().isAdvanced() ? (DimensionalCellConfiguration.advancedFactor.get() * DimensionalCellConfiguration.simpleFactor.get()) : DimensionalCellConfiguration.simpleFactor.get();
    }

    public int getEnergy() {
        return energy;
    }

    public GlobalCoordinate getGlobalPos() {
        return new GlobalCoordinate(getPos(), world.getDimension().getType());
    }

    public long getTotalExtracted() {
        return totalExtracted;
    }

    public long getTotalInserted() {
        return totalInserted;
    }

    public void resetTotalExtracted() {
        this.totalExtracted = 0;
    }

    public void resetTotalInserted() {
        this.totalInserted = 0;
    }

    public int receiveEnergyFacing(Direction from, int maxReceive, boolean simulate) {
        if (modes[from.ordinal()] != Mode.MODE_INPUT) {
            return 0;
        }
        maxReceive = Math.min(maxReceive, getRfPerTickPerSide());
        int networkId = getNetworkId();
        int received;
        if (networkId == -1) {
            received = receiveEnergyLocal(maxReceive, simulate);
        } else {
            received = receiveEnergyMulti(maxReceive, simulate);
        }
        if (!simulate) {
            totalInserted += received;
            powerIn += received;
            markDirty();
        }
        return received;
    }

    private int receiveEnergyMulti(int maxReceive, boolean simulate) {
        if (world.isRemote) {
            return 0;   // Safety
        }
        DimensionalCellNetwork.Network network = getNetwork();
        int totEnergy = network.calculateMaximumEnergy();

        int maxInsert = Math.min(totEnergy - network.getEnergy(), maxReceive);
        if (maxInsert > 0) {
            if (!simulate) {
                maxInsert = network.receiveEnergy(maxInsert);
                DimensionalCellNetwork.getChannels().save();
            }
        }
        return getDimensionalCellType().isCreative() ? maxReceive : maxInsert;
    }

    private int receiveEnergyLocal(int maxReceive, boolean simulate) {
        long capacityL = (long) DimensionalCellConfiguration.rfPerNormalCell.get() * getPowerFactor() / DimensionalCellConfiguration.simpleFactor.get();
        int capacity = capacityL > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacityL;
        int maxInsert = Math.min(capacity - energy, maxReceive);
        if (maxInsert > 0) {
            if (!simulate) {
                energy += maxInsert;
                markDirty();
            }
        }
        return getDimensionalCellType().isCreative() ? maxReceive : maxInsert;
    }

    private int extractEnergyInternal(int maxExtract, boolean simulate, int maximum) {
        int networkId = getNetworkId();
        int extracted;
        if (networkId == -1) {
            extracted = extractEnergyLocal(maxExtract, simulate, maximum);
        } else {
            extracted = extractEnergyMulti(maxExtract, simulate, maximum);
        }
        if (!simulate) {
            totalExtracted += extracted;
            powerOut += extracted;
            markDirty();
        }
        return extracted;
    }

    private int extractEnergyMulti(int maxExtract, boolean simulate, int maximum) {
        if (world.isRemote) {
            return 0; // Safety
        }
        DimensionalCellNetwork.Network network = getNetwork();
        if (maxExtract > maximum) {
            maxExtract = maximum;
        }
        if (!simulate) {
            maxExtract = network.extractEnergy(maxExtract);
            DimensionalCellNetwork.getChannels().save();
        }
        return maxExtract;
    }

    private int extractEnergyLocal(int maxExtract, boolean simulate, int maximum) {
        // We act as a single block
        if (maxExtract > energy) {
            maxExtract = energy;
        }
        if (maxExtract > maximum) {
            maxExtract = maximum;
        }
        if (!simulate) {
            energy -= maxExtract;
            markDirty();
        }
        return maxExtract;
    }

    public int getEnergyStored() {
        if (world.isRemote) {
            return 0;
        }
        int networkId = getNetworkId();
        if (networkId == -1) {
            return energy;
        }
        DimensionalCellNetwork.Network network = getNetwork();
        return network.getEnergy();
    }

    public int getMaxEnergyStored() {
        if (world.isRemote) {
            return 0;
        }
        int networkId = getNetworkId();
        if (networkId == -1) {
            return DimensionalCellConfiguration.rfPerNormalCell.get() * getPowerFactor() / DimensionalCellConfiguration.simpleFactor.get();
        }
        DimensionalCellNetwork.Network network = getNetwork();
        return network.calculateMaximumEnergy();
    }


    public void setAllOutput() {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            modes[facing.ordinal()] = Mode.MODE_OUTPUT;
        }
        updateState();
    }

    private void setAllInput() {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            modes[facing.ordinal()] = Mode.MODE_INPUT;
        }
        updateState();
    }

    private void setAllNone() {
        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            modes[facing.ordinal()] = Mode.MODE_NONE;
        }
        updateState();
    }

    @Override
    public void selectBlock(PlayerEntity player, BlockPos pos) {
        dumpNetwork(player, this);
    }

    public static void dumpNetwork(PlayerEntity player, DimensionalCellTileEntity dimensionalCellTileEntity) {
        DimensionalCellNetwork.Network network = dimensionalCellTileEntity.getNetwork();
        Set<GlobalCoordinate> blocks = network.getBlocks();
//        System.out.println("blocks.size() = " + blocks.size());
        blocks.forEach(b -> {
            String msg;
            World w = mcjty.lib.varia.TeleportationTools.getWorldForDimension(b.getDimension());
            if (w == null) {
                msg = "dimension missing!";
            } else {
                Block block = w.getBlockState(b.getCoordinate()).getBlock();
                if (block == DimensionalCellSetup.dimensionalCellBlock) {
                    msg = "normal";
                } else if (block == DimensionalCellSetup.advancedDimensionalCellBlock) {
                    msg = "advanced";
                } else if (block == DimensionalCellSetup.creativeDimensionalCellBlock) {
                    msg = "creative";
                } else {
                    msg = "not a powercell!";
                }
                TileEntity te = w.getTileEntity(b.getCoordinate());
                if (te instanceof DimensionalCellTileEntity) {
                    DimensionalCellTileEntity power = (DimensionalCellTileEntity) te;
                    msg += " (+:" + power.getTotalInserted() + ", -:" + power.getTotalExtracted() + ")";
                }
            }

            Logging.message(player, "Block: " + BlockPosTools.toString(b.getCoordinate()) + " (" + b.getDimension() + "): " + msg);
        });
    }

    @Override
    public TypedMap executeWithResult(String command, TypedMap args) {
        TypedMap rc = super.executeWithResult(command, args);
        if (rc != null) {
            return rc;
        }
        if (CMD_GET_INFO.equals(command)) {
            if (networkId == -1) {
                return TypedMap.builder()
                        .put(PARAM_ENERGY, getEnergy())
                        .put(PARAM_BLOCKS, 1)
                        .put(PARAM_SIMPLEBLOCKS, getDimensionalCellType().isSimple() ? 1 : 0)
                        .put(PARAM_ADVANCEDBLOCKS, getDimensionalCellType().isAdvanced() ? 1 : 0)
                        .put(PARAM_TOTAL_INSERTED, getTotalInserted())
                        .put(PARAM_TOTAL_EXTRACTED, getTotalExtracted())
                        .put(PARAM_RFPERTICK, getRfPerTickPerSide())
                        .put(PARAM_COSTFACTOR, 1.0)
                        .build();
            } else {
                DimensionalCellNetwork.Network network = getNetwork();
                return TypedMap.builder()
                        .put(PARAM_ENERGY, network.getEnergy())
                        .put(PARAM_BLOCKS, network.getBlockCount())
                        .put(PARAM_SIMPLEBLOCKS, network.getSimpleBlockCount())
                        .put(PARAM_ADVANCEDBLOCKS, network.getAdvancedBlockCount())
                        .put(PARAM_TOTAL_INSERTED, getTotalInserted())
                        .put(PARAM_TOTAL_EXTRACTED, getTotalExtracted())
                        .put(PARAM_RFPERTICK, getRfPerTickPerSide())
                        .put(PARAM_COSTFACTOR, (double) getCostFactor())
                        .build();
            }
        }
        return null;
    }

    @Override
    public boolean receiveDataFromServer(String command, @Nonnull TypedMap result) {
        boolean rc = super.receiveDataFromServer(command, result);
        if (rc) {
            return true;
        }
        if (CMD_GET_INFO.equals(command)) {
            tooltipEnergy = result.get(PARAM_ENERGY);
            tooltipBlocks = result.get(PARAM_BLOCKS);
            tooltipSimpleBlocks = result.get(PARAM_SIMPLEBLOCKS);
            tooltipAdvancedBlocks = result.get(PARAM_ADVANCEDBLOCKS);
            tooltipInserted = result.get(PARAM_TOTAL_INSERTED);
            tooltipExtracted = result.get(PARAM_TOTAL_EXTRACTED);
            tooltipRfPerTick = result.get(PARAM_RFPERTICK);
            DimensionalCellTileEntity.tooltipCostFactor = result.get(PARAM_COSTFACTOR).floatValue();
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null) {
                return nullStorage.cast();
            } else {
                return sidedStorages[facing.ordinal()].cast();
            }
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (capability == CapabilityInfusable.INFUSABLE_CAPABILITY) {
            return infusableHandler.cast();
        }
        if (capability == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
            return infoHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    private class SidedHandler implements IEnergyStorage {
        private final Direction facing;

        private SidedHandler(Direction facing) {
            this.facing = facing;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return DimensionalCellTileEntity.this.receiveEnergyFacing(facing, maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return DimensionalCellTileEntity.this.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return DimensionalCellTileEntity.this.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    private class NullHandler implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return DimensionalCellTileEntity.this.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return DimensionalCellTileEntity.this.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(DimensionalCellTileEntity.this, DimensionalCellContainer.CONTAINER_FACTORY) {

            @Override
            public boolean isItemValid(int index, @Nonnull ItemStack stack) {
                if (index == DimensionalCellContainer.SLOT_CARD && stack.getItem() != DimensionalCellSetup.POWERCELL_CARD) {
                    return false;
                }
                if (index == DimensionalCellContainer.SLOT_CARDCOPY && stack.getItem() != DimensionalCellSetup.POWERCELL_CARD) {
                    return false;
                }
                return true;
            }


            @Override
            protected void onUpdate(int index) {
                super.onUpdate(index);
                if (index == DimensionalCellContainer.SLOT_CARD) {
                    if (getStackInSlot(index).isEmpty()) {
                        handleCardRemoval();
                    } else {
                        handleCardInsertion();
                    }
                } else if (index == DimensionalCellContainer.SLOT_CARDCOPY) {
                    if (!getStackInSlot(index).isEmpty()) {
                        PowerCellCardItem.setId(getStackInSlot(index), networkId);
                    }
                }
            }
        };
    }

    private IMachineInformation createMachineInfo() {
        return new IMachineInformation() {
            private final String[] TAGS = new String[]{"rfpertick_out", "rfpertick_in", "rftotal_in", "rftotal_out"};
            private final String[] TAG_DESCRIPTIONS = new String[]{
                    "The current RF/t output given by this block (last 2 seconds)",
                    "The current RF/t input received by this block (last 2 seconds)",
                    "The total RF/t output given by this block",
                    "The current RF/t input received by this block"};

            @Override
            public int getTagCount() {
                return TAGS.length;
            }

            @Override
            public String getTagName(int index) {
                return TAGS[index];
            }

            @Override
            public String getTagDescription(int index) {
                return TAG_DESCRIPTIONS[index];
            }

            @Override
            public String getData(int index, long millis) {
                switch (index) {
                    case 0:
                        return lastRfPerTickOut + "RF/t";
                    case 1:
                        return lastRfPerTickIn + "RF/t";
                    case 2:
                        return totalExtracted + "RF";
                    case 3:
                        return totalInserted + "RF";
                }
                return null;
            }
        };
    }
}