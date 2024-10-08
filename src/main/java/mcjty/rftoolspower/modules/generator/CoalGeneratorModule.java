package mcjty.rftoolspower.modules.generator;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.modules.generator.data.CoalGeneratorData;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static mcjty.rftoolspower.setup.Registration.*;

public class CoalGeneratorModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, CoalGeneratorTileEntity> COALGENERATOR = RBLOCKS.registerBlock("coalgenerator",
            CoalGeneratorTileEntity.class,
            CoalGeneratorTileEntity::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            CoalGeneratorTileEntity::new
    );
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COALGENERATOR = CONTAINERS.register("coalgenerator", GenericContainer::createContainerType);

    public static final Supplier<AttachmentType<CoalGeneratorData>> COAL_GENERATOR_DATA = ATTACHMENT_TYPES.register(
            "coal_generator_data", () -> AttachmentType.builder(() -> new CoalGeneratorData(0))
                    .serialize(CoalGeneratorData.CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CoalGeneratorData>> ITEM_COAL_GENERATOR_DATA = COMPONENTS.registerComponentType(
            "coal_generator_data",
            builder -> builder
                    .persistent(CoalGeneratorData.CODEC)
                    .networkSynchronized(CoalGeneratorData.STREAM_CODEC));

    public CoalGeneratorModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiCoalGenerator.register(event);
    }

    @Override
    public void initConfig(IEventBus bus) {
        CoalGeneratorConfig.setup(Config.SERVER_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(COALGENERATOR)
                        .ironPickaxeTags()
                        .standardLoot()
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_FRAME.get(), Items.REDSTONE_TORCH)),
                                "cTc", "cFc", "cTc")
        );
    }
}
