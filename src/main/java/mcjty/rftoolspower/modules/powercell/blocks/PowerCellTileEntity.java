package mcjty.rftoolspower.modules.powercell.blocks;

import cpw.mods.util.Lazy;
import mcjty.lib.api.power.IBigPower;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.api.infoscreen.IInformationScreenInfo;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.rftoolspower.modules.powercell.data.PowerCellNetwork;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static mcjty.rftoolspower.modules.powercell.data.SideType.NONE;

public class PowerCellTileEntity extends TickingTileEntity implements IBigPower {

    private PowerCellNetwork network = null;
    private long localEnergy = 0;
    private final Tier tier;

    public static final ModelProperty<SideType> NORTH = new ModelProperty<>();
    public static final ModelProperty<SideType> SOUTH = new ModelProperty<>();
    public static final ModelProperty<SideType> WEST = new ModelProperty<>();
    public static final ModelProperty<SideType> EAST = new ModelProperty<>();
    public static final ModelProperty<SideType> UP = new ModelProperty<>();
    public static final ModelProperty<SideType> DOWN = new ModelProperty<>();
    public static final ModelProperty<Tier> TIER = new ModelProperty<>();


    private final Lazy<IInformationScreenInfo> infoScreenInfo = Lazy.of(this::createScreenInfo);
    private final Lazy<NullHandler> nullStorage = Lazy.of(this::createNullHandler);
    private final Lazy<SidedHandler>[] sidedStorages;

