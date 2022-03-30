package me.ramidzkh.mekae2;

import appeng.items.storage.BasicStorageCell;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AppliedMekanisticsClient {

    public static void initialize() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AppliedMekanisticsClient::registerItemColors);
        AMChemicalStackRenderer.initialize(bus);
    }

    private static void registerItemColors(ColorHandlerEvent.Item event) {
        for (var tier : AMItems.Tier.values()) {
            event.getItemColors().register(BasicStorageCell::getColor, AMItems.get(tier)::get);
        }
    }
}
