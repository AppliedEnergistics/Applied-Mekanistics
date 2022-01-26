package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.ProcessingPatternItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ProcessingPatternItem.class, remap = false)
public class ProcessingPatternItemMixin {

    @Overwrite
    private static void checkItemsOrFluids(GenericStack[] stacks) {
    }
}
