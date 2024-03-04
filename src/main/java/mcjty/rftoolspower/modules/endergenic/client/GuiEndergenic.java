package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketRequestDataFromServer;
import mcjty.lib.typed.TypedMap;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GuiEndergenic extends GenericGuiContainer<EndergenicTileEntity, GenericContainer> {

    private EnergyBar energyBar;
    private TextField lastRfPerTick;
    private TextField lastLostPearls;
    private TextField lastLaunchedPearls;
    private TextField lastOpportunities;

    private int timer = 10;

    public GuiEndergenic(EndergenicTileEntity endergenicTileEntity, GenericContainer container, Inventory inventory) {
        super(endergenicTileEntity, container, inventory, EndergenicModule.ENDERGENIC.get().getManualEntry());
    }

    public static void register() {
        register(EndergenicModule.CONTAINER_ENDERGENIC.get(), GuiEndergenic::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, new ResourceLocation(RFToolsPower.MODID, "gui/endergenic.gui"));
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
    protected void renderBg(@Nonnull GuiGraphics graphics, float v, int i, int i2) {
        updateFields();
        drawWindow(graphics, xxx, xxx, yyy);
        checkStats();
    }

    private void checkStats() {
        timer--;
        if (timer <= 0) {
            timer = 20;
            Networking.sendToServer(PacketRequestDataFromServer.create(tileEntity.getDimension(), tileEntity.getBlockPos(), ((ICommand) EndergenicTileEntity.CMD_GETSTATS).name(), TypedMap.EMPTY, false));
        }
        updateEnergyBar(energyBar);
    }
}
