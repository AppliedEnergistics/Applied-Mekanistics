package me.ramidzkh.mekae2.mixin;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ListTag.class)
public abstract class ListTagMixin {

    @Shadow
    public abstract Tag remove(int i);

    public Tag c(int index) {
        return remove(index);
    }
}
