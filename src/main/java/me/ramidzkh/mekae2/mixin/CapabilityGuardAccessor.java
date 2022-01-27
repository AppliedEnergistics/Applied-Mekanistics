package me.ramidzkh.mekae2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "appeng/parts/p2p/CapabilityP2PTunnelPart$CapabilityGuard")
public interface CapabilityGuardAccessor<C> {

    @Accessor
    C callGet();
}
