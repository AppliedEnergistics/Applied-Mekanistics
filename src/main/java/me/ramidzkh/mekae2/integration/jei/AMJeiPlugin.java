package me.ramidzkh.mekae2.integration.jei;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import me.ramidzkh.mekae2.AppliedMekanistics;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

@JeiPlugin
public class AMJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AppliedMekanistics.ID, "jei_plugin");
    }

    public AMJeiPlugin() {
        if (isModLoaded("ae2jeiintegration")) {
            AE2JeiIntegrationHelper.register();
        }
    }

    private static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }
}
