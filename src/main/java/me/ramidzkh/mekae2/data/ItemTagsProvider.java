package me.ramidzkh.mekae2.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AppliedMekanistics;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

    public ItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, AppliedMekanistics.ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(AMItems.MEKANISM_TANKS).addOptional(new ResourceLocation("mekanism", "basic_chemical_tank"));
        tag(AMItems.MEKANISM_TANKS).addOptional(new ResourceLocation("mekanism", "advanced_chemical_tank"));
        tag(AMItems.MEKANISM_TANKS).addOptional(new ResourceLocation("mekanism", "elite_chemical_tank"));
        tag(AMItems.MEKANISM_TANKS).addOptional(new ResourceLocation("mekanism", "ultimate_chemical_tank"));
        tag(AMItems.MEKANISM_TANKS).addOptional(new ResourceLocation("mekanism", "creative_chemical_tank"));
    }
}
