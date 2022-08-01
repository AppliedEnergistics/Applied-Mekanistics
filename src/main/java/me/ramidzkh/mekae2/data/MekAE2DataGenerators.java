package me.ramidzkh.mekae2.data;

import net.minecraftforge.data.event.GatherDataEvent;

public class MekAE2DataGenerators {

    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();

        var blockTagsProvider = new BlockTagsProvider(generator, existingFileHelper);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ItemTagsProvider(generator, blockTagsProvider, existingFileHelper));

        generator.addProvider(true, new BlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new ItemModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new RecipeProvider(generator));
    }
}
