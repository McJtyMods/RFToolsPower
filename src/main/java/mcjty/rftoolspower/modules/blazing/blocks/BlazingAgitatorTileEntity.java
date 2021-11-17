package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.Val;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.modules.blazing.items.BlazingRodStack;
import mcjty.rftoolspower.modules.blazing.logic.BlazingAgitatorAlgorithm;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class BlazingAgitatorTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static int BUFFER_SIZE = 9;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE * 2)
            .box(specific(BlazingAgitatorTileEntity::isValidBlazingRod).in(), CONTAINER_CONTAINER, 0, 28, 7, 3, 3)
            .box(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).out(), CONTAINER_CONTAINER, BUFFER_SIZE, 117, 7, 3, 3)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final NoDirectionItemHander items = createItemHandler();

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, BlazingConfiguration.AGITATOR_MAXENERGY.get(),
            BlazingConfiguration.AGITATOR_ENERGY_INPUT_PERTICK.get());

    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(BlazingAgitatorTileEntity.this);

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Blazing Agitator")
            .containerSupplier((windowId,player) -> new GenericContainer(BlazingModule.CONTAINER_BLAZING_AGITATOR.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), BlazingAgitatorTileEntity.this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage));

    public static VoxelShape SLAB = VoxelShapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);

    @Val
    public static final Value<?, Boolean> VALUE_LOCK_00 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(0, 0), (te, v) -> te.setLocked(0, 0, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_01 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(0, 1), (te, v) -> te.setLocked(0, 1, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_02 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(0, 2), (te, v) -> te.setLocked(0, 2, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_10 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(1, 0), (te, v) -> te.setLocked(1, 0, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_11 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(1, 1), (te, v) -> te.setLocked(1, 1, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_12 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(1, 2), (te, v) -> te.setLocked(1, 2, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_20 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(2, 0), (te, v) -> te.setLocked(2, 0, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_21 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(2, 1), (te, v) -> te.setLocked(2, 1, v));
    @Val
    public static final Value<?, Boolean> VALUE_LOCK_22 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(2, 2), (te, v) -> te.setLocked(2, 2, v));

    // For the client (renderer): rotation speed and current angle
    private float[] rotationSpeed = new float[9];
    private float[] currentAngle = new float[9];
    // Update on server to notify the client:
    private int updateSpeedCounter = 10;  // Every 10 ticks we check if we potentially have to update rotation speed on the client

    private BlazingAgitatorAlgorithm algorithm;
    private boolean locked[] = new boolean[BUFFER_SIZE];

    public BlazingAgitatorTileEntity() {
        super(BlazingModule.TYPE_BLAZING_AGITATOR.get());
        algorithm = new BlazingAgitatorAlgorithm(slot -> new BlazingRodStack(items.getStackInSlot(slot)));
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                AbstractBlock.Properties.of(Material.METAL).strength(2.0f).sound(SoundType.METAL))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/blazingagitator"))
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
        if (stack.getItem() == BlazingModule.BLAZING_ROD.get()) {
            return BlazingRod.getInfusionStepsLeft(stack) >= BlazingRod.MAX_INFUSION_STEPS;
        }
        return false;
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            tagCompound.putFloat("rs" + i, rotationSpeed[i]);
        }
        writeItemHandlerCap(tagCompound);
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
        if (!level.isClientSide) {
            boolean active = false;
            if (isMachineEnabled()) {
                if (energyStorage.getEnergy() >= BlazingConfiguration.AGITATOR_USE_PER_TICK.get()) {
                    energyStorage.consumeEnergy(BlazingConfiguration.AGITATOR_USE_PER_TICK.get());
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
                    items.setStackInSlot(i, new ItemStack(BlazingModule.BLAZING_ROD.get()));
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
                level.sendBlockUpdated(worldPosition, state, state, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NO_RERENDER);
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
        setChanged();
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
}
