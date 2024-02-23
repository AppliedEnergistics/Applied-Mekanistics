package me.ramidzkh.mekae2;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;

public class AMMenus {

    public static final MenuType<MEStorageMenu> PORTABLE_CHEMICAL_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_chemical_cell");

    @SuppressWarnings("RedundantTypeArguments")
    public static void initialize(IEventBus bus) {
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(PORTABLE_CHEMICAL_CELL_TYPE,
                    MEStorageScreen::new, "/screens/terminals/portable_chemical_cell.json");
        }));
    }
}
