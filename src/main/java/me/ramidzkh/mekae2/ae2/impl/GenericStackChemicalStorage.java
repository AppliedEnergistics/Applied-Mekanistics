package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.helpers.BaseActionSource;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.NotNull;

public class GenericStackChemicalStorage implements IGasHandler {

    private final GenericStackInv inv;

    public GenericStackChemicalStorage(GenericStackInv inv) {
        this.inv = inv;
    }

    @Override
    public int getTanks() {
        return inv.size();
    }

    @Override
    public GasStack getChemicalInTank(int tank) {
        if (inv.getKey(tank) instanceof MekanismKey.Gas what) {
            return new GasStack(what.getStack(), inv.getAmount(tank));
        }

        return getEmptyStack();
    }

    @Override
    public void setChemicalInTank(int i, GasStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTankCapacity(int tank) {
        return inv.getCapacity(MekanismKeyType.GAS);
    }

    @Override
    public boolean isValid(int tank, @NotNull GasStack stack) {
        var what = MekanismKey.Gas.of(stack);
        return what == null || inv.isAllowed(what);
    }

    @Override
    public GasStack insertChemical(int tank, GasStack stack, Action action) {
        var what = MekanismKey.Gas.of(stack);

        if (what == null) {
            return stack;
        }

        long remainder = stack.getAmount() - inv.insert(what, stack.getAmount(), actionable(action), new BaseActionSource());

        if (remainder == 0) {
            return getEmptyStack();
        }

        return new GasStack(stack, remainder);
    }

    @Override
    public GasStack extractChemical(int tank, long amount, Action action) {
        if (!(inv.getKey(tank) instanceof MekanismKey.Gas what)) {
            return getEmptyStack();
        }

        var extracted = inv.extract(what, amount, actionable(action), new BaseActionSource());

        if (extracted > 0) {
            return new GasStack(what.getStack(), extracted);
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
