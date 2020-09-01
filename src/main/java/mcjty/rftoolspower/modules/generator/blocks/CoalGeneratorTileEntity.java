package mcjty.rftoolspower.modules.generator.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.generator.CoalGeneratorConfig;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class CoalGeneratorTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT_COALINPUT = 0;
    public static final int SLOT_CHARGEITEM = 1;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.COAL_BLOCK)),
                    CONTAINER_CONTAINER, SLOT_COALINPUT, 82, 24)
            .slot(specific(EnergyTools::isEnergyItem), CONTAINER_CONTAINER, SLOT_CHARGEITEM, 118, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, false, CoalGeneratorConfig.MAXENERGY.get(), 0);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> storage);

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crafter")
            .containerSupplier((windowId,player) -> new GenericContainer(CoalGeneratorModule.CONTAINER_COALGENERATOR.get(), windowId, CONTAINER_FACTORY.get(), getPos(), CoalGeneratorTileEntity.this))
            .itemHandler(itemHandler)
            .energyHandler(energyHandler));

    private final IInfusable infusable = new DefaultInfusable(CoalGeneratorTileEntity.this);
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> infusable);
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
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> Long.toString(CoalGeneratorConfig.RFPERTICK.get()) + " RF/FE"))
        ) {
            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
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
            markDirtyClient();
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {

            markDirtyQuick();
            handleChargingItem(items);
            handleSendingEnergy();

            if (!isMachineEnabled()) {
                return;
            }

            handlePowerGeneration();
        }
    }

    private void handlePowerGeneration() {
        if (burning > 0) {
            burning--;
            long rf = getRfPerTick();
            storage.produceEnergy(rf);
        } else if (!items.getStackInSlot(SLOT_COALINPUT).isEmpty()) {
            ItemStack extracted = items.extractItem(SLOT_COALINPUT, 1, false);
            burning = CoalGeneratorConfig.TICKSPERCOAL.get();
            if (extracted.getItem() == Item.getItemFromBlock(Blocks.COAL_BLOCK)) {
                burning *= 9;
            }
            float factor = infusable.getInfusedFactor();
            burning += (int) (burning * factor / 2.0f);
        }

        BlockState state = world.getBlockState(pos);
        if (state.get(BlockStateProperties.LIT) != isWorking()) {
            world.setBlockState(pos, state.with(BlockStateProperties.LIT, isWorking()), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
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
            long storedPower = storage.getEnergy();
            long rfToGive = Math.min(CoalGeneratorConfig.CHARGEITEMPERTICK.get(), storedPower);
            long received = EnergyTools.receiveEnergy(stack, rfToGive);
            storage.consumeEnergy(received);
        }
    }

    private void handleSendingEnergy() {
        long storedPower = storage.getEnergy();
        EnergyTools.handleSendingEnergy(world, pos, storedPower, CoalGeneratorConfig.SENDPERTICK.get(), storage);
    }
    
    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        burning = info.getInt("burning");
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        CompoundNBT infoTag = getOrCreateInfo(tagCompound);
        infoTag.putInt("burning", burning);
        return tagCompound;
    }

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

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_COALINPUT) {
                    return (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL || stack.getItem() == Items.COAL_BLOCK);
                } else {
                    return EnergyTools.isEnergyItem(stack);
                }
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return automationItemHandler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        if (cap == CapabilityInfusable.INFUSABLE_CAPABILITY) {
            return infusableHandler.cast();
        }
        if (cap == CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY) {
            return powerInfoHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
