package mcjty.rftoolspower.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.modules.various.VariousModule;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.monitor.MonitorModule;
import mcjty.rftoolspower.modules.powercell.PowerCellModule;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', VariousModule.MACHINE_FRAME.get());
        add('A', VariousModule.MACHINE_BASE.get());
        add('s', VariousModule.DIMENSIONALSHARD.get());
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(CoalGeneratorModule.COALGENERATOR.get())
                        .unlockedBy("frame", InventoryChangeTrigger.Instance.hasItems(VariousModule.MACHINE_FRAME.get(), Items.REDSTONE_TORCH)),
                "cTc", "cFc", "cTc");
        build(consumer, ShapedRecipeBuilder.shaped(PowerCellModule.POWER_CORE1.get())
                        .unlockedBy("core", InventoryChangeTrigger.Instance.hasItems(Items.DIAMOND, Items.REDSTONE)),
                " d ", "rRr", " r ");
        build(consumer, ShapedRecipeBuilder.shaped(PowerCellModule.POWER_CORE2.get())
                        .unlockedBy("core", InventoryChangeTrigger.Instance.hasItems(Items.EMERALD, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                "ses", "rRr", "srs");
        build(consumer, ShapedRecipeBuilder.shaped(PowerCellModule.POWER_CORE3.get())
                        .unlockedBy("core", InventoryChangeTrigger.Instance.hasItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                "sds", "rRr", "ses");
        build(consumer, ShapedRecipeBuilder.shaped(PowerCellModule.CELL1.get())
                        .define('K', PowerCellModule.POWER_CORE1.get())
                        .unlockedBy("frame", InventoryChangeTrigger.Instance.hasItems(VariousModule.MACHINE_FRAME.get(), PowerCellModule.POWER_CORE1.get())),
                "rKr", "KFK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellModule.CELL2.get())
                        .define('K', PowerCellModule.POWER_CORE2.get())
                        .define('P', PowerCellModule.CELL1.get())
                        .unlockedBy("cell", has(PowerCellModule.CELL1.get())),
                "rKr", "KPK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellModule.CELL3.get())
                        .define('K', PowerCellModule.POWER_CORE3.get())
                        .define('P', PowerCellModule.CELL2.get())
                        .unlockedBy("cell", has(PowerCellModule.CELL2.get())),
                "rKr", "KPK", "rKr");
        build(consumer, ShapedRecipeBuilder.shaped(DimensionalCellModule.DIMENSIONAL_CELL.get())
                        .define('P', Items.PRISMARINE_SHARD)
                        .unlockedBy("frame", InventoryChangeTrigger.Instance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "RdR", "PFP", "ReR");
        build(consumer, ShapedRecipeBuilder.shaped(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get())
                        .define('q', Items.QUARTZ)
                        .unlockedBy("frame", InventoryChangeTrigger.Instance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "RdR", "qFq", "RdR");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get())
                        .define('K', DimensionalCellModule.DIMENSIONAL_CELL.get())
                        .define('*', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("frame", InventoryChangeTrigger.Instance.hasItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "R*R", "*K*", "R*R");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellModule.POWERCELL_CARD.get())
                        .define('g', Items.GOLD_NUGGET)
                        .unlockedBy("paper", InventoryChangeTrigger.Instance.hasItems(Items.PAPER, Items.REDSTONE)),
                "rgr", "gpg", "rgr");
        build(consumer, ShapedRecipeBuilder.shaped(MonitorModule.POWER_MONITOR.get())
                        .define('P', Items.COMPARATOR)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " R ");
        build(consumer, ShapedRecipeBuilder.shaped(MonitorModule.POWER_LEVEL.get())
                        .define('P', Tags.Items.GLASS_PANES)
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " R ");
        build(consumer, ShapedRecipeBuilder.shaped(EndergenicModule.ENDER_MONITOR.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_BASE.get())),
                " o ", "rAr", "TrT");
        build(consumer, ShapedRecipeBuilder.shaped(EndergenicModule.ENDERGENIC.get())
                        .define('Z', VariousModule.INFUSED_ENDERPEARL.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "dod", "ZFZ", "dod");
        build(consumer, ShapedRecipeBuilder.shaped(EndergenicModule.PEARL_INJECTOR.get())
                        .define('C', Tags.Items.CHESTS)
                        .define('H', Blocks.HOPPER)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                " C ", "rFr", " H ");
        build(consumer, ShapedRecipeBuilder.shaped(BlazingModule.BLAZING_GENERATOR.get())
                        .define('X', Tags.Items.RODS_BLAZE)
                        .define('Y', VariousModule.INFUSED_DIAMOND.get())
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "XYX", "RFR", "XYX");
        build(consumer, ShapedRecipeBuilder.shaped(BlazingModule.BLAZING_AGITATOR.get())
                        .define('X', Tags.Items.RODS_BLAZE)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "XXX", "GFG", "iii");
        build(consumer, ShapedRecipeBuilder.shaped(BlazingModule.BLAZING_INFUSER.get())
                        .define('X', Tags.Items.RODS_BLAZE)
                        .unlockedBy("frame", has(VariousModule.MACHINE_FRAME.get())),
                "iXi", "sFs", "iXi");
    }
}
