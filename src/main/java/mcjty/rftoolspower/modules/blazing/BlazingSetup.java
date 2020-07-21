package mcjty.rftoolspower.modules.blazing;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class BlazingSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<BaseBlock> BLAZING_GENERATOR = BLOCKS.register("blazing_generator", BlazingGeneratorTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_GENERATOR_ITEM = ITEMS.register("blazing_generator", () -> new BlockItem(BLAZING_GENERATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_BLAZING_GENERATOR = TILES.register("blazing_generator", () -> TileEntityType.Builder.create(BlazingGeneratorTileEntity::new, BLAZING_GENERATOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_BLAZING_GENERATOR = CONTAINERS.register("blazing_generator", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> BLAZING_AGITATOR = BLOCKS.register("blazing_agitator", BlazingAgitatorTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_AGITATOR_ITEM = ITEMS.register("blazing_agitator", () -> new BlockItem(BLAZING_AGITATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_BLAZING_AGITATOR = TILES.register("blazing_agitator", () -> TileEntityType.Builder.create(BlazingAgitatorTileEntity::new, BLAZING_AGITATOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_BLAZING_AGITATOR = CONTAINERS.register("blazing_agitator", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> BLAZING_INFUSER = BLOCKS.register("blazing_infuser", BlazingInfuserTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_INFUSER_ITEM = ITEMS.register("blazing_infuser", () -> new BlockItem(BLAZING_INFUSER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_BLAZING_INFUSER = TILES.register("blazing_infuser", () -> TileEntityType.Builder.create(BlazingInfuserTileEntity::new, BLAZING_INFUSER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_BLAZING_INFUSER = CONTAINERS.register("blazing_infuser", GenericContainer::createContainerType);

    public static final RegistryObject<BlazingRod> BLAZING_ROD = ITEMS.register("blazing_rod", BlazingRod::new);

}
