package me.ramidzkh.mekae2;

import net.minecraft.resources.ResourceLocation;

import me.ramidzkh.mekae2.ae2.ChemicalIngredientConverter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

import appeng.api.integrations.jei.IngredientConverters;

@JeiPlugin
public class AMJEIPlugin implements IModPlugin {

    public AMJEIPlugin() {
        IngredientConverters.register(new ChemicalIngredientConverter.OfGas());
        IngredientConverters.register(new ChemicalIngredientConverter.OfInfusion());
        IngredientConverters.register(new ChemicalIngredientConverter.OfPigment());
        IngredientConverters.register(new ChemicalIngredientConverter.OfSlurry());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AppliedMekanistics.id("jei");
    }
}
