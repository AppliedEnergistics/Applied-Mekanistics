package me.ramidzkh.mekae2;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import me.ramidzkh.mekae2.ae2.AMChemicalStackRenderer;

import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;

public class AppliedMekanisticsClient {

    public static void initialize(IEventBus bus) {
        bus.addListener(AppliedMekanisticsClient::registerItemColors);
        AMChemicalStackRenderer.initialize(bus);
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (var tier : AMItems.Tier.values()) {
            event.register(BasicStorageCell::getColor, AMItems.get(tier)::get);
            event.register(PortableCellItem::getColor, AMItems.getPortableCell(tier)::get);
        }
    }
}
