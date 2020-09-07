package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBlazingInfuser extends GenericGuiContainer<BlazingInfuserTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiBlazingInfuser(BlazingInfuserTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, BlazingModule.BLAZING_INFUSER.get().getManualEntry());
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/blazing_infuser.gui"));
        super.init();
        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        drawWindow(matrixStack);
        updateEnergyBar(energyBar);
    }
}
