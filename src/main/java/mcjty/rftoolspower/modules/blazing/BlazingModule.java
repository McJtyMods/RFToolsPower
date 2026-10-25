package mcjty.rftoolspower.modules.blazing;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingInfuserTileEntity;
import mcjty.rftoolspower.modules.blazing.client.BlazingAgitatorRenderer;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingAgitator;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingGenerator;
import mcjty.rftoolspower.modules.blazing.client.GuiBlazingInfuser;
import mcjty.rftoolspower.modules.blazing.data.AgitatorData;
import mcjty.rftoolspower.modules.blazing.data.BlazingRodData;
import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import mcjty.lib.datagen.model.ModelFile;
import mcjty.lib.datagen.model.VariantBlockStateBuilder;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;

import java.util.ArrayList;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class BlazingModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, BlazingGeneratorTileEntity> BLAZING_GENERATOR = RBLOCKS.registerBlock("blazing_generator",
            BlazingGeneratorTileEntity.class,
            BlazingGeneratorTileEntity::createBlock,
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            BlazingGeneratorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_GENERATOR = CONTAINERS.register("blazing_generator", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, BlazingAgitatorTileEntity> BLAZING_AGITATOR = RBLOCKS.registerBlock("blazing_agitator",
            BlazingAgitatorTileEntity.class,
            BlazingAgitatorTileEntity::createBlock,
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            BlazingAgitatorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_AGITATOR = CONTAINERS.register("blazing_agitator", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, BlazingInfuserTileEntity> BLAZING_INFUSER = RBLOCKS.registerBlock("blazing_infuser",
            BlazingInfuserTileEntity.class,
            BlazingInfuserTileEntity::createBlock,
            (block, itemProperties) -> new BlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            BlazingInfuserTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_BLAZING_INFUSER = CONTAINERS.register("blazing_infuser", GenericContainer::createContainerType);

    public static final DeferredItem<BlazingRod> BLAZING_ROD = ITEMS.register("blazing_rod", tab(BlazingRod::new));

    public static final AttachmentType<AgitatorData> AGITATOR_DATA = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
            net.minecraft.resources.Identifier.fromNamespaceAndPath("rftoolspower", "agitator_data"), builder -> builder.initializer(() -> new AgitatorData(new ArrayList<>())).persistent(AgitatorData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AgitatorData>> ITEM_AGITATOR_DATA = COMPONENTS.registerComponentType(
            "agitator_data",
            builder -> builder
                    .persistent(AgitatorData.CODEC)
                    .networkSynchronized(AgitatorData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlazingRodData>> ITEM_BLAZING_ROD_DATA = COMPONENTS.registerComponentType(
            "blazing_rod_data",
            builder -> builder
                    .persistent(BlazingRodData.CODEC)
                    .networkSynchronized(BlazingRodData.STREAM_CODEC));


    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerScreens();
        BlazingAgitatorRenderer.register();
    }

    public void registerScreens() {
        GuiBlazingAgitator.register();
        GuiBlazingGenerator.register();
        GuiBlazingInfuser.register();
    }

    @Override
    public void initConfig() {
        BlazingConfiguration.setup(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(BLAZING_AGITATOR)
                        .ironPickaxeTags()
                        .standardLoot(BlazingModule.ITEM_AGITATOR_DATA.get(), mcjty.lib.setup.Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> {
                            p.simpleBlock(BLAZING_AGITATOR.block().get(), p.models().getBuilder("blazing_agitator").parent(p.models().getExistingFile(Identifier.withDefaultNamespace("block/slab"))).texture("side",
                                    p.modLoc("block/blazing/blazing_agitator_side")).texture("bottom",
                                    Identifier.fromNamespaceAndPath("rftoolsbase", "block/base/machinebottom")).texture("top",
                                    Identifier.fromNamespaceAndPath("rftoolsbase", "block/base/machinetop")));
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.parse("c:rods/blaze")))
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "XXX", "GFG", "iii"),
                Dob.blockBuilder(BLAZING_GENERATOR)
                        .ironPickaxeTags()
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_INFUSABLE.get())
                        .blockState(p -> {
                            ModelFile modelIdle = p.frontBasedModel("blazing_generator_idle", p.modLoc("block/blazing/blazing_generator_idle"));
                            ModelFile modelBusy = p.frontBasedModel("blazing_generator_busy", p.modLoc("block/blazing/blazing_generator_busy"));
                            VariantBlockStateBuilder builder = p.getVariantBuilder(BLAZING_GENERATOR.block().get());
                            builder.forAllStates(state -> mcjty.rftoolsbase.tools.ModelDataGen.rotated(
                                    state.getValue(BlazingGeneratorTileEntity.WORKING) ? modelBusy : modelIdle, state.getValue(BlockStateProperties.FACING)));
                        })
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('X', net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.parse("c:rods/blaze")))
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
                                        .define('X', net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.parse("c:rods/blaze")))
                                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                                "iXi", "sFs", "iXi")
        );
    }
}
