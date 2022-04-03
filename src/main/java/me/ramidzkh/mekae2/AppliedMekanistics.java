package me.ramidzkh.mekae2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import me.ramidzkh.mekae2.ae2.ChemicalContainerItemStrategy;
import me.ramidzkh.mekae2.ae2.GenericStackChemicalStorage;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.ae2.stack.MekanismExternalStorageStrategy;
import me.ramidzkh.mekae2.ae2.stack.MekanismStackExportStrategy;
import me.ramidzkh.mekae2.ae2.stack.MekanismStackImportStrategy;
import me.ramidzkh.mekae2.data.MekAE2DataGenerators;
import me.ramidzkh.mekae2.qio.QioSupport;

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
import appeng.capabilities.Capabilities;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.common.MEStorageMenu;
import appeng.parts.automation.StackWorldBehaviors;

@Mod(AppliedMekanistics.ID)
public class AppliedMekanistics {

    public static final String ID = "appmek";

    public AppliedMekanistics() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        AMItems.initialize(bus);
        AMMenus.initialize(bus);

        bus.addListener(MekAE2DataGenerators::onGatherData);

        bus.addGenericListener(AEKeyType.class, (RegistryEvent.Register<AEKeyType> event) -> {
            AEKeyTypes.register(MekanismKeyType.TYPE);
        });

        StackWorldBehaviors.registerImportStrategy(MekanismKeyType.TYPE, MekanismStackImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(MekanismKeyType.TYPE, MekanismStackExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(MekanismKeyType.TYPE, MekanismExternalStorageStrategy::new);

        ContainerItemStrategy.register(MekanismKeyType.TYPE, MekanismKey.class, new ChemicalContainerItemStrategy());
        GenericSlotCapacities.register(MekanismKeyType.TYPE, GenericSlotCapacities.getMap().get(AEKeyType.fluids()));

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::initializeCapabilities);

        QioSupport.initialize();

        bus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(this::initializeModels);
            event.enqueueWork(this::initializeUpgrades);
            event.enqueueWork(this::initializeAttunement);
        });

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AppliedMekanisticsClient::initialize);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    private void initializeCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        var blockEntity = event.getObject();

        event.addCapability(id("generic_inv_wrapper"), new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                if (capability == MekCapabilities.GAS_HANDLER_CAPABILITY) {
                    return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackChemicalStorage.OfGas::new).cast();
                } else if (capability == MekCapabilities.INFUSION_HANDLER_CAPABILITY) {
                    return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackChemicalStorage.OfInfusion::new).cast();
                } else if (capability == MekCapabilities.PIGMENT_HANDLER_CAPABILITY) {
                    return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackChemicalStorage.OfPigment::new).cast();
                } else if (capability == MekCapabilities.SLURRY_HANDLER_CAPABILITY) {
                    return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                            .lazyMap(GenericStackChemicalStorage.OfSlurry::new).cast();
                }

                return LazyOptional.empty();
            }
        });
    }

    private void initializeModels() {
        StorageCells.addCellGuiHandler(new ICellGuiHandler() {
            @Override
            public boolean isSpecializedFor(ItemStack cell) {
                return cell.getItem()instanceof IBasicCellItem basicCellItem
                        && basicCellItem.getKeyType() == MekanismKeyType.TYPE;
            }

            @Override
            public void openChestGui(Player player, IChestOrDrive chest, ICellHandler cellHandler, ItemStack cell) {
                chest.getUp();
                MenuOpener.open(MEStorageMenu.TYPE, player,
                        MenuLocators.forBlockEntity((BlockEntity) chest));
            }
        });

        StorageCellModels.registerModel(AMItems.CHEMICAL_CELL_CREATIVE::get,
                AppEng.makeId("block/drive/cells/creative_cell"));

        for (var tier : AMItems.Tier.values()) {
            var cell = AMItems.get(tier);
            var portable = AMItems.getPortableCell(tier);

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

        for (var tier : AMItems.Tier.values()) {
            Upgrades.add(AEItems.INVERTER_CARD, AMItems.getPortableCell(tier)::get, 1, storageCellGroup);
        }

        for (var tier : AMItems.Tier.values()) {
            var portableCell = AMItems.getPortableCell(tier);
            Upgrades.add(AEItems.INVERTER_CARD, portableCell::get, 1, portableStorageCellGroup);
            Upgrades.add(AEItems.ENERGY_CARD, portableCell::get, 2, portableStorageCellGroup);
        }
    }

    private void initializeAttunement() {
        P2PTunnelAttunement.addItemByTag(AMItems.MEKANISM_TANKS, AMItems.CHEMICAL_P2P_TUNNEL::get);
    }
}
