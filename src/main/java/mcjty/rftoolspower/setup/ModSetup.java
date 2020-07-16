package mcjty.rftoolspower.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolspower.modules.endergenic.data.TickOrderHandler;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public boolean rftoolsDimensions = false;   // @todo 1.14 support for RFTools Dimensions

    public ModSetup() {
        createTab("rftoolspower", () -> new ItemStack(PowerCellSetup.CELL1.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsPowerMessages.registerMessages("rftoolspower");
        MinecraftForge.EVENT_BUS.addListener((TickEvent.WorldTickEvent event) -> {
            if (!event.world.isRemote) {
                TickOrderHandler.postWorldTick(event.world.getDimension().getType());
            }
        });
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
//        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }
}
