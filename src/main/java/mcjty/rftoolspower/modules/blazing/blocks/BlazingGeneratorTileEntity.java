package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.RedstoneMode;
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.GenericItemHandler.match;
import static mcjty.lib.container.SlotDefinition.specific;

public class BlazingGeneratorTileEntity extends TickingTileEntity {

    public static final int BUFFER_SIZE = 4;

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).in(),0, 10, 7)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).in(),1, 10+18*4, 7)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).in(),2, 10, 7+18*2)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).in(),3, 10+18*4, 7+18*2)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .slotLimit(1)
            .itemValid(match(BlazingModule.BLAZING_ROD))
            .onUpdate((slot, stack) -> updateSlot(slot))
            .build();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, BlazingConfiguration.GENERATOR_MAXENERGY.get(), 0);

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(BlazingGeneratorTileEntity.this);

    // Maximum RF/tick for a slot for the given blazing rod
    private final int[] rfPerTickMax = new int[BUFFER_SIZE];
    // Current RF/tick for a slot
    private final float[] rfPerTick = new float[BUFFER_SIZE];
    // Ticks remaining until the blazing rod is spent
    private final int[] ticksRemaining = new int[BUFFER_SIZE];

    @Cap(type = CapType.CONTAINER)
    private final Lazy<MenuProvider> screenHandler = Lazy.of(() -> new DefaultContainerProvider<GenericContainer>("Blazing Generator")
            .containerSupplier(container(BlazingModule.CONTAINER_BLAZING_GENERATOR, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage)
            .shortListener(Sync.integer(() -> (int) rfPerTick[0], v -> rfPerTick[0] = v))
            .shortListener(Sync.integer(() -> (int) rfPerTick[1], v1 -> rfPerTick[1] = v1))
            .shortListener(Sync.integer(() -> (int) rfPerTick[2], v2 -> rfPerTick[2] = v2))
            .shortListener(Sync.integer(() -> (int) rfPerTick[3], v3 -> rfPerTick[3] = v3))
            .setupSync(this));

    public BlazingGeneratorTileEntity(BlockPos pos, BlockState state) {
        super(BlazingModule.TYPE_BLAZING_GENERATOR.get(), pos, state);
    }


    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.METAL))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/blazinggenerator"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(BlazingGeneratorTileEntity::new)) {
            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
                super.createBlockStateDefinition(builder);
                builder.add(WORKING);
            }
        };
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    protected void tickServer() {
        handleSendingEnergy();
        handlePowerGeneration();
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
        long storedPower = energyStorage.getEnergy();
        EnergyTools.handleSendingEnergy(level, worldPosition, storedPower, BlazingConfiguration.GENERATOR_SENDPERTICK.get(), energyStorage);
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
        energyStorage.produceEnergy(totalRfGenerated);

        boolean generating = totalRfGenerated > 0;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(WORKING) != generating) {
            level.setBlock(worldPosition, state.setValue(WORKING, generating), Block.UPDATE_ALL);
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
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            rfPerTickMax[i] = tagCompound.getInt("rftMax" + i);
            rfPerTick[i] = tagCompound.getFloat("rft" + i);
            ticksRemaining[i] = tagCompound.getInt("ticks" + i);
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            tagCompound.putInt("rftMax" + i, rfPerTickMax[i]);
            tagCompound.putFloat("rft" + i, rfPerTick[i]);
            tagCompound.putInt("ticks" + i, ticksRemaining[i]);
        }
        super.saveAdditional(tagCompound);
    }

}
