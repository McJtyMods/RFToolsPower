package mcjty.rftoolspower.blocks.generator;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.information.IMachineInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolspower.blocks.ModBlocks;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.config.CoalGeneratorConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoalGeneratorTileEntity extends GenericTileEntity implements ITickableTileEntity, IMachineInformation {

    public static final String REGNAME = "coalgenerator";
    public static final String CMD_RSMODE = "coalgen.setRsMode";

    public static final int SLOT_COALINPUT = 0;
    public static final int SLOT_CHARGEITEM = 1;

    public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(2) {
        @Override
        protected void setup() {
            slot(SlotDefinition.specific(new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.COAL_BLOCK)),
                    CONTAINER_CONTAINER, SLOT_COALINPUT, 82, 24);
            slot(SlotDefinition.specific(stack -> EnergyTools.isEnergyItem(stack)), CONTAINER_CONTAINER, SLOT_CHARGEITEM, 118, 24);
            playerSlots(10, 70);
        }
    };

    private LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> new NoDirectionItemHander(this, CONTAINER_FACTORY));
    private LazyOptional<GenericEnergyStorage> energyHandler = LazyOptional.of(() -> new GenericEnergyStorage(this, true, CoalGeneratorConfig.MAXENERGY.get(), 0));
    private LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crafter")
            .containerSupplier((windowId,player) -> new GenericContainer(ModBlocks.CONTAINER_COALGENERATOR, windowId, CONTAINER_FACTORY, getPos(), CoalGeneratorTileEntity.this))
            .itemHandler(itemHandler)
            .energyHandler(energyHandler));
    private LazyOptional<IInfusable> infusableHandler = LazyOptional.of(() -> new DefaultInfusable(CoalGeneratorTileEntity.this));

    private int burning;

    public CoalGeneratorTileEntity() {
        super(ModBlocks.TYPE_COALGENERATOR);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(REGNAME, new BlockBuilder()
                .tileEntitySupplier(CoalGeneratorTileEntity::new)
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .info("message.rftoolspower.shiftmessage")
                .infoExtended("message.rftoolspower.coalgenerator")
                .infoExtendedParameter(stack -> Long.toString(CoalGeneratorConfig.RFPERTICK.get()))
        ) {
            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(BlockStateProperties.LIT);
            }

            @Override
            public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
                return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
            }
        };
    }


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

            itemHandler.ifPresent(itemHandler -> {
                energyHandler.ifPresent(energy -> {
                    markDirtyQuick();
                    handleChargingItem(itemHandler, energy);
                    handleSendingEnergy(energy);

                    if (!isMachineEnabled()) {
                        return;
                    }

                    if (burning > 0) {
                        burning--;
                        long rf = getRfPerTick();
                        energy.produceEnergy(rf);
                    } else if (!itemHandler.getStackInSlot(SLOT_COALINPUT).isEmpty()) {
                        ItemStack extracted = itemHandler.extractItem(SLOT_COALINPUT, 1, false);
                        burning = CoalGeneratorConfig.TICKSPERCOAL.get();
                        if (extracted.getItem() == Item.getItemFromBlock(Blocks.COAL_BLOCK)) {
                            burning *= 9;
                        }
                        float factor = infusableHandler.map(inf -> inf.getInfusedFactor()).orElse(0.0f);
                        burning += (int) (burning * factor / 2.0f);
                    }
                });
            });

            BlockState state = world.getBlockState(pos);
            if (state.get(BlockStateProperties.LIT) != isWorking()) {
                world.setBlockState(pos, state.with(BlockStateProperties.LIT, isWorking()), 3);
            }
        }
    }

    public long getRfPerTick() {
        long rf = CoalGeneratorConfig.RFPERTICK.get();
        float factor = infusableHandler.map(inf -> inf.getInfusedFactor()).orElse(0.0f);
        rf += (long) (rf * factor);
        return rf;
    }

    public boolean isWorking() {
        return burning > 0 && isMachineEnabled();
    }

    private void handleChargingItem(IItemHandler handler, GenericEnergyStorage storage) {
        ItemStack stack = handler.getStackInSlot(SLOT_CHARGEITEM);
        if (!stack.isEmpty()) {
            long storedPower = storage.getEnergy();
            long rfToGive = Math.min(CoalGeneratorConfig.CHARGEITEMPERTICK.get(), storedPower);
            long received = EnergyTools.receiveEnergy(stack, rfToGive);
            storage.consumeEnergy(received);
        }
    }

    private void handleSendingEnergy(GenericEnergyStorage storage) {
        long storedPower = storage.getEnergy();

        for (Direction facing : OrientationTools.DIRECTION_VALUES) {
            BlockPos p = pos.offset(facing);
            TileEntity te = world.getTileEntity(p);
            Direction opposite = facing.getOpposite();
            if (EnergyTools.isEnergyTE(te, opposite)) {
                long rfToGive = Math.min(CoalGeneratorConfig.SENDPERTICK.get(), storedPower);
                long received = EnergyTools.receiveEnergy(te, opposite, rfToGive);
                storage.consumeEnergy(received);
                storedPower = storage.getEnergy();
                if (storedPower <= 0) {
                    break;
                }
            }
        }
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

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_RSMODE.equals(command)) {
            setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)]);
            return true;
        }

        return false;
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    @Optional.Method(modid = "waila")
//    public void addWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//        super.addWailaBody(itemStack, currenttip, accessor, config);
//        if (isWorking()) {
//            currenttip.add(TextFormatting.GREEN + "Producing " + getRfPerTick() + " RF/t");
//        }
//    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
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
