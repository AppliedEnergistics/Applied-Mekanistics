package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.helpers.BaseActionSource;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
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

import javax.annotation.Nullable;

public abstract sealed class GenericStackChemicalStorage<C extends Chemical<C>, S extends ChemicalStack<C>, K extends MekanismKey<S>> implements IChemicalHandler<C, S> {

    private final AEKeyType type;
    private final GenericStackInv inv;

    private GenericStackChemicalStorage(AEKeyType type, GenericStackInv inv) {
        this.type = type;
        this.inv = inv;
    }

    public static final class OfGas extends GenericStackChemicalStorage<Gas, GasStack, MekanismKey.Gas> implements IGasHandler {
        public OfGas(GenericStackInv inv) {
            super(MekanismKeyType.GAS, inv);
        }

        @Nullable
        @Override
        protected MekanismKey.Gas of(GasStack stack) {
            return MekanismKey.Gas.of(stack);
        }

        @Override
        protected GasStack withAmount(GasStack stack, long amount) {
            return new GasStack(stack, amount);
        }
    }

    public static final class OfInfusion extends GenericStackChemicalStorage<InfuseType, InfusionStack, MekanismKey.Infusion> implements IInfusionHandler {
        public OfInfusion(GenericStackInv inv) {
            super(MekanismKeyType.INFUSION, inv);
        }

        @Nullable
        @Override
        protected MekanismKey.Infusion of(InfusionStack stack) {
            return MekanismKey.Infusion.of(stack);
        }

        @Override
        protected InfusionStack withAmount(InfusionStack stack, long amount) {
            return new InfusionStack(stack, amount);
        }
    }

    public static final class OfPigment extends GenericStackChemicalStorage<Pigment, PigmentStack, MekanismKey.Pigment> implements IPigmentHandler {
        public OfPigment(GenericStackInv inv) {
            super(MekanismKeyType.PIGMENT, inv);
        }

        @Nullable
        @Override
        protected MekanismKey.Pigment of(PigmentStack stack) {
            return MekanismKey.Pigment.of(stack);
        }

        @Override
        protected PigmentStack withAmount(PigmentStack stack, long amount) {
            return new PigmentStack(stack, amount);
        }
    }

    public static final class OfSlurry extends GenericStackChemicalStorage<Slurry, SlurryStack, MekanismKey.Slurry> implements ISlurryHandler {
        public OfSlurry(GenericStackInv inv) {
            super(MekanismKeyType.SLURRY, inv);
        }

        @Nullable
        @Override
        protected MekanismKey.Slurry of(SlurryStack stack) {
            return MekanismKey.Slurry.of(stack);
        }

        @Override
        protected SlurryStack withAmount(SlurryStack stack, long amount) {
            return new SlurryStack(stack, amount);
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

    @Nullable
    protected abstract K of(S stack);

    protected abstract S withAmount(S stack, long amount);

    public static Actionable actionable(Action action) {
        return switch (action) {
            case EXECUTE -> Actionable.MODULATE;
            case SIMULATE -> Actionable.SIMULATE;
        };
    }
}
