package mcjty.rftoolspower.modules.generator.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

public class GuiCoalGenerator extends GenericGuiContainer<CoalGeneratorTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiCoalGenerator(CoalGeneratorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsPower.instance, tileEntity, container, inventory, 0, "coalgen");
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/coalgenerator.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawWindow();

        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> {
            energyBar.maxValue(((GenericEnergyStorage)e).getCapacity());
            energyBar.value(((GenericEnergyStorage)e).getEnergy());
        });
    }
}
