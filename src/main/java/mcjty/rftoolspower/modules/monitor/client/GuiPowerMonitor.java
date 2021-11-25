package mcjty.rftoolspower.modules.monitor.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.ScrollableLabel;
import mcjty.lib.gui.widgets.Slider;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;

import java.awt.*;

public class GuiPowerMonitor extends GenericGuiContainer<PowerMonitorTileEntity, GenericContainer> {

    public GuiPowerMonitor(PowerMonitorTileEntity te, GenericContainer container, PlayerInventory inventory) {
        super(te, container, inventory, MonitorModule.POWER_MONITOR.get().getManualEntry());

        imageWidth = 256;
        imageHeight = 50;
    }

    public static void register() {
        register(MonitorModule.CONTAINER_POWER_MONITOR.get(), GuiPowerMonitor::new);
    }

    @Override
    public void init() {
        super.init();

        ScrollableLabel minimumLabel = new ScrollableLabel().name("minimum").suffix("%").desiredWidth(30).realMinimum(0).realMaximum(100)
                .realValue(tileEntity.getMinimum());
        Slider mininumSlider = new Slider().desiredHeight(15).horizontal().minimumKnobSize(15).tooltips("Minimum level").scrollableName("minimum");
        Panel minimumPanel = Widgets.horizontal().children(Widgets.label("Min:").desiredWidth(30), mininumSlider, minimumLabel).desiredHeight(20);

        ScrollableLabel maximumLabel = new ScrollableLabel().name("maximum").suffix("%").desiredWidth(30).realMinimum(0).realMaximum(100)
                .realValue(tileEntity.getMaximum());
        Slider maximumSlider = new Slider().desiredHeight(15).horizontal().minimumKnobSize(15).tooltips("Maximum level").scrollableName("maximum");
        Panel maximumPanel = Widgets.horizontal().children(Widgets.label("Max:").desiredWidth(30), maximumSlider, maximumLabel).desiredHeight(20);

        Panel toplevel = Widgets.vertical().filledRectThickness(2).children(minimumPanel, maximumPanel);
        toplevel.setBounds(new Rectangle(leftPos, topPos, imageWidth, imageHeight));
        window = new Window(this, toplevel);

        window.bind(RFToolsPowerMessages.INSTANCE, "minimum", tileEntity, "minimum");
        window.bind(RFToolsPowerMessages.INSTANCE, "maximum", tileEntity, "maximum");
    }
}
