package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.helpers.iface.IInterfaceTarget;
import appeng.me.storage.ExternalStorageFacade;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;

public abstract sealed class ChemicalExternalStorageFacade<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>> extends ExternalStorageFacade implements ChemicalBridge<S> {

    private final H handler;

    private ChemicalExternalStorageFacade(H handler) {
        this.handler = handler;
    }

    @Nullable
    public static IInterfaceTarget get(BlockEntity be, Direction side, IActionSource src, @Nullable IInterfaceTarget in) {
        var facades = new HashMap<AEKeyType, ExternalStorageFacade>();

        be.getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY, side).map(ChemicalExternalStorageFacade.OfGas::new).ifPresent(x -> facades.put(MekanismKeyType.GAS, x));
        be.getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY, side).map(ChemicalExternalStorageFacade.OfInfusion::new).ifPresent(x -> facades.put(MekanismKeyType.INFUSION, x));
        be.getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, side).map(ChemicalExternalStorageFacade.OfPigment::new).ifPresent(x -> facades.put(MekanismKeyType.PIGMENT, x));
        be.getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY, side).map(ChemicalExternalStorageFacade.OfSlurry::new).ifPresent(x -> facades.put(MekanismKeyType.SLURRY, x));

        if (facades.isEmpty()) {
            return in;
        }

        return new CascadingInterfaceTarget(in, facades, src);
    }

    public static final class OfGas extends ChemicalExternalStorageFacade<Gas, GasStack, IGasHandler> implements ChemicalBridge.OfGas {
        public OfGas(IGasHandler handler) {
            super(handler);
        }

        @Override
        public AEKeyType getKeyType() {
            return MekanismKeyType.GAS;
        }
    }

    public static final class OfInfusion extends ChemicalExternalStorageFacade<InfuseType, InfusionStack, IInfusionHandler> implements ChemicalBridge.OfInfusion {
        public OfInfusion(IInfusionHandler handler) {
            super(handler);
        }

        @Override
        public AEKeyType getKeyType() {
            return MekanismKeyType.INFUSION;
        }
    }

    public static final class OfPigment extends ChemicalExternalStorageFacade<Pigment, PigmentStack, IPigmentHandler> implements ChemicalBridge.OfPigment {
        public OfPigment(IPigmentHandler handler) {
            super(handler);
        }

        @Override
        public AEKeyType getKeyType() {
            return MekanismKeyType.PIGMENT;
        }
    }

    public static final class OfSlurry extends ChemicalExternalStorageFacade<Slurry, SlurryStack, ISlurryHandler> implements ChemicalBridge.OfSlurry {
        public OfSlurry(ISlurryHandler handler) {
            super(handler);
        }

        @Override
        public AEKeyType getKeyType() {
            return MekanismKeyType.SLURRY;
        }
    }

    @Override
    public int getSlots() {
        return handler.getTanks();
    }

    @Nullable
    @Override
    public GenericStack getStackInSlot(int slot) {
        var stack = handler.getChemicalInTank(slot);
        var key = of(stack);
        return key == null ? null : new GenericStack(key, stack.getAmount());
    }

    @Override
    protected int insertExternal(AEKey what, int amount, Actionable mode) {
        if (what.getType() != getKeyType()) {
            return 0;
        }

        var stack = withAmount(((MekanismKey<S>) what).getStack(), amount);
        return (int) (amount - handler.insertChemical(stack, action(mode)).getAmount());
    }

    @Override
    protected int extractExternal(AEKey what, int amount, Actionable mode) {
        if (what.getType() != getKeyType()) {
            return 0;
        }

        var stack = withAmount(((MekanismKey<S>) what).getStack(), amount);
        return (int) handler.extractChemical(stack, action(mode)).getAmount();
    }

    @Override
    public boolean containsAnyFuzzy(Set<AEKey> keys) {
        for (int i = 0; i < handler.getTanks(); i++) {
            var what = of(handler.getChemicalInTank(i));

            if (what != null) {
                if (keys.contains(what)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Action action(Actionable actionable) {
        return switch (actionable) {
            case MODULATE -> Action.EXECUTE;
            case SIMULATE -> Action.SIMULATE;
        };
    }
}
