package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiPearlInjector extends GenericGuiContainer<PearlInjectorTileEntity, GenericContainer> {

    public GuiPearlInjector(PearlInjectorTileEntity pearlInjectorTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsPower.instance, pearlInjectorTileEntity, container, inventory, ManualHelper.create("rftoolspower:xxx"));   // @todo 1.15 manual
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/pearl_injector.gui"));
        super.init();
    }
}
