package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.modules.blazing.items.BlazingRodStack;
import mcjty.rftoolspower.modules.blazing.logic.BlazingAgitatorAlgorithm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
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

public class BlazingAgitatorTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static int BUFFER_SIZE = 9;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE * 2)
            .box(specific(BlazingAgitatorTileEntity::isValidBlazingRod), CONTAINER_CONTAINER, 0, 28, 7, 3, 3)
            .box(specific(new ItemStack(BlazingSetup.BLAZING_ROD.get())), CONTAINER_CONTAINER, BUFFER_SIZE, 117, 7, 3, 3)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items) {
        @Override
        public boolean canAutomationInsert(int slot) {
            return slot < BUFFER_SIZE;
        }

        @Override
        public boolean canAutomationExtract(int slot) {
            return slot >= BUFFER_SIZE;
        }
    });


    private final GenericEnergyStorage storage = new GenericEnergyStorage(this, true, BlazingConfiguration.AGITATOR_MAXENERGY.get(),
            BlazingConfiguration.AGITATOR_ENERGY_INPUT_PERTICK.get());
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> storage);

    private final IInfusable infusable = new DefaultInfusable(BlazingAgitatorTileEntity.this);
    private final LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> infusable);

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Blazing Agitator")
            .containerSupplier((windowId,player) -> new GenericContainer(BlazingSetup.CONTAINER_BLAZING_AGITATOR.get(), windowId, CONTAINER_FACTORY.get(), getPos(), BlazingAgitatorTileEntity.this))
            .itemHandler(itemHandler)
            .energyHandler(energyHandler));

    public static VoxelShape SLAB = VoxelShapes.create(0f, 0f, 0f, 1f, 0.5f, 1f);

    public static final Key<Boolean> VALUE_LOCK_00 = new Key<>("lock00", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_01 = new Key<>("lock01", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_02 = new Key<>("lock02", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_10 = new Key<>("lock10", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_11 = new Key<>("lock11", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_12 = new Key<>("lock12", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_20 = new Key<>("lock20", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_21 = new Key<>("lock21", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_LOCK_22 = new Key<>("lock22", Type.BOOLEAN);

    // For the client (renderer): rotation speed and current angle
    private float[] rotationSpeed = new float[9];
    private float[] currentAngle = new float[9];
    // Update on server to notify the client:
    private int updateSpeedCounter = 10;  // Every 10 ticks we check if we potentially have to update rotation speed on the client

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_LOCK_00, () -> isLocked(0, 0), b -> setLocked(0, 0, b)),
                new DefaultValue<>(VALUE_LOCK_01, () -> isLocked(0, 1), b -> setLocked(0, 1, b)),
                new DefaultValue<>(VALUE_LOCK_02, () -> isLocked(0, 2), b -> setLocked(0, 2, b)),
                new DefaultValue<>(VALUE_LOCK_10, () -> isLocked(1, 0), b -> setLocked(1, 0, b)),
                new DefaultValue<>(VALUE_LOCK_11, () -> isLocked(1, 1), b -> setLocked(1, 1, b)),
                new DefaultValue<>(VALUE_LOCK_12, () -> isLocked(1, 2), b -> setLocked(1, 2, b)),
                new DefaultValue<>(VALUE_LOCK_20, () -> isLocked(2, 0), b -> setLocked(2, 0, b)),
                new DefaultValue<>(VALUE_LOCK_21, () -> isLocked(2, 1), b -> setLocked(2, 1, b)),
                new DefaultValue<>(VALUE_LOCK_22, () -> isLocked(2, 2), b -> setLocked(2, 2, b))
        };
    }

    private BlazingAgitatorAlgorithm algorithm;
    private boolean locked[] = new boolean[BUFFER_SIZE];

    public BlazingAgitatorTileEntity() {
        super(BlazingSetup.TYPE_BLAZING_AGITATOR.get());
        algorithm = new BlazingAgitatorAlgorithm(slot -> new BlazingRodStack(items.getStackInSlot(slot)));
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                Block.Properties.create(Material.IRON).hardnessAndResistance(2.0f).sound(SoundType.METAL))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold())
                .tileEntitySupplier(BlazingAgitatorTileEntity::new)) {
            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
                return SLAB;
            }
        };
    }

    // Return true if this blaze rod or blazing rod is valid on the left side. This means
    // it must be either a normal blaze rod, or else it must be a non-infused blazing rod
    private static boolean isValidBlazingRod(ItemStack stack) {
        if (stack.getItem() == Items.BLAZE_ROD) {
            return true;
        }
        if (stack.getItem() == BlazingSetup.BLAZING_ROD.get()) {
            return BlazingRod.getInfusionStepsLeft(stack) >= BlazingRod.MAX_INFUSION_STEPS;
        }
        return false;
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            tagCompound.putFloat("rs" + i, rotationSpeed[i]);
        }
    }

    @Override
    public void readClientDataFromNBT(CompoundNBT tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            rotationSpeed[i] = tagCompound.getFloat("rs" + i);
        }
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            boolean active = false;
            if (isMachineEnabled()) {
                if (storage.getEnergy() >= BlazingConfiguration.AGITATOR_USE_PER_TICK.get()) {
                    storage.consumeEnergy(BlazingConfiguration.AGITATOR_USE_PER_TICK.get());
                    active = true;
                    tickRods();
                }
            }

            updateClientRotationSpeed(active);
        }
    }

    private void tickRods() {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.BLAZE_ROD) {
                    items.setStackInSlot(i, new ItemStack(BlazingSetup.BLAZING_ROD.get()));
                } else {
                    float timeLeft = BlazingRod.getAgitationTimeLeft(stack);
                    if (timeLeft > 0) {
                        algorithm.tickBlazingRod(i, new BlazingRodStack(stack), timeLeft, infusable.getInfusedFactor());
                    } else if (!locked[i]) {
                        moveToOutput(i, stack);
                    }
                }
                markDirtyQuick();
            }
        }
    }

    private void updateClientRotationSpeed(boolean active) {
        updateSpeedCounter--;
        if (updateSpeedCounter < 0) {
            updateSpeedCounter = 10;
            boolean changed = false;
            // Check for current speed
            for (int i = 0 ; i < BUFFER_SIZE ; i++) {
                float newspeed = 0;
                if (active) {
                    ItemStack stack = items.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        float percentage = BlazingRod.getAgitationTimePercentage(stack);
                        newspeed = percentage / 50.0f;
                    }
                }
                if (Math.abs(newspeed - rotationSpeed[i]) > 0.01) {
                    rotationSpeed[i] = newspeed;
                    changed = true;
                }
            }
            if (changed) {
                BlockState state = getBlockState();
                world.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NO_RERENDER);
            }
        }
    }

    private void moveToOutput(int i, ItemStack stack) {
        // Move the rod to the output area if there is room
        for (int j = BUFFER_SIZE ; j < BUFFER_SIZE + BUFFER_SIZE ; j++) {
            if (items.getStackInSlot(j).isEmpty()) {
                items.setStackInSlot(i, ItemStack.EMPTY);
                items.setStackInSlot(j, stack);
                return;
            }
        }
    }

    public float getCurrentAngle(int x, int y) {
        return currentAngle[y*3 + x];
    }

    public void setCurrentAngle(int x, int y, float currentAngle) {
        this.currentAngle[y*3 + x] = currentAngle;
    }

    public float getRotationSpeed(int x, int y) {
        return rotationSpeed[y*3 + x];
    }

    public boolean isLocked(int x, int y) {
        return locked[y*3 + x];
    }

    public void setLocked(int x, int y, boolean v) {
        locked[y*3 + x] = v;
        markDirtyClient();
    }

    @Override
    protected void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        byte[] bytes = info.getByteArray("locked");
        if (bytes.length >= BUFFER_SIZE) {
            for (int i = 0 ; i < BUFFER_SIZE ; i++) {
                locked[i] = bytes[i] != 0;
            }
        }
    }

    @Override
    protected void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        byte[] bytes = new byte[BUFFER_SIZE];
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            bytes[i] = (byte) (locked[i] ? 1 : 0);
        }
        info.putByteArray("locked", bytes);
    }


    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isValidBlazingRod(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
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
