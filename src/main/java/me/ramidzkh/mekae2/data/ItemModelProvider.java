package me.ramidzkh.mekae2.data;

import appeng.core.AppEng;
import me.ramidzkh.mekae2.AE2MekanismAddons;
import me.ramidzkh.mekae2.AItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AE2MekanismAddons.ID, existingFileHelper);

        existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existingFileHelper.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(AItems.GAS_CELL_HOUSING, "item/gas_cell_housing");
        flatSingleLayer(AItems.INFUSION_CELL_HOUSING, "item/infusion_cell_housing");
        flatSingleLayer(AItems.PIGMENT_CELL_HOUSING, "item/pigment_cell_housing");
        flatSingleLayer(AItems.SLURRY_CELL_HOUSING, "item/slurry_cell_housing");

        flatSingleLayer(AItems.GAS_CELL_CREATIVE, "item/creative_gas_cell");
        flatSingleLayer(AItems.INFUSION_CELL_CREATIVE, "item/creative_infusion_cell");
        flatSingleLayer(AItems.PIGMENT_CELL_CREATIVE, "item/creative_pigment_cell");
        flatSingleLayer(AItems.SLURRY_CELL_CREATIVE, "item/creative_slurry_cell");

        cell(AItems.GAS_CELL_1K, AItems.PORTABLE_GAS_CELL_1K, "item/gas_storage_cell_1k");
        cell(AItems.GAS_CELL_4K, AItems.PORTABLE_GAS_CELL_4K, "item/gas_storage_cell_4k");
        cell(AItems.GAS_CELL_16K, AItems.PORTABLE_GAS_CELL_16K, "item/gas_storage_cell_16k");
        cell(AItems.GAS_CELL_64K, AItems.PORTABLE_GAS_CELL_64K, "item/gas_storage_cell_64k");

        cell(AItems.INFUSION_CELL_1K, AItems.PORTABLE_INFUSION_CELL_1K, "item/infusion_storage_cell_1k");
        cell(AItems.INFUSION_CELL_4K, AItems.PORTABLE_INFUSION_CELL_4K, "item/infusion_storage_cell_4k");
        cell(AItems.INFUSION_CELL_16K, AItems.PORTABLE_INFUSION_CELL_16K, "item/infusion_storage_cell_16k");
        cell(AItems.INFUSION_CELL_64K, AItems.PORTABLE_INFUSION_CELL_64K, "item/infusion_storage_cell_64k");

        cell(AItems.PIGMENT_CELL_1K, AItems.PORTABLE_PIGMENT_CELL_1K, "item/pigment_storage_cell_1k");
        cell(AItems.PIGMENT_CELL_4K, AItems.PORTABLE_PIGMENT_CELL_4K, "item/pigment_storage_cell_4k");
        cell(AItems.PIGMENT_CELL_16K, AItems.PORTABLE_PIGMENT_CELL_16K, "item/pigment_storage_cell_16k");
        cell(AItems.PIGMENT_CELL_64K, AItems.PORTABLE_PIGMENT_CELL_64K, "item/pigment_storage_cell_64k");

        cell(AItems.SLURRY_CELL_1K, AItems.PORTABLE_SLURRY_CELL_1K, "item/slurry_storage_cell_1k");
        cell(AItems.SLURRY_CELL_4K, AItems.PORTABLE_SLURRY_CELL_4K, "item/slurry_storage_cell_4k");
        cell(AItems.SLURRY_CELL_16K, AItems.PORTABLE_SLURRY_CELL_16K, "item/slurry_storage_cell_16k");
        cell(AItems.SLURRY_CELL_64K, AItems.PORTABLE_SLURRY_CELL_64K, "item/slurry_storage_cell_64k");
    }

    private void cell(RegistryObject<Item> cell, RegistryObject<Item> portable, String background) {
        singleTexture(cell.getId().getPath(), mcLoc("item/generated"), "layer0", AE2MekanismAddons.id(background))
                .texture("layer1", STORAGE_CELL_LED);
        singleTexture(portable.getId().getPath(), mcLoc("item/generated"), "layer0", AE2MekanismAddons.id(background))
                .texture("layer1", PORTABLE_CELL_LED);
    }

    private void flatSingleLayer(RegistryObject<Item> item, String texture) {
        singleTexture(item.getId().getPath(), mcLoc("item/generated"), "layer0", AE2MekanismAddons.id(texture));
    }
}
