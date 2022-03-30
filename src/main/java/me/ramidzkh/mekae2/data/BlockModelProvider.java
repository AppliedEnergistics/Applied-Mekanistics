package me.ramidzkh.mekae2.data;

import appeng.core.AppEng;
import me.ramidzkh.mekae2.AppliedMekanistics;
import me.ramidzkh.mekae2.AMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Locale;

public class BlockModelProvider extends net.minecraftforge.client.model.generators.BlockModelProvider {

    private static final ResourceLocation DRIVE_CELL = AppEng.makeId("block/drive/drive_cell");

    public BlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AppliedMekanistics.ID, existingFileHelper);

        existingFileHelper.trackGenerated(DRIVE_CELL, MODEL);
    }

    @Override
    protected void registerModels() {
        for (var tier : AMItems.Tier.values()) {
            cell("chemical_storage_cell" + tier.toString().toLowerCase(Locale.ROOT));
        }
    }

    private void cell(String path) {
        withExistingParent("block/drive/cells/" + path, DRIVE_CELL).texture("cell", "block/drive/cells/" + path);
    }
}
