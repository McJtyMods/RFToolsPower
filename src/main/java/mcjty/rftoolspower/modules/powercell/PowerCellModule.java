package mcjty.rftoolspower.modules.powercell;

import mcjty.lib.blocks.RBlock;
import mcjty.lib.client.ModelTools;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.client.ClientSetup;
import mcjty.rftoolspower.modules.powercell.data.Tier;
import mcjty.rftoolspower.modules.powercell.items.PowerCoreItem;
import mcjty.rftoolspower.setup.Config;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import static mcjty.lib.datagen.DataGen.has;
import static mcjty.rftoolspower.RFToolsPower.tab;
import static mcjty.rftoolspower.setup.Registration.*;

public class PowerCellModule implements IModule {

    public static final RBlock<PowerCellBlock, BlockItem, PowerCellTileEntity> CELL1 = RBLOCKS.registerBlock("powercell1",
            PowerCellTileEntity.class,
            () -> new PowerCellBlock(Tier.TIER1),
            block -> new BlockItem(block.get(), createStandardProperties()),
            (pos, state) -> new PowerCellTileEntity(Tier.TIER1, pos, state)
    );

    public static final RBlock<PowerCellBlock, BlockItem, PowerCellTileEntity> CELL2 = RBLOCKS.registerBlock("powercell2",
            PowerCellTileEntity.class,
            () -> new PowerCellBlock(Tier.TIER2),
            block -> new BlockItem(block.get(), createStandardProperties()),
            (pos, state) -> new PowerCellTileEntity(Tier.TIER2, pos, state)
    );

    public static final RBlock<PowerCellBlock, BlockItem, PowerCellTileEntity> CELL3 = RBLOCKS.registerBlock("powercell3",
            PowerCellTileEntity.class,
            () -> new PowerCellBlock(Tier.TIER3),
            block -> new BlockItem(block.get(), createStandardProperties()),
            (pos, state) -> new PowerCellTileEntity(Tier.TIER3, pos, state)
    );

    public static final DeferredItem<Item> POWER_CORE1 = ITEMS.register("power_core1", tab(PowerCoreItem::new));
    public static final DeferredItem<Item> POWER_CORE2 = ITEMS.register("power_core2", tab(PowerCoreItem::new));
    public static final DeferredItem<Item> POWER_CORE3 = ITEMS.register("power_core3", tab(PowerCoreItem::new));

    public PowerCellModule(IEventBus bus, Dist dist) {
        if (dist.isClient()) {
            ModelTools.registerModelBakeEvent(bus, ClientSetup::onModelBake);
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
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(CELL1)
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_ENERGY.get())
                        .ironPickaxeTags()
                        .shaped(builder -> builder
                                        .define('F', VariousModule.MACHINE_FRAME.get())
                                        .define('K', POWER_CORE1.get())
                                        .unlockedBy("frame", InventoryChangeTrigger.TriggerInstance.hasItems(VariousModule.MACHINE_FRAME.get(), PowerCellModule.POWER_CORE1.get())),
                                "rKr", "KFK", "rKr"),
                Dob.blockBuilder(CELL2)
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_ENERGY.get())
                        .ironPickaxeTags()
                        .shapedComponentPreserve(builder -> builder
                                        .define('K', POWER_CORE2.get())
                                        .define('P', CELL1.block().get())
                                        .unlockedBy("cell", has(PowerCellModule.CELL1.block().get())),
                                "rKr", "KPK", "rKr"),
                Dob.blockBuilder(CELL3)
                        .standardLoot(mcjty.lib.setup.Registration.ITEM_ENERGY.get())
                        .ironPickaxeTags()
                        .shapedComponentPreserve(builder -> builder
                                        .define('K', POWER_CORE3.get())
                                        .define('P', CELL2.block().get())
                                        .unlockedBy("cell", has(CELL2.block().get())),
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
