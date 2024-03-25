package me.ramidzkh.mekae2.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

import appeng.api.integrations.emi.EmiStackConverters;

@EmiEntrypoint
public class AMEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        EmiStackConverters.register(new ChemicalIngredientConverter());
    }
}
