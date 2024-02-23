package me.ramidzkh.mekae2.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import me.ramidzkh.mekae2.AMItems;
import me.ramidzkh.mekae2.AppliedMekanistics;

import appeng.api.features.P2PTunnelAttunement;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

    public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTagsProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsProvider, AppliedMekanistics.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var tanks = P2PTunnelAttunement.getAttunementTag(AMItems.CHEMICAL_P2P_TUNNEL::get);

        tag(tanks).addOptional(new ResourceLocation("mekanism", "basic_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "advanced_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "elite_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "ultimate_chemical_tank"));
        tag(tanks).addOptional(new ResourceLocation("mekanism", "creative_chemical_tank"));
    }
}
