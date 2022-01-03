package mcjty.rftoolspower.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public boolean rftoolsDimensions = false;   // @todo 1.14 support for RFTools Dimensions

    public ModSetup() {
        createTab("rftoolspower", () -> new ItemStack(PowerCellModule.CELL1.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        RFToolsPowerMessages.registerMessages("rftoolspower");
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
//        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "mcjty.rftools.compat.theoneprobe.TheOneProbeSupport");
    }
}
