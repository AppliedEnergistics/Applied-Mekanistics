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
        event.getItemColors().register(BasicStorageCell::getColor,
                AItems.GAS_CELL_1K::get, AItems.GAS_CELL_4K::get, AItems.GAS_CELL_16K::get, AItems.GAS_CELL_64K::get,
                AItems.INFUSION_CELL_1K::get, AItems.INFUSION_CELL_4K::get, AItems.INFUSION_CELL_16K::get, AItems.INFUSION_CELL_64K::get,
                AItems.PIGMENT_CELL_1K::get, AItems.PIGMENT_CELL_4K::get, AItems.PIGMENT_CELL_16K::get, AItems.PIGMENT_CELL_64K::get,
                AItems.SLURRY_CELL_1K::get, AItems.SLURRY_CELL_4K::get, AItems.SLURRY_CELL_16K::get, AItems.SLURRY_CELL_64K::get);
    }
}
