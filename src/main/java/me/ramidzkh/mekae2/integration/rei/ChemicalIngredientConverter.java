package me.ramidzkh.mekae2.integration.rei;

import org.jetbrains.annotations.Nullable;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import mekanism.api.chemical.ChemicalStack;

import appeng.api.integrations.rei.IngredientConverter;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public record ChemicalIngredientConverter<S extends ChemicalStack<?>> (
        EntryType<S> ingredientType) implements IngredientConverter<S> {

    @Override
    public EntryType<S> getIngredientType() {
        return ingredientType();
    }

    @Nullable
    @Override
    public EntryStack<S> getIngredientFromStack(GenericStack stack) {
        if (stack.what()instanceof MekanismKey key) {
            return EntryStack.of(getIngredientType(), (S) key.getStack());
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public GenericStack getStackFromIngredient(EntryStack<S> ingredient) {
        AEKey what = MekanismKey.of(ingredient.getValue());

        if (what != null) {
            return new GenericStack(what, 1);
        }

        return null;
    }
}
