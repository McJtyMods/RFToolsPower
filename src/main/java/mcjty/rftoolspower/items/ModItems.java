package mcjty.rftoolspower.items;

public class ModItems {
    public static PowerCoreItem powerCore1Item;
    public static PowerCoreItem powerCore2Item;
    public static PowerCoreItem powerCore3Item;

    public static void init() {
        powerCore1Item = new PowerCoreItem("1");
        powerCore2Item = new PowerCoreItem("2");
        powerCore3Item = new PowerCoreItem("3");
    }
}