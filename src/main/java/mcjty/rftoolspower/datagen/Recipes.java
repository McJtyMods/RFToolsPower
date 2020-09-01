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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CoalGeneratorModule.COALGENERATOR.get())
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(VariousModule.MACHINE_FRAME.get(), Items.REDSTONE_TORCH)),
                "cTc", "cFc", "cTc");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellModule.POWER_CORE1.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.DIAMOND, Items.REDSTONE)),
                " d ", "rRr", " r ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellModule.POWER_CORE2.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                "ses", "rRr", "srs");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellModule.POWER_CORE3.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, VariousModule.DIMENSIONALSHARD.get())),
                "sds", "rRr", "ses");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellModule.CELL1.get())
                        .key('K', PowerCellModule.POWER_CORE1.get())
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(VariousModule.MACHINE_FRAME.get(), PowerCellModule.POWER_CORE1.get())),
                "rKr", "KFK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellModule.CELL2.get())
                        .key('K', PowerCellModule.POWER_CORE2.get())
                        .key('P', PowerCellModule.CELL1.get())
                        .addCriterion("cell", hasItem(PowerCellModule.CELL1.get())),
                "rKr", "KPK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellModule.CELL3.get())
                        .key('K', PowerCellModule.POWER_CORE3.get())
                        .key('P', PowerCellModule.CELL2.get())
                        .addCriterion("cell", hasItem(PowerCellModule.CELL2.get())),
                "rKr", "KPK", "rKr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionalCellModule.DIMENSIONAL_CELL.get())
                        .key('P', Items.PRISMARINE_SHARD)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "RdR", "PFP", "ReR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionalCellModule.DIMENSIONAL_CELL_SIMPLE.get())
                        .key('q', Items.QUARTZ)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "RdR", "qFq", "RdR");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellModule.DIMENSIONAL_CELL_ADVANCED.get())
                        .key('K', DimensionalCellModule.DIMENSIONAL_CELL.get())
                        .key('*', VariousModule.INFUSED_DIAMOND.get())
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(VariousModule.MACHINE_BASE.get(), Items.REDSTONE)),
                "R*R", "*K*", "R*R");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellModule.POWERCELL_CARD.get())
                        .key('g', Items.GOLD_NUGGET)
                        .addCriterion("paper", InventoryChangeTrigger.Instance.forItems(Items.PAPER, Items.REDSTONE)),
                "rgr", "gpg", "rgr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(MonitorModule.POWER_MONITOR.get())
                        .key('P', Items.COMPARATOR)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(MonitorModule.POWER_LEVEL.get())
                        .key('P', Tags.Items.GLASS_PANES)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                " P ", "rAr", " R ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EndergenicModule.ENDER_MONITOR.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_BASE.get())),
                " o ", "rAr", "TrT");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EndergenicModule.ENDERGENIC.get())
                        .key('Z', VariousModule.INFUSED_ENDERPEARL.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "dod", "ZFZ", "dod");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(EndergenicModule.PEARL_INJECTOR.get())
                        .key('C', Tags.Items.CHESTS)
                        .key('H', Blocks.HOPPER)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                " C ", "rFr", " H ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BlazingModule.BLAZING_GENERATOR.get())
                        .key('X', Tags.Items.RODS_BLAZE)
                        .key('Y', VariousModule.INFUSED_DIAMOND.get())
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "XYX", "RFR", "XYX");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BlazingModule.BLAZING_AGITATOR.get())
                        .key('X', Tags.Items.RODS_BLAZE)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "XXX", "GFG", "iii");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BlazingModule.BLAZING_INFUSER.get())
                        .key('X', Tags.Items.RODS_BLAZE)
                        .addCriterion("frame", hasItem(VariousModule.MACHINE_FRAME.get())),
                "iXi", "sFs", "iXi");
    }
}
