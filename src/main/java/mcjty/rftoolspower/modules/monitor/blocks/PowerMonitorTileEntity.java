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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerMonitorTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Power Monitor")
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

    public PowerMonitorTileEntity() {
        super(MonitorModule.TYPE_POWER_MONITOR.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powermonitor/powermonitor"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(PowerMonitorTileEntity::new)) {
            @Override
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(LEVEL);
            }
        };
    }

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
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
        TileEntity tileEntity = level.getBlockEntity(inputPos);
        if (!EnergyTools.isEnergyTE(tileEntity, null)) {
            setInvalid();
            return;
        }
        EnergyTools.EnergyLevel energy = EnergyTools.getEnergyLevelMulti(tileEntity, null);
        long maxEnergy = energy.getMaxEnergy();
        int ratio = 0;  // Will be set as metadata;
        boolean alarm = false;

        if (maxEnergy > 0) {
            long stored = energy.getEnergy();
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
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(LEVEL, rflevel), Constants.BlockFlags.DEFAULT_AND_RERENDER);
            setChanged();
        }
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
        inAlarm = tagCompound.getBoolean("inAlarm");
    }

    @Override
    public void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        rflevel = info.getInt("rflevel");
        minimum = info.getByte("minimum");
        maximum = info.getByte("maximum");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
        tagCompound.putBoolean("inAlarm", inAlarm);
    }

    @Override
    public void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("rflevel", rflevel);
        info.putByte("minimum", (byte) minimum);
        info.putByte("maximum", (byte) maximum);
    }
}
