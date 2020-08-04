package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
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
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class BlazingGeneratorTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static int BUFFER_SIZE = 4;

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE)
            .slot(specific(new ItemStack(BlazingSetup.BLAZING_ROD.get())), CONTAINER_CONTAINER, 0, 10, 7)
            .slot(specific(new ItemStack(BlazingSetup.BLAZING_ROD.get())), CONTAINER_CONTAINER, 1, 10+18*4, 7)
            .slot(specific(new ItemStack(BlazingSetup.BLAZING_ROD.get())), CONTAINER_CONTAINER, 2, 10, 7+18*2)
            .slot(specific(new ItemStack(BlazingSetup.BLAZING_ROD.get())), CONTAINER_CONTAINER, 3, 10+18*4, 7+18*2)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, false, BlazingConfiguration.GENERATOR_MAXENERGY.get(), 0);
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> storage);

    private final IInfusable infusable = new DefaultInfusable(BlazingGeneratorTileEntity.this);
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> infusable);

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Blazing Generator")
            .containerSupplier((windowId,player) -> new GenericContainer(BlazingSetup.CONTAINER_BLAZING_GENERATOR.get(), windowId, CONTAINER_FACTORY.get(), getPos(), BlazingGeneratorTileEntity.this))
            .itemHandler(itemHandler)
            .energyHandler(energyHandler)
            .shortListener(getRfPerTickHolder(0))
            .shortListener(getRfPerTickHolder(1))
            .shortListener(getRfPerTickHolder(2))
            .shortListener(getRfPerTickHolder(3)));

    private IntReferenceHolder getRfPerTickHolder(int slot) {
        return new IntReferenceHolder() {
            @Override
            public int get() {
                return (int) rfPerTick[slot];
            }

            @Override
            public void set(int value) {
                // Client side value
                rfPerTick[slot] = value;
            }
        };
    }

    public BlazingGeneratorTileEntity() {
        super(BlazingSetup.TYPE_BLAZING_GENERATOR.get());
    }

    // Maximum RF/tick for a slot for the given blazing rod
    private int rfPerTickMax[] = new int[BUFFER_SIZE];
    // Current RF/tick for a slot
    private float rfPerTick[] = new float[BUFFER_SIZE];
    // Ticks remaining until the blazing rod is spent
    private int ticksRemaining[] = new int[BUFFER_SIZE];

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                Block.Properties.create(Material.IRON).hardnessAndResistance(2.0f).sound(SoundType.METAL))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(BlazingGeneratorTileEntity::new)) {
            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(WORKING);
            }
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            handleSendingEnergy();
            handlePowerGeneration();
        }
    }

    @Override
    public void setPowerInput(int powered) {
        if (powerLevel != powered) {
            super.setPowerInput(powered);
            for (int i = 0 ; i < BUFFER_SIZE ; i++) {
                updateSlot(i);
            }
        }
    }

    @Override
    public void setRSMode(RedstoneMode redstoneMode) {
        super.setRSMode(redstoneMode);
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            updateSlot(i);
        }
    }

    // Used client side to display in the gui
    public float getRfPerTick(int slot) {
        return rfPerTick[slot];
    }

    private void handleSendingEnergy() {
        long storedPower = storage.getEnergy();
        EnergyTools.handleSendingEnergy(world, pos, storedPower, BlazingConfiguration.GENERATOR_SENDPERTICK.get(), storage);
    }

    private void handlePowerGeneration() {
        int totalRfGenerated = 0;
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            if (rfPerTick[i] > rfPerTickMax[i]) {
                // We need to bring power down
                rfPerTick[i] += (rfPerTickMax[i] - rfPerTick[i]) / 30.0f;
                if (rfPerTick[i] < rfPerTickMax[i]) {
                    rfPerTick[i] = rfPerTickMax[i];
                }
            } else if (rfPerTick[i] < rfPerTickMax[i]) {
                // We need to bring power up
                rfPerTick[i] += (rfPerTickMax[i] - rfPerTick[i]) / 150.0f + 0.01f;
                if (rfPerTick[i] > rfPerTickMax[i]) {
                    rfPerTick[i] = rfPerTickMax[i];
                }
            }
            totalRfGenerated += (int) rfPerTick[i];
            if (isMachineEnabled()) {
                ticksRemaining[i]--;
                ItemStack stack = items.getStackInSlot(i);
                if (ticksRemaining[i] <= 0) {
                    ticksRemaining[i] = 0;
                    rfPerTickMax[i] = 0;
                    if (!stack.isEmpty()) {
                        items.setStackInSlot(i, ItemStack.EMPTY);
                    }
                } else {
                    if (!stack.isEmpty()) {
                        BlazingRod.setPowerDuration(stack, ticksRemaining[i]);
                    }
                }
            }
        }
        storage.produceEnergy(totalRfGenerated);

        boolean generating = totalRfGenerated > 0;
        BlockState state = world.getBlockState(pos);
        if (state.get(WORKING) != generating) {
            world.setBlockState(pos, state.with(WORKING, generating), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }

        markDirtyQuick();
    }

    private void updateSlot(int slot) {
        ItemStack stack = items.getStackInSlot(slot);
        if (stack.isEmpty() || !isMachineEnabled()) {
            rfPerTickMax[slot] = 0;
            ticksRemaining[slot] = 0;
        } else {
            rfPerTickMax[slot] = (int) (BlazingRod.getRfPerTick(stack) * (infusable.getInfusedFactor() * 0.1f + 1.0f));
            ticksRemaining[slot] = BlazingRod.getTotalTicks(stack);
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            rfPerTickMax[i] = tagCompound.getInt("rftMax" + i);
            rfPerTick[i] = tagCompound.getFloat("rft" + i);
            ticksRemaining[i] = tagCompound.getInt("ticks" + i);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            tagCompound.putInt("rftMax" + i, rfPerTickMax[i]);
            tagCompound.putFloat("rft" + i, rfPerTick[i]);
            tagCompound.putInt("ticks" + i, ticksRemaining[i]);
        }
        return super.write(tagCompound);
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == BlazingSetup.BLAZING_ROD.get();
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onUpdate(int index) {
                updateSlot(index);
                super.onUpdate(index);
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
        return super.getCapability(cap, facing);
    }
}
