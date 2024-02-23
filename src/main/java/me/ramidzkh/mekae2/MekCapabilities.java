package me.ramidzkh.mekae2;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;

public class MekCapabilities {

    private MekCapabilities() {
    }

    public static final CapSet<IGasHandler> GAS = new CapSet<>(rl("gas_handler"), IGasHandler.class);
    public static final CapSet<IInfusionHandler> INFUSION = new CapSet<>(rl("infusion_handler"),
            IInfusionHandler.class);
    public static final CapSet<IPigmentHandler> PIGMENT = new CapSet<>(rl("pigment_handler"), IPigmentHandler.class);
    public static final CapSet<ISlurryHandler> SLURRY = new CapSet<>(rl("slurry_handler"), ISlurryHandler.class);

    public static final List<CapSet<?>> HANDLERS = List.of(GAS, INFUSION, PIGMENT, SLURRY);

    public record CapSet<T extends IChemicalHandler<?, ?>>(BlockCapability<T, @Nullable Direction> block,
            ItemCapability<T, Void> item) {
        public CapSet(ResourceLocation name, Class<T> handlerClass) {
            this(BlockCapability.createSided(name, handlerClass), ItemCapability.createVoid(name, handlerClass));
        }
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(MekanismAPI.MEKANISM_MODID, path);
    }
}
