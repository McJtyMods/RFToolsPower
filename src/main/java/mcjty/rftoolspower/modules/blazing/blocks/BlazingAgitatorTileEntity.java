package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.data.AgitatorData;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.modules.blazing.items.BlazingRodStack;
import mcjty.rftoolspower.modules.blazing.logic.BlazingAgitatorAlgorithm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.container.SlotDefinition.specific;

public class BlazingAgitatorTileEntity extends TickingTileEntity {

    public static final int BUFFER_SIZE = 9;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE * 2)
            .box(specific(BlazingAgitatorTileEntity::isValidBlazingRod).in(), 0, 28, 7, 3, 3)
            .box(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).out(), BUFFER_SIZE, 117, 7, 3, 3)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .slotLimit(1)
            .itemValid((slot, stack) -> isValidBlazingRod(stack))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<BlazingAgitatorTileEntity, GenericItemHandler> ITEM_CAP = be -> be.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, BlazingConfiguration.AGITATOR_MAXENERGY.get(),
            BlazingConfiguration.AGITATOR_ENERGY_INPUT_PERTICK.get());
    @Cap(type = CapType.ENERGY)
    private static final Function<BlazingAgitatorTileEntity, GenericEnergyStorage> ENERGY_CAP = be -> be.energyStorage;

    private final DefaultInfusable infusable = new DefaultInfusable(BlazingAgitatorTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<BlazingAgitatorTileEntity, IInfusable> INFUSABLE_CAP = be -> be.infusable;

    @Cap(type = CapType.CONTAINER)
    private static final Function<BlazingAgitatorTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Blazing Agitator")
            .containerSupplier(container(BlazingModule.CONTAINER_BLAZING_AGITATOR, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .setupSync(be);

    public static final VoxelShape SLAB = Shapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);

    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_00 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock00", Type.BOOLEAN, te -> te.isLocked(0, 0), (te, v) -> te.setLocked(0, 0, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_01 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock01", Type.BOOLEAN, te -> te.isLocked(0, 1), (te, v) -> te.setLocked(0, 1, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_02 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock02", Type.BOOLEAN, te -> te.isLocked(0, 2), (te, v) -> te.setLocked(0, 2, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_10 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock10", Type.BOOLEAN, te -> te.isLocked(1, 0), (te, v) -> te.setLocked(1, 0, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_11 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock11", Type.BOOLEAN, te -> te.isLocked(1, 1), (te, v) -> te.setLocked(1, 1, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_12 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock12", Type.BOOLEAN, te -> te.isLocked(1, 2), (te, v) -> te.setLocked(1, 2, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_20 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock20", Type.BOOLEAN, te -> te.isLocked(2, 0), (te, v) -> te.setLocked(2, 0, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_21 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock21", Type.BOOLEAN, te -> te.isLocked(2, 1), (te, v) -> te.setLocked(2, 1, v));
    @GuiValue
    public static final Value<?, Boolean> VALUE_LOCK_22 = Value.<BlazingAgitatorTileEntity, Boolean>create("lock22", Type.BOOLEAN, te -> te.isLocked(2, 2), (te, v) -> te.setLocked(2, 2, v));

    // For the client (renderer): rotation speed and current angle
    private final float[] rotationSpeed = new float[9];
    private final float[] currentAngle = new float[9];
    // Update on server to notify the client:
    private int updateSpeedCounter = 10;  // Every 10 ticks we check if we potentially have to update rotation speed on the client

    private final BlazingAgitatorAlgorithm algorithm;

    public BlazingAgitatorTileEntity(BlockPos pos, BlockState state) {
        super(BlazingModule.BLAZING_AGITATOR.be().get(), pos, state);
        algorithm = new BlazingAgitatorAlgorithm(slot -> new BlazingRodStack(items.getStackInSlot(slot)));
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.METAL))
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
            @Nonnull
            public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
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
    public void saveClientDataToNBT(CompoundTag tag) {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            tag.putFloat("rs" + i, rotationSpeed[i]);
        }
        items.save(tag, "items", getLevel().registryAccess());
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag) {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            rotationSpeed[i] = tag.getFloat("rs" + i);
        }
        items.load(tag, "items", getLevel().registryAccess());
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    protected void tickServer() {
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

    private void tickRods() {
        for (int i = 0 ; i < BUFFER_SIZE ; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.BLAZE_ROD) {
                    items.setStackInSlot(i, new ItemStack(BlazingModule.BLAZING_ROD.get()));
                } else {
                    AgitatorData data = getData(BlazingModule.AGITATOR_DATA);
                    float timeLeft = BlazingRod.getAgitationTimeLeft(stack);
                    if (timeLeft > 0) {
                        algorithm.tickBlazingRod(i, new BlazingRodStack(stack), timeLeft, infusable.getInfusedFactor());
                    } else if (!data.isLocked(i)) {
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
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS + Block.UPDATE_INVISIBLE);
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
        AgitatorData data = getData(BlazingModule.AGITATOR_DATA);
        return data.isLocked(y*3 + x);
    }

    public void setLocked(int x, int y, boolean v) {
        AgitatorData data = getData(BlazingModule.AGITATOR_DATA);
        data.setLocked(y*3 + x, v);
        setData(BlazingModule.AGITATOR_DATA, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        items.save(tag, "items", provider);
        energyStorage.save(tag, "energy", provider);
        infusable.save(tag, "infusable");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        items.load(tag, "items", provider);
        energyStorage.load(tag, "energy", provider);
        infusable.load(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(BlazingModule.ITEM_AGITATOR_DATA);
        if (data != null) {
            setData(BlazingModule.AGITATOR_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        infusable.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(BlazingModule.ITEM_AGITATOR_DATA, getData(BlazingModule.AGITATOR_DATA));
        energyStorage.collectImplicitComponents(builder);
        infusable.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }
}
