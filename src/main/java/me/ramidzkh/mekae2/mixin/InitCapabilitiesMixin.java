package me.ramidzkh.mekae2.mixin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.GenericStackChemicalStorage;

import appeng.capabilities.Capabilities;

@Mixin(targets = "appeng/init/InitCapabilities$1", remap = false)
public class InitCapabilitiesMixin {

    @Unique
    private BlockEntity blockEntity;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(AttachCapabilitiesEvent<BlockEntity> event, CallbackInfo callbackInfo) {
        blockEntity = event.getObject();
    }

    @Inject(method = "getCapability", at = @At("RETURN"), cancellable = true)
    private <T> void getCapability(@NotNull Capability<T> cap, @Nullable Direction side,
            CallbackInfoReturnable<LazyOptional<T>> callbackInfoReturnable) {
        if (cap == MekCapabilities.GAS_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                    .lazyMap(GenericStackChemicalStorage.OfGas::new).cast());
        } else if (cap == MekCapabilities.INFUSION_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                    .lazyMap(GenericStackChemicalStorage.OfInfusion::new).cast());
        } else if (cap == MekCapabilities.PIGMENT_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                    .lazyMap(GenericStackChemicalStorage.OfPigment::new).cast());
        } else if (cap == MekCapabilities.SLURRY_HANDLER_CAPABILITY) {
            callbackInfoReturnable.setReturnValue(blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                    .lazyMap(GenericStackChemicalStorage.OfSlurry::new).cast());
        }
    }
}
