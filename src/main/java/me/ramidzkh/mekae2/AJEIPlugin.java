package me.ramidzkh.mekae2;

import appeng.api.integrations.jei.IngredientConverters;
import me.ramidzkh.mekae2.ae2.ChemicalIngredientConverter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class AJEIPlugin implements IModPlugin {

    public AJEIPlugin() {
        IngredientConverters.register(new ChemicalIngredientConverter.OfGas());
        IngredientConverters.register(new ChemicalIngredientConverter.OfInfusion());
        IngredientConverters.register(new ChemicalIngredientConverter.OfPigment());
        IngredientConverters.register(new ChemicalIngredientConverter.OfSlurry());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AE2MekanismAddons.id("jei");
    }
}
