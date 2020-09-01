package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;

import static mcjty.lib.container.SlotDefinition.container;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule.CONTAINER_DIMENSIONAL_CELL;

public class DimensionalCellContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_CARD = 0;
    public static final int SLOT_CARDCOPY = 1;
    public static final int SLOT_CHARGEITEM = 2;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(3)
            .slot(specific(new ItemStack(DimensionalCellModule.POWERCELL_CARD.get())), CONTAINER_INVENTORY, SLOT_CARD, 28, 8)
            .slot(specific(new ItemStack(DimensionalCellModule.POWERCELL_CARD.get())), CONTAINER_INVENTORY, SLOT_CARDCOPY, 64, 30)
            .slot(container(), CONTAINER_INVENTORY, SLOT_CHARGEITEM, 64, 8)
            .playerSlots(10, 70));

    public DimensionalCellContainer(int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(CONTAINER_DIMENSIONAL_CELL.get(), id, factory, pos, te);
    }
}
