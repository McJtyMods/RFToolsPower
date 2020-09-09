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
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBlazingGenerator extends GenericGuiContainer<BlazingGeneratorTileEntity, GenericContainer> {

    private EnergyBar energyBar;
    private Label[] labels = new Label[4];

    public GuiBlazingGenerator(BlazingGeneratorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, BlazingModule.BLAZING_GENERATOR.get().getManualEntry());
    }

    public static void register() {
        register(BlazingModule.CONTAINER_BLAZING_GENERATOR.get(), GuiBlazingGenerator::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/blazing_generator.gui"));
        super.init();
        initializeFields();
    }

    private void initializeFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        energyBar = window.findChild("energybar");
        for (int i = 0 ; i < BlazingGeneratorTileEntity.BUFFER_SIZE ; i++) {
            labels[i] = window.findChild("gen" + i);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();
        updateEnergyBar(energyBar);

        for (int i = 0 ; i < BlazingGeneratorTileEntity.BUFFER_SIZE ; i++) {
            labels[i].text(String.valueOf((int) tileEntity.getRfPerTick(i)));
        }
    }
}
