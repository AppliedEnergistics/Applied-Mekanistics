package me.ramidzkh.mekae2.integration.emi;

import appeng.api.integrations.emi.EmiStackConverters;

public final class MekanismJemiAdapter {
    private MekanismJemiAdapter() {
    }

    public static void init() {
        EmiStackConverters.register(new ChemicalIngredientConverter());
    }
}
