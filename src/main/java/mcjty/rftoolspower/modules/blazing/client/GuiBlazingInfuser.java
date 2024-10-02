package mcjty.rftoolspower.modules.blazing.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

public class GuiBlazingInfuser extends GenericGuiContainer<BlazingInfuserTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiBlazingInfuser(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, BlazingModule.BLAZING_INFUSER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(BlazingModule.CONTAINER_BLAZING_INFUSER.get(), GuiBlazingInfuser::new);
    }

    @Override
    public void init() {
        window = new Window(this, getTE(), ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "gui/blazing_infuser.gui"));
        super.init();
        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        drawWindow(graphics, partialTicks, mouseX, mouseY);
        updateEnergyBar(energyBar);
    }
}
