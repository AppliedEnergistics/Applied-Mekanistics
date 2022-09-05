package me.ramidzkh.mekae2.integration.jade;

import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

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
            // This is ugly, but nothing else worked perfectly due to Jade using old server data for new blocks.
            // TODO: check if this is still needed in 1.19+
            for (var loc : CHEMICALS) {
                if (tooltip.get(loc).size() != 9) {
                    return;
                }
            }

            // If we have 9 of each 4, remove them.
            for (var loc : CHEMICALS) {
                tooltip.remove(loc);
            }
        });
    }
}
