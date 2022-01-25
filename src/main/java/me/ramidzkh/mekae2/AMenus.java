package me.ramidzkh.mekae2;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AMenus {

    public static final MenuType<MEStorageMenu> PORTABLE_GAS_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_gas_cell");
    public static final MenuType<MEStorageMenu> PORTABLE_INFUSION_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_infusion_cell");
    public static final MenuType<MEStorageMenu> PORTABLE_PIGMENT_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_pigment_cell");
    public static final MenuType<MEStorageMenu> PORTABLE_SLURRY_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_slurry_cell");

    @SuppressWarnings("RedundantTypeArguments")
    public static void initialize(IEventBus bus) {
        bus.addGenericListener(MenuType.class, (RegistryEvent.Register<MenuType<?>> event) -> {
            event.getRegistry().registerAll(PORTABLE_GAS_CELL_TYPE, PORTABLE_INFUSION_CELL_TYPE, PORTABLE_PIGMENT_CELL_TYPE, PORTABLE_SLURRY_CELL_TYPE);
        });

        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(PORTABLE_GAS_CELL_TYPE, MEStorageScreen::new, "/screens/terminals/portable_gas_cell.json");
            InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(PORTABLE_INFUSION_CELL_TYPE, MEStorageScreen::new, "/screens/terminals/portable_infusion_cell.json");
            InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(PORTABLE_PIGMENT_CELL_TYPE, MEStorageScreen::new, "/screens/terminals/portable_pigment_cell.json");
            InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(PORTABLE_SLURRY_CELL_TYPE, MEStorageScreen::new, "/screens/terminals/portable_slurry_cell.json");
        }));
    }
}
