package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.impl.MekanismJEIBridge;
import mekanism.api.chemical.ChemicalStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = GenericEntryStackHelper.class, remap = false)
public class GenericEntryStackHelperMixin {

    @Shadow
    @Final
    @Mutable
    public static List<GenericEntryStackHelper.IngredientType<?>> INGREDIENT_TYPES;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void postInit(CallbackInfo callbackInfo) {
        INGREDIENT_TYPES = new ArrayList<>(INGREDIENT_TYPES);
        INGREDIENT_TYPES.add(new GenericEntryStackHelper.IngredientType<>(MekanismJEIBridge.TYPE_GAS, i -> i.getAllIngredients().stream()
                .map(GenericEntryStackHelperMixin::toGenericStack).toList()));
        INGREDIENT_TYPES.add(new GenericEntryStackHelper.IngredientType<>(MekanismJEIBridge.TYPE_INFUSION, i -> i.getAllIngredients().stream()
                .map(GenericEntryStackHelperMixin::toGenericStack).toList()));
        INGREDIENT_TYPES.add(new GenericEntryStackHelper.IngredientType<>(MekanismJEIBridge.TYPE_PIGMENT, i -> i.getAllIngredients().stream()
                .map(GenericEntryStackHelperMixin::toGenericStack).toList()));
        INGREDIENT_TYPES.add(new GenericEntryStackHelper.IngredientType<>(MekanismJEIBridge.TYPE_SLURRY, i -> i.getAllIngredients().stream()
                .map(GenericEntryStackHelperMixin::toGenericStack).toList()));
    }

    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void hookOf(Object ingredient, CallbackInfoReturnable<GenericStack> callbackInfoReturnable) {
        if (ingredient instanceof ChemicalStack<?> chemicalStack) {
            callbackInfoReturnable.setReturnValue(toGenericStack(chemicalStack));
        }
    }

    @Nullable
    private static GenericStack toGenericStack(ChemicalStack<?> ingredient) {
        AEKey what = MekanismKey.of(ingredient);

        if (what != null) {
            return new GenericStack(what, ingredient.getAmount());
        }

        return null;
    }
}
