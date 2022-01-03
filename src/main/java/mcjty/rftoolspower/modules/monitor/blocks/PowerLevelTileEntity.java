package mcjty.rftoolspower.modules.monitor.blocks;


import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerLevelTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    public PowerLevelTileEntity() {
        super(MonitorModule.TYPE_POWER_LEVEL.get());
    }

    private int counter = 20;

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(PowerLevelTileEntity::new));
    }

    public LogicSupport getLogicSupport() {
        return support;
    }
    @Override
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }


    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("power", support.getPowerOutput());
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundTag tag) {
        super.handleUpdateTag(state, tag);
        support.setPowerOutput(tag.getInt("power"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(getBlockState(), pkt.getTag());
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
            support.setRedstoneState(this, 0);
            return;
        }
        EnergyTools.EnergyLevel energy = EnergyTools.getEnergyLevelMulti(tileEntity, null);
        long maxEnergy = energy.getMaxEnergy();
        int ratio = 0;

        if (maxEnergy > 0) {
            long stored = energy.getEnergy();
            ratio = (int) (stored * 10 / maxEnergy);
            if (ratio < 0) {
                ratio = 0;
            } else if (ratio > 9) {
                ratio = 9;
            }
        }
        support.setRedstoneState(this, ratio);
    }
}
