package me.ramidzkh.mekae2.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class MekAE2DataGenerators {

    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        var blockTagsProvider = new BlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ItemTagsProvider(packOutput, lookupProvider,
                blockTagsProvider.contentsGetter(), existingFileHelper));

        generator.addProvider(true, new BlockModelProvider(packOutput, existingFileHelper));
        generator.addProvider(true, new ItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(true, new RecipeProvider(packOutput, lookupProvider));
    }
}
