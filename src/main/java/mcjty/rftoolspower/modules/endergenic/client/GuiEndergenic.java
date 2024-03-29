package mcjty.rftoolspower.modules.endergenic.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.setup.RFToolsPowerMessages;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class GuiEndergenic extends GenericGuiContainer<EndergenicTileEntity, GenericContainer> {

    private EnergyBar energyBar;
    private TextField lastRfPerTick;
    private TextField lastLostPearls;
    private TextField lastLaunchedPearls;
    private TextField lastOpportunities;

    private int timer = 10;

    public GuiEndergenic(EndergenicTileEntity endergenicTileEntity, GenericContainer container, PlayerInventory inventory) {
        super(endergenicTileEntity, container, inventory, EndergenicModule.ENDERGENIC.get().getManualEntry());
    }

    public static void register() {
        register(EndergenicModule.CONTAINER_ENDERGENIC.get(), GuiEndergenic::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, RFToolsPowerMessages.INSTANCE, new ResourceLocation(RFToolsPower.MODID, "gui/endergenic.gui"));
        super.init();

        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
        lastRfPerTick = window.findChild("lastrft");
        lastLostPearls = window.findChild("lastlost");
        lastLaunchedPearls = window.findChild("lastlaunched");
        lastOpportunities = window.findChild("lastopp");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        energyBar.maxValue(tileEntity.getCapacity());

        lastRfPerTick.text(tileEntity.clientLastRfPerTick + " RF/tick");
        lastLostPearls.text(tileEntity.clientLastPearlsLost + " pearls");
        lastLaunchedPearls.text(tileEntity.clientLastPearlsLaunched + " pearls");
        lastOpportunities.text(tileEntity.clientLastPearlOpportunities + " times");
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float v, int i, int i2) {
        updateFields();
        drawWindow(matrixStack);
        checkStats();
    }

    private void checkStats() {
        timer--;
        if (timer <= 0) {
            timer = 20;
            tileEntity.requestDataFromServer(RFToolsPowerMessages.INSTANCE, EndergenicTileEntity.CMD_GETSTATS, TypedMap.EMPTY);
        }
        updateEnergyBar(energyBar);
    }
}
