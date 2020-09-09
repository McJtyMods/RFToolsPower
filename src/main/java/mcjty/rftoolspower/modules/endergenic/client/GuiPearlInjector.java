package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiPearlInjector extends GenericGuiContainer<PearlInjectorTileEntity, GenericContainer> {

    public GuiPearlInjector(PearlInjectorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, EndergenicModule.PEARL_INJECTOR.get().getManualEntry());
    }

    public static void register() {
        register(EndergenicModule.CONTAINER_PEARL_INJECTOR.get(), GuiPearlInjector::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/pearl_injector.gui"));
        super.init();
    }
}
