package me.ramidzkh.mekae2.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AppliedMekanistics;

import appeng.api.features.P2PTunnelAttunement;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

    public ItemTagsProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, AppliedMekanistics.ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        var tanks = P2PTunnelAttunement.getAttunementTag(AMItems.CHEMICAL_P2P_TUNNEL::get);

        tag(tanks).addOptional(new ResourceLocation("mekanism", "basic_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "advanced_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "elite_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "ultimate_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "creative_chemical_tank"));
    }
}
