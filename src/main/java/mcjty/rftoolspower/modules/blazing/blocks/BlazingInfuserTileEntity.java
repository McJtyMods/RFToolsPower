package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.blazing.BlazingConfiguration;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class BlazingInfuserTileEntity extends GenericTileEntity implements ITickableTileEntity {

    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final int SLOT_CATALYST = 2;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(3)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).in(), CONTAINER_CONTAINER, SLOT_INPUT, 46, 7)
            .slot(specific(new ItemStack(BlazingModule.BLAZING_ROD.get())).out(), CONTAINER_CONTAINER, SLOT_OUTPUT, 100, 7)
            .slot(specific(stack -> getCatalystImprovement(stack) != null).in(), CONTAINER_CONTAINER, SLOT_CATALYST, 46, 25)
            .playerSlots(10, 70));

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, BlazingConfiguration.INFUSER_MAXENERGY.get(),
            BlazingConfiguration.INFUSER_ENERGY_INPUT_PERTICK.get());
    @Cap(type = CapType.ENERGY)
    private final LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private final NoDirectionItemHander items = createItemHandler();
    @Cap(type = CapType.ITEMS)
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Blazing Infuserr")
            .containerSupplier((windowId,player) -> new GenericContainer(BlazingModule.CONTAINER_BLAZING_INFUSER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), BlazingInfuserTileEntity.this))
            .itemHandler(() -> items)
            .energyHandler(() -> energyStorage));

    public BlazingInfuserTileEntity() {
        super(BlazingModule.TYPE_BLAZING_INFUSER.get());
    }

    private int counter = 10;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder().properties(
                AbstractBlock.Properties.of(Material.METAL).strength(2.0f).sound(SoundType.METAL))
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/blazinginfuser"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(BlazingInfuserTileEntity::new));
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            counter--;
            if (counter < 0) {
                counter = 10;
                ItemStack stack = items.getStackInSlot(SLOT_INPUT);
                if (stack.getItem() == BlazingModule.BLAZING_ROD.get()) {
                    int steps = BlazingRod.getInfusionStepsLeft(stack);
                    if (steps > 0) {
                        ItemStack catalyst = items.getStackInSlot(SLOT_CATALYST);
                        if (!catalyst.isEmpty()) {
                            if (energyStorage.getEnergy() >= BlazingConfiguration.INFUSER_USE_PER_TICK.get()) {
                                energyStorage.consumeEnergy(BlazingConfiguration.INFUSER_USE_PER_TICK.get());
                                steps--;
                                BlazingRod.setInfusionStepsLeft(stack, steps);
                                items.extractItem(SLOT_CATALYST, 1, false);
                                infuse(stack, catalyst);
                                if (steps <= 0) {
                                    // Move the infused blazing rod to the output area if possible
                                    if (items.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
                                        items.setStackInSlot(SLOT_OUTPUT, stack);
                                        items.setStackInSlot(SLOT_INPUT, ItemStack.EMPTY);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            markDirtyQuick();
        }
    }

    // Return <quality,duration> improvement factor (expressed in percentage when an entire stack would be used for infusion)
    @Nullable
    private static Pair<Float, Float> getCatalystImprovement(ItemStack stack) {
        if (stack.getItem() == Items.REDSTONE) {
            return Pair.of(40.0f, 0.0f);
        } else if (stack.getItem() == Items.GLOWSTONE_DUST) {
            return Pair.of(10.0f, 30.0f);
        } else if (stack.getItem() == Items.QUARTZ) {
            return Pair.of(0.0f, 40.0f);
        } else if (stack.getItem() == VariousModule.DIMENSIONALSHARD.get()) {
            return Pair.of(60.0f, 60.0f);
        } else if (stack.getItem() == Items.NETHER_STAR) {
            return Pair.of(120.0f, 120.0f);
        } else if (stack.getItem() == Items.GHAST_TEAR) {
            return Pair.of(80.0f, 80.0f);
        } else {
            return null;
        }
    }

    private void infuse(ItemStack stack, ItemStack catalyst) {
        Pair<Float, Float> pair = getCatalystImprovement(catalyst);
        if (pair != null) {
            improveQuality(stack, pair.getLeft());
            improveDuration(stack, pair.getRight());
        }
    }

    private void improveDuration(ItemStack stack, float factor) {
        float duration = BlazingRod.getPowerDuration(stack);
        duration += duration * factor / (100 * BlazingRod.MAX_INFUSION_STEPS);
        BlazingRod.setPowerDuration(stack, duration);
    }

    private void improveQuality(ItemStack stack, float factor) {
        float quality = BlazingRod.getPowerQuality(stack);
        quality += quality * factor / (100 * BlazingRod.MAX_INFUSION_STEPS);
        BlazingRod.setPowerQuality(stack, quality);
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot) {
                    case SLOT_INPUT:
                    case SLOT_OUTPUT:
                        return stack.getItem() == BlazingModule.BLAZING_ROD.get();
                    case SLOT_CATALYST:
                        return getCatalystImprovement(stack) != null;
                    default:
                        return false;
                }
            }


            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }
}
