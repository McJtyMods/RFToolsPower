package mcjty.rftoolspower.modules.monitor.blocks;


import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class PowerLevelTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    public PowerLevelTileEntity(BlockPos pos, BlockState state) {
        super(MonitorModule.POWER_LEVEL.be().get(), pos, state);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        CompoundTag tag = super.getUpdateTag(lookup);
        tag.putInt("power", support.getPowerOutput());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) {
        super.handleUpdateTag(tag, lookup);
        support.setPowerOutput(tag.getInt("power"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag) {
        tag.putByte("powered", powerLevel);
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag) {
        powerLevel = tag.getByte("powered");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookup) {
        handleUpdateTag(pkt.getTag(), lookup);
    }

    @Override
    protected void tickServer() {
        counter--;
        if (counter > 0) {
            return;
        }
        counter = 10;

        Direction inputSide = LogicSupport.getFacing(level.getBlockState(getBlockPos())).getInputSide();
        BlockPos inputPos = getBlockPos().relative(inputSide);
        BlockEntity tileEntity = level.getBlockEntity(inputPos);
        if (!EnergyTools.isEnergyTE(tileEntity, null)) {
            support.setRedstoneState(this, 0);
            return;
        }
        EnergyTools.EnergyLevel energy = EnergyTools.getEnergyLevelMulti(tileEntity, null);
        long maxEnergy = energy.maxEnergy();
        int ratio = 0;

        if (maxEnergy > 0) {
            long stored = energy.energy();
            ratio = (int) (stored * 10 / maxEnergy);
            if (ratio < 0) {
                ratio = 0;
            } else if (ratio > 9) {
                ratio = 9;
            }
        }
        if (support.getPowerOutput() != ratio) {
            support.setRedstoneState(this, ratio);
            markDirtyClient();
        }
    }
}
