package mcjty.rftoolspower.modules.endergenic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class EnderMonitorTileEntity extends LogicTileEntity implements ITickableTileEntity, TickOrderHandler.IOrderTicker {

    @GuiValue
    private EnderMonitorMode mode = EnderMonitorMode.MODE_LOSTPEARL;

    private boolean needpulse = false;

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Ender Monitor")
            .containerSupplier(empty(EndergenicModule.CONTAINER_ENDER_MONITOR, this))
            .setupSync(this));


    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/endergenic"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(EnderMonitorTileEntity::new));
    }

    public EnderMonitorTileEntity() {
        super(EndergenicModule.TYPE_ENDER_MONITOR.get());
    }

    public EnderMonitorMode getMode() {
        return mode;
    }

    public void setMode(EnderMonitorMode mode) {
        this.mode = mode;
        setChanged();
    }

    /**
     * Callback from the endergenic in case something happens.
     * @param mode is the mode to fire
     */
    public void fireFromEndergenic(EnderMonitorMode mode) {
        if (this.mode != mode) {
            return; // Not monitoring this mode. We do nothing.
        }

        needpulse = true;
        markDirtyQuick();
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            TickOrderHandler.queue(this);
        }
    }

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_2;
    }

    @Override
    public void tickOnServer() {
        int newout = 0;

        if (needpulse) {
            markDirtyQuick();
            newout = 15;
            needpulse = false;
        }

        setRedstoneState(newout);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
        needpulse = tagCompound.getBoolean("needPulse");
    }

    @Override
    public void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        int m = info.getInt("mode");
        mode = EnderMonitorMode.values()[m];
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        tagCompound.putBoolean("needPulse", needpulse);
    }

    @Override
    public void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        getOrCreateInfo(tagCompound).putInt("mode", mode.ordinal());
    }
}
