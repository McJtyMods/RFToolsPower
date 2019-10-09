package mcjty.rftoolspower.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.datagen.BaseRecipeProvider;
import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

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
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ModBlocks.COALGENERATOR)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, Items.REDSTONE_TORCH)),
                "cTc", "cFc", "cTc");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ModBlocks.INFORMATION_SCREEN)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE)),
                "---", "rAr");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE1)
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.DIAMOND, Items.REDSTONE)),
                " d ", "rRr", " r ");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE2)
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.REDSTONE, ModItems.DIMENSIONALSHARD)),
                "ses", "rRr", "srs");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE3)
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, ModItems.DIMENSIONALSHARD)),
                "sds", "rRr", "ses");
        build(consumer, ShapedRecipeBuilder.shapedRecipe(ModBlocks.CELL1)
                .key('K', mcjty.rftoolspower.items.ModItems.POWER_CORE1)
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, mcjty.rftoolspower.items.ModItems.POWER_CORE1)),
                "rKr", "KFK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(ModBlocks.CELL2)
                .key('K', mcjty.rftoolspower.items.ModItems.POWER_CORE2)
                .key('P', ModBlocks.CELL1)
                .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(ModBlocks.CELL1)),
                "rKr", "KFK", "rKr");
        build(consumer, CopyNBTRecipeBuilder.shapedRecipe(ModBlocks.CELL3)
                .key('K', mcjty.rftoolspower.items.ModItems.POWER_CORE3)
                .key('P', ModBlocks.CELL2)
                .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(ModBlocks.CELL2)),
                "rKr", "KFK", "rKr");
    }
}
