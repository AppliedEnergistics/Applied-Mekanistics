package me.ramidzkh.mekae2;

import appeng.items.storage.BasicStorageCell;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

public class AE2MekanismAddonsClient {

    public static void initialize() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AE2MekanismAddonsClient::registerItemColors);
        AChemicalStackRenderer.initialize(bus);
    }

    private static void registerItemColors(ColorHandlerEvent.Item event) {
        for (var type : AItems.Type.values()) {
            for (var tier : List.of(AItems.Tier._1K, AItems.Tier._4K, AItems.Tier._16K, AItems.Tier._64K)) {
                event.getItemColors().register(BasicStorageCell::getColor, AItems.get(type, tier)::get);
            }
        }
    }
}
