package me.ramidzkh.mekae2.ae2;

import org.jetbrains.annotations.Nullable;

import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.chemical.ChemicalStack;
import mezz.jei.api.ingredients.IIngredientType;

import appeng.api.integrations.jei.IngredientConverter;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public record ChemicalIngredientConverter<S extends ChemicalStack<?>> (
        IIngredientType<S> type) implements IngredientConverter<S> {

    @Override
    public IIngredientType<S> getIngredientType() {
        return type;
    }

    @Nullable
    @Override
    public S getIngredientFromStack(GenericStack stack) {
        if (stack.what()instanceof MekanismKey key) {
            return ChemicalBridge.withAmount((S) key.getStack(), Math.max(1, stack.amount()));
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public GenericStack getStackFromIngredient(S ingredient) {
        AEKey what = MekanismKey.of(ingredient);

        if (what != null) {
            return new GenericStack(what, ingredient.getAmount());
        }

        return null;
    }
}
