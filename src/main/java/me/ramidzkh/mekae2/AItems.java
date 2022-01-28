package me.ramidzkh.mekae2;

import appeng.api.parts.PartModels;
import appeng.core.definitions.AEItems;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.CreativeCellItem;
import appeng.items.tools.powered.PortableCellItem;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.ramidzkh.mekae2.ae2.ChemicalP2PTunnelPart;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.item.ChemicalPortableCellItem;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;
import net.minecraft.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class AItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AE2MekanismAddons.ID);

    public static void initialize(IEventBus bus) {
        ITEMS.register(bus);
    }

    private static Item basic() {
        return new MaterialItem(properties());
    }

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(AE2MekanismAddons.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AItems.GAS_CELL_64K.get());
        }
    };

    private static Item.Properties properties() {
        return new Item.Properties().tab(CREATIVE_TAB);
    }

    public static final RegistryObject<Item> GAS_CELL_HOUSING = ITEMS.register("gas_cell_housing", AItems::basic);
    public static final RegistryObject<Item> INFUSION_CELL_HOUSING = ITEMS.register("infusion_cell_housing", AItems::basic);
    public static final RegistryObject<Item> PIGMENT_CELL_HOUSING = ITEMS.register("pigment_cell_housing", AItems::basic);
    public static final RegistryObject<Item> SLURRY_CELL_HOUSING = ITEMS.register("slurry_cell_housing", AItems::basic);

    public static final RegistryObject<Item> GAS_CELL_CREATIVE = ITEMS.register("creative_gas_cell", () -> new CreativeCellItem(properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> INFUSION_CELL_CREATIVE = ITEMS.register("creative_infusion_cell", () -> new CreativeCellItem(properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> PIGMENT_CELL_CREATIVE = ITEMS.register("creative_pigment_cell", () -> new CreativeCellItem(properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SLURRY_CELL_CREATIVE = ITEMS.register("creative_slurry_cell", () -> new CreativeCellItem(properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> GAS_CELL_1K = ITEMS.register("gas_storage_cell_1k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_1K, GAS_CELL_HOUSING.get(), 0.5f, 1, 8, 5, MekanismKeyType.GAS));
    public static final RegistryObject<Item> GAS_CELL_4K = ITEMS.register("gas_storage_cell_4k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_4K, GAS_CELL_HOUSING.get(), 1.0f, 4, 32, 5, MekanismKeyType.GAS));
    public static final RegistryObject<Item> GAS_CELL_16K = ITEMS.register("gas_storage_cell_16k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_16K, GAS_CELL_HOUSING.get(), 1.5f, 16, 128, 5, MekanismKeyType.GAS));
    public static final RegistryObject<Item> GAS_CELL_64K = ITEMS.register("gas_storage_cell_64k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_64K, GAS_CELL_HOUSING.get(), 2.0f, 64, 512, 5, MekanismKeyType.GAS));

    public static final RegistryObject<Item> INFUSION_CELL_1K = ITEMS.register("infusion_storage_cell_1k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_1K, INFUSION_CELL_HOUSING.get(), 0.5f, 1, 8, 5, MekanismKeyType.INFUSION));
    public static final RegistryObject<Item> INFUSION_CELL_4K = ITEMS.register("infusion_storage_cell_4k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_4K, INFUSION_CELL_HOUSING.get(), 1.0f, 4, 32, 5, MekanismKeyType.INFUSION));
    public static final RegistryObject<Item> INFUSION_CELL_16K = ITEMS.register("infusion_storage_cell_16k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_16K, INFUSION_CELL_HOUSING.get(), 1.5f, 16, 128, 5, MekanismKeyType.INFUSION));
    public static final RegistryObject<Item> INFUSION_CELL_64K = ITEMS.register("infusion_storage_cell_64k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_64K, INFUSION_CELL_HOUSING.get(), 2.0f, 64, 512, 5, MekanismKeyType.INFUSION));

    public static final RegistryObject<Item> PIGMENT_CELL_1K = ITEMS.register("pigment_storage_cell_1k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_1K, PIGMENT_CELL_HOUSING.get(), 0.5f, 1, 8, 5, MekanismKeyType.PIGMENT));
    public static final RegistryObject<Item> PIGMENT_CELL_4K = ITEMS.register("pigment_storage_cell_4k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_4K, PIGMENT_CELL_HOUSING.get(), 1.0f, 4, 32, 5, MekanismKeyType.PIGMENT));
    public static final RegistryObject<Item> PIGMENT_CELL_16K = ITEMS.register("pigment_storage_cell_16k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_16K, PIGMENT_CELL_HOUSING.get(), 1.5f, 16, 128, 5, MekanismKeyType.PIGMENT));
    public static final RegistryObject<Item> PIGMENT_CELL_64K = ITEMS.register("pigment_storage_cell_64k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_64K, PIGMENT_CELL_HOUSING.get(), 2.0f, 64, 512, 5, MekanismKeyType.PIGMENT));

    public static final RegistryObject<Item> SLURRY_CELL_1K = ITEMS.register("slurry_storage_cell_1k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_1K, SLURRY_CELL_HOUSING.get(), 0.5f, 1, 8, 5, MekanismKeyType.SLURRY));
    public static final RegistryObject<Item> SLURRY_CELL_4K = ITEMS.register("slurry_storage_cell_4k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_4K, SLURRY_CELL_HOUSING.get(), 1.0f, 4, 32, 5, MekanismKeyType.SLURRY));
    public static final RegistryObject<Item> SLURRY_CELL_16K = ITEMS.register("slurry_storage_cell_16k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_16K, SLURRY_CELL_HOUSING.get(), 1.5f, 16, 128, 5, MekanismKeyType.SLURRY));
    public static final RegistryObject<Item> SLURRY_CELL_64K = ITEMS.register("slurry_storage_cell_64k", () -> new ChemicalStorageCell(properties().stacksTo(1), AEItems.CELL_COMPONENT_64K, SLURRY_CELL_HOUSING.get(), 2.0f, 64, 512, 5, MekanismKeyType.SLURRY));

    public static final RegistryObject<Item> PORTABLE_GAS_CELL_1K = ITEMS.register("portable_gas_storage_cell_1k", () -> new ChemicalPortableCellItem(MekanismKeyType.GAS, AMenus.PORTABLE_GAS_CELL_TYPE, PortableCellItem.SIZE_1K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_GAS_CELL_4K = ITEMS.register("portable_gas_storage_cell_4k", () -> new ChemicalPortableCellItem(MekanismKeyType.GAS, AMenus.PORTABLE_GAS_CELL_TYPE, PortableCellItem.SIZE_4K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_GAS_CELL_16K = ITEMS.register("portable_gas_storage_cell_16k", () -> new ChemicalPortableCellItem(MekanismKeyType.GAS, AMenus.PORTABLE_GAS_CELL_TYPE, PortableCellItem.SIZE_16K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_GAS_CELL_64K = ITEMS.register("portable_gas_storage_cell_64k", () -> new ChemicalPortableCellItem(MekanismKeyType.GAS, AMenus.PORTABLE_GAS_CELL_TYPE, PortableCellItem.SIZE_64K, properties().stacksTo(1)));

    public static final RegistryObject<Item> PORTABLE_INFUSION_CELL_1K = ITEMS.register("portable_infusion_storage_cell_1k", () -> new ChemicalPortableCellItem(MekanismKeyType.INFUSION, AMenus.PORTABLE_INFUSION_CELL_TYPE, PortableCellItem.SIZE_1K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_INFUSION_CELL_4K = ITEMS.register("portable_infusion_storage_cell_4k", () -> new ChemicalPortableCellItem(MekanismKeyType.INFUSION, AMenus.PORTABLE_INFUSION_CELL_TYPE, PortableCellItem.SIZE_4K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_INFUSION_CELL_16K = ITEMS.register("portable_infusion_storage_cell_16k", () -> new ChemicalPortableCellItem(MekanismKeyType.INFUSION, AMenus.PORTABLE_INFUSION_CELL_TYPE, PortableCellItem.SIZE_16K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_INFUSION_CELL_64K = ITEMS.register("portable_infusion_storage_cell_64k", () -> new ChemicalPortableCellItem(MekanismKeyType.INFUSION, AMenus.PORTABLE_INFUSION_CELL_TYPE, PortableCellItem.SIZE_64K, properties().stacksTo(1)));

    public static final RegistryObject<Item> PORTABLE_PIGMENT_CELL_1K = ITEMS.register("portable_pigment_storage_cell_1k", () -> new ChemicalPortableCellItem(MekanismKeyType.PIGMENT, AMenus.PORTABLE_PIGMENT_CELL_TYPE, PortableCellItem.SIZE_1K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_PIGMENT_CELL_4K = ITEMS.register("portable_pigment_storage_cell_4k", () -> new ChemicalPortableCellItem(MekanismKeyType.PIGMENT, AMenus.PORTABLE_PIGMENT_CELL_TYPE, PortableCellItem.SIZE_4K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_PIGMENT_CELL_16K = ITEMS.register("portable_pigment_storage_cell_16k", () -> new ChemicalPortableCellItem(MekanismKeyType.PIGMENT, AMenus.PORTABLE_PIGMENT_CELL_TYPE, PortableCellItem.SIZE_16K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_PIGMENT_CELL_64K = ITEMS.register("portable_pigment_storage_cell_64k", () -> new ChemicalPortableCellItem(MekanismKeyType.PIGMENT, AMenus.PORTABLE_PIGMENT_CELL_TYPE, PortableCellItem.SIZE_64K, properties().stacksTo(1)));

    public static final RegistryObject<Item> PORTABLE_SLURRY_CELL_1K = ITEMS.register("portable_slurry_storage_cell_1k", () -> new ChemicalPortableCellItem(MekanismKeyType.SLURRY, AMenus.PORTABLE_SLURRY_CELL_TYPE, PortableCellItem.SIZE_1K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_SLURRY_CELL_4K = ITEMS.register("portable_slurry_storage_cell_4k", () -> new ChemicalPortableCellItem(MekanismKeyType.SLURRY, AMenus.PORTABLE_SLURRY_CELL_TYPE, PortableCellItem.SIZE_4K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_SLURRY_CELL_16K = ITEMS.register("portable_slurry_storage_cell_16k", () -> new ChemicalPortableCellItem(MekanismKeyType.SLURRY, AMenus.PORTABLE_SLURRY_CELL_TYPE, PortableCellItem.SIZE_16K, properties().stacksTo(1)));
    public static final RegistryObject<Item> PORTABLE_SLURRY_CELL_64K = ITEMS.register("portable_slurry_storage_cell_64k", () -> new ChemicalPortableCellItem(MekanismKeyType.SLURRY, AMenus.PORTABLE_SLURRY_CELL_TYPE, PortableCellItem.SIZE_64K, properties().stacksTo(1)));

    public static final RegistryObject<PartItem<ChemicalP2PTunnelPart>> CHEMICAL_P2P_TUNNEL = Util.make(() -> {
        PartModels.registerModels(PartModelsHelper.createModels(ChemicalP2PTunnelPart.class));
        return ITEMS.register("chemical_p2p_tunnel", () -> new PartItem<>(properties(), ChemicalP2PTunnelPart.class, ChemicalP2PTunnelPart::new));
    });

    private static final Table<Type, Tier, RegistryObject<Item>> MATRIX = Util.make(HashBasedTable.create(), table -> {
        table.put(Type.GAS, Tier.HOUSING, GAS_CELL_HOUSING);
        table.put(Type.GAS, Tier.CREATIVE, GAS_CELL_CREATIVE);
        table.put(Type.GAS, Tier._1K, GAS_CELL_1K);
        table.put(Type.GAS, Tier._4K, GAS_CELL_4K);
        table.put(Type.GAS, Tier._16K, GAS_CELL_16K);
        table.put(Type.GAS, Tier._64K, GAS_CELL_64K);

        table.put(Type.INFUSION, Tier.HOUSING, INFUSION_CELL_HOUSING);
        table.put(Type.INFUSION, Tier.CREATIVE, INFUSION_CELL_CREATIVE);
        table.put(Type.INFUSION, Tier._1K, INFUSION_CELL_1K);
        table.put(Type.INFUSION, Tier._4K, INFUSION_CELL_4K);
        table.put(Type.INFUSION, Tier._16K, INFUSION_CELL_16K);
        table.put(Type.INFUSION, Tier._64K, INFUSION_CELL_64K);

        table.put(Type.PIGMENT, Tier.HOUSING, PIGMENT_CELL_HOUSING);
        table.put(Type.PIGMENT, Tier.CREATIVE, PIGMENT_CELL_CREATIVE);
        table.put(Type.PIGMENT, Tier._1K, PIGMENT_CELL_1K);
        table.put(Type.PIGMENT, Tier._4K, PIGMENT_CELL_4K);
        table.put(Type.PIGMENT, Tier._16K, PIGMENT_CELL_16K);
        table.put(Type.PIGMENT, Tier._64K, PIGMENT_CELL_64K);

        table.put(Type.SLURRY, Tier.HOUSING, SLURRY_CELL_HOUSING);
        table.put(Type.SLURRY, Tier.CREATIVE, SLURRY_CELL_CREATIVE);
        table.put(Type.SLURRY, Tier._1K, SLURRY_CELL_1K);
        table.put(Type.SLURRY, Tier._4K, SLURRY_CELL_4K);
        table.put(Type.SLURRY, Tier._16K, SLURRY_CELL_16K);
        table.put(Type.SLURRY, Tier._64K, SLURRY_CELL_64K);
    });

    private static final Table<Type, Tier, RegistryObject<Item>> PORTABLE_MATRIX = Util.make(HashBasedTable.create(), table -> {
        table.put(Type.GAS, Tier._1K, PORTABLE_GAS_CELL_1K);
        table.put(Type.GAS, Tier._4K, PORTABLE_GAS_CELL_4K);
        table.put(Type.GAS, Tier._16K, PORTABLE_GAS_CELL_16K);
        table.put(Type.GAS, Tier._64K, PORTABLE_GAS_CELL_64K);

        table.put(Type.INFUSION, Tier._1K, PORTABLE_INFUSION_CELL_1K);
        table.put(Type.INFUSION, Tier._4K, PORTABLE_INFUSION_CELL_4K);
        table.put(Type.INFUSION, Tier._16K, PORTABLE_INFUSION_CELL_16K);
        table.put(Type.INFUSION, Tier._64K, PORTABLE_INFUSION_CELL_64K);

        table.put(Type.PIGMENT, Tier._1K, PORTABLE_PIGMENT_CELL_1K);
        table.put(Type.PIGMENT, Tier._4K, PORTABLE_PIGMENT_CELL_4K);
        table.put(Type.PIGMENT, Tier._16K, PORTABLE_PIGMENT_CELL_16K);
        table.put(Type.PIGMENT, Tier._64K, PORTABLE_PIGMENT_CELL_64K);

        table.put(Type.SLURRY, Tier._1K, PORTABLE_SLURRY_CELL_1K);
        table.put(Type.SLURRY, Tier._4K, PORTABLE_SLURRY_CELL_4K);
        table.put(Type.SLURRY, Tier._16K, PORTABLE_SLURRY_CELL_16K);
        table.put(Type.SLURRY, Tier._64K, PORTABLE_SLURRY_CELL_64K);
    });

    public static RegistryObject<Item> get(Type type, Tier tier) {
        // noinspection ConstantConditions The matrix is guaranteed to be filled for all possible valid inputs
        return MATRIX.get(type, tier);
    }

    public static RegistryObject<Item> getPortableCell(Type type, Tier tier) {
        return Objects.requireNonNull(getPortableCellNullable(type, tier), "Tier " + tier + " not supported as a portable cell");
    }

    @Nullable
    public static RegistryObject<Item> getPortableCellNullable(Type type, Tier tier) {
        return PORTABLE_MATRIX.get(type, tier);
    }

    public enum Type {
        GAS,
        INFUSION,
        PIGMENT,
        SLURRY;
    }

    public enum Tier {
        HOUSING,
        CREATIVE,
        _1K,
        _4K,
        _16K,
        _64K;

        public static final List<Tier> PORTABLE = List.of(_1K, _4K, _16K, _64K);
    }
}
