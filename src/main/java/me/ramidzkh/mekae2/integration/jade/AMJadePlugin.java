package me.ramidzkh.mekae2.integration.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

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
        // Run in TAIL to be able to remove Mekanism's chemical tooltips
        registration.registerComponentProvider((tooltip, blockAccessor, pluginConfig) -> {
            // This is ugly, but nothing else worked perfectly due to Jade using old server data for new blocks.
            for (var loc : CHEMICALS) {
                if (tooltip.get(loc).size() != 9) {
                    return;
                }
            }

            // If we have 9 of each 4, remove them.
            for (var loc : CHEMICALS) {
                tooltip.remove(loc);
            }
        }, TooltipPosition.TAIL, Block.class); // We must use Block.class because of the desyncs mentioned above.
    }
}
