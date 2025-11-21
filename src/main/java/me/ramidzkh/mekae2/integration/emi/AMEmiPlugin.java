package me.ramidzkh.mekae2.integration.emi;

import net.minecraftforge.fml.ModList;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

import appeng.api.integrations.emi.EmiStackConverters;

@EmiEntrypoint
public class AMEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
    }

    static {
        if (ModList.get().isLoaded("jei")) {
            // We need both EMI and JEI to do anything!
            EmiStackConverters.register(new ChemicalIngredientConverter());
        }
    }
}
