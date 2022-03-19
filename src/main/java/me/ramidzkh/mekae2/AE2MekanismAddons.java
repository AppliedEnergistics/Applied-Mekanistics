package me.ramidzkh.mekae2;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
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
import appeng.parts.automation.StackWorldBehaviors;
import me.ramidzkh.mekae2.ae2.ChemicalContainerItemStrategy;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.ae2.stack.MekanismExternalStorageStrategy;
import me.ramidzkh.mekae2.ae2.stack.MekanismStackExportStrategy;
import me.ramidzkh.mekae2.ae2.stack.MekanismStackImportStrategy;
import me.ramidzkh.mekae2.data.MekAE2DataGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("ae2_mekanism_addons")
public class AE2MekanismAddons {

    public static final String ID = "ae2-mekanism-addons";

    public AE2MekanismAddons() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        AItems.initialize(bus);
        AMenus.initialize(bus);

        bus.addListener(MekAE2DataGenerators::onGatherData);

        AEKeyTypes.register(MekanismKeyType.TYPE);

        StackWorldBehaviors.registerImportStrategy(MekanismKeyType.TYPE, MekanismStackImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(MekanismKeyType.TYPE, MekanismStackExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(MekanismKeyType.TYPE, MekanismExternalStorageStrategy::new);

        ContainerItemStrategy.register(MekanismKeyType.TYPE, MekanismKey.class, new ChemicalContainerItemStrategy());
        GenericSlotCapacities.register(MekanismKeyType.TYPE, GenericSlotCapacities.getMap().get(AEKeyType.fluids()));

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
        StorageCells.addCellGuiHandler(new ICellGuiHandler() {
            @Override
            public boolean isSpecializedFor(ItemStack cell) {
                return cell.getItem() instanceof IBasicCellItem basicCellItem
                        && basicCellItem.getKeyType() == MekanismKeyType.TYPE;
            }

            @Override
            public void openChestGui(Player player, IChestOrDrive chest, ICellHandler cellHandler, ItemStack cell) {
                chest.getUp();
                MenuOpener.open(MEStorageMenu.TYPE, player,
                        MenuLocators.forBlockEntity((BlockEntity) chest));
            }
        });

        StorageCellModels.registerModel(AItems.CHEMICAL_CELL_CREATIVE::get, AppEng.makeId("block/drive/cells/creative_cell"));

        for (var tier : AItems.Tier.values()) {
            var cell = AItems.get(tier);
            var portable = AItems.getPortableCell(tier);

            registerCell(cell::get, portable::get, cell.getId().getPath());
        }
    }

    private void registerCell(ItemLike cell, ItemLike portableCell, String path) {
        StorageCellModels.registerModel(cell, id("block/drive/cells/" + path));
        StorageCellModels.registerModel(portableCell, id("block/drive/cells/" + path));
    }

    private void initializeUpgrades() {
        var storageCellGroup = GuiText.StorageCells.getTranslationKey();
        var portableStorageCellGroup = GuiText.PortableCells.getTranslationKey();

        for (var tier : AItems.Tier.values()) {
            Upgrades.add(AEItems.INVERTER_CARD, AItems.getPortableCell(tier)::get, 1, storageCellGroup);
        }

        for (var tier : AItems.Tier.values()) {
            var portableCell = AItems.getPortableCell(tier);
            Upgrades.add(AEItems.INVERTER_CARD, portableCell::get, 1, portableStorageCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell::get, 2, portableStorageCellGroup);
        }
    }

    private void initializeAttunement() {
        var basic = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "basic_chemical_tank"));
        var advanced = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "advanced_chemical_tank"));
        var elite = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "elite_chemical_tank"));
        var ultimate = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "ultimate_chemical_tank"));
        var creative = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mekanism", "creative_chemical_tank"));

        if (basic != null) {
            P2PTunnelAttunement.addItem(basic, AItems.CHEMICAL_P2P_TUNNEL::get);
        }

        if (advanced != null) {
            P2PTunnelAttunement.addItem(advanced, AItems.CHEMICAL_P2P_TUNNEL::get);
        }

        if (elite != null) {
            P2PTunnelAttunement.addItem(elite, AItems.CHEMICAL_P2P_TUNNEL::get);
        }

        if (ultimate != null) {
            P2PTunnelAttunement.addItem(ultimate, AItems.CHEMICAL_P2P_TUNNEL::get);
        }

        if (creative != null) {
            P2PTunnelAttunement.addItem(creative, AItems.CHEMICAL_P2P_TUNNEL::get);
        }
    }
}
