package me.ramidzkh.mekae2.data;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import me.ramidzkh.mekae2.AppliedMekanistics;

public class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {

    public BlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, AppliedMekanistics.ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
    }
}
