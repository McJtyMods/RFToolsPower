package mcjty.rftoolspower.setup;


import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.informationscreen.InformationScreenSetup;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsPower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        CoalGeneratorSetup.registerBlocks(event);
        DimensionalCellSetup.registerBlocks(event);
        PowerCellSetup.registerBlocks(event);
        InformationScreenSetup.registerBlocks(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        CoalGeneratorSetup.registerItems(event);
        DimensionalCellSetup.registerItems(event);
        PowerCellSetup.registerItems(event);
        InformationScreenSetup.registerItems(event);

    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        CoalGeneratorSetup.registerTiles(event);
        DimensionalCellSetup.registerTiles(event);
        PowerCellSetup.registerTiles(event);
        InformationScreenSetup.registerTiles(event);
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        CoalGeneratorSetup.registerContainers(event);
        DimensionalCellSetup.registerContainers(event);
        PowerCellSetup.registerContainers(event);
        InformationScreenSetup.registerContainers(event);
    }

}
