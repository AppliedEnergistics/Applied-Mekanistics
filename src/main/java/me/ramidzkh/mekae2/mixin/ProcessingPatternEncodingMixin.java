package me.ramidzkh.mekae2.mixin;

import java.util.Objects;

import com.google.common.base.Preconditions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import appeng.api.stacks.GenericStack;

@Mixin(targets = "appeng/crafting/pattern/ProcessingPatternEncoding", remap = false)
public class ProcessingPatternEncodingMixin {

    @Overwrite
    public static GenericStack[] getMixedList(CompoundTag nbt, String nbtKey, int maxSize) {
        Objects.requireNonNull(nbt, "Pattern must have a tag.");

        var tag = nbt.getList(nbtKey, Tag.TAG_COMPOUND);
        Preconditions.checkArgument(tag.size() <= maxSize, "Cannot use more than " + maxSize + " ingredients");

        var result = new GenericStack[tag.size()];

        for (var x = 0; x < tag.size(); ++x) {
            var entry = tag.getCompound(x);

            if (entry.isEmpty()) {
                continue;
            }

            var stack = GenericStack.readTag(entry);

            if (stack == null) {
                throw new IllegalArgumentException("Pattern references missing stack: " + entry);
            }

            result[x] = stack;
        }

        return result;
    }
}
