package mcjty.rftoolspower.modules.endergenic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorData;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class EnderMonitorTileEntity extends TickingTileEntity implements TickOrderHandler.IOrderTicker {

    private final LogicSupport support = new LogicSupport();

    private boolean needpulse = false;

    @Cap(type = CapType.CONTAINER)
    private static final Function<EnderMonitorTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Ender Monitor")
            .containerSupplier(empty(EndergenicModule.CONTAINER_ENDER_MONITOR, be))
            .data(EndergenicModule.ENDER_MONITOR_DATA, EnderMonitorData.STREAM_CODEC)
            .setupSync(be);


    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/endergenic"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(EnderMonitorTileEntity::new));
    }

    public EnderMonitorTileEntity(BlockPos pos, BlockState state) {
        super(EndergenicModule.ENDER_MONITOR.be().get(), pos, state);
    }

    public EnderMonitorMode getMode() {
        return getData(EndergenicModule.ENDER_MONITOR_DATA).mode();
    }

    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public void setMode(EnderMonitorMode mode) {
        setData(EndergenicModule.ENDER_MONITOR_DATA, new EnderMonitorData(mode));
    }

    /**
     * Callback from the endergenic in case something happens.
     * @param mode is the mode to fire
     */
    public void fireFromEndergenic(EnderMonitorMode mode) {
        EnderMonitorData data = getData(EndergenicModule.ENDER_MONITOR_DATA);
        if (data.mode() != mode) {
            return; // Not monitoring this mode. We do nothing.
        }

        needpulse = true;
        markDirtyQuick();
    }

    @Override
    protected void tickServer() {
        TickOrderHandler.queue(this);
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

        support.setRedstoneState(this, newout);
    }

    @Override
    public void loadAdditional(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
        needpulse = tagCompound.getBoolean("needPulse");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound, HolderLookup.Provider lookup) {
        super.saveAdditional(tagCompound, lookup);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
        tagCompound.putBoolean("needPulse", needpulse);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(EndergenicModule.ITEM_ENDER_MONITOR_DATA);
        if (data != null) {
            setData(EndergenicModule.ENDER_MONITOR_DATA, data);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        var data = getData(EndergenicModule.ENDER_MONITOR_DATA);
        builder.set(EndergenicModule.ITEM_ENDER_MONITOR_DATA, data);
    }
}
