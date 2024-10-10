package mcjty.rftoolspower.modules.blazing.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import javax.annotation.Nonnull;

public class GuiBlazingGenerator extends GenericGuiContainer<BlazingGeneratorTileEntity, GenericContainer> {

    private EnergyBar energyBar;
    private final Label[] labels = new Label[4];

    public GuiBlazingGenerator(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, BlazingModule.BLAZING_GENERATOR.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(BlazingModule.CONTAINER_BLAZING_GENERATOR.get(), GuiBlazingGenerator::new);
    }

    @Override
    public void init() {
        window = new Window(this, getBE(), ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "gui/blazing_generator.gui"));
        super.init();
        initializeFields();
    }

    private void initializeFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(getBE().getRSMode().ordinal());
        energyBar = window.findChild("energybar");
        for (int i = 0 ; i < BlazingGeneratorTileEntity.BUFFER_SIZE ; i++) {
            labels[i] = window.findChild("gen" + i);
        }
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        updateEnergyBar(energyBar);

        BlazingGeneratorTileEntity te = getBE();
        for (int i = 0 ; i < BlazingGeneratorTileEntity.BUFFER_SIZE ; i++) {
            labels[i].text(String.valueOf((int) te.getRfPerTick(i)));
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        updateFields();
        drawWindow(graphics, partialTicks, mouseX, mouseY);
    }

}
