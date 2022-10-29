package me.ramidzkh.mekae2;

import net.minecraft.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import me.ramidzkh.mekae2.ae2.ChemicalP2PTunnelPart;
import me.ramidzkh.mekae2.item.ChemicalPortableCellItem;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;

import appeng.api.parts.PartModels;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.CreativeCellItem;
import appeng.items.storage.StorageTier;

public class AMItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AppliedMekanistics.ID);

    public static void initialize(IEventBus bus) {
        ITEMS.register(bus);
    }

    private static Item basic() {
        return new MaterialItem(properties());
    }

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(AppliedMekanistics.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AMItems.CHEMICAL_CELL_64K.get());
        }
    };

    private static Item.Properties properties() {
        return new Item.Properties().tab(CREATIVE_TAB);
    }

    public static final RegistryObject<Item> CHEMICAL_CELL_HOUSING = ITEMS.register("chemical_cell_housing",
            AMItems::basic);

    public static final RegistryObject<Item> CHEMICAL_CELL_CREATIVE = ITEMS.register("creative_chemical_cell",
            () -> new CreativeCellItem(properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> CHEMICAL_CELL_1K = ITEMS.register("chemical_storage_cell_1k",
            () -> new ChemicalStorageCell(properties().stacksTo(1), StorageTier.SIZE_1K, CHEMICAL_CELL_HOUSING.get()));
    public static final RegistryObject<Item> CHEMICAL_CELL_4K = ITEMS.register("chemical_storage_cell_4k",
            () -> new ChemicalStorageCell(properties().stacksTo(1), StorageTier.SIZE_4K, CHEMICAL_CELL_HOUSING.get()));
    public static final RegistryObject<Item> CHEMICAL_CELL_16K = ITEMS.register("chemical_storage_cell_16k",
            () -> new ChemicalStorageCell(properties().stacksTo(1), StorageTier.SIZE_16K, CHEMICAL_CELL_HOUSING.get()));
    public static final RegistryObject<Item> CHEMICAL_CELL_64K = ITEMS.register("chemical_storage_cell_64k",
            () -> new ChemicalStorageCell(properties().stacksTo(1), StorageTier.SIZE_64K, CHEMICAL_CELL_HOUSING.get()));
    public static final RegistryObject<Item> CHEMICAL_CELL_256K = ITEMS.register("chemical_storage_cell_256k",
            () -> new ChemicalStorageCell(properties().stacksTo(1), StorageTier.SIZE_256K,
                    CHEMICAL_CELL_HOUSING.get()));

    public static final RegistryObject<Item> PORTABLE_CHEMICAL_CELL_1K = ITEMS.register(
            "portable_chemical_storage_cell_1k", () -> new ChemicalPortableCellItem(AMMenus.PORTABLE_CHEMICAL_CELL_TYPE,
                    StorageTier.SIZE_1K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_CHEMICAL_CELL_4K = ITEMS.register(
            "portable_chemical_storage_cell_4k", () -> new ChemicalPortableCellItem(AMMenus.PORTABLE_CHEMICAL_CELL_TYPE,
                    StorageTier.SIZE_4K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_CHEMICAL_CELL_16K = ITEMS.register(
            "portable_chemical_storage_cell_16k",
            () -> new ChemicalPortableCellItem(AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, StorageTier.SIZE_16K,
                    properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_CHEMICAL_CELL_64K = ITEMS.register(
            "portable_chemical_storage_cell_64k",
            () -> new ChemicalPortableCellItem(AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, StorageTier.SIZE_64K,
                    properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_CHEMICAL_CELL_256K = ITEMS.register(
            "portable_chemical_storage_cell_256k",
            () -> new ChemicalPortableCellItem(AMMenus.PORTABLE_CHEMICAL_CELL_TYPE, StorageTier.SIZE_256K,
                    properties().stacksTo(1)));

    public static final RegistryObject<PartItem<ChemicalP2PTunnelPart>> CHEMICAL_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(ChemicalP2PTunnelPart.class));
        return ITEMS.register("chemical_p2p_tunnel",
                () -> new PartItem<>(properties(), ChemicalP2PTunnelPart.class, ChemicalP2PTunnelPart::new));
    });

    public static RegistryObject<Item> get(Tier tier) {
        return switch (tier) {
            case _1K -> CHEMICAL_CELL_1K;
            case _4K -> CHEMICAL_CELL_4K;
            case _16K -> CHEMICAL_CELL_16K;
            case _64K -> CHEMICAL_CELL_64K;
            case _256K -> CHEMICAL_CELL_256K;
        };
    }

    public static RegistryObject<Item> getPortableCell(Tier tier) {
        return switch (tier) {
            case _1K -> PORTABLE_CHEMICAL_CELL_1K;
            case _4K -> PORTABLE_CHEMICAL_CELL_4K;
            case _16K -> PORTABLE_CHEMICAL_CELL_16K;
            case _64K -> PORTABLE_CHEMICAL_CELL_64K;
            case _256K -> PORTABLE_CHEMICAL_CELL_256K;
        };
    }

    public enum Tier {
        _1K,
        _4K,
        _16K,
        _64K,
        _256K
    }
}
