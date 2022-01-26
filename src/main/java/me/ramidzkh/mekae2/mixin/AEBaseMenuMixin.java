package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEKey;
import appeng.menu.AEBaseMenu;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.impl.MenuIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AEBaseMenu.class, remap = false)
public class AEBaseMenuMixin {

    @Inject(method = "handleFillingHeldItem", at = @At("HEAD"), cancellable = true)
    private void hookHandleFillingHeldItem(@Coerce Object source, AEKey what, CallbackInfo info) {
        if (what instanceof MekanismKey<?> key && MenuIo.fill((AEBaseMenu) (Object) this, source, key)) {
            info.cancel();
        }
    }

    @Inject(method = "handleEmptyHeldItem", at = @At("HEAD"), cancellable = true)
    private void hookHandleEmptyHeldItem(@Coerce Object sink, CallbackInfo info) {
        if (MenuIo.empty((AEBaseMenu) (Object) this, sink)) {
            info.cancel();
        }
    }
}
