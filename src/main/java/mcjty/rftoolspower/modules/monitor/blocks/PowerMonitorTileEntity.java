package mcjty.rftoolspower.modules.monitor.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.data.PowerMonitorData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerMonitorTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    @Cap(type = CapType.CONTAINER)
    private static final Function<PowerMonitorTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Power Monitor")
            .containerSupplier(empty(MonitorModule.CONTAINER_POWER_MONITOR, be))
            .data(MonitorModule.POWER_MONITOR_DATA, PowerMonitorData.STREAM_CODEC, PowerMonitorData.CODEC)
            .setupSync(be);

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);

    // Transient data
    private boolean inAlarm = false;
    private int counter = 20;

    public PowerMonitorTileEntity(BlockPos pos, BlockState state) {
        super(MonitorModule.POWER_MONITOR.be().get(), pos, state);
    }

    public static LogicSlabBlock createBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties properties) {
        return new LogicSlabBlock(new BlockBuilder().properties(mcjty.rftoolsbase.tools.RegistryProperties.block(properties))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsbase:powermonitor/powermonitor"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(PowerMonitorTileEntity::new)) {
            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(LEVEL);
            }
        };
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getMinimum() {
        return getData(MonitorModule.POWER_MONITOR_DATA).minimum();
    }

    public void setMinimum(int minimum) {
        PowerMonitorData data = getData(MonitorModule.POWER_MONITOR_DATA);
        data = data.withMinimum((byte) minimum);
        setData(MonitorModule.POWER_MONITOR_DATA, data);
    }

    public int getMaximum() {
        return getData(MonitorModule.POWER_MONITOR_DATA).maximum();
    }

    public void setMaximum(int maximum) {
        PowerMonitorData data = getData(MonitorModule.POWER_MONITOR_DATA);
        data = data.withMaximum((byte) maximum);
        setData(MonitorModule.POWER_MONITOR_DATA, data);
    }

    public void setInvalid() {
        changeRfLevel(0);
        support.setRedstoneState(this, 0);
    }

    @Override
    protected void tickServer() {
        counter--;
        if (counter > 0) {
            return;
        }
        counter = 20;

        Direction inputSide = LogicSupport.getFacing(level.getBlockState(getBlockPos())).getInputSide();
        BlockPos inputPos = getBlockPos().relative(inputSide);
        BlockEntity tileEntity = level.getBlockEntity(inputPos);
        if (!EnergyTools.isEnergyTE(tileEntity, null)) {
            setInvalid();
            return;
        }
        EnergyTools.EnergyLevel energy = EnergyTools.getEnergyLevelMulti(tileEntity, null);
        long maxEnergy = energy.maxEnergy();
        int ratio = 0;  // Will be set as metadata;
        boolean alarm = false;

        PowerMonitorData data = getData(MonitorModule.POWER_MONITOR_DATA);

        if (maxEnergy > 0) {
            long stored = energy.energy();
            ratio = (int) (1 + (stored * 5) / maxEnergy);
            if (ratio < 1) {
                ratio = 1;
            } else if (ratio > 5) {
                ratio = 5;
            }
            long percentage = stored * 100 / maxEnergy;
            alarm = percentage >= data.minimum() && percentage <= data.maximum();
        }

        if (data.rflevel() != ratio) {
            changeRfLevel(ratio);
            setChanged();
        }
        if (alarm != inAlarm) {
            inAlarm = alarm;
            support.setRedstoneState(this, inAlarm ? 15 : 0);
            setChanged();
        }
    }

    private void changeRfLevel(int newRfLevel) {
        PowerMonitorData data = getData(MonitorModule.POWER_MONITOR_DATA);
        if (newRfLevel != data.rflevel()) {
            data = data.withRflevel(newRfLevel);
            setData(MonitorModule.POWER_MONITOR_DATA, data);
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(LEVEL, data.rflevel()), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tagCompound = input.read(com.mojang.serialization.MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
        HolderLookup.Provider provider = input.lookup();
        support.setPowerOutput(tagCompound.getBooleanOr("rs", false) ? 15 : 0);
        inAlarm = tagCompound.getBooleanOr("inAlarm", false);
    }

    @Override
    public void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tagCompound = new CompoundTag();
        HolderLookup.Provider provider = (level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
        tagCompound.putBoolean("inAlarm", inAlarm);

        output.store(com.mojang.serialization.MapCodec.assumeMapUnsafe(CompoundTag.CODEC), tagCompound);
    }

    @Override
    protected void applyImplicitComponents(net.minecraft.core.component.DataComponentGetter input) {
        super.applyImplicitComponents(input);
        var data = input.get(MonitorModule.ITEM_POWER_MONITOR_DATA.get());
        if (data != null) {
            setData(MonitorModule.POWER_MONITOR_DATA, data);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        var data = getData(MonitorModule.POWER_MONITOR_DATA);
        builder.set(MonitorModule.ITEM_POWER_MONITOR_DATA.get(), data);
    }
}
