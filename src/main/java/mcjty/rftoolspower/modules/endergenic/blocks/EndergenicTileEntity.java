package mcjty.rftoolspower.modules.endergenic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ResultCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.network.PacketSendClientCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.*;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.RFToolsBase;
import mcjty.rftoolsbase.api.client.IHudSupport;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.endergenic.ClientCommandHandler;
import mcjty.rftoolspower.modules.endergenic.EndergenicConfiguration;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import mcjty.rftoolspower.modules.endergenic.data.EndergenicPearl;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.rftoolsbase.modules.hud.Hud.COMMAND_GETHUDLOG;

public class EndergenicTileEntity extends TickingTileEntity implements IHudSupport, TickOrderHandler.IOrderTicker {

    private static final Random random = new Random();

    public static final int CHARGE_IDLE = 0;
    public static final int CHARGE_HOLDING = -1;

    public long clientLastRfPerTick = 0;
    public int clientLastPearlsLost = 0;
    public int clientLastPearlsLaunched = 0;
    public int clientLastPearlOpportunities = 0;

    public static final VoxelShape SHAPE = Shapes.box(0.002, 0.002, 0.002, 0.998, 0.998, 0.998);

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, EndergenicConfiguration.MAXENERGY.get(), 0);

    private final LazyOptional<IMachineInformation> infoHandler = LazyOptional.of(this::createMachineInfo);

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(EndergenicTileEntity.this);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Endergenic")
            .containerSupplier(empty(EndergenicModule.CONTAINER_ENDERGENIC, this))
            .energyHandler(() -> energyStorage));

    // The current chargingMode status.
    // CHARGE_IDLE means this entity is doing nothing.
    // A positive number means it is chargingMode up from 0 to 15. When it reaches 15 it will go back to idle unless
    // it was hit by an endergenic pearl in the mean time. In that case it goes to 'holding' state.
    // CHARGE_HOLDING means this entity is holding an endergenic pearl. Whie it does that it consumes
    // energy. If internal energy is depleted then the endergenic pearl is lost and the mode goes
    // back to idle.
    private int chargingMode = CHARGE_IDLE;

    // The current age of the pearl we're holding. Will be used to calculate bonuses
    // for powergeneration on pearls that are in the network for a longer time.
    private int currentAge = 0;

    // The location of the destination endergenic generator.
    private BlockPos destination = null;
    private int distance = 0;           // Distance between this block and destination in ticks

    // For pulse detection.
    private boolean prevIn = false;

    // Statistics for this generator.
    // These values count what is happening.
    private long rfGained = 0;
    private long rfLost = 0;
    private int pearlsLaunched = 0;
    private int pearlsLost = 0;
    private int chargeCounter = 0;
    private int pearlArrivedAt = -2;
    private int ticks = 100;

    // These values actually contain valid statistics.
    private long lastRfPerTick = 0;
    private long lastRfGained = 0;
    private long lastRfLost = 0;
    private int lastPearlsLost = 0;
    private int lastPearlsLaunched = 0;
    private int lastChargeCounter = 0;
    private int lastPearlArrivedAt = 0;
    private String lastPearlsLostReason = "";

    // Current traveling pearls.
    private List<EndergenicPearl> pearls = new ArrayList<>();

    private long lastHudTime = 0;
    private List<String> clientHudLog = new ArrayList<>();

    // Used for rendering a 'bad' and 'good' effect client-side
    private int badCounter = 0;
    private int goodCounter = 0;

    // This table indicates how much RF is produced when an endergenic pearl hits this block
    // at that specific chargingMode.
    private static final long[] rfPerHit = new long[]{0, 100, 150, 200, 400, 800, 1600, 3200, 6400, 8000, 12800, 8000, 6400, 2500, 1000, 100};

    private int tickCounter = 0;            // Only used for logging, counts server ticks.
    private long ticker = -1;       // Used by TickOrderHandler to detect that we've already been added to the queue


    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                BlockBehaviour.Properties.of(Material.METAL).strength(2.0f).sound(SoundType.METAL).noOcclusion())
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/endergenic"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(EndergenicTileEntity::new)) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }
        };
    }

    public EndergenicTileEntity(BlockPos pos, BlockState state) {
        super(EndergenicModule.TYPE_ENDERGENIC.get(), pos, state);
    }

    @Override
    public void tick() {
        // bad and good counter are handled both client and server side
        if (badCounter > 0) {
            badCounter--;
            markDirtyQuick();
        }
        if (goodCounter > 0) {
            goodCounter--;
            markDirtyQuick();
        }

        // The pearl injector will queue endergenics
    }

    public long getTicker() {
        return ticker;
    }

    public void setTicker(long ticker) {
        this.ticker = ticker;
    }

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_1;
    }

    @Override
    public Direction getBlockOrientation() {
        return null;
    }

    @Override
    public boolean isBlockAboveAir() {
        return level.isEmptyBlock(worldPosition.above());
    }

    public List<String> getHudLog() {
        List<String> list = new ArrayList<>();
        list.add(ChatFormatting.BLUE + "Last 5 seconds:");
        list.add("    Charged: " + getLastChargeCounter());
        list.add("    Fired: " + getLastPearlsLaunched());
        list.add("    Lost: " + getLastPearlsLost());
        if (getLastPearlsLost() > 0) {
            list.add(ChatFormatting.RED + "    " + getLastPearlsLostReason());
        }
        if (getLastPearlArrivedAt() > -2) {
            list.add("    Last pearl at " + getLastPearlArrivedAt());
        }
        list.add(ChatFormatting.BLUE + "Power:");
        list.add(ChatFormatting.GREEN + "    RF Gain " + getLastRfGained());
        list.add(ChatFormatting.RED + "    RF Lost " + getLastRfLost());
        list.add(ChatFormatting.GREEN + "    RF/t " + getLastRfPerTick());
        return list;
    }

    @Override
    public BlockPos getHudPos() {
        return getBlockPos();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition, worldPosition.offset(1, 2, 1));
    }


    @Override
    public List<String> getClientLog() {
        return clientHudLog;
    }

    @Override
    public long getLastUpdateTime() {
        return lastHudTime;
    }

    @Override
    public void setLastUpdateTime(long t) {
        lastHudTime = t;
    }

    public int getBadCounter() {
        return badCounter;
    }

    public long getLastRfPerTick() {
        return lastRfPerTick;
    }

    public long getLastRfGained() {
        return lastRfGained;
    }

    public long getLastRfLost() {
        return lastRfLost;
    }

    public int getLastPearlsLost() {
        return lastPearlsLost;
    }

    public int getLastPearlsLaunched() {
        return lastPearlsLaunched;
    }

    public int getLastChargeCounter() {
        return lastChargeCounter;
    }

    public int getLastPearlArrivedAt() {
        return lastPearlArrivedAt;
    }

    public String getLastPearlsLostReason() {
        return lastPearlsLostReason;
    }

    public int getGoodCounter() {
        return goodCounter;
    }

    @Override
    public void tickOnServer() {
        tickCounter++;

        ticks--;
        if (ticks < 0) {
            lastRfGained = rfGained;
            lastRfLost = rfLost;
            lastRfPerTick = (rfGained - rfLost) / 100;
            lastPearlsLost = pearlsLost;
            lastPearlsLaunched = pearlsLaunched;
            lastChargeCounter = chargeCounter;
            lastPearlArrivedAt = pearlArrivedAt;
//
//            System.out.println(BlockPosTools.toString(getPos()) + " RF: +" + lastRfGained + " -" + lastRfLost + " (" + lastRfPerTick + ")  "
//                + "Pearls: F" + lastPearlsLaunched + " L" + lastPearlsLost + "  Charges: " + lastChargeCounter);

            ticks = 100;
            rfGained = 0;
            rfLost = 0;
            pearlsLaunched = 0;
            pearlsLost = 0;
            chargeCounter = 0;
            pearlArrivedAt = -2;
        }

        handlePearls();
        handleSendingEnergy();

        // First check if we're holding a pearl to see if the pearl will be lost.
        if (chargingMode == CHARGE_HOLDING) {
            if (random.nextInt(1000) <= EndergenicConfiguration.chanceLost.get()) {
                // Pearl is lost.
                log("Server Tick: discard pearl randomly");
                discardPearl("Random pearl discard");
            }
        }

        boolean pulse = (powerLevel > 0) && !prevIn;
        prevIn = powerLevel > 0;
        if (pulse) {
            if (chargingMode == CHARGE_IDLE) {
                log("Server Tick: pulse -> start charging");
                startCharging();
                return;
            } else if (chargingMode == CHARGE_HOLDING) {
                log("Server Tick: pulse -> fire pearl");
                firePearl();
                return;
            }
        }

        if (chargingMode == CHARGE_IDLE) {
            // Do nothing
            return;
        }

        if (chargingMode == CHARGE_HOLDING) {
            // Consume energy to keep the endergenic pearl.
            long rf = EndergenicConfiguration.rfToHoldPearl.get();
            rf = (long) (rf * (3.0f - infusable.getInfusedFactor()) / 3.0f);

            long rfStored = energyStorage.getEnergy();
            if (rfStored < rf) {
                // Not enough energy. Pearl is lost.
                log("Server Tick: insufficient energy to hold pearl (" + rfStored + " vs " + rf + ")");
                discardPearl("Not enough energy to hold pearl");
            } else {
                long rfExtracted = energyStorage.extractEnergy((int) rf, false);
                log("Server Tick: holding pearl, consume " + rfExtracted + " RF");
                rfLost += rfExtracted;
            }
            return;
        }

        // Else we're charging up.
        markDirtyQuick();
        chargingMode++;
        if (chargingMode >= 16) {
            log("Server Tick: charging mode ends -> idle");
            chargingMode = CHARGE_IDLE;
        }
    }

    @Nonnull
    private IMachineInformation createMachineInfo() {
        return new IMachineInformation() {
            private final String[] TAGS = new String[]{"rftick", "lost", "launched", "opportunities"};
            private final String[] TAG_DESCRIPTIONS = new String[]{"Average RF/tick for the last 5 seconds", "Amount of pearls that were lost during the last 5 seconds",
                    "Amount of pearls that were launched during the last 5 seconds", "Number of opportunities for the last 5 seconds"};

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
                return switch (index) {
                    case 0 -> Long.toString(lastRfPerTick);
                    case 1 -> Integer.toString(lastPearlsLost);
                    case 2 -> Integer.toString(lastPearlsLaunched);
                    case 3 -> Integer.toString(lastChargeCounter);
                    default -> null;
                };
            }
        };
    }


    private void log(String message) {
        /* RFToolsPower.log(world, this, message);*/
    }

    public void modifyEnergyStored(long e) {
        long capacity = energyStorage.getCapacity();
        long energy = energyStorage.getEnergy();
        if (e > capacity - energy) {
            e = capacity - energy;
        } else if (e < -energy) {
            e = -energy;
        }
        energy += e;
        energyStorage.setEnergy(energy);
    }

    public static final Direction[] HORIZ_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    /**
     * Something happens, we need to notify all ender monitors.
     *
     * @param mode is the new mode
     */
    private void fireMonitors(EnderMonitorMode mode) {
        BlockPos pos = getBlockPos();
        for (Direction dir : OrientationTools.DIRECTION_VALUES) {
            BlockPos c = pos.relative(dir);
            BlockEntity te = level.getBlockEntity(c);
            if (te instanceof EnderMonitorTileEntity) {
                Direction inputSide = LogicSupport.getFacing(level.getBlockState(c)).getInputSide();
                if (inputSide == dir.getOpposite()) {
                    EnderMonitorTileEntity enderMonitorTileEntity = (EnderMonitorTileEntity) te;
                    enderMonitorTileEntity.fireFromEndergenic(mode);
                }
            }
        }
    }

    private void handleSendingEnergy() {
        long storedPower = energyStorage.getEnergy() - EndergenicConfiguration.ENDERGENIC_KEEPRF.get();
        if (storedPower <= 0) {
            return;
        }
        EnergyTools.handleSendingEnergy(level, worldPosition, storedPower, EndergenicConfiguration.ENDERGENIC_SENDPERTICK.get(), energyStorage);
    }

    // Handle all pearls that are currently in transit.
    private void handlePearls() {
        if (pearls.isEmpty()) {
            return;
        }
        List<EndergenicPearl> newlist = new ArrayList<>();
        for (EndergenicPearl pearl : pearls) {
            log("Pearls: age=" + pearl.getAge() + ", ticks left=" + pearl.getTicksLeft());
            if (!pearl.handleTick(level)) {
                // Keep the pearl. It has not arrived yet.
                newlist.add(pearl);
            }
        }

        // Replace the old list with the new one.
        pearls = newlist;
    }

    private void markDirtyClientNoRender() {
        setChanged();
        if (level != null) {
            level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(32),
                    p -> worldPosition.distToCenterSqr(p.getX(), p.getY(), p.getZ()) < 32 * 32)
                    .forEach(p -> RFToolsPowerMessages.INSTANCE.sendTo(
                            new PacketSendClientCommand(RFToolsPower.MODID, ClientCommandHandler.CMD_FLASH_ENDERGENIC,
                                    TypedMap.builder()
                                            .put(ClientCommandHandler.PARAM_POS, getBlockPos())
                                            .put(ClientCommandHandler.PARAM_GOODCOUNTER, goodCounter)
                                            .put(ClientCommandHandler.PARAM_BADCOUNTER, badCounter)
                                            .build()),
                            ((ServerPlayer) p).connection.connection, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    public void syncCountersFromServer(int goodCounter, int badCounter) {
        this.goodCounter = goodCounter;
        this.badCounter = badCounter;
    }

    private void discardPearl(String reason) {
        badCounter = 20;
        markDirtyClientNoRender();
        pearlsLost++;
        lastPearlsLostReason = reason;
        chargingMode = CHARGE_IDLE;
        fireMonitors(EnderMonitorMode.MODE_LOSTPEARL);
    }

    /**
     * Get the current destination. This function checks first if that destination is
     * still valid and if not it is reset to null (i.e. the destination was removed).
     *
     * @return the destination TE or null if there is no valid one
     */
    public EndergenicTileEntity getDestinationTE() {
        if (destination == null) {
            return null;
        }
        BlockEntity te = level.getBlockEntity(destination);
        if (te instanceof EndergenicTileEntity) {
            return (EndergenicTileEntity) te;
        } else {
            destination = null;
            markDirtyClient();
            return null;
        }
    }

    public void firePearl() {
        markDirtyQuick();
        // This method assumes we're in holding mode.
        getDestinationTE();
        if (destination == null) {
            // There is no destination so the pearl is simply lost.
            log("Fire Pearl: pearl lost due to lack of destination");
            discardPearl("Missing destination");
        } else {
            log("Fire Pearl: pearl is launched to " + destination.getX() + "," + destination.getY() + "," + destination.getZ());
            chargingMode = CHARGE_IDLE;
            pearlsLaunched++;
            pearls.add(new EndergenicPearl(distance, destination, currentAge + 1));
            fireMonitors(EnderMonitorMode.MODE_PEARLFIRED);
        }
    }

    public void firePearlFromInjector() {
        markDirtyQuick();
        // This method assumes we're not in holding mode.
        getDestinationTE();
        chargingMode = CHARGE_IDLE;
        if (destination == null) {
            // There is no destination so the injected pearl is simply lost.
            log("Fire Pearl from injector: pearl lost due to lack of destination");
            discardPearl("Missing destination");
        } else {
            log("Fire Pearl from injector: pearl is launched to " + destination.getX() + "," + destination.getY() + "," + destination.getZ());
            pearlsLaunched++;
            pearls.add(new EndergenicPearl(distance, destination, 0));
            fireMonitors(EnderMonitorMode.MODE_PEARLFIRED);
        }
    }

    // This generator receives a pearl. The age of the pearl is how many times the pearl has
    // already generated power.
    public void receivePearl(int age) {
        fireMonitors(EnderMonitorMode.MODE_PEARLARRIVED);
        markDirtyQuick();
        if (chargingMode == CHARGE_HOLDING) {
            log("Receive Pearl: pearl arrives but already holding -> both are lost");
            // If this block is already holding a pearl and it still has one then both pearls are
            // automatically lost.
            discardPearl("Pearl arrived while holding");
        } else if (chargingMode == CHARGE_IDLE) {
            log("Receive Pearl: pearl arrives but generator is idle -> pearl is lost");
            // If this block is idle and it is hit by a pearl then the pearl is lost and nothing
            // happens.
            discardPearl("Pearl arrived while idle");
        } else {
            pearlArrivedAt = chargingMode;
            // Otherwise we get RF and this block goes into holding mode.
            long rf = (long) (rfPerHit[chargingMode] * EndergenicConfiguration.powergenFactor.get());
            rf = (long) (rf * (infusable.getInfusedFactor() + 2.0f) / 2.0f);

            // Give a bonus for pearls that have been around a bit longer.
            int a = age * 5;
            if (a > 100) {
                a = 100;
            }
            rf += rf * a / 100;     // Maximum 200% bonus. Minimum no bonus.
            rfGained += rf;
            log("Receive Pearl: pearl arrives at tick " + chargingMode + ", age=" + age + ", RF=" + rf);
            modifyEnergyStored(rf);

            goodCounter = 10;
            markDirtyClientNoRender();

            chargingMode = CHARGE_HOLDING;
            currentAge = age;
        }
    }

    public void startCharging() {
        markDirtyQuick();
        chargingMode = 1;
        chargeCounter++;
    }

    // Called from client side when a wrench is used.
    public void useWrenchClient(Player player) {
        BlockPos thisCoord = getBlockPos();
        BlockPos coord = RFToolsBase.instance.clientInfo.getSelectedTE();
        BlockEntity tileEntity = null;
        if (coord != null) {
            tileEntity = level.getBlockEntity(coord);
        }

        if (!(tileEntity instanceof EndergenicTileEntity)) {
            // None selected. Just select this one.
            RFToolsBase.instance.clientInfo.setSelectedTE(thisCoord);
            EndergenicTileEntity destinationTE = getDestinationTE();
            if (destinationTE == null) {
                RFToolsBase.instance.clientInfo.setDestinationTE(null);
                Logging.message(player, "Select another endergenic generator as destination");
            } else {
                RFToolsBase.instance.clientInfo.setDestinationTE(destinationTE.getBlockPos());
                int distance = getDistanceInTicks();
                Logging.message(player, "Select another endergenic generator as destination (current distance " + distance + ")");
            }
        } else if (coord.equals(thisCoord)) {
            // Unselect this one.
            RFToolsBase.instance.clientInfo.setSelectedTE(null);
            RFToolsBase.instance.clientInfo.setDestinationTE(null);
        } else {
            // Make a link.
            EndergenicTileEntity otherTE = (EndergenicTileEntity) tileEntity;
            int distance = otherTE.calculateDistance(thisCoord);
            if (distance >= 5) {
                Logging.warn(player, "Distance is too far (maximum 4)");
                return;
            }
            otherTE.setDestination(thisCoord);
            RFToolsBase.instance.clientInfo.setSelectedTE(null);
            RFToolsBase.instance.clientInfo.setDestinationTE(null);
            Logging.message(player, "Destination is set (distance " + otherTE.getDistanceInTicks() + " ticks)");
        }
    }

    public int getChargingMode() {
        return chargingMode;
    }

    /**
     * Calculate the distance in ticks between this endergenic generator and the given coordinate.
     *
     * @param destination is the coordinate of the new destination
     * @return is the distance in ticks
     */
    public int calculateDistance(BlockPos destination) {
        double d = new Vec3(destination.getX(), destination.getY(), destination.getZ()).distanceTo(new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
        return (int) (d / 3.0f) + 1;
    }


    public BlockPos getDestination() {
        return destination;
    }

    public void setDestination(BlockPos destination) {
        markDirtyQuick();
        this.destination = destination;
        distance = calculateDistance(destination);

        if (level.isClientSide) {
            // We're on the client. Send change to server.
            PacketServerCommandTyped packet = new PacketServerCommandTyped(getBlockPos(), getDimension(), CMD_SETDESTINATION.name(), TypedMap.builder()
                    .put(PARAM_DESTINATION, destination)
                    .build());
            RFToolsPowerMessages.INSTANCE.sendToServer(packet);
        }
    }

    public int getDistanceInTicks() {
        return distance;
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        chargingMode = tagCompound.getInt("charging");
        currentAge = tagCompound.getInt("age");
        destination = BlockPosTools.read(tagCompound, "dest");
        distance = tagCompound.getInt("distance");
        prevIn = tagCompound.getBoolean("prevIn");
        badCounter = tagCompound.getByte("bad");
        goodCounter = tagCompound.getByte("good");
        pearls.clear();
        ListTag list = tagCompound.getList("pearls", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tc = list.getCompound(i);
            EndergenicPearl pearl = new EndergenicPearl(tc);
            pearls.add(pearl);
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);

        tagCompound.putInt("charging", chargingMode);
        tagCompound.putInt("age", currentAge);
        BlockPosTools.write(tagCompound, "dest", destination);
        tagCompound.putInt("distance", distance);
        tagCompound.putBoolean("prevIn", prevIn);
        tagCompound.putByte("bad", (byte) badCounter);
        tagCompound.putByte("good", (byte) goodCounter);

        ListTag pearlList = new ListTag();
        for (EndergenicPearl pearl : pearls) {
            pearlList.add(pearl.getTagCompound());
        }
        tagCompound.put("pearls", pearlList);
    }

    public static final Key<Long> PARAM_STATRF = new Key<>("statrf", Type.LONG);
    public static final Key<Integer> PARAM_STATLOST = new Key<>("statlost", Type.INTEGER);
    public static final Key<Integer> PARAM_STATLAUNCHED = new Key<>("statlaunched", Type.INTEGER);
    public static final Key<Integer> PARAM_STATOPPORTUNITIES = new Key<>("statopportunities", Type.INTEGER);
    @ServerCommand
    public static final ResultCommand<?> CMD_GETSTATS = ResultCommand.<EndergenicTileEntity>create("getStats",
            (te, player, params) -> TypedMap.builder()
                    .put(PARAM_STATRF, te.lastRfPerTick)
                    .put(PARAM_STATLOST, te.lastPearlsLost)
                    .put(PARAM_STATLAUNCHED, te.lastPearlsLaunched)
                    .put(PARAM_STATOPPORTUNITIES, te.lastChargeCounter)
                    .build(),
            (te, player, params) -> {
                te.clientLastRfPerTick = params.get(PARAM_STATRF);
                te.clientLastPearlsLost = params.get(PARAM_STATLOST);
                te.clientLastPearlsLaunched = params.get(PARAM_STATLAUNCHED);
                te.clientLastPearlOpportunities = params.get(PARAM_STATOPPORTUNITIES);
            });

    public static final Key<BlockPos> PARAM_DESTINATION = new Key<>("dest", Type.BLOCKPOS);
    @ServerCommand
    public static final Command<?> CMD_SETDESTINATION = Command.<EndergenicTileEntity>create("setDestination",
            (te, player, params) -> te.setDestination(params.get(PARAM_DESTINATION)));

    @ServerCommand
    public static final ListCommand<?, ?> CMD_GETHUDLOG = ListCommand.<EndergenicTileEntity, String>create(COMMAND_GETHUDLOG,
            (te, player, params) -> te.getHudLog(),
            (te, player, params, list) -> te.clientHudLog = list);


    @Override
    public boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
        if (world.isClientSide) {
            SoundEvent pling = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note.pling"));
            world.playSound(player, pos, pling, SoundSource.BLOCKS, 1.0f, 1.0f);
            useWrenchClient(player);
        }
        return true;
    }

    public long getCapacity() {
        return energyStorage.getCapacity();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY) {
            return infoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
