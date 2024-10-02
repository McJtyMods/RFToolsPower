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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiDimensionalCell extends GenericGuiContainer<DimensionalCellTileEntity, GenericContainer> {
    public static final int POWERCELL_WIDTH = 180;
    public static final int POWERCELL_HEIGHT = 152;

    private EnergyBar energyBar;
    private Button stats;

    private static long lastTime = 0;

    private static final ResourceLocation iconLocation = ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "textures/gui/dimensionalcell.png");

    public GuiDimensionalCell(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, DimensionalCellModule.DIMENSIONAL_CELL.block().get().getManualEntry());

        imageWidth = POWERCELL_WIDTH;
        imageHeight = POWERCELL_HEIGHT;
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(DimensionalCellModule.CONTAINER_DIMENSIONAL_CELL.get(), GuiDimensionalCell::new);
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
        toplevel.bounds(leftPos, topPos, imageWidth, imageHeight);

        window = new Window(this, toplevel);

        window.action("allnone", getTE(), DimensionalCellTileEntity.ACTION_SETNONE);
        window.action("allinput", getTE(), DimensionalCellTileEntity.ACTION_SETINPUT);
        window.action("alloutput", getTE(), DimensionalCellTileEntity.ACTION_SETOUTPUT);
        window.action("clearstats", getTE(), DimensionalCellTileEntity.ACTION_CLEARSTATS);

        requestRF();
    }

    private void requestRF() {
        if (System.currentTimeMillis() - lastTime > 250) {
            lastTime = System.currentTimeMillis();
            Networking.sendToServer(PacketRequestDataFromServer.create(getTE().getDimension(), getTE().getBlockPos(), ((ICommand) DimensionalCellTileEntity.CMD_GET_INFO).name(), TypedMap.EMPTY, false));
        }
    }


    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        drawWindow(graphics, partialTicks, mouseX, mouseY);

        requestRF();

        DimensionalCellTileEntity tileEntity = getTE();
        stats.tooltips("Power statistics. Press to clear:", "Inserted: " + tileEntity.tooltipInserted, "Extracted: " + tileEntity.tooltipExtracted);

        int maxValue = (tileEntity.tooltipBlocks - tileEntity.tooltipAdvancedBlocks - tileEntity.tooltipSimpleBlocks) * DimensionalCellConfiguration.rfPerNormalCell.get();
        maxValue += tileEntity.tooltipAdvancedBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() * DimensionalCellConfiguration.advancedFactor.get();
        maxValue += tileEntity.tooltipSimpleBlocks * DimensionalCellConfiguration.rfPerNormalCell.get() / DimensionalCellConfiguration.simpleFactor.get();
        energyBar.maxValue(maxValue);
        energyBar.value(tileEntity.tooltipEnergy);
    }
}
