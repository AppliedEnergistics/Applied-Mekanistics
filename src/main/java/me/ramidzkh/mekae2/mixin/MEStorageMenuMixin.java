package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.FluidContainerHelper;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.interaction.StackInteractions;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(value = MEStorageMenu.class, remap = false)
public class MEStorageMenuMixin {

    @Redirect(method = "handleNetworkInteraction", at = @At(value = "INVOKE", target = "Lappeng/api/stacks/AEFluidKey;is(Lappeng/api/stacks/AEKey;)Z"))
    private boolean isFluid(AEKey key) {
        return AEFluidKey.is(key) || key instanceof MekanismKey<?>;
    }

    @Redirect(method = "handleNetworkInteraction", at = @At(value = "INVOKE", target = "Lappeng/helpers/FluidContainerHelper;getContainedStack(Lnet/minecraft/world/item/ItemStack;)Lappeng/api/stacks/GenericStack;"))
    @Nullable
    private GenericStack getContainedStack(ItemStack stack) {
        var contained = FluidContainerHelper.getContainedStack(stack);

        if (contained != null) {
            return contained;
        }

        if (StackInteractions.getEmptyingAction(stack) != null) {
            // noinspection ConstantConditions We just need the if statement to go true
            return new GenericStack(null, 0);
        }

        return null;
    }
}
