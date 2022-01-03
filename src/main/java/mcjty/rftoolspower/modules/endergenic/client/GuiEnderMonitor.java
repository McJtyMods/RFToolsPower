package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

public class GuiEnderMonitor extends GenericGuiContainer<EnderMonitorTileEntity, GenericContainer> {

    public GuiEnderMonitor(EnderMonitorTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, EndergenicModule.ENDER_MONITOR.get().getManualEntry());
    }

    public static void register() {
        register(EndergenicModule.CONTAINER_ENDER_MONITOR.get(), GuiEnderMonitor::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/endermonitor.gui"));
        super.init();
    }
}
