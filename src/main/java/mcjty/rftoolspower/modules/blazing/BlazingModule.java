package mcjty.rftoolspower.modules.blazing;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
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
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class BlazingModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, BlazingGeneratorTileEntity> BLAZING_GENERATOR = RBLOCKS.registerBlock("blazing_generator",
            BlazingGeneratorTileEntity.class,
            BlazingGeneratorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            BlazingGeneratorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_GENERATOR = CONTAINERS.register("blazing_generator", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, BlazingAgitatorTileEntity> BLAZING_AGITATOR = RBLOCKS.registerBlock("blazing_agitator",
            BlazingAgitatorTileEntity.class,
            BlazingAgitatorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            BlazingAgitatorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_AGITATOR = CONTAINERS.register("blazing_agitator", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, BlazingInfuserTileEntity> BLAZING_INFUSER = RBLOCKS.registerBlock("blazing_infuser",
            BlazingInfuserTileEntity.class,
            BlazingInfuserTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            BlazingInfuserTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_INFUSER = CONTAINERS.register("blazing_infuser", GenericContainer::createContainerType);

    public static final DeferredItem<BlazingRod> BLAZING_ROD = ITEMS.register("blazing_rod", tab(BlazingRod::new));

    public BlazingModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        BlazingAgitatorRenderer.register();
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiBlazingAgitator.register(event);
        GuiBlazingGenerator.register(event);
        GuiBlazingInfuser.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {
        BlazingConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(BLAZING_AGITATOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> {
                            p.simpleBlock(BLAZING_AGITATOR.block().get(), p.models().slab("blazing_agitator",
                                    p.modLoc("block/blazing/blazing_agitator_side"),
                                    ResourceLocation.fromNamespaceAndPath("rftoolsbase", "block/base/machinebottom"),
                                    ResourceLocation.fromNamespaceAndPath("rftoolsbase", "block/base/machinetop")));
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "XXX", "GFG", "iii"),
                Dob.blockBuilder(BLAZING_GENERATOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .blockState(p -> {
                            ModelFile modelIdle = p.frontBasedModel("blazing_generator_idle", p.modLoc("block/blazing/blazing_generator_idle"));
                            ModelFile modelBusy = p.frontBasedModel("blazing_generator_busy", p.modLoc("block/blazing/blazing_generator_busy"));
                            VariantBlockStateBuilder builder = p.getVariantBuilder(BLAZING_GENERATOR.block().get());
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
                        .standardLoot()
                        .blockState(p -> p.orientedBlock(BLAZING_INFUSER.block().get(), p.frontBasedModel("blazing_infuser", p.modLoc("block/blazing/blazing_infuser"))))
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .define('X', Tags.Items.RODS_BLAZE)
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "iXi", "sFs", "iXi")
        );
    }
}
