package me.ramidzkh.mekae2.data;

import appeng.core.AppEng;
import me.ramidzkh.mekae2.AE2MekanismAddons;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelProvider extends net.minecraftforge.client.model.generators.BlockModelProvider {

    private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public BlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AE2MekanismAddons.ID, existingFileHelper);

        existingFileHelper.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        cell("1k_gas_cell");
        cell("4k_gas_cell");
        cell("16k_gas_cell");
        cell("64k_gas_cell");

        cell("1k_infusion_cell");
        cell("4k_infusion_cell");
        cell("16k_infusion_cell");
        cell("64k_infusion_cell");

        cell("1k_pigment_cell");
        cell("4k_pigment_cell");
        cell("16k_pigment_cell");
        cell("64k_pigment_cell");

        cell("1k_slurry_cell");
        cell("4k_slurry_cell");
        cell("16k_slurry_cell");
        cell("64k_slurry_cell");
    }

    private void cell(String path) {
        withExistingParent("block/drive/cells/" + path, DRIVE_CELL).texture("cell", "block/drive/cells/" + path);
    }
}
