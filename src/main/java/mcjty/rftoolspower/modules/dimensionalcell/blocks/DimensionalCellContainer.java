package mcjty.rftoolspower.modules.dimensionalcell.blocks;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

import static mcjty.lib.container.SlotDefinition.container;
import static mcjty.lib.container.SlotDefinition.specific;
import static mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup.CONTAINER_DIMENSIONAL_CELL;

public class DimensionalCellContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_CARD = 0;
    public static final int SLOT_CARDCOPY = 1;
    public static final int SLOT_CHARGEITEM = 2;

    public static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory(3) {
        @Override
        protected void setup() {
            slot(specific(new ItemStack(DimensionalCellSetup.POWERCELL_CARD)), CONTAINER_INVENTORY, SLOT_CARD, 28, 8);
            slot(specific(new ItemStack(DimensionalCellSetup.POWERCELL_CARD)), CONTAINER_INVENTORY, SLOT_CARDCOPY, 64, 30);
            slot(container(), CONTAINER_INVENTORY, SLOT_CHARGEITEM, 64, 8);
            playerSlots(10, 70);
        }
    };

    public DimensionalCellContainer(int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(CONTAINER_DIMENSIONAL_CELL, id, factory, pos, te);
    }
}
