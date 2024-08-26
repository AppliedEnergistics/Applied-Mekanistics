package me.ramidzkh.mekae2.integration.jei;

import org.jetbrains.annotations.Nullable;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.IMekanismAccess;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.integration.jei.IMekanismJEIHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;

import appeng.api.stacks.GenericStack;

public class ChemicalIngredientConverter implements IngredientConverter<ChemicalStack> {
    private final IIngredientType<ChemicalStack> ingredientType;

    public ChemicalIngredientConverter() {
        IMekanismJEIHelper mekJeiHelper = IMekanismAccess.INSTANCE.jeiHelper();
        IIngredientHelper<ChemicalStack> chemicalStackHelper = mekJeiHelper.getChemicalStackHelper();
        this.ingredientType = chemicalStackHelper.getIngredientType();
    }

    @Override
    public IIngredientType<ChemicalStack> getIngredientType() {
        return ingredientType;
    }

    @Override
    public @Nullable ChemicalStack getIngredientFromStack(GenericStack genericStack) {
        if (genericStack.what() instanceof MekanismKey key) {
            return key.withAmount(genericStack.amount());
        }
        return null;
    }

    @Override
    public @Nullable GenericStack getStackFromIngredient(ChemicalStack chemicalStack) {
        var what = MekanismKey.of(chemicalStack);
        if (what == null) {
            return null;
        }
        return new GenericStack(what, chemicalStack.getAmount());
    }
}
