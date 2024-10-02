package mcjty.rftoolspower.modules.blazing.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

public class GuiBlazingAgitator extends GenericGuiContainer<BlazingAgitatorTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiBlazingAgitator(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, BlazingModule.BLAZING_AGITATOR.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(BlazingModule.CONTAINER_BLAZING_AGITATOR.get(), GuiBlazingAgitator::new);
    }

    @Override
    public void init() {
        window = new Window(this, getTE(), ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "gui/blazing_agitator.gui"));
        super.init();
        initializeFields();
        setupEvents();
    }

    private void initializeFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(getTE().getRSMode().ordinal());
        energyBar = window.findChild("energybar");
    }

    private void setupEvents() {
        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                String channel = "lock" + x + "" + y;
                window.bind(channel, getTE(), channel);
            }
        }
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        updateEnergyBar(energyBar);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(graphics, partialTicks, mouseX, mouseY);
    }
}
