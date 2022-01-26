package me.ramidzkh.mekae2.mixin;

import appeng.api.stacks.AEKeyType;
import appeng.helpers.InterfaceLogic;
import appeng.util.ConfigInventory;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.ae2.impl.GenericStackChemicalStorage;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InterfaceLogic.class, remap = false)
public abstract class InterfaceLogicMixin {

    private GenericStackChemicalStorage localGasStorage;

    @Shadow
    public abstract ConfigInventory getConfig();

    @Shadow
    public abstract ConfigInventory getStorage();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo callbackInfo) {
        localGasStorage = new GenericStackChemicalStorage(getStorage());

        long fluidConfigCapacity = getConfig().getCapacity(AEKeyType.fluids());
        long fluidStorageCapacity = getStorage().getCapacity(AEKeyType.fluids());

        getConfig().setCapacity(MekanismKeyType.GAS, fluidConfigCapacity);
        getStorage().setCapacity(MekanismKeyType.GAS, fluidStorageCapacity);
        getConfig().setCapacity(MekanismKeyType.INFUSION, fluidConfigCapacity);
        getStorage().setCapacity(MekanismKeyType.INFUSION, fluidStorageCapacity);
        getConfig().setCapacity(MekanismKeyType.PIGMENT, fluidConfigCapacity);
        getStorage().setCapacity(MekanismKeyType.PIGMENT, fluidStorageCapacity);
        getConfig().setCapacity(MekanismKeyType.SLURRY, fluidConfigCapacity);
        getStorage().setCapacity(MekanismKeyType.SLURRY, fluidStorageCapacity);
    }

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true)
    private <T> void hookGetCapability(Capability<T> capability, Direction facing, CallbackInfoReturnable<LazyOptional<T>> callbackInfoReturnable) {
        if (capability == MekCapabilities.GAS_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(LazyOptional.of(() -> localGasStorage).cast());
        } else if (capability == MekCapabilities.INFUSION_HANDLER_CAPABILITY) {
        } else if (capability == MekCapabilities.PIGMENT_HANDLER_CAPABILITY) {
        } else if (capability == MekCapabilities.SLURRY_HANDLER_CAPABILITY) {
        }
    }
}
