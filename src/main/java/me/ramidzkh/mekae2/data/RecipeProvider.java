package me.ramidzkh.mekae2.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AppliedMekanistics;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.recipes.game.StorageCellUpgradeRecipe;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMItems.CHEMICAL_CELL_HOUSING::get)
                .pattern("QRQ")
                .pattern("R R")
                .pattern("OOO")
                .define('Q', AEBlocks.QUARTZ_GLASS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('O', ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/osmium")))
                .unlockedBy("has_dusts/redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(output, AppliedMekanistics.id("chemical_cell_housing"));

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

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItems.get(tier)::get)
                    .requires(housing)
                    .requires(cellComponent)
                    .unlockedBy("has_cell_component" + tierName, has(cellComponent))
                    .unlockedBy("has_chemical_housing", has(housing))
                    .save(output);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AMItems.getPortableCell(tier)::get)
                    .requires(AEBlocks.ME_CHEST)
                    .requires(cellComponent)
                    .requires(AEBlocks.ENERGY_CELL)
                    .requires(housing)
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(housing).getPath(), has(housing))
                    .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
                    .save(output);
        }

        // Copied from ae2 UpgradeRecipes.java
        storageCellUpgradeRecipes(
                output,
                List.of(
                        new CellUpgradeTier("1k", AMItems.CHEMICAL_CELL_1K, AEItems.CELL_COMPONENT_1K),
                        new CellUpgradeTier("4k", AMItems.CHEMICAL_CELL_4K, AEItems.CELL_COMPONENT_4K),
                        new CellUpgradeTier("16k", AMItems.CHEMICAL_CELL_16K, AEItems.CELL_COMPONENT_16K),
                        new CellUpgradeTier("64k", AMItems.CHEMICAL_CELL_64K, AEItems.CELL_COMPONENT_64K),
                        new CellUpgradeTier("256k", AMItems.CHEMICAL_CELL_256K, AEItems.CELL_COMPONENT_256K)),
                List.of(AMItems.CHEMICAL_CELL_HOUSING));
        storageCellUpgradeRecipes(
                output,
                List.of(
                        new CellUpgradeTier("1k", AMItems.PORTABLE_CHEMICAL_CELL_1K, AEItems.CELL_COMPONENT_1K),
                        new CellUpgradeTier("4k", AMItems.PORTABLE_CHEMICAL_CELL_4K, AEItems.CELL_COMPONENT_4K),
                        new CellUpgradeTier("16k", AMItems.PORTABLE_CHEMICAL_CELL_16K, AEItems.CELL_COMPONENT_16K),
                        new CellUpgradeTier("64k", AMItems.PORTABLE_CHEMICAL_CELL_64K, AEItems.CELL_COMPONENT_64K),
                        new CellUpgradeTier("256k", AMItems.PORTABLE_CHEMICAL_CELL_256K, AEItems.CELL_COMPONENT_256K)),
                List.of(AEBlocks.ME_CHEST, AEBlocks.ENERGY_CELL, AMItems.CHEMICAL_CELL_HOUSING));
    }

    private void storageCellUpgradeRecipes(RecipeOutput output, List<CellUpgradeTier> tiers,
            List<ItemLike> additionalDisassemblyItems) {
        for (int i = 0; i < tiers.size(); i++) {
            var fromTier = tiers.get(i);
            var inputCell = fromTier.cell().asItem();
            var inputId = fromTier.cell().getId();
            var resultComponent = fromTier.component().asItem();

            cellDisassembly(output, additionalDisassemblyItems, fromTier);

            // Allow a direct upgrade to any higher tier
            for (int j = i + 1; j < tiers.size(); j++) {
                var toTier = tiers.get(j);
                var resultCell = toTier.cell().asItem();
                var inputComponent = toTier.component().asItem();

                var recipeId = inputId.withPath(path -> "upgrade/" + path + "_to_" + toTier.suffix);

                output.accept(
                        recipeId,
                        new StorageCellUpgradeRecipe(
                                inputCell, inputComponent,
                                resultCell, resultComponent),
                        null);
            }
        }
    }

    private void cellDisassembly(RecipeOutput consumer, List<ItemLike> additionalReturn, CellUpgradeTier tier) {
        List<ItemStack> results = new ArrayList<>();
        for (var itemLike : additionalReturn) {
            results.add(itemLike.asItem().getDefaultInstance());
        }
        results.add(tier.component.asItem().getDefaultInstance());

        consumer.accept(
                tier.cell.getId().withPrefix("cell_upgrade/"),
                new StorageCellDisassemblyRecipe(
                        tier.cell.asItem(),
                        results),
                null);
    }

    record CellUpgradeTier(String suffix, DeferredItem<Item> cell, ItemLike component) {
    }
}
