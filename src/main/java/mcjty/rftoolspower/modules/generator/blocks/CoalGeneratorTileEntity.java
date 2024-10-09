package mcjty.rftoolspower.modules.generator.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.IPowerInformation;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.EnergyTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.generator.CoalGeneratorConfig;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.generator.data.CoalGeneratorData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;

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

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid((slot, stack) -> {
                        if (slot == SLOT_COALINPUT) {
                            return isValidFuel(stack);
                        } else {
                            return EnergyTools.isEnergyItem(stack);
                        }
                    }
            ).build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<CoalGeneratorTileEntity, GenericItemHandler> ITEM_CAP = tile -> tile.items;

    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, false, CoalGeneratorConfig.MAXENERGY.get(), 0);
    @Cap(type = CapType.ENERGY)
    private static final Function<CoalGeneratorTileEntity, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    @Cap(type = CapType.CONTAINER)
    private static final Function<CoalGeneratorTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Coal Generator")
            .containerSupplier(container(CoalGeneratorModule.CONTAINER_COALGENERATOR, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .setupSync(be);

    private final DefaultInfusable infusable = new DefaultInfusable(CoalGeneratorTileEntity.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<CoalGeneratorTileEntity, IInfusable> INFUSABLE_CAP = tile -> tile.infusable;

    private final IPowerInformation powerInfoHandler = createPowerInfo();
    @Cap(type = CapType.POWER_INFO)
    private static final Function<CoalGeneratorTileEntity, IPowerInformation> POWER_INFO_CAP = tile -> tile.powerInfoHandler;

    public CoalGeneratorTileEntity(BlockPos pos, BlockState state) {
        super(CoalGeneratorModule.COALGENERATOR.be().get(), pos, state);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(CoalGeneratorTileEntity::new)
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/coalgenerator"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> CoalGeneratorConfig.RFPERTICK.get() + " RF/FE"))
        ) {
            @Override
            protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
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
        CoalGeneratorData data = getData(CoalGeneratorModule.COAL_GENERATOR_DATA);
        int burning = data.burning();
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
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, isWorking()), Block.UPDATE_ALL);
        }
        data = data.withBurning(burning);
        setData(CoalGeneratorModule.COAL_GENERATOR_DATA, data);
    }

    public long getRfPerTick() {
        long rf = CoalGeneratorConfig.RFPERTICK.get();
        float factor = infusable.getInfusedFactor();
        rf += (long) (rf * factor);
        return rf;
    }

    public boolean isWorking() {
        return getData(CoalGeneratorModule.COAL_GENERATOR_DATA).burning() > 0 && isMachineEnabled();
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        energyStorage.save(tag, "energy", provider);
        items.save(tag, "items", provider);
        infusable.save(tag, "infusable");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyStorage.load(tag, "energy", provider);
        items.load(tag, "items", provider);
        infusable.load(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(CoalGeneratorModule.ITEM_COAL_GENERATOR_DATA);
        if (data != null) {
            setData(CoalGeneratorModule.COAL_GENERATOR_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
        infusable.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(CoalGeneratorModule.ITEM_COAL_GENERATOR_DATA, getData(CoalGeneratorModule.COAL_GENERATOR_DATA));
        energyStorage.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
        infusable.collectImplicitComponents(builder);
    }

    @Nonnull
    private IPowerInformation createPowerInfo() {
        return new IPowerInformation() {
            @Override
            public long getEnergyDiffPerTick() {
                return getData(CoalGeneratorModule.COAL_GENERATOR_DATA).burning() > 0 ? getRfPerTick() : 0;
            }

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

            @Override
            public String getMachineStatus() {
                return getData(CoalGeneratorModule.COAL_GENERATOR_DATA).burning() > 0 ? "generating power" : "idle";
            }
        };
    }

    private static boolean isValidFuel(@Nonnull ItemStack stack) {
        return stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL || stack.getItem() == Items.COAL_BLOCK;
    }
}
