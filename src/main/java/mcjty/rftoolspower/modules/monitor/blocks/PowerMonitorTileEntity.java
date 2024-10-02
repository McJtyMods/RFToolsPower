package mcjty.rftoolspower.modules.monitor.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerMonitorTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Power Monitor")
            .containerSupplier(empty(MonitorModule.CONTAINER_POWER_MONITOR, this))
            .setupSync(this));

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);

    // Persisted data
    @GuiValue
    private int minimum;        // Minimum power percentage
    @GuiValue
    private int maximum;        // Maximum power percentage

    // Transient data
    private int rflevel = 0;
    private boolean inAlarm = false;
    private int counter = 20;

    public PowerMonitorTileEntity(BlockPos pos, BlockState state) {
        super(MonitorModule.TYPE_POWER_MONITOR.get(), pos, state);
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powermonitor/powermonitor"))
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
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
        setChanged();
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        setChanged();
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

        if (maxEnergy > 0) {
            long stored = energy.energy();
            ratio = (int) (1 + (stored * 5) / maxEnergy);
            if (ratio < 1) {
                ratio = 1;
            } else if (ratio > 5) {
                ratio = 5;
            }
            long percentage = stored * 100 / maxEnergy;
            alarm = percentage >= minimum && percentage <= maximum;
        }

        if (rflevel != ratio) {
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
        if (newRfLevel != rflevel) {
            rflevel = newRfLevel;
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(LEVEL, rflevel), Block.UPDATE_ALL_IMMEDIATE);
            setChanged();
        }
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        // @todo 1.21 data
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
        inAlarm = tagCompound.getBoolean("inAlarm");
    }

    @Override
    public void loadInfo(CompoundTag tagCompound) {
        super.loadInfo(tagCompound);
        // @todo 1.21 data
        CompoundTag info = tagCompound.getCompound("Info");
        rflevel = info.getInt("rflevel");
        minimum = info.getByte("minimum");
        maximum = info.getByte("maximum");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.saveAdditional(tagCompound, provider);
        // @todo 1.21 data
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
        tagCompound.putBoolean("inAlarm", inAlarm);
    }

    @Override
    public void saveInfo(CompoundTag tagCompound) {
        super.saveInfo(tagCompound);
        // @todo 1.21 data
        CompoundTag info = getOrCreateInfo(tagCompound);
        info.putInt("rflevel", rflevel);
        info.putByte("minimum", (byte) minimum);
        info.putByte("maximum", (byte) maximum);
    }
}
