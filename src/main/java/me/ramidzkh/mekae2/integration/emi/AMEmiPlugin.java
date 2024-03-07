package me.ramidzkh.mekae2.integration.emi;

import net.neoforged.fml.ModList;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class AMEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
    }

    static {
        if (ModList.get().isLoaded("jei")) {
            // We need both EMI and JEI to do anything!
            MekanismJemiAdapter.init();
        }
    }
}
