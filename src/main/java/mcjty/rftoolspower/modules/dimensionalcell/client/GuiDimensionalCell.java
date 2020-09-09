package mcjty.rftoolspower.modules.dimensionalcell.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellContainer;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiDimensionalCell extends GenericGuiContainer<DimensionalCellTileEntity, DimensionalCellContainer> {
    public static final int POWERCELL_WIDTH = 180;
    public static final int POWERCELL_HEIGHT = 152;

    private EnergyBar energyBar;
    private Button stats;

    private static long lastTime = 0;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsPower.MODID, "textures/gui/dimensionalcell.png");

    public GuiDimensionalCell(DimensionalCellTileEntity te, DimensionalCellContainer container, PlayerInventory inventory) {
        super(te, container, inventory, DimensionalCellModule.DIMENSIONAL_CELL.get().getManualEntry());

        xSize = POWERCELL_WIDTH;
        ySize = POWERCELL_HEIGHT;
    }

    public static void register() {
        register(DimensionalCellModule.CONTAINER_DIMENSIONAL_CELL.get(), GuiDimensionalCell::new);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().vertical().maxValue(1000).hint(10, 7, 8, 54).showText(false);
        energyBar.value(0);

        Button allNone = button(140, 10, 32, 15, "None")
                .name("allnone")
                .tooltips("Set all sides to 'none'");
        Button allInput = button(140, 27, 32, 15, "In")
                .name("allinput")
                .tooltips("Set all sides to", "accept energy");
        Button allOutput = button(140, 44, 32, 15, "Out")
                .name("alloutput")
                .tooltips("Set all sides to", "send energy");

        stats = button(100, 10, 32, 15, "Stats")
                .name("clearstats")
                .tooltips("Power statistics. Press to clear:");

        Label label = label(26, 30, 40, 18, "Link:").tooltips("Link a powercard to card", "on the left");

        Panel toplevel = positional().background(iconLocation)
                .children(energyBar, allNone, allInput, allOutput, label, stats);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);

        window = new Window(this, toplevel);

        window.action(RFToolsPowerMessages.INSTANCE, "allnone", tileEntity, DimensionalCellTileEntity.ACTION_SETNONE);
        window.action(RFToolsPowerMessages.INSTANCE, "allinput", tileEntity, DimensionalCellTileEntity.ACTION_SETINPUT);
        window.action(RFToolsPowerMessages.INSTANCE, "alloutput", tileEntity, DimensionalCellTileEntity.ACTION_SETOUTPUT);
        window.action(RFToolsPowerMessages.INSTANCE, "clearstats", tileEntity, DimensionalCellTileEntity.ACTION_CLEARSTATS);

        requestRF();
    }

    private void requestRF() {
        if (System.currentTimeMillis() - lastTime > 250) {
            lastTime = System.currentTimeMillis();
            tileEntity.requestDataFromServer(RFToolsPowerMessages.INSTANCE, DimensionalCellTileEntity.CMD_GET_INFO, TypedMap.EMPTY);
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float v, int i, int i2) {
        drawWindow(matrixStack);

        requestRF();

        stats.tooltips("Power statistics. Press to clear:", "Inserted: " + DimensionalCellTileEntity.tooltipInserted, "Extracted: " + DimensionalCellTileEntity.tooltipExtracted);

        int maxValue = (DimensionalCellTileEntity.tooltipBlocks - DimensionalCellTileEntity.tooltipAdvancedBlocks - DimensionalCellTileEntity.tooltipSimpleBlocks) * DimensionalCellConfiguration.rfPerNormalCell.get();
        maxValue += DimensionalCellTileEntity.tooltipAdvancedBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() * DimensionalCellConfiguration.advancedFactor.get();
        maxValue += DimensionalCellTileEntity.tooltipSimpleBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() / DimensionalCellConfiguration.simpleFactor.get();
        energyBar.maxValue(maxValue);
        energyBar.value(DimensionalCellTileEntity.tooltipEnergy);
    }
}
