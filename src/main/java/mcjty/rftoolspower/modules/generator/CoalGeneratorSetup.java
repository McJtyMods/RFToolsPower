package mcjty.rftoolspower.modules.generator;

import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class CoalGeneratorSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<Block> COALGENERATOR = BLOCKS.register("coalgenerator", CoalGeneratorTileEntity::createBlock);
    public static final RegistryObject<Item> COALGENERATOR_ITEM = ITEMS.register("coalgenerator", () -> new BlockItem(COALGENERATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_COALGENERATOR = TILES.register("coalgenerator", () -> TileEntityType.Builder.create(CoalGeneratorTileEntity::new, COALGENERATOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_COALGENERATOR = CONTAINERS.register("coalgenerator", GenericContainer::createContainerType);
}
