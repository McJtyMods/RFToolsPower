package mcjty.rftoolspower.modules.blazing.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBlazingGenerator extends GenericGuiContainer<BlazingGeneratorTileEntity, GenericContainer> {

    public GuiBlazingGenerator(BlazingGeneratorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsPower.instance, tileEntity, container, inventory, ManualHelper.create("rftoolspower:xxx"));   // @todo 1.15 manual
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/blazing_generator.gui"));
        super.init();
    }
}
