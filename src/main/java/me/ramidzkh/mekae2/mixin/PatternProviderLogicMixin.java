package me.ramidzkh.mekae2.mixin;

import appeng.helpers.iface.PatternProviderLogic;
import appeng.helpers.iface.PatternProviderLogicHost;
import appeng.helpers.iface.PatternProviderReturnInventory;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternProviderLogic.class, remap = false)
public class PatternProviderLogicMixin {

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow
    @Final
    private PatternProviderReturnInventory returnInv;

    @Inject(method = "addDrops", at = @At("HEAD"), cancellable = true)
    private void onAddDrops(CallbackInfo callbackInfo) {
        for (var i = 0; i < returnInv.size(); i++) {
            var stack = returnInv.getStack(i);

            if (stack != null && stack.what() instanceof MekanismKey mekanismKey && mekanismKey.getStack() instanceof GasStack gasStack) {
                MekanismAPI.getRadiationManager().dumpRadiation(new Coord4D(host.getBlockEntity()), ChemicalBridge.withAmount(gasStack, stack.amount()));
            }
        }
    }
}
