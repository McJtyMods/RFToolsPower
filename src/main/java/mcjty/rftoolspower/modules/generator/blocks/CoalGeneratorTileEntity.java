package mcjty.rftoolspower.modules.generator.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.*;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.generator.CoalGeneratorConfig;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class CoalGeneratorTileEntity extends TickingTileEntity {

    public static final int SLOT_COALINPUT = 0;
    public static final int SLOT_CHARGEITEM = 1;

    private static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(CoalGeneratorTileEntity::isValidFuel).in(), SLOT_COALINPUT, 82, 24)
            .slot(specific(EnergyTools::isEnergyItem).in().out(), SLOT_CHARGEITEM, 118, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> {
                        if (slot == SLOT_COALINPUT) {
                            return isValidFuel(stack);
                        } else {
                            return EnergyTools.isEnergyItem(stack);
                        }
                    }
            ).build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, CoalGeneratorConfig.MAXENERGY.get(), 0);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Coal Generator")
            .containerSupplier(container(CoalGeneratorModule.CONTAINER_COALGENERATOR, CONTAINER_FACTORY, this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .setupSync(this));

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(CoalGeneratorTileEntity.this);

    @Cap(type = CapType.POWER_INFO)
    private final LazyOptional<IPowerInformation> powerInfoHandler = LazyOptional.of(this::createPowerInfo);

    private int burning;

    public CoalGeneratorTileEntity() {
        super(CoalGeneratorModule.TYPE_COALGENERATOR.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(CoalGeneratorTileEntity::new)
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/coalgenerator"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> Long.toString(CoalGeneratorConfig.RFPERTICK.get()) + " RF/FE"))
        ) {
            @Override
            protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(BlockStateProperties.LIT);
            }
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void setPowerInput(int powered) {
        boolean changed = powerLevel != powered;
        super.setPowerInput(powered);
        if (changed) {
            setChanged();
        }
    }

    @Override
    protected void tickServer() {
        markDirtyQuick();
        handleChargingItem(items);
        handleSendingEnergy();

        if (!isMachineEnabled()) {
            return;
        }

        handlePowerGeneration();
    }

    private void handlePowerGeneration() {
        if (burning > 0) {
            burning--;
            long rf = getRfPerTick();
            energyStorage.produceEnergy(rf);
        } else if (!items.getStackInSlot(SLOT_COALINPUT).isEmpty()) {
            ItemStack extracted = items.extractItem(SLOT_COALINPUT, 1, false);
            burning = CoalGeneratorConfig.TICKSPERCOAL.get();
            if (extracted.getItem() == Blocks.COAL_BLOCK.asItem()) {
                burning *= 9;
            }
            float factor = infusable.getInfusedFactor();
            burning += (int) (burning * factor / 2.0f);
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BlockStateProperties.LIT) != isWorking()) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, isWorking()), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public long getRfPerTick() {
        long rf = CoalGeneratorConfig.RFPERTICK.get();
        float factor = infusable.getInfusedFactor();
        rf += (long) (rf * factor);
        return rf;
    }

    public boolean isWorking() {
        return burning > 0 && isMachineEnabled();
    }

    private void handleChargingItem(IItemHandler handler) {
        ItemStack stack = handler.getStackInSlot(SLOT_CHARGEITEM);
        if (!stack.isEmpty()) {
            long storedPower = energyStorage.getEnergy();
            long rfToGive = Math.min(CoalGeneratorConfig.CHARGEITEMPERTICK.get(), storedPower);
            long received = EnergyTools.receiveEnergy(stack, rfToGive);
            energyStorage.consumeEnergy(received);
        }
    }

    private void handleSendingEnergy() {
        long storedPower = energyStorage.getEnergy();
        EnergyTools.handleSendingEnergy(level, worldPosition, storedPower, CoalGeneratorConfig.SENDPERTICK.get(), energyStorage);
    }

    @Override
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        burning = info.getInt("burning");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        CompoundNBT infoTag = getOrCreateInfo(tagCompound);
        infoTag.putInt("burning", burning);
    }

    @Nonnull
    private IPowerInformation createPowerInfo() {
        return new IPowerInformation() {
            @Override
            public long getEnergyDiffPerTick() {
                return burning > 0 ? getRfPerTick() : 0;
            }

            @Nullable
            @Override
            public String getEnergyUnitName() {
                return "RF";
            }

            @Override
            public boolean isMachineActive() {
                return isMachineEnabled();
            }

            @Override
            public boolean isMachineRunning() {
                return isMachineEnabled();
            }

            @Nullable
            @Override
            public String getMachineStatus() {
                return burning > 0 ? "generating power" : "idle";
            }
        };
    }

    private static boolean isValidFuel(@Nonnull ItemStack stack) {
        return stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL || stack.getItem() == Items.COAL_BLOCK;
    }
}
