package mcjty.rftoolspower.modules.blazing;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.modules.various.VariousModule;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.lib.datagen.DataGen.has;
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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(BLAZING_AGITATOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_BLAZING_AGITATOR)
                        .blockState(p -> {
                            p.simpleBlock(BLAZING_AGITATOR.get(), p.models().slab("blazing_agitator",
                                    p.modLoc("block/blazing/blazing_agitator_side"),
                                    new ResourceLocation("rftoolsbase", "block/base/machinebottom"),
                                    new ResourceLocation("rftoolsbase", "block/base/machinetop")));
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "XXX", "GFG", "iii"),
                Dob.blockBuilder(BLAZING_GENERATOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_BLAZING_GENERATOR)
                        .blockState(p -> {
                            ModelFile modelIdle = p.frontBasedModel("blazing_generator_idle", p.modLoc("block/blazing/blazing_generator_idle"));
                            ModelFile modelBusy = p.frontBasedModel("blazing_generator_busy", p.modLoc("block/blazing/blazing_generator_busy"));
                            VariantBlockStateBuilder builder = p.getVariantBuilder(BLAZING_GENERATOR.get());
                            for (Direction direction : OrientationTools.DIRECTION_VALUES) {
                                p.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(BlazingGeneratorTileEntity.WORKING, false)
                                        .modelForState().modelFile(modelIdle), direction);
                                p.applyRotation(builder.partialState().with(BlockStateProperties.FACING, direction).with(BlazingGeneratorTileEntity.WORKING, true)
                                        .modelForState().modelFile(modelBusy), direction);
                            }
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .define('Y', VariousModule.INFUSED_DIAMOND.get())
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "XYX", "RFR", "XYX"),
                Dob.blockBuilder(BLAZING_INFUSER)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_BLAZING_INFUSER)
                        .blockState(p -> p.orientedBlock(BLAZING_INFUSER.get(), p.frontBasedModel("blazing_infuser", p.modLoc("block/blazing/blazing_infuser"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "iXi", "sFs", "iXi")
        );
    }
}
