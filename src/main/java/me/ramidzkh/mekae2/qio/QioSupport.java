package me.ramidzkh.mekae2.qio;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import mekanism.api.MekanismAPI;
import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.api.security.ISecurityObject;

import appeng.api.networking.GridHelper;
import appeng.capabilities.AppEngCapabilities;

public class QioSupport {

    public static void onBlockEntityCapability(RegisterCapabilitiesEvent event) {
        event.registerBlock(AppEngCapabilities.ME_STORAGE, (level, pos, state, be, side) -> {
            if (be != null && side != null) {
                // guess the source...
                // if you're trying to qio across a compact machine wall or something, sorry!
                var host = GridHelper.getNodeHost(level, pos.relative(side));

                if (host == null) {
                    return null;
                }

                var source = host.getGridNode(side.getOpposite());

                // I don't know of any full-block nodes which query inventories, but we'll see
                if (source == null) {
                    source = host.getGridNode(null);
                }

                if (source == null) {
                    return null;
                }

                var adapter = new QioStorageAdapter<>((BlockEntity & IQIOComponent & ISecurityObject) be, side,
                        source.getOwningPlayerProfileId());

                if (adapter.getFrequency() != null) {
                    return adapter;
                }
            }

            return null;
        }, BuiltInRegistries.BLOCK.get(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "qio_dashboard")));
    }
}
