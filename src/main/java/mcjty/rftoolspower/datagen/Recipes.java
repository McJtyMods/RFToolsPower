package mcjty.rftoolspower.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.rftoolsbase.items.ModItems;
import mcjty.rftoolspower.blocks.ModBlocks;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.COALGENERATOR)
                .patternLine("cTc")
                .patternLine("cMc")
                .patternLine("cTc")
                .key('c', ItemTags.COALS)
                .key('T', Items.REDSTONE_TORCH)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("rftools")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, Items.REDSTONE_TORCH))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.INFORMATION_SCREEN)
                .patternLine("ppp")
                .patternLine("rMr")
                .key('p', Items.GLASS_PANE)
                .key('r', Items.REDSTONE)
                .key('M', ModItems.MACHINE_BASE)
                .setGroup("rftools")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_BASE, Items.REDSTONE))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE1)
                .patternLine(" d ")
                .patternLine("rRr")
                .patternLine(" r ")
                .key('r', Items.REDSTONE)
                .key('R', Items.REDSTONE_BLOCK)
                .key('d', Items.DIAMOND)
                .setGroup("rftools")
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.DIAMOND, Items.REDSTONE))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE2)
                .patternLine("ses")
                .patternLine("rRr")
                .patternLine("srs")
                .key('s', ModItems.DIMENSIONALSHARD)
                .key('r', Items.REDSTONE)
                .key('R', Items.REDSTONE_BLOCK)
                .key('e', Items.EMERALD)
                .setGroup("rftools")
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.REDSTONE, ModItems.DIMENSIONALSHARD))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(mcjty.rftoolspower.items.ModItems.POWER_CORE3)
                .patternLine("sds")
                .patternLine("rRr")
                .patternLine("ses")
                .key('s', ModItems.DIMENSIONALSHARD)
                .key('r', Items.REDSTONE)
                .key('R', Items.REDSTONE_BLOCK)
                .key('e', Items.EMERALD)
                .key('d', Items.DIAMOND)
                .setGroup("rftools")
                .addCriterion("core", InventoryChangeTrigger.Instance.forItems(Items.EMERALD, Items.DIAMOND, Items.REDSTONE, ModItems.DIMENSIONALSHARD))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.CELL1)
                .patternLine("rRr")
                .patternLine("RMR")
                .patternLine("rRr")
                .key('r', Items.REDSTONE)
                .key('R', mcjty.rftoolspower.items.ModItems.POWER_CORE1)
                .key('M', ModItems.MACHINE_FRAME)
                .setGroup("rftools")
                .addCriterion("frame", InventoryChangeTrigger.Instance.forItems(ModItems.MACHINE_FRAME, mcjty.rftoolspower.items.ModItems.POWER_CORE1))
                .build(consumer);
        CopyNBTRecipeBuilder.shapedRecipe(ModBlocks.CELL2)
                .patternLine("rRr")
                .patternLine("RMR")
                .patternLine("rRr")
                .key('r', Items.REDSTONE)
                .key('R', mcjty.rftoolspower.items.ModItems.POWER_CORE2)
                .key('M', ModBlocks.CELL1)
                .setGroup("rftools")
                .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(ModBlocks.CELL1))
                .build(consumer);
        CopyNBTRecipeBuilder.shapedRecipe(ModBlocks.CELL3)
                .patternLine("rRr")
                .patternLine("RMR")
                .patternLine("rRr")
                .key('r', Items.REDSTONE)
                .key('R', mcjty.rftoolspower.items.ModItems.POWER_CORE3)
                .key('M', ModBlocks.CELL2)
                .setGroup("rftools")
                .addCriterion("cell", InventoryChangeTrigger.Instance.forItems(ModBlocks.CELL2))
                .build(consumer);
    }
}
