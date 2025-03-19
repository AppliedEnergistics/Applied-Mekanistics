package me.ramidzkh.mekae2.ae2;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;

public class GenericStackChemicalStorage implements IChemicalHandler {

    private final GenericInternalInventory inv;

    public GenericStackChemicalStorage(GenericInternalInventory inv) {
        this.inv = inv;
    }

    @Override
    public int getChemicalTanks() {
        return inv.size();
    }

    @Override
    public ChemicalStack getChemicalInTank(int tank) {
        if (inv.getKey(tank) instanceof MekanismKey what) {
            return what.withAmount(inv.getAmount(tank));
        }

        return ChemicalStack.EMPTY;
    }

    @Override
    public void setChemicalInTank(int tank, ChemicalStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getChemicalTankCapacity(int tank) {
        return inv.getCapacity(MekanismKeyType.TYPE);
    }

    @Override
    public boolean isValid(int tank, ChemicalStack stack) {
        var what = MekanismKey.of(stack);
        return what == null || inv.isAllowedIn(tank, what);
    }

    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack stack, Action action) {
        var what = MekanismKey.of(stack);

        if (what == null) {
            return stack;
        }

        var remainder = stack.getAmount()
                - inv.insert(tank, what, stack.getAmount(), Actionable.of(action.toFluidAction()));

        if (remainder == 0) {
            return ChemicalStack.EMPTY;
        }

        return stack.copyWithAmount(remainder);
    }

    @Override
    public ChemicalStack extractChemical(int tank, long amount, Action action) {
        if (!(inv.getKey(tank) instanceof MekanismKey what)) {
            return ChemicalStack.EMPTY;
        }

        var extracted = inv.extract(tank, what, amount, Actionable.of(action.toFluidAction()));

        if (extracted == 0) {
            return ChemicalStack.EMPTY;
        }

        return what.withAmount(extracted);
    }

    @Override
    public ChemicalStack extractChemical(ChemicalStack stack, Action action) {
        var key = MekanismKey.of(stack);
        if (key == null) {
            return ChemicalStack.EMPTY;
        }
        var mode = Actionable.of(action.toFluidAction());
        for (int slot = 0; slot < inv.size(); slot++) {
            var extracted = inv.extract(slot, key, stack.getAmount(), mode);
            if (extracted != 0) {
                return key.withAmount(extracted);
            }
        }
        return ChemicalStack.EMPTY;
    }

}
