package mcjty.rftoolspower.modules.generator;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.generator.client.GuiCoalGenerator;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class CoalGeneratorModule implements IModule {

    public static final DeferredBlock<BaseBlock> COALGENERATOR = BLOCKS.register("coalgenerator", CoalGeneratorTileEntity::createBlock);
    public static final DeferredItem<Item> COALGENERATOR_ITEM = ITEMS.register("coalgenerator", tab(() -> new BlockItem(COALGENERATOR.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_COALGENERATOR = TILES.register("coalgenerator", () -> BlockEntityType.Builder.of(CoalGeneratorTileEntity::new, COALGENERATOR.get()).build(null));
    public static final Supplier<MenuType<GenericContainer>> CONTAINER_COALGENERATOR = CONTAINERS.register("coalgenerator", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiCoalGenerator.register();
        });
    }

    @Override
    public void initConfig() {
        CoalGeneratorConfig.setup(Config.SERVER_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(COALGENERATOR)
                        .ironPickaxeTags()
                        .standardLoot(TYPE_COALGENERATOR)
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_FRAME.get(), Items.REDSTONE_TORCH)),
                                "cTc", "cFc", "cTc")
        );
    }
}
