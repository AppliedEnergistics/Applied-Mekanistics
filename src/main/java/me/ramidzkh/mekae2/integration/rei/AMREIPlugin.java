package me.ramidzkh.mekae2.integration.rei;

import java.util.Locale;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.forge.REIPlugin;
import mekanism.api.MekanismAPI;
import mezz.jei.api.ingredients.IIngredientType;

import appeng.api.integrations.rei.IngredientConverters;

@REIPlugin(Dist.CLIENT)
public class AMREIPlugin implements REIClientPlugin {

    public AMREIPlugin() {
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        var helper = MekanismAPI.getJeiHelper();

        IngredientConverters
                .register(new ChemicalIngredientConverter<>(compat(helper.getGasStackHelper().getIngredientType())));
        IngredientConverters
                .register(
                        new ChemicalIngredientConverter<>(compat(helper.getInfusionStackHelper().getIngredientType())));
        IngredientConverters
                .register(
                        new ChemicalIngredientConverter<>(compat(helper.getPigmentStackHelper().getIngredientType())));
        IngredientConverters
                .register(new ChemicalIngredientConverter<>(compat(helper.getSlurryStackHelper().getIngredientType())));
    }

    @Override
    public String getPluginProviderName() {
        return "Applied Mekanistics";
    }

    private static <T> EntryType<T> compat(IIngredientType<T> type) {
        return EntryType.deferred(new ResourceLocation(MekanismAPI.MEKANISM_MODID,
                "jei_plugin_jei_compat_" + type.getIngredientClass().getSimpleName().toLowerCase(Locale.ROOT)));
    }
}
