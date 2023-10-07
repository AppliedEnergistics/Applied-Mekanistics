package me.ramidzkh.mekae2.integration.jei;

import net.minecraft.resources.ResourceLocation;

import me.ramidzkh.mekae2.AppliedMekanistics;
import me.ramidzkh.mekae2.ae2.ChemicalIngredientConverter;
import mekanism.api.IMekanismAccess;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

import appeng.api.integrations.jei.IngredientConverters;

@JeiPlugin
public class AMJEIPlugin implements IModPlugin {

    public AMJEIPlugin() {
        var helper = IMekanismAccess.INSTANCE.jeiHelper();

        IngredientConverters
                .register(new ChemicalIngredientConverter<>(helper.getGasStackHelper().getIngredientType()));
        IngredientConverters
                .register(new ChemicalIngredientConverter<>(helper.getInfusionStackHelper().getIngredientType()));
        IngredientConverters
                .register(new ChemicalIngredientConverter<>(helper.getPigmentStackHelper().getIngredientType()));
        IngredientConverters
                .register(new ChemicalIngredientConverter<>(helper.getSlurryStackHelper().getIngredientType()));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AppliedMekanistics.id("jei");
    }
}
