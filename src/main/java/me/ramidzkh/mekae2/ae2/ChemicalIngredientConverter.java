package me.ramidzkh.mekae2.ae2;

import org.jetbrains.annotations.Nullable;

import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mezz.jei.api.ingredients.IIngredientType;

import appeng.api.integrations.jei.IngredientConverter;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public abstract sealed class ChemicalIngredientConverter<S extends ChemicalStack<?>> implements IngredientConverter<S> {

    @SuppressWarnings("unchecked")
    private static <T> IIngredientType<T> find(String name) {
        try {
            var jei = Class.forName("mekanism.client.jei.MekanismJEI");
            var field = jei.getDeclaredField(name);
            field.setAccessible(true);
            return (IIngredientType<T>) field.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException("Could not find Mekanism's " + name + " ingredient type", exception);
        }
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

    public static final class OfGas extends ChemicalIngredientConverter<GasStack> {
        private static final IIngredientType<GasStack> TYPE_GAS = find("TYPE_GAS");

        @Override
        public IIngredientType<GasStack> getIngredientType() {
            return TYPE_GAS;
        }
    }

    public static final class OfInfusion extends ChemicalIngredientConverter<InfusionStack> {
        private static final IIngredientType<InfusionStack> TYPE_INFUSION = find("TYPE_INFUSION");

        @Override
        public IIngredientType<InfusionStack> getIngredientType() {
            return TYPE_INFUSION;
        }
    }

    public static final class OfPigment extends ChemicalIngredientConverter<PigmentStack> {
        private static final IIngredientType<PigmentStack> TYPE_PIGMENT = find("TYPE_PIGMENT");

        @Override
        public IIngredientType<PigmentStack> getIngredientType() {
            return TYPE_PIGMENT;
        }
    }

    public static final class OfSlurry extends ChemicalIngredientConverter<SlurryStack> {
        private static final IIngredientType<SlurryStack> TYPE_SLURRY = find("TYPE_SLURRY");

        @Override
        public IIngredientType<SlurryStack> getIngredientType() {
            return TYPE_SLURRY;
        }
    }
}
