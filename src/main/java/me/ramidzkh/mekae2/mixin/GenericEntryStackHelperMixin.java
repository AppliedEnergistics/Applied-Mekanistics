package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GenericEntryStackHelper.class, remap = false)
public class GenericEntryStackHelperMixin {

    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void hookOf(Object ingredient, CallbackInfoReturnable<GenericStack> callbackInfoReturnable) {
        if (!(ingredient instanceof ChemicalStack<?> chemicalStack)) {
            return;
        }

        AEKey what = null;

        if (ingredient instanceof GasStack stack) {
            what = MekanismKey.Gas.of(stack);
        } else if (ingredient instanceof InfusionStack stack) {
            what = MekanismKey.Infusion.of(stack);
        } else if (ingredient instanceof PigmentStack stack) {
            what = MekanismKey.Pigment.of(stack);
        } else if (ingredient instanceof SlurryStack stack) {
            what = MekanismKey.Slurry.of(stack);
        }

        if (what != null) {
            callbackInfoReturnable.setReturnValue(new GenericStack(what, chemicalStack.getAmount()));
        }
    }
}