    public PowerCellTileEntity(Tier tier, BlockPos pos, BlockState state) {
        super(tier.getType(), pos, state);
        this.tier = tier;
        sidedStorages = new Lazy[OrientationTools.DIRECTION_VALUES.length];
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            sidedStorages[direction.ordinal()] = Lazy.of(() -> createSidedHandler(direction));
        }
    }

    private final SideType[] modes = new SideType[] {
            NONE, NONE, NONE,
            NONE, NONE, NONE };
    private int outputCount = 0;        // Caches the number of sides that have outputs

    public SideType getMode(Direction side) {
        return modes[side.ordinal()];
    }

    public void toggleMode(Direction side) {
        switch (modes[side.ordinal()]) {
            case NONE -> modes[side.ordinal()] = SideType.INPUT;
            case INPUT -> modes[side.ordinal()] = SideType.OUTPUT;
            case OUTPUT -> modes[side.ordinal()] = SideType.NONE;
        }
        updateOutputCount();
        markDirtyClient();  // In world render change. So this is needed
    }

    private void updateOutputCount() {
        outputCount = 0;
        for (SideType mode : modes) {
            if (mode == SideType.OUTPUT) {
                outputCount++;
            }
        }
    }

    public Tier getTier() {
        return tier;
    }

    // @todo temporary until long values work in forge!
    public static long safeCast(Object o) {
        if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Integer) {
            return (long) (Integer) o;
        } else {
            return 0;
        }
    }

    public long getLocalMaxEnergy() {
        return switch (tier) {
            case TIER1 -> safeCast(PowerCellConfig.TIER1_MAXRF.get());
            case TIER2 -> safeCast(PowerCellConfig.TIER2_MAXRF.get());
            case TIER3 -> safeCast(PowerCellConfig.TIER3_MAXRF.get());
        };
    }

    public long getRfPerTickPerSide() {
        return switch (tier) {
            case TIER1 -> PowerCellConfig.TIER1_RFPERTICK.get();
            case TIER2 -> PowerCellConfig.TIER2_RFPERTICK.get();
            case TIER3 -> PowerCellConfig.TIER3_RFPERTICK.get();
        };
    }

    public long getRfPerTickReal() {
        if (PowerCellConfig.RFPERTICK_SCALE.get() > 0) {
            return (long) (getRfPerTickPerSide() + (getNetwork().getPositions().size()-1) * getRfPerTickPerSide() * PowerCellConfig.RFPERTICK_SCALE.get());
        } else {
            return getRfPerTickPerSide();
        }
    }

    @Override
    public long getStoredPower() {
        return getNetwork().getEnergy();
    }

    @Override
    public long getCapacity() {
        return getNetwork().getMaxEnergy();
    }

    private long receiveEnergyFacing(Direction from, long maxReceive, boolean simulate) {
        if (modes[from.ordinal()] != SideType.INPUT) {
            return 0;
        }

        PowerCellNetwork network = getNetwork();
        if (network == null || !network.isValid()) {
            return 0;
        }

        maxReceive = Math.min(maxReceive, getRfPerTickReal());
        long received = receiveEnergyLocal(maxReceive, simulate);
        if (received > 0) {
            if (!simulate) {
                network.addEnergy(received);
            }
            maxReceive -= received;
        }
        long totReceived = received;
        if (maxReceive > 0) {
            for (Long l : network.getPositions()) {
                BlockPos p = BlockPos.of(l);
                BlockEntity te = level.getBlockEntity(p);
                if (te instanceof PowerCellTileEntity powercell) {
                    received = powercell.receiveEnergyLocal(maxReceive, simulate);
                    if (received > 0) {
                        if (!simulate) {
                            network.addEnergy(received);
                        }
                        maxReceive -= received;
                        totReceived += received;
                    }
                }
            }
        }
        return totReceived;
    }

    private long receiveEnergyLocal(long maxReceive, boolean simulate) {
        long maxInsert = Math.min(getLocalMaxEnergy() - localEnergy, maxReceive);
        if (maxInsert > 0) {
            if (!simulate) {
                localEnergy += maxInsert;
                markDirtyQuick();
            }
        }
        return maxInsert;
    }



    private int getEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getEnergy()); // TODO replace with EnergyTools one
    }

    private int getMaxEnergyStoredAsInt() {
        return (int) Math.min(Integer.MAX_VALUE, getNetwork().getMaxEnergy()); // TODO replace with EnergyTools one
    }

    @Override
    protected void tickServer() {
        if (outputCount > 0) {
            PowerCellNetwork network = getNetwork();
            if (network != null && network.isValid()) {
                long energyStored = network.getEnergy();
                if (energyStored <= 0) {
                    return;
                }
                sendOutEnergy(energyStored);
            }
        }
    }

    public void redistributeNetwork() {
        PowerCellNetwork network = getNetwork();
        if (network == null || network.getPositions().isEmpty()) {
            return;
        }
        final long[] energy = {0, 0, 0};  // For each tier
        final int[] count = {0, 0, 0};
        network.getPositions().forEach(l -> {
            BlockPos p = BlockPos.of(l);
            BlockEntity te = level.getBlockEntity(p);
            if (te instanceof PowerCellTileEntity powercell) {
                int t = powercell.getTier().ordinal();
                energy[t] += powercell.getLocalEnergy();
                count[t]++;
            }
        });
        for (Tier tier : Tier.values()) {
            if (count[tier.ordinal()] > 0) {
                long energyPerBlock = energy[tier.ordinal()] / count[tier.ordinal()];
                final long[] energyToSet = {energyPerBlock + energy[tier.ordinal()] % count[tier.ordinal()]};   // First block gets more (remainder)
                network.getPositions().forEach(l -> {
                    BlockPos p = BlockPos.of(l);
                    BlockEntity te = level.getBlockEntity(p);
                    if (te instanceof PowerCellTileEntity powercell) {
                        if (powercell.getTier() == tier) {
                            powercell.setLocalEnergy(energyToSet[0]);
                            powercell.markDirtyQuick();
                            energyToSet[0] = energyPerBlock;
                        }
                    }
                });
            }
        }
    }

    private void validateNetwork() {
        final long[] energy = {0};
        final long[] maxEnergy = {0};
        getNetwork().getPositions().forEach(l -> {
            BlockPos p = BlockPos.of(l);
            BlockEntity te = level.getBlockEntity(p);
            if (te instanceof PowerCellTileEntity powercell) {
                energy[0] += powercell.getLocalEnergy();
                maxEnergy[0] += powercell.getLocalMaxEnergy();
                if (powercell.network != this.network) {
                    System.out.println("##### Network doesn't match at: " + p);
                }
            } else {
                System.out.println("##### Not a powercell: " + p);
            }
        });
        if (network.getEnergy() != energy[0]) {
            System.out.println("##### Energy mismatch! Got " + energy[0] + ", expected " + network.getEnergy());
        }
        if (network.getMaxEnergy() != maxEnergy[0]) {
            System.out.println("##### Max energy mismatch! Got " + maxEnergy[0] + ", expected " + network.getMaxEnergy());
        }
    }

    private void sendOutEnergy(long energyStored) {
        final long[] stored = {energyStored};
        final long[] energyExtracted = {0};

        for (Direction face : Direction.values()) {
            if (modes[face.ordinal()] == SideType.OUTPUT) {
                BlockPos pos = getBlockPos().relative(face);
                BlockEntity te = getLevel().getBlockEntity(pos);
                Direction opposite = face.getOpposite();
                if (te != null) {
                    IEnergyStorage e = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, pos, opposite);
                    if (e != null) {
                        if (!(te instanceof PowerCellTileEntity)) {
                            long rfPerTick = getRfPerTickReal();
                            long rfToGive = Math.min(rfPerTick, stored[0]);

                            long received = EnergyTools.receiveEnergy(te, opposite, rfToGive);

                            stored[0] -= received;
                            energyExtracted[0] += received;
                        }
                    };
                    if (stored[0] <= 0) {
                        break;
                    }
                }
            }
        }

        if (energyExtracted[0] > 0) {
            network.extractEnergy(energyExtracted[0]);
            extractEnergyFromNetwork(energyExtracted[0]);
        }
    }

    private void extractEnergyFromNetwork(long energyExtracted) {
        long toExtractLocal = Math.min(energyExtracted, localEnergy);
        if (toExtractLocal > 0) {
            // First extract locally
            localEnergy -= toExtractLocal;
            energyExtracted -= toExtractLocal;
            markDirtyQuick();
        }

        // If we still have energy to extract go find another block in the network
        if (energyExtracted > 0) {
            for (Long l : network.getPositions()) {
                BlockPos p = BlockPos.of(l);
                BlockEntity te = level.getBlockEntity(p);
                if (te instanceof PowerCellTileEntity powercell) {
                    toExtractLocal = Math.min(energyExtracted, powercell.localEnergy);
                    if (toExtractLocal > 0) {
                        powercell.localEnergy -= toExtractLocal;
                        energyExtracted -= toExtractLocal;
                        powercell.markDirtyQuick();
                    }
                    if (energyExtracted <= 0) {
                        break;      // We're done
                    }
                }
            }
        }
    }

    public long getLocalEnergy() {
        return localEnergy;
    }

    public void setLocalEnergy(long localEnergy) {
        this.localEnergy = localEnergy;
    }

    public PowerCellNetwork getNetwork() {
        if (network == null) {
            // This block has no network. Create one and distribute to all connected powercells
            buildNetwork(new PowerCellNetwork(), worldPosition);
        }
        return network;
    }

    public void setNetwork(PowerCellNetwork network) {
        this.network = network;
    }

    @Override
    public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
        if (state.getBlock() == newstate.getBlock()) {
            return;
        }

        if (getNetwork() != null) {
            dismantleNetwork(getNetwork());
        }
        BlockState stateUp = world.getBlockState(pos.above());
        if (stateUp.getBlock() instanceof PowerCellBlock) {
            world.sendBlockUpdated(pos.above(), stateUp, stateUp, Block.UPDATE_ALL);
        }
        BlockState stateDown = world.getBlockState(pos.below());
        if (stateDown.getBlock() instanceof PowerCellBlock) {
            world.sendBlockUpdated(pos.below(), stateDown, stateDown, Block.UPDATE_ALL);
        }
    }

    public void dismantleNetwork(PowerCellNetwork network) {
        network.getPositions().stream().map(BlockPos::of).forEach(pos -> {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof PowerCellTileEntity powercell) {
                powercell.setNetwork(null);
            }
        });
    }

    private static final Set<BlockPos> alreadyReportedBad = new HashSet<>();
    private static final Set<BlockPos> alreadyReportedUnexpected = new HashSet<>();

    private void buildNetwork(PowerCellNetwork network, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof PowerCellTileEntity powercell) {

            if (network.contains(pos)) {
                if (powercell.network != network) {
                    if (!alreadyReportedBad.contains(pos)) {
                        System.out.println("Bad network at pos = " + pos + " (dimension " + level.dimension().location().getPath() + ")");
                        alreadyReportedBad.add(pos);
                    }
                }
                return;
            }

            if (powercell.network == network) {
                if (!alreadyReportedUnexpected.contains(pos)) {
                    System.out.println("Unexpected network at pos = " + pos + " (dimension " + level.dimension().location().getPath() + ")");
                    alreadyReportedUnexpected.add(pos);
                }
                return;
            }

            if (powercell.network != null) {
                // Connected to some other network. First break that down
                dismantleNetwork(powercell.network);
            }

            powercell.setNetwork(network);
            network.add(pos);
            network.setEnergy(network.getEnergy() + powercell.getLocalEnergy());
            network.setMaxEnergy(network.getMaxEnergy() + powercell.getLocalMaxEnergy());

            for (Direction facing : OrientationTools.DIRECTION_VALUES) {
                buildNetwork(network, pos.relative(facing));
            }
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookupProvider) {
        SideType[] old = new SideType[] { modes[0], modes[1], modes[2], modes[3], modes[4], modes[5] };
        super.onDataPacket(net, packet, lookupProvider);
        for (int i = 0 ; i < 6 ; i++) {
            if (old[i] != modes[i]) {
                requestModelDataUpdate();
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
                return;
            }
        }
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(NORTH, getMode(Direction.NORTH))
                .with(SOUTH, getMode(Direction.SOUTH))
                .with(WEST, getMode(Direction.WEST))
                .with(EAST, getMode(Direction.EAST))
                .with(UP, getMode(Direction.UP))
                .with(DOWN, getMode(Direction.DOWN))
                .with(TIER, tier)
                .build();
    }


    @Override
    public void loadAdditional(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        loadClientDataFromNBT(tagCompound);
        CompoundTag info = tagCompound.getCompound("Info");
        localEnergy = info.getLong("energy");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        CompoundTag info = getOrCreateInfo(tagCompound);
        saveClientDataToNBT(tagCompound);
        info.putLong("energy", localEnergy);
        super.saveAdditional(tagCompound, provider);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound) {
        CompoundTag info = getOrCreateInfo(tagCompound);
        String mode = "";
        for (int i = 0 ; i < 6 ; i++) {
            mode += modes[i].ordinal();
        }
        info.putString("mode", mode);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound) {
        CompoundTag info = tagCompound.getCompound("Info");
        String mode = info.getString("mode");
        if (mode.length() >= 6) {
            for (int i = 0 ; i < 6 ; i++) {
                modes[i] = SideType.VALUES[Integer.parseInt(mode.substring(i, i+1))];
            }
        }
        updateOutputCount();
    }

    public IInformationScreenInfo getInfoScreenInfo() {
        return infoScreenInfo.get();
    }

    public IEnergyStorage getEnergyStorage(Direction facing) {
        if (facing == null) {
            return nullStorage.get();
        } else {
            return sidedStorages[facing.ordinal()].get();
        }
    }

    class SidedHandler implements IEnergyStorage {

        private final Direction facing;

        public SidedHandler(Direction facing) {
            this.facing = facing;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int)PowerCellTileEntity.this.receiveEnergyFacing(facing, maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return PowerCellTileEntity.this.getEnergyStoredAsInt();
        }

        @Override
        public int getMaxEnergyStored() {
            return PowerCellTileEntity.this.getMaxEnergyStoredAsInt();
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

    @Nonnull
    private IInformationScreenInfo createScreenInfo() {
        return new PowerCellInformationScreenInfo(this);
    }

    private SidedHandler createSidedHandler(Direction facing) {
        return new SidedHandler(facing);
    }

    class NullHandler implements IEnergyStorage {
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
            return PowerCellTileEntity.this.getEnergyStoredAsInt();
        }

        @Override
        public int getMaxEnergyStored() {
            return PowerCellTileEntity.this.getMaxEnergyStoredAsInt();
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

    @Nonnull
    private NullHandler createNullHandler() {
        return new NullHandler();
    }
}
