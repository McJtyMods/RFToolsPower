package mcjty.rftoolspower.modules.blazing.client;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

public class GuiBlazingAgitator extends GenericGuiContainer<BlazingAgitatorTileEntity, GenericContainer> {

    private EnergyBar energyBar;


    public GuiBlazingAgitator(BlazingAgitatorTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(RFToolsPower.instance, tileEntity, container, inventory, ManualHelper.create("rftoolspower:xxx"));   // @todo 1.15 manual
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/blazing_agitator.gui"));
        super.init();
        initializeFields();
        setupEvents();
    }

    private void initializeFields() {
        ((ImageChoiceLabel) window.findChild("redstone")).setCurrentChoice(tileEntity.getRSMode().ordinal());
        energyBar = window.findChild("energybar");
    }

    private void setupEvents() {
        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                String channel = "lock" + x + "" + y;
                window.bind(RFToolsPowerMessages.INSTANCE, channel, tileEntity, channel);
            }
        }
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