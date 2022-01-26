package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.helpers.BaseActionSource;
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

public abstract sealed class GenericStackChemicalStorage<C extends Chemical<C>, S extends ChemicalStack<C>, K extends MekanismKey<S>> implements IChemicalHandler<C, S>, ChemicalBridge<S> {

    private final AEKeyType type;
    private final GenericStackInv inv;

    private GenericStackChemicalStorage(AEKeyType type, GenericStackInv inv) {
        this.type = type;
        this.inv = inv;
    }

    public static final class OfGas extends GenericStackChemicalStorage<Gas, GasStack, MekanismKey.Gas> implements IGasHandler, ChemicalBridge.OfGas {
        public OfGas(GenericStackInv inv) {
            super(MekanismKeyType.GAS, inv);
        }
    }

    public static final class OfInfusion extends GenericStackChemicalStorage<InfuseType, InfusionStack, MekanismKey.Infusion> implements IInfusionHandler, ChemicalBridge.OfInfusion {
        public OfInfusion(GenericStackInv inv) {
            super(MekanismKeyType.INFUSION, inv);
        }
    }

    public static final class OfPigment extends GenericStackChemicalStorage<Pigment, PigmentStack, MekanismKey.Pigment> implements IPigmentHandler, ChemicalBridge.OfPigment {
        public OfPigment(GenericStackInv inv) {
            super(MekanismKeyType.PIGMENT, inv);
        }
    }

    public static final class OfSlurry extends GenericStackChemicalStorage<Slurry, SlurryStack, MekanismKey.Slurry> implements ISlurryHandler, ChemicalBridge.OfSlurry {
        public OfSlurry(GenericStackInv inv) {
            super(MekanismKeyType.SLURRY, inv);
        }
    }

    @Override
    public int getTanks() {
        return inv.size();
    }

    @Override
    public S getChemicalInTank(int tank) {
        AEKey what = inv.getKey(tank);

        if (what != null && what.getType() == type) {
            return withAmount(((K) what).getStack(), inv.getAmount(tank));
        }

        return getEmptyStack();
    }

    @Override
    public void setChemicalInTank(int tank, S stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTankCapacity(int tank) {
        return inv.getCapacity(type);
    }

    @Override
    public boolean isValid(int tank, S stack) {
        var what = of(stack);
        return what == null || inv.isAllowed(what);
    }

    @Override
    public S insertChemical(int tank, S stack, Action action) {
        var what = of(stack);

        if (what == null) {
            return stack;
        }

        long remainder = stack.getAmount() - inv.insert(what, stack.getAmount(), actionable(action), new BaseActionSource());

        if (remainder == 0) {
            return getEmptyStack();
        }

        return withAmount(stack, remainder);
    }

    @Override
    public S extractChemical(int tank, long amount, Action action) {
        AEKey what = inv.getKey(tank);

        if (what == null || what.getType() != type) {
            return getEmptyStack();
        }

        var extracted = inv.extract(what, amount, actionable(action), new BaseActionSource());

        if (extracted > 0) {
            return withAmount(((K) what).getStack(), extracted);
        }

        return getEmptyStack();
    }

    public static Actionable actionable(Action action) {
        return switch (action) {
            case EXECUTE -> Actionable.MODULATE;
            case SIMULATE -> Actionable.SIMULATE;
        };
    }
}
