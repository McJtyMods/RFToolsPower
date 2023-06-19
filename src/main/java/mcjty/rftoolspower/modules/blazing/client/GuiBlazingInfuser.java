package mcjty.rftoolspower.modules.blazing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiBlazingInfuser extends GenericGuiContainer<BlazingInfuserTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiBlazingInfuser(BlazingInfuserTileEntity tileEntity, GenericContainer container, Inventory inventory) {
        super(tileEntity, container, inventory, BlazingModule.BLAZING_INFUSER.get().getManualEntry());
    }

    public static void register() {
        register(BlazingModule.CONTAINER_BLAZING_INFUSER.get(), GuiBlazingInfuser::new);
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
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        drawWindow(graphics);
        updateEnergyBar(energyBar);
    }
}
