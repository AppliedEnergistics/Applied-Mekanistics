package me.ramidzkh.mekae2.integration.jei;

import net.minecraft.resources.ResourceLocation;

import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.api.IMekanismAccess;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;

import appeng.api.integrations.jei.IngredientConverters;

@JeiPlugin
public class AMJEIPlugin implements IModPlugin {

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
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
