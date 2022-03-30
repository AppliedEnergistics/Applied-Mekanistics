package me.ramidzkh.mekae2;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.heat.IHeatHandler;

public class MekCapabilities {

    public static final Capability<IGasHandler> GAS_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final Capability<IInfusionHandler> INFUSION_HANDLER_CAPABILITY = CapabilityManager
            .get(new CapabilityToken<>() {
            });

    public static final Capability<IPigmentHandler> PIGMENT_HANDLER_CAPABILITY = CapabilityManager
            .get(new CapabilityToken<>() {
            });

    public static final Capability<ISlurryHandler> SLURRY_HANDLER_CAPABILITY = CapabilityManager
            .get(new CapabilityToken<>() {
            });

    public static final Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = CapabilityManager
            .get(new CapabilityToken<>() {
            });
}
