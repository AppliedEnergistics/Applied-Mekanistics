package me.ramidzkh.mekae2;

import appeng.api.client.StorageCellModels;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.cells.ICellGuiHandler;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.common.MEStorageMenu;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.data.MekAE2DataGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod("ae2_mekanism_addons")
public class AE2MekanismAddons {

    public static final String ID = "ae2-mekanism-addons";

    public AE2MekanismAddons() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        AItems.initialize(bus);
        AMenus.initialize(bus);

        bus.addListener(MekAE2DataGenerators::onGatherData);

        AEKeyTypes.register(MekanismKeyType.GAS);
        AEKeyTypes.register(MekanismKeyType.INFUSION);
        AEKeyTypes.register(MekanismKeyType.PIGMENT);
        AEKeyTypes.register(MekanismKeyType.SLURRY);

        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(this::initializeModels);
            event.enqueueWork(this::initializeUpgrades);
            event.enqueueWork(this::initializeAttunement);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AE2MekanismAddonsClient::initialize);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    private void initializeModels() {
        record CellGuiHandler(AEKeyType type) implements ICellGuiHandler {
            @Override
            public boolean isSpecializedFor(ItemStack cell) {
                return cell.getItem() instanceof IBasicCellItem basicCellItem
                        && basicCellItem.getKeyType() == type;
            }

            @Override
            public void openChestGui(Player player, IChestOrDrive chest, ICellHandler cellHandler, ItemStack cell) {
                chest.getUp();
                MenuOpener.open(MEStorageMenu.TYPE, player,
                        MenuLocators.forBlockEntity((BlockEntity) chest));
            }
        }

        StorageCells.addCellGuiHandler(new CellGuiHandler(MekanismKeyType.GAS));
        StorageCells.addCellGuiHandler(new CellGuiHandler(MekanismKeyType.INFUSION));
        StorageCells.addCellGuiHandler(new CellGuiHandler(MekanismKeyType.PIGMENT));
        StorageCells.addCellGuiHandler(new CellGuiHandler(MekanismKeyType.SLURRY));

        StorageCellModels.registerModel(AItems.GAS_CELL_CREATIVE::get, AppEng.makeId("block/drive/cells/creative_cell"));
        StorageCellModels.registerModel(AItems.INFUSION_CELL_CREATIVE::get, AppEng.makeId("block/drive/cells/creative_cell"));
        StorageCellModels.registerModel(AItems.PIGMENT_CELL_CREATIVE::get, AppEng.makeId("block/drive/cells/creative_cell"));
        StorageCellModels.registerModel(AItems.SLURRY_CELL_CREATIVE::get, AppEng.makeId("block/drive/cells/creative_cell"));

        registerCell(AItems.GAS_CELL_1K::get, AItems.PORTABLE_GAS_CELL_1K::get, "1k_gas_cell");
        registerCell(AItems.GAS_CELL_4K::get, AItems.PORTABLE_GAS_CELL_4K::get, "4k_gas_cell");
        registerCell(AItems.GAS_CELL_16K::get, AItems.PORTABLE_GAS_CELL_16K::get, "16k_gas_cell");
        registerCell(AItems.GAS_CELL_64K::get, AItems.PORTABLE_GAS_CELL_64K::get, "64k_gas_cell");

        registerCell(AItems.INFUSION_CELL_1K::get, AItems.PORTABLE_INFUSION_CELL_1K::get, "1k_infusion_cell");
        registerCell(AItems.INFUSION_CELL_4K::get, AItems.PORTABLE_INFUSION_CELL_4K::get, "4k_infusion_cell");
        registerCell(AItems.INFUSION_CELL_16K::get, AItems.PORTABLE_INFUSION_CELL_16K::get, "16k_infusion_cell");
        registerCell(AItems.INFUSION_CELL_64K::get, AItems.PORTABLE_INFUSION_CELL_64K::get, "64k_infusion_cell");

        registerCell(AItems.PIGMENT_CELL_1K::get, AItems.PORTABLE_PIGMENT_CELL_1K::get, "1k_pigment_cell");
        registerCell(AItems.PIGMENT_CELL_4K::get, AItems.PORTABLE_PIGMENT_CELL_4K::get, "4k_pigment_cell");
        registerCell(AItems.PIGMENT_CELL_16K::get, AItems.PORTABLE_PIGMENT_CELL_16K::get, "16k_pigment_cell");
        registerCell(AItems.PIGMENT_CELL_64K::get, AItems.PORTABLE_PIGMENT_CELL_64K::get, "64k_pigment_cell");

        registerCell(AItems.SLURRY_CELL_1K::get, AItems.PORTABLE_SLURRY_CELL_1K::get, "1k_slurry_cell");
        registerCell(AItems.SLURRY_CELL_4K::get, AItems.PORTABLE_SLURRY_CELL_4K::get, "4k_slurry_cell");
        registerCell(AItems.SLURRY_CELL_16K::get, AItems.PORTABLE_SLURRY_CELL_16K::get, "16k_slurry_cell");
        registerCell(AItems.SLURRY_CELL_64K::get, AItems.PORTABLE_SLURRY_CELL_64K::get, "64k_slurry_cell");
    }

    private void registerCell(ItemLike cell, ItemLike portableCell, String path) {
        StorageCellModels.registerModel(cell, id("block/drive/cells/" + path));
        StorageCellModels.registerModel(portableCell, id("block/drive/cells/" + path));
    }

    private void initializeUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableStorageCellGroup = GuiText.PortableCells.getTranslationKey();

        var cells = List.of(
                AItems.GAS_CELL_1K, AItems.GAS_CELL_4K, AItems.GAS_CELL_16K, AItems.GAS_CELL_64K,
                AItems.INFUSION_CELL_1K, AItems.INFUSION_CELL_4K, AItems.INFUSION_CELL_16K, AItems.INFUSION_CELL_64K,
                AItems.PIGMENT_CELL_1K, AItems.PIGMENT_CELL_4K, AItems.PIGMENT_CELL_16K, AItems.PIGMENT_CELL_64K,
                AItems.SLURRY_CELL_1K, AItems.SLURRY_CELL_4K, AItems.SLURRY_CELL_16K, AItems.SLURRY_CELL_64K
        );

        for (var cell : cells) {
            Upgrades.add(AEItems.INVERTER_CARD, cell::get, 1, storageCellGroup);
        }

        var portableCells = List.of(
                AItems.PORTABLE_GAS_CELL_1K, AItems.PORTABLE_GAS_CELL_4K, AItems.PORTABLE_GAS_CELL_16K, AItems.PORTABLE_GAS_CELL_64K,
                AItems.PORTABLE_INFUSION_CELL_1K, AItems.PORTABLE_INFUSION_CELL_4K, AItems.PORTABLE_INFUSION_CELL_16K, AItems.PORTABLE_INFUSION_CELL_64K,
                AItems.PORTABLE_PIGMENT_CELL_1K, AItems.PORTABLE_PIGMENT_CELL_4K, AItems.PORTABLE_PIGMENT_CELL_16K, AItems.PORTABLE_PIGMENT_CELL_64K,
                AItems.PORTABLE_SLURRY_CELL_1K, AItems.PORTABLE_SLURRY_CELL_4K, AItems.PORTABLE_SLURRY_CELL_16K, AItems.PORTABLE_SLURRY_CELL_64K);

        for (var portableCell : portableCells) {
            Upgrades.add(AEItems.INVERTER_CARD, portableCell::get, 1, portableStorageCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell::get, 2, portableStorageCellGroup);
        }
    }

    private void initializeAttunement() {
        P2PTunnelAttunement.addItem(Blocks.TORCH, AItems.CHEMICAL_P2P_TUNNEL::get);
        P2PTunnelAttunement.addItem(Blocks.GLOWSTONE, AItems.CHEMICAL_P2P_TUNNEL::get);
    }
}
