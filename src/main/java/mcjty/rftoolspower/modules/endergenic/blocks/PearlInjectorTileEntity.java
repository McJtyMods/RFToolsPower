package mcjty.rftoolspower.modules.endergenic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbase.tools.TickOrderHandler;
import mcjty.rftoolspower.compat.RFToolsPowerTOPDriver;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.GenericItemHandler.match;
import static mcjty.lib.container.SlotDefinition.specific;

public class PearlInjectorTileEntity extends TickingTileEntity implements TickOrderHandler.IOrderTicker {

    public static final int BUFFER_SIZE = (9*2);
    public static final int SLOT_BUFFER = 0;
    public static final int SLOT_PLAYERINV = SLOT_BUFFER + BUFFER_SIZE;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(BUFFER_SIZE)
            .box(specific(Items.ENDER_PEARL).in(), SLOT_BUFFER, 10, 25, 9, 2)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY)
            .itemValid(match(Items.ENDER_PEARL))
            .build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<PearlInjectorTileEntity, GenericItemHandler> ITEM_CAP = be -> be.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<PearlInjectorTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Pearl Injector")
            .containerSupplier(container(EndergenicModule.CONTAINER_PEARL_INJECTOR, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .setupSync(be);

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

    public PearlInjectorTileEntity(BlockPos pos, BlockState state) {
        super(EndergenicModule.PEARL_INJECTOR.be().get(), pos, state);
    }

    public EndergenicTileEntity findEndergenicTileEntity() {
        BlockState state = level.getBlockState(getBlockPos());
        Direction k = OrientationTools.getOrientation(state);
        EndergenicTileEntity te = getEndergenicGeneratorAt(k.getOpposite());
        if (te != null) {
            return te;
        }
        return getEndergenicGeneratorAt(Direction.UP);
    }

    private EndergenicTileEntity getEndergenicGeneratorAt(Direction k) {
        BlockPos o = getBlockPos().relative(k);
        BlockEntity te = level.getBlockEntity(o);
        if (te instanceof EndergenicTileEntity) {
            return (EndergenicTileEntity) te;
        }
        return null;
    }

    @Override
    protected void tickServer() {
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

    @Override
    public TickOrderHandler.Rank getRank() {
        return TickOrderHandler.Rank.RANK_0;
    }

    @Override
    public void tickOnServer() {
        boolean pulse = (powerLevel > 0) && !prevIn;
        if (prevIn == powerLevel > 0) {
            return;
        }
        prevIn = powerLevel > 0;

        if (pulse) {
            injectPearl();
        }
        setChanged();
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        prevIn = tag.getBoolean("prevIn");
        items.load(tag, "items", provider);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("prevIn", prevIn);
        items.save(tag, "items", provider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }
}
