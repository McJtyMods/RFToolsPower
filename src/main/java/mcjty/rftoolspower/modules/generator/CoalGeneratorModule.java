package mcjty.rftoolspower.modules.generator;

import java.util.function.Supplier;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.items.BaseBlockItem;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.modules.generator.data.CoalGeneratorData;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import mcjty.lib.setup.DeferredHolder;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;
import static mcjty.rftoolspower.setup.Registration.*;

public class CoalGeneratorModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, CoalGeneratorTileEntity> COALGENERATOR = RBLOCKS.registerBlock("coalgenerator",
            CoalGeneratorTileEntity.class,
            CoalGeneratorTileEntity::createBlock,
            (block, itemProperties) -> new BaseBlockItem(block.get(), itemProperties.useBlockDescriptionPrefix()),
            CoalGeneratorTileEntity::new);
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COALGENERATOR = CONTAINERS.register("coalgenerator", GenericContainer::createContainerType);

    public static final AttachmentType<CoalGeneratorData> COAL_GENERATOR_DATA = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
            net.minecraft.resources.Identifier.fromNamespaceAndPath("rftoolspower", "coal_generator_data"), builder -> builder.initializer(() -> new CoalGeneratorData(0)).persistent(CoalGeneratorData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CoalGeneratorData>> ITEM_COAL_GENERATOR_DATA = COMPONENTS.registerComponentType(
            "coal_generator_data",
            builder -> builder
                    .persistent(CoalGeneratorData.CODEC)
                    .networkSynchronized(CoalGeneratorData.STREAM_CODEC));

    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        registerMenuScreens();
    }

    public void registerMenuScreens() {
        GuiCoalGenerator.register();
    }

    @Override
    public void initConfig() {
        CoalGeneratorConfig.setup(Config.SERVER_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(COALGENERATOR)
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get())
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_FRAME.get(), Items.REDSTONE_TORCH)),
                                "cTc", "cFc", "cTc")
        );
    }
}
