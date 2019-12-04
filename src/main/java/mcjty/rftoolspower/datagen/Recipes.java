package mcjty.rftoolspower.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellSetup;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.informationscreen.InformationScreenSetup;
import mcjty.rftoolspower.modules.powercell.PowerCellSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('F', ModItems.MACHINE_FRAME);
        add('A', ModItems.MACHINE_BASE);
        add('s', ModItems.DIMENSIONALSHARD);
        group("rftools");
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(CoalGeneratorSetup.COALGENERATOR.get())
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, Items.REDSTONE_TORCH)),
                "cTc", "cFc", "cTc");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(InformationScreenSetup.INFORMATION_SCREEN.get())
                        .key('-', Tags.Items.GLASS_PANES)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE)),
                "---", "rAr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellSetup.POWER_CORE1.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.DIAMOND, Items.REDSTONE)),
                " d ", "rRr", " r ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellSetup.POWER_CORE2.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.REDSTONE, ModItems.DIMENSIONALSHARD)),
                "ses", "rRr", "srs");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellSetup.POWER_CORE3.get())
                        .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, ModItems.DIMENSIONALSHARD)),
                "sds", "rRr", "ses");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(PowerCellSetup.CELL1.get())
                        .key('K', PowerCellSetup.POWER_CORE1.get())
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, PowerCellSetup.POWER_CORE1.get())),
                "rKr", "KFK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellSetup.CELL2.get())
                        .key('K', PowerCellSetup.POWER_CORE2.get())
                        .key('P', PowerCellSetup.CELL1.get())
                        .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(PowerCellSetup.CELL1.get())),
                "rKr", "KPK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(PowerCellSetup.CELL3.get())
                        .key('K', PowerCellSetup.POWER_CORE3.get())
                        .key('P', PowerCellSetup.CELL2.get())
                        .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(PowerCellSetup.CELL2.get())),
                "rKr", "KPK", "rKr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionalCellSetup.DIMENSIONAL_CELL.get())
                        .key('P', Items.PRISMARINE_SHARD)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE)),
                "RdR", "PFP", "ReR");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(DimensionalCellSetup.DIMENSIONAL_CELL_SIMPLE.get())
                        .key('q', Items.QUARTZ)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE)),
                "RdR", "qFq", "RdR");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellSetup.DIMENSIONAL_CELL_ADVANCED.get())
                        .key('K', DimensionalCellSetup.DIMENSIONAL_CELL.get())
                        .key('*', ModItems.INFUSED_DIAMOND)
                        .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE)),
                "R*R", "*K*", "R*R");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(DimensionalCellSetup.POWERCELL_CARD.get())
                        .key('g', Items.GOLD_NUGGET)
                        .addCriterion("paper", InventoryChangeTrigger.Instance.forItems(Items.PAPER, Items.REDSTONE)),
                "rgr", "gpg", "rgr");
    }
}
