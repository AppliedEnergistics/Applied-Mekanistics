package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.menu.me.common.MEStorageMenu;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MEStorageMenu.class, remap = false)
public class MEStorageMenuMixin {

    @Redirect(method = "handleNetworkInteraction", at = @At(value = "INVOKE", target = "Lappeng/api/stacks/AEFluidKey;is(Lappeng/api/stacks/AEKey;)Z"))
    private boolean isFluid(AEKey key) {
        return AEFluidKey.is(key) || key instanceof MekanismKey;
    }
}
