package me.ramidzkh.mekae2.data;

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class MekAE2DataGenerators {

    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();

        var blockTagsProvider = new BlockTagsProvider(generator, existingFileHelper);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new ItemTagsProvider(generator, blockTagsProvider, existingFileHelper));

        generator.addProvider(new BlockModelProvider(generator, existingFileHelper));
        generator.addProvider(new ItemModelProvider(generator, existingFileHelper));
        generator.addProvider(new RecipeProvider(generator));
    }
}
