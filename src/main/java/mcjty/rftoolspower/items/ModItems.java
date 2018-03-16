package mcjty.rftoolspower.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    public static PowerCoreItem powerCore1Item;
    public static PowerCoreItem powerCore2Item;
    public static PowerCoreItem powerCore3Item;

    public static void init() {
        powerCore1Item = new PowerCoreItem("1");
        powerCore2Item = new PowerCoreItem("2");
        powerCore3Item = new PowerCoreItem("3");
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        powerCore1Item.initModel();
        powerCore2Item.initModel();
        powerCore3Item.initModel();
    }
}