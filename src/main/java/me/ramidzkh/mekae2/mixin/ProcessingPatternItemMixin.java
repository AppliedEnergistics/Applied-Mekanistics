package me.ramidzkh.mekae2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.ProcessingPatternItem;

@Mixin(value = ProcessingPatternItem.class, remap = false)
public class ProcessingPatternItemMixin {

    @Overwrite
    private static void checkItemsOrFluids(GenericStack[] stacks) {
    }
}
