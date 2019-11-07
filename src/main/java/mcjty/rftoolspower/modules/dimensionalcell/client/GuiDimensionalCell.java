package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellContainer;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.network.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiDimensionalCell extends GenericGuiContainer<DimensionalCellTileEntity, DimensionalCellContainer> {
    public static final int POWERCELL_WIDTH = 180;
    public static final int POWERCELL_HEIGHT = 152;

    private EnergyBar energyBar;
    private Button stats;

    private static long lastTime = 0;

    private static final ResourceLocation iconLocation = new ResourceLocation(RFToolsPower.MODID, "textures/gui/powercell.png");

    public GuiDimensionalCell(DimensionalCellTileEntity te, DimensionalCellContainer container, PlayerInventory inventory) {
        super(RFToolsPower.instance, RFToolsPowerMessages.INSTANCE, te, container, inventory, 0/*@todo 1.14 GuiProxy.GUI_MANUAL_MAIN*/, "powercell");

        xSize = POWERCELL_WIDTH;
        ySize = POWERCELL_HEIGHT;
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar(minecraft, this).setVertical().setMaxValue(1000).setLayoutHint(10, 7, 8, 54).setShowText(false);
        energyBar.setValue(0);

        Button allNone = new Button(minecraft, this)
                .setName("allnone")
                .setText("None").setTooltips("Set all sides to 'none'")
                .setLayoutHint(140, 10, 32, 15);
        Button allInput = new Button(minecraft, this)
                .setName("allinput")
                .setText("In").setTooltips("Set all sides to", "accept energy")
                .setLayoutHint(140, 27, 32, 15);
        Button allOutput = new Button(minecraft, this)
                .setName("alloutput")
                .setText("Out").setTooltips("Set all sides to", "send energy")
                .setLayoutHint(140, 44, 32, 15);

        stats = new Button(minecraft, this)
                .setName("clearstats")
                .setText("Stats").setTooltips("Power statistics. Press to clear:")
                .setLayoutHint(100, 10, 32, 15);

        Label label = new Label(minecraft, this);
        label.setText("Link:").setTooltips("Link a powercard to card", "on the left").setLayoutHint(26, 30, 40, 18);

        Panel toplevel = new Panel(minecraft, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChildren(energyBar, allNone, allInput, allOutput, label, stats);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

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
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();

        requestRF();

        stats.setTooltips("Power statistics. Press to clear:", "Inserted: " + DimensionalCellTileEntity.tooltipInserted, "Extracted: " + DimensionalCellTileEntity.tooltipExtracted);

        int maxValue = (DimensionalCellTileEntity.tooltipBlocks - DimensionalCellTileEntity.tooltipAdvancedBlocks - DimensionalCellTileEntity.tooltipSimpleBlocks) * DimensionalCellConfiguration.rfPerNormalCell.get();
        maxValue += DimensionalCellTileEntity.tooltipAdvancedBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() * DimensionalCellConfiguration.advancedFactor.get();
        maxValue += DimensionalCellTileEntity.tooltipSimpleBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() / DimensionalCellConfiguration.simpleFactor.get();
        energyBar.setMaxValue(maxValue);
        energyBar.setValue(DimensionalCellTileEntity.tooltipEnergy);
    }
}
