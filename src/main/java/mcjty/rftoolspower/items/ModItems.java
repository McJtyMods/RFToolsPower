package mcjty.rftoolspower.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    public static PowerCoreItem powerCoreItem;

    public static void init() {
        powerCoreItem = new PowerCoreItem();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        powerCoreItem.initModel();
    }
}