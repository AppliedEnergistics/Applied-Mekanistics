package me.ramidzkh.mekae2.impl;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mezz.jei.api.ingredients.IIngredientType;

public class MekanismJEIBridge {

    public static final IIngredientType<GasStack> TYPE_GAS = find("TYPE_GAS");
    public static final IIngredientType<InfusionStack> TYPE_INFUSION = find("TYPE_INFUSION");
    public static final IIngredientType<PigmentStack> TYPE_PIGMENT = find("TYPE_PIGMENT");
    public static final IIngredientType<SlurryStack> TYPE_SLURRY = find("TYPE_SLURRY");

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
}
