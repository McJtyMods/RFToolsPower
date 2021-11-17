package mcjty.rftoolspower.modules.generator.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.RFToolsPower;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
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
            .slot(specific(new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.COAL_BLOCK)).in(),
                    CONTAINER_CONTAINER, SLOT_COALINPUT, 82, 24)
            .slot(specific(EnergyTools::isEnergyItem).in().out(), CONTAINER_CONTAINER, SLOT_CHARGEITEM, 118, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, CoalGeneratorConfig.MAXENERGY.get(), 0);

    @Cap(type =  CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crafter")
            .containerSupplier((windowId,player) -> new GenericContainer(CoalGeneratorModule.CONTAINER_COALGENERATOR.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), CoalGeneratorTileEntity.this))
            .dataListener(Sync.values(new ResourceLocation(RFToolsPower.MODID, "data"), this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage));

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
            protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
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
    public void tick() {
        if (!level.isClientSide) {

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
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        burning = info.getInt("burning");
    }

    @Override
    @Nonnull
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
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
}
