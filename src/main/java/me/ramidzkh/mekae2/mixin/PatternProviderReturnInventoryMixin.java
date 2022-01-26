package me.ramidzkh.mekae2.mixin;

import appeng.helpers.iface.PatternProviderReturnInventory;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.impl.ChemicalReturnHandler;
import me.ramidzkh.mekae2.ae2.impl.PatternProviderReturnInventoryAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PatternProviderReturnInventory.class, remap = false)
public class PatternProviderReturnInventoryMixin implements PatternProviderReturnInventoryAccessor {

    private final ChemicalReturnHandler.OfGas gasHandler = new ChemicalReturnHandler.OfGas((PatternProviderReturnInventory) (Object) this);
    private final ChemicalReturnHandler.OfInfusion infusionHandler = new ChemicalReturnHandler.OfInfusion((PatternProviderReturnInventory) (Object) this);
    private final ChemicalReturnHandler.OfPigment pigmentHandler = new ChemicalReturnHandler.OfPigment((PatternProviderReturnInventory) (Object) this);
    private final ChemicalReturnHandler.OfSlurry slurryHandler = new ChemicalReturnHandler.OfSlurry((PatternProviderReturnInventory) (Object) this);

    @Shadow
    private boolean injectingIntoNetwork;

    @Override
    public boolean isInjectingIntoNetwork() {
        return injectingIntoNetwork;
    }

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true)
    private <T> void hookGetCapability(Capability<T> capability, CallbackInfoReturnable<LazyOptional<T>> callbackInfoReturnable) {
        if (capability == MekCapabilities.GAS_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(LazyOptional.of(() -> gasHandler).cast());
        } else if (capability == MekCapabilities.INFUSION_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(LazyOptional.of(() -> infusionHandler).cast());
        } else if (capability == MekCapabilities.PIGMENT_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(LazyOptional.of(() -> pigmentHandler).cast());
        } else if (capability == MekCapabilities.SLURRY_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(LazyOptional.of(() -> slurryHandler).cast());
        }
    }
}
