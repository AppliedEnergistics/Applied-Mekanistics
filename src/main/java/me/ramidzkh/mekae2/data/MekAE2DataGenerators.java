package me.ramidzkh.mekae2.data;

import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class MekAE2DataGenerators {

    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(new BlockModelProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(new ItemModelProvider(event.getGenerator(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(new RecipeProvider(event.getGenerator()));
    }
}
