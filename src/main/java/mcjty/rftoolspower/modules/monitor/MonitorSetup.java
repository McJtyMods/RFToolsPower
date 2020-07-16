package mcjty.rftoolspower.modules.monitor;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class MonitorSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<LogicSlabBlock> POWER_MONITOR = BLOCKS.register("power_monitor", PowerMonitorTileEntity::createBlock);
    public static final RegistryObject<Item> POWER_MONITOR_ITEM = ITEMS.register("power_monitor", () -> new BlockItem(POWER_MONITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_POWER_MONITOR = TILES.register("power_monitor", () -> TileEntityType.Builder.create(PowerMonitorTileEntity::new, POWER_MONITOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_POWER_MONITOR = CONTAINERS.register("power_monitor", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> POWER_LEVEL = BLOCKS.register("power_level", PowerLevelTileEntity::createBlock);
    public static final RegistryObject<Item> POWER_LEVEL_ITEM = ITEMS.register("power_level", () -> new BlockItem(POWER_LEVEL.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<PowerLevelTileEntity>> TYPE_POWER_LEVEL = TILES.register("power_level", () -> TileEntityType.Builder.create(PowerLevelTileEntity::new, POWER_LEVEL.get()).build(null));

}
