package me.ramidzkh.mekae2.data;

import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AppliedMekanistics;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(AMItems.CHEMICAL_CELL_HOUSING::get)
                .pattern("QRQ")
                .pattern("R R")
                .pattern("OOO")
                .define('Q', AEBlocks.QUARTZ_GLASS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('O', ItemTags.create(new ResourceLocation("forge", "ingots/osmium")))
                .unlockedBy("has_dusts/redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(consumer, AppliedMekanistics.id("chemical_cell_housing"));

        var housing = AMItems.CHEMICAL_CELL_HOUSING.get();

        for (var tier : AMItems.Tier.values()) {
            var cellComponent = switch (tier) {
                case _1K -> AEItems.CELL_COMPONENT_1K;
                case _4K -> AEItems.CELL_COMPONENT_4K;
                case _16K -> AEItems.CELL_COMPONENT_16K;
                case _64K -> AEItems.CELL_COMPONENT_64K;
                case _256K -> AEItems.CELL_COMPONENT_256K;
            };

            var tierName = tier.toString().toLowerCase(Locale.ROOT);

            ShapelessRecipeBuilder.shapeless(AMItems.get(tier).get())
                    .requires(housing)
                    .requires(cellComponent)
                    .unlockedBy("has_cell_component" + tierName, has(cellComponent))
                    .save(consumer);
            ShapelessRecipeBuilder.shapeless(AMItems.getPortableCell(tier)::get)
                    .requires(AEBlocks.CHEST)
                    .requires(cellComponent)
                    .requires(AEBlocks.ENERGY_CELL)
                    .requires(housing)
                    .unlockedBy("has_" + housing.builtInRegistryHolder().key().location().getPath(), has(housing))
                    .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
                    .save(consumer);
        }
    }
}
