package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiPearlInjector extends GenericGuiContainer<PearlInjectorTileEntity, GenericContainer> {

    public GuiPearlInjector(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, EndergenicModule.PEARL_INJECTOR.get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(EndergenicModule.CONTAINER_PEARL_INJECTOR.get(), GuiPearlInjector::new);
    }

    @Override
    public void init() {
        window = new Window(this, getTE(), ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "gui/pearl_injector.gui"));
        super.init();
    }
}
