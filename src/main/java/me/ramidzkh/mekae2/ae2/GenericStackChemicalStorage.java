package me.ramidzkh.mekae2.ae2;

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

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;

public abstract sealed class GenericStackChemicalStorage<C extends Chemical<C>, S extends ChemicalStack<C>>
        implements IChemicalHandler<C, S> {

    private final GenericInternalInventory inv;

    private GenericStackChemicalStorage(GenericInternalInventory inv) {
        this.inv = inv;
    }

    @Override
    public int getTanks() {
        return inv.size();
    }

    @Override
    public S getChemicalInTank(int tank) {
        if (inv.getKey(tank)instanceof MekanismKey what) {
            return (S) ChemicalBridge.withAmount(what.getStack(), inv.getAmount(tank));
        }

        return getEmptyStack();
    }

    @Override
    public void setChemicalInTank(int tank, S stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTankCapacity(int tank) {
        return inv.getCapacity(MekanismKeyType.TYPE);
    }

    @Override
    public boolean isValid(int tank, S stack) {
        var what = MekanismKey.of(stack);
        return what == null || inv.isAllowed(what);
    }

    @Override
    public S insertChemical(int tank, S stack, Action action) {
        var what = MekanismKey.of(stack);

        if (what == null) {
            return stack;
        }

        var remainder = stack.getAmount()
                - inv.insert(tank, what, stack.getAmount(), Actionable.of(action.toFluidAction()));

        if (remainder == 0) {
            return getEmptyStack();
        }

        return ChemicalBridge.withAmount(stack, remainder);
    }

    @Override
    public S extractChemical(int tank, long amount, Action action) {
        if (!(inv.getKey(tank)instanceof MekanismKey what)) {
            return getEmptyStack();
        }

        var extracted = inv.extract(tank, what, amount, Actionable.of(action.toFluidAction()));

        if (extracted > 0) {
            return (S) ChemicalBridge.withAmount(what.getStack(), extracted);
        }

        return getEmptyStack();
    }

    public static final class OfGas extends GenericStackChemicalStorage<Gas, GasStack> implements IGasHandler {
        public OfGas(GenericInternalInventory inv) {
            super(inv);
        }
    }

    public static final class OfInfusion extends GenericStackChemicalStorage<InfuseType, InfusionStack>
            implements IInfusionHandler {
        public OfInfusion(GenericInternalInventory inv) {
            super(inv);
        }
    }

    public static final class OfPigment extends GenericStackChemicalStorage<Pigment, PigmentStack>
            implements IPigmentHandler {
        public OfPigment(GenericInternalInventory inv) {
            super(inv);
        }
    }

    public static final class OfSlurry extends GenericStackChemicalStorage<Slurry, SlurryStack>
            implements ISlurryHandler {
        public OfSlurry(GenericInternalInventory inv) {
            super(inv);
        }
    }
}
