package mcjty.rftoolspower.modules.endergenic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.specific;

public class PearlInjectorTileEntity extends GenericTileEntity implements ITickableTileEntity, TickOrderHandler.IOrderTicker {

    public static final int BUFFER_SIZE = (9*2);
    public static final int SLOT_BUFFER = 0;
    public static final int SLOT_PLAYERINV = SLOT_BUFFER + BUFFER_SIZE;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE)
            .box(specific(new ItemStack(Items.ENDER_PEARL)).in(), CONTAINER_CONTAINER, SLOT_BUFFER, 10, 25, 9, 2)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Pearl Injector")
            .containerSupplier((windowId,player) -> new GenericContainer(EndergenicModule.CONTAINER_PEARL_INJECTOR.get(), windowId, CONTAINER_FACTORY.get(), getPos(), PearlInjectorTileEntity.this))
            .itemHandler(() -> items));

    // For pulse detection.
    private boolean prevIn = false;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolspower:powergeneration/pearlinjector"))
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(PearlInjectorTileEntity::new));
    }

    public PearlInjectorTileEntity() {
        super(EndergenicModule.TYPE_PEARL_INJECTOR.get());
    }

    public EndergenicTileEntity findEndergenicTileEntity() {
        BlockState state = world.getBlockState(getPos());
        Direction k = OrientationTools.getOrientation(state);
        EndergenicTileEntity te = getEndergenicGeneratorAt(k.getOpposite());
        if (te != null) {
            return te;
        }
        return getEndergenicGeneratorAt(Direction.UP);
    }

    private EndergenicTileEntity getEndergenicGeneratorAt(Direction k) {
        BlockPos o = getPos().offset(k);
        TileEntity te = world.getTileEntity(o);
        if (te instanceof EndergenicTileEntity) {
            return (EndergenicTileEntity) te;
        }
        return null;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            long ticker = TickOrderHandler.getTicker();
            TickOrderHandler.queue(this);

            // Find all connected endergenics in order
            EndergenicTileEntity endergenic = findEndergenicTileEntity();
            Set<BlockPos> connectedEndergenics = new HashSet<>();
            while (endergenic != null && !connectedEndergenics.contains(endergenic.getBlockPos())) {
                // Don't add endergenics that have already been added this tick
                if (ticker != endergenic.getTicker()) {
                    endergenic.setTicker(ticker);
                    TickOrderHandler.queue(endergenic);
                }
                connectedEndergenics.add(endergenic.getBlockPos());
                endergenic = endergenic.getDestinationTE();
            }
        }
    }

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_0;
    }

    @Override
    public void tickServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        if (prevIn == powerLevel > 0) {
            return;
        }
        prevIn = powerLevel > 0;

        if (pulse) {
            injectPearl();
        }
        markDirty();
    }

    private boolean takePearl() {
        for (int i = 0 ; i < items.getSlots() ; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (!stack.isEmpty() && Items.ENDER_PEARL.equals(stack.getItem()) && stack.getCount() > 0) {
                items.decrStackSize(i, 1);
                return true;
            }
        }
        return false;
    }

    public void injectPearl() {
        EndergenicTileEntity endergen = findEndergenicTileEntity();
        if (endergen != null) {
            if (!takePearl()) {
                // No pearls in the inventory.
                return;
            }
            int mode = endergen.getChargingMode();
            // If the endergenic is already holding a pearl then this one is lost.
            if (mode != EndergenicTileEntity.CHARGE_HOLDING) {
                // It can accept a pearl.
                endergen.firePearlFromInjector();
            }
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        prevIn = tagCompound.getBoolean("prevIn");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.putBoolean("prevIn", prevIn);
        return tagCompound;
    }


    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == Items.ENDER_PEARL;
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
            return itemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
