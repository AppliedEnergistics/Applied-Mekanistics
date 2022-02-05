package me.ramidzkh.mekae2;

import appeng.items.storage.BasicStorageCell;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AE2MekanismAddonsClient {

    public static void initialize() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AE2MekanismAddonsClient::registerItemColors);
        AChemicalStackRenderer.initialize(bus);
    }

    private static void registerItemColors(ColorHandlerEvent.Item event) {
        for (var tier : AItems.Tier.values()) {
            event.getItemColors().register(BasicStorageCell::getColor, AItems.get(tier)::get);
        }
    }
}
