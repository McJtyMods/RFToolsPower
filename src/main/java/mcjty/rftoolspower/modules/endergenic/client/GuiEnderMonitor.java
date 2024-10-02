package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiEnderMonitor extends GenericGuiContainer<EnderMonitorTileEntity, GenericContainer> {

    public GuiEnderMonitor(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, EndergenicModule.ENDER_MONITOR.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(EndergenicModule.CONTAINER_ENDER_MONITOR.get(), GuiEnderMonitor::new);
    }

    @Override
    public void init() {
        window = new Window(this, getTE(), ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "gui/endermonitor.gui"));
        super.init();
    }
}
