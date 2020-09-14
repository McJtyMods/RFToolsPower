package mcjty.rftoolspower.modules.monitor.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerMonitorTileEntity extends LogicTileEntity implements ITickableTileEntity {

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Power Monitor")
            .containerSupplier((windowId,player) -> new GenericContainer(MonitorModule.CONTAINER_POWER_MONITOR.get(), windowId, ContainerFactory.EMPTY.get(), getPos(), PowerMonitorTileEntity.this)));

    public static IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);

    public static final Key<Integer> VALUE_MINIMUM = new Key<>("minimum", Type.INTEGER);
    public static final Key<Integer> VALUE_MAXIMUM = new Key<>("maximum", Type.INTEGER);

    // Persisted data
    private int minimum;        // Minimum power percentage
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
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(LEVEL);
            }
        };
    }

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_MINIMUM, this::getMinimum, this::setMinimum),
                new DefaultValue<>(VALUE_MAXIMUM, this::getMaximum, this::setMaximum)
        };
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
        markDirtyClient();
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        markDirtyClient();
    }

    public void setInvalid() {
        changeRfLevel(0);
        setRedstoneState(0);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        counter--;
        if (counter > 0) {
            return;
        }
        counter = 20;

        Direction inputSide = getFacing(world.getBlockState(getPos())).getInputSide();
        BlockPos inputPos = getPos().offset(inputSide);
        TileEntity tileEntity = world.getTileEntity(inputPos);
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
            markDirtyClient();
        }
        if (alarm != inAlarm) {
            inAlarm = alarm;
            setRedstoneState(inAlarm ? 15 : 0);
            markDirty();
        }
    }

    private void changeRfLevel(int newRfLevel) {
        if (newRfLevel != rflevel) {
            rflevel = newRfLevel;
            world.setBlockState(pos, world.getBlockState(pos).with(LEVEL, rflevel), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            markDirty();
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
        inAlarm = tagCompound.getBoolean("inAlarm");
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        rflevel = info.getInt("rflevel");
        minimum = info.getByte("minimum");
        maximum = info.getByte("maximum");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        tagCompound.putBoolean("inAlarm", inAlarm);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("rflevel", rflevel);
        info.putByte("minimum", (byte) minimum);
        info.putByte("maximum", (byte) maximum);
    }

//    @Override
//    public void checkRedstone(World world, BlockPos pos) {
//        super.checkRedstone(world, pos);
//        tick();
//    }
//
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
