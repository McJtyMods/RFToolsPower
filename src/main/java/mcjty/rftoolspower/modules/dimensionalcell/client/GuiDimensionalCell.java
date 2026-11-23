package mcjty.rftoolspower.modules.dimensionalcell.client;

import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellConfiguration;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiDimensionalCell extends GenericGuiContainer<DimensionalCellTileEntity, GenericContainer> {
    public static final int POWERCELL_WIDTH = 180;
    public static final int POWERCELL_HEIGHT = 152;
    private static final int STATS_WIDTH = 202;

    private EnergyBar energyBar;
    private Button stats;
    private Label statsTitle;
    private Label statsInserted;
    private Label statsExtracted;

    private static long lastTime = 0;

    private static final Identifier iconLocation = Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "textures/gui/dimensionalcell.png");

    public GuiDimensionalCell(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, DimensionalCellModule.DIMENSIONAL_CELL.block().get().getManualEntry());

        setWindowDimensions(POWERCELL_WIDTH, POWERCELL_HEIGHT);
    }

    public static void register() {
        net.minecraft.client.gui.screens.MenuScreens.register(DimensionalCellModule.CONTAINER_DIMENSIONAL_CELL.get(), GuiDimensionalCell::new);
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
                .event(this::openStats);
        statsTitle = label(8, 4, STATS_WIDTH - 16, 14, "");
        statsInserted = label(8, 20, STATS_WIDTH - 16, 14, "");
        statsExtracted = label(8, 35, STATS_WIDTH - 16, 14, "");

        Label label = label(26, 30, 40, 18, "Link:").tooltips("Link a powercard to card", "on the left");

        Panel toplevel = positional().background(iconLocation)
                .children(energyBar, allNone, allInput, allOutput, label, stats);
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);

        window.action("allnone", getBE(), DimensionalCellTileEntity.ACTION_SETNONE);
        window.action("allinput", getBE(), DimensionalCellTileEntity.ACTION_SETINPUT);
        window.action("alloutput", getBE(), DimensionalCellTileEntity.ACTION_SETOUTPUT);

        requestRF();
    }

    private void openStats() {
        Panel panel = positional()
                .filledBackground(0xffc6c6c6, 0xff555555)
                .filledRectThickness(1);
        panel.bounds(leftPos + (imageWidth - STATS_WIDTH) / 2, topPos, STATS_WIDTH, 69);
        Window statsWindow = getWindowManager().createModalWindow(panel);
        panel.children(statsTitle,
                statsInserted, statsExtracted,
                button(43, 51, 54, 15, "Clear").name("clearstats"),
                button(105, 51, 54, 15, "Close").event(() -> getWindowManager().closeWindow(statsWindow)));
        statsWindow.action("clearstats", getBE(), DimensionalCellTileEntity.ACTION_CLEARSTATS);
    }

    private void requestRF() {
        if (System.currentTimeMillis() - lastTime > 250) {
            lastTime = System.currentTimeMillis();
            Networking.sendToServer(PacketRequestDataFromServer.create(getBE().getDimension(), getBE().getBlockPos(), ((ICommand) DimensionalCellTileEntity.CMD_GET_INFO).name(), TypedMap.EMPTY, false));
        }
    }


    @Override
    protected void renderBg(@Nonnull GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
        requestRF();

        DimensionalCellTileEntity tileEntity = getBE();
        String inserted = "Inserted: " + tileEntity.tooltipInserted + " FE";
        String extracted = "Extracted: " + tileEntity.tooltipExtracted + " FE";
        String title = tileEntity.tooltipNetworked ? "Network Power Statistics" : "Power Statistics";
        stats.tooltips(title, inserted, extracted);
        statsTitle.text(title);
        statsInserted.text(inserted);
        statsExtracted.text(extracted);

        int maxValue = (tileEntity.tooltipBlocks - tileEntity.tooltipAdvancedBlocks - tileEntity.tooltipSimpleBlocks) * DimensionalCellConfiguration.rfPerNormalCell.get();
        maxValue += tileEntity.tooltipAdvancedBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() * DimensionalCellConfiguration.advancedFactor.get();
        maxValue += tileEntity.tooltipSimpleBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() / DimensionalCellConfiguration.simpleFactor.get();
        energyBar.maxValue(maxValue);
        energyBar.value(tileEntity.tooltipEnergy);

        drawWindow(graphics, partialTicks, mouseX, mouseY);
    }
}
