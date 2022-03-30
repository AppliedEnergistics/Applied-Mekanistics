package me.ramidzkh.mekae2.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;

import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.util.ConfigInventory;

@Mixin(value = InterfaceLogic.class, remap = false)
public class InterfaceLogicMixin {

    @Shadow
    @Final
    protected InterfaceLogicHost host;

    @Shadow
    @Final
    private ConfigInventory storage;

    @Inject(method = "addDrops", at = @At("HEAD"), cancellable = true)
    private void onAddDrops(CallbackInfo callbackInfo) {
        for (var i = 0; i < storage.size(); ++i) {
            var stack = storage.getStack(i);

            if (stack != null && stack.what()instanceof MekanismKey mekanismKey
                    && mekanismKey.getStack()instanceof GasStack gasStack) {
                MekanismAPI.getRadiationManager().dumpRadiation(new Coord4D(host.getBlockEntity()),
                        ChemicalBridge.withAmount(gasStack, stack.amount()));
            }
        }
    }
}
