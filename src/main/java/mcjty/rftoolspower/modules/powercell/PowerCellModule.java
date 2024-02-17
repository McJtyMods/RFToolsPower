package mcjty.rftoolspower.modules.powercell;

import mcjty.lib.client.ModelTools;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.ClientTools;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.client.ClientSetup;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import mcjty.rftoolspower.modules.powercell.items.PowerCoreItem;
import mcjty.rftoolspower.setup.Config;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class PowerCellModule implements IModule {

    public static final DeferredBlock<Block> CELL1 = BLOCKS.register("cell1", () -> new PowerCellBlock(Tier.TIER1));
    public static final DeferredItem<Item> CELL1_ITEM = ITEMS.register("cell1", tab(() -> new BlockItem(CELL1.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CELL1 = TILES.register("cell1", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER1, pos, state), CELL1.get()).build(null));

    public static final DeferredBlock<Block> CELL2 = BLOCKS.register("cell2", () -> new PowerCellBlock(Tier.TIER2));
    public static final DeferredItem<Item> CELL2_ITEM = ITEMS.register("cell2", tab(() -> new BlockItem(CELL2.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CELL2 = TILES.register("cell2", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER2, pos, state), CELL2.get()).build(null));

    public static final DeferredBlock<Block> CELL3 = BLOCKS.register("cell3", () -> new PowerCellBlock(Tier.TIER3));
    public static final DeferredItem<Item> CELL3_ITEM = ITEMS.register("cell3", tab(() -> new BlockItem(CELL3.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<?>> TYPE_CELL3 = TILES.register("cell3", () -> BlockEntityType.Builder.of((pos, state) -> new PowerCellTileEntity(Tier.TIER3, pos, state), CELL3.get()).build(null));

    public static final DeferredItem<Item> POWER_CORE1 = ITEMS.register("power_core1", tab(PowerCoreItem::new));
    public static final DeferredItem<Item> POWER_CORE2 = ITEMS.register("power_core2", tab(PowerCoreItem::new));
    public static final DeferredItem<Item> POWER_CORE3 = ITEMS.register("power_core3", tab(PowerCoreItem::new));

    public PowerCellModule(IEventBus bus, Dist dist) {
        if (dist.isClient()) {
            ModelTools.registerModelBakeEvent(bus, ClientSetup::onModelBake);
            ClientTools.onTextureStitch(bus, ClientSetup::onTextureStitch);
        }
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
    }

    @Override
    public void initConfig(IEventBus bus) {
        PowerCellConfig.setup(Config.SERVER_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(CELL1)
                        .standardLoot(TYPE_CELL1)
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('K', POWER_CORE1.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_FRAME.get(), PowerCellModule.POWER_CORE1.get())),
                                "rKr", "KFK", "rKr"),
                Dob.blockBuilder(CELL2)
                        .standardLoot(TYPE_CELL2)
                        .ironPickaxeTags()
                        .shapedNBT(builder -> builder
                                        .define('K', POWER_CORE2.get())
                                        .define('P', CELL1.get())
                                        .unlockedBy("cell", has(PowerCellModule.CELL1.get())),
                                "rKr", "KPK", "rKr"),
                Dob.blockBuilder(CELL3)
                        .standardLoot(TYPE_CELL3)
                        .ironPickaxeTags()
                        .shapedNBT(builder -> builder
                                        .define('K', POWER_CORE3.get())
                                        .define('P', CELL2.get())
                                        .unlockedBy("cell", has(CELL2.get())),
                                "rKr", "KPK", "rKr"),
                Dob.itemBuilder(POWER_CORE1)
                        .shaped(builder -> builder
                                        .unlockedBy("core", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND, Items.REDSTONE)),
                                " d ", "rRr", " r "),
                Dob.itemBuilder(POWER_CORE2)
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("core", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                                "ses", "rRr", "srs"),
                Dob.itemBuilder(POWER_CORE3)
                        .shaped(builder -> builder
                                        .define('s', VariousModule.DIMENSIONALSHARD.get())
                                        .unlockedBy("core", InventoryChangeTrigger.TriggerInstance.hasItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                                "sds", "rRr", "ses")
        );
    }
}
