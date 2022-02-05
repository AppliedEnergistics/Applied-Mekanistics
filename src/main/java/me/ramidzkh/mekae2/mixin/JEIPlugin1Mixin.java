package me.ramidzkh.mekae2.mixin;

import appeng.client.gui.AEBaseScreen;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng/integration/modules/jei/JEIPlugin$1", remap = false)
public class JEIPlugin1Mixin {

    @Inject(method = "getIngredientUnderMouse", at = @At("HEAD"), cancellable = true)
    private void hookGetIngredientUnderMouse(AEBaseScreen<?> screen, double mouseX, double mouseY, CallbackInfoReturnable<Object> callbackInfoReturnable) {
        var stack = screen.getStackUnderMouse(mouseX, mouseY);

        if (stack != null && stack.what() instanceof MekanismKey key) {
            callbackInfoReturnable.setReturnValue(key.getStack());
        }
    }
}
