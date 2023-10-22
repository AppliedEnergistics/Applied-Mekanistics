package me.ramidzkh.mekae2.integration.jade;

import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;

/**
 * Plugin to remove the mekanism-added chemical handler lines for interfaces and pattern providers.
 */
@WailaPlugin
public class AMJadePlugin implements IWailaPlugin {
    private static final ResourceLocation[] CHEMICALS = {
            new ResourceLocation("mekanism", "gas"),
            new ResourceLocation("mekanism", "infuse_type"),
            new ResourceLocation("mekanism", "pigment"),
            new ResourceLocation("mekanism", "slurry"),
    };

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();

            if (target instanceof InterfaceLogicHost || target instanceof PatternProviderLogicHost) {
                for (var loc : CHEMICALS) {
                    tooltip.remove(loc);
                }
            }
        });
    }
}
