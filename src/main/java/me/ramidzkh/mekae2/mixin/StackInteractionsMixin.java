package me.ramidzkh.mekae2.mixin;

import appeng.menu.me.interaction.StackInteractionHandler;
import appeng.menu.me.interaction.StackInteractions;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.impl.MekanismStackInteraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = StackInteractions.class, remap = false)
public class StackInteractionsMixin {

    @Shadow
    @Final
    @Mutable
    private static List<StackInteractionHandler> HANDLERS;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void postInit(CallbackInfo callbackInfo) {
        HANDLERS = new ArrayList<>(HANDLERS);
        HANDLERS.add(new MekanismStackInteraction(MekCapabilities.GAS_HANDLER_CAPABILITY));
        HANDLERS.add(new MekanismStackInteraction(MekCapabilities.INFUSION_HANDLER_CAPABILITY));
        HANDLERS.add(new MekanismStackInteraction(MekCapabilities.PIGMENT_HANDLER_CAPABILITY));
        HANDLERS.add(new MekanismStackInteraction(MekCapabilities.SLURRY_HANDLER_CAPABILITY));
    }
}
