package mcjty.rftoolspower.modules.blazing;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import mcjty.rftoolspower.modules.blazing.client.BlazingAgitatorRenderer;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingAgitator;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingGenerator;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingInfuser;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static mcjty.rftoolspower.setup.Registration.*;

public class BlazingModule implements IModule {

    public static final RegistryObject<BaseBlock> BLAZING_GENERATOR = BLOCKS.register("blazing_generator", BlazingGeneratorTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_GENERATOR_ITEM = ITEMS.register("blazing_generator", () -> new BlockItem(BLAZING_GENERATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_BLAZING_GENERATOR = TILES.register("blazing_generator", () -> BlockEntityType.Builder.of(BlazingGeneratorTileEntity::new, BLAZING_GENERATOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_BLAZING_GENERATOR = CONTAINERS.register("blazing_generator", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> BLAZING_AGITATOR = BLOCKS.register("blazing_agitator", BlazingAgitatorTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_AGITATOR_ITEM = ITEMS.register("blazing_agitator", () -> new BlockItem(BLAZING_AGITATOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<BlazingAgitatorTileEntity>> TYPE_BLAZING_AGITATOR = TILES.register("blazing_agitator", () -> BlockEntityType.Builder.of(BlazingAgitatorTileEntity::new, BLAZING_AGITATOR.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_BLAZING_AGITATOR = CONTAINERS.register("blazing_agitator", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> BLAZING_INFUSER = BLOCKS.register("blazing_infuser", BlazingInfuserTileEntity::createBlock);
    public static final RegistryObject<Item> BLAZING_INFUSER_ITEM = ITEMS.register("blazing_infuser", () -> new BlockItem(BLAZING_INFUSER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<?>> TYPE_BLAZING_INFUSER = TILES.register("blazing_infuser", () -> BlockEntityType.Builder.of(BlazingInfuserTileEntity::new, BLAZING_INFUSER.get()).build(null));
    public static final RegistryObject<MenuType<GenericContainer>> CONTAINER_BLAZING_INFUSER = CONTAINERS.register("blazing_infuser", GenericContainer::createContainerType);

    public static final RegistryObject<BlazingRod> BLAZING_ROD = ITEMS.register("blazing_rod", BlazingRod::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiBlazingAgitator.register();
            GuiBlazingGenerator.register();
            GuiBlazingInfuser.register();
        });

        BlazingAgitatorRenderer.register();
    }

    @Override
    public void initConfig() {
        BlazingConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
