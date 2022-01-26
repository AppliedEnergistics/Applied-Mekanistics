package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.helpers.iface.PatternProviderReturnInventory;
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

public abstract sealed class ChemicalReturnHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S>, ChemicalBridge<S> {

    private final AEKeyType type;
    private final PatternProviderReturnInventory parent;

    private ChemicalReturnHandler(AEKeyType type, PatternProviderReturnInventory parent) {
        this.type = type;
        this.parent = parent;
    }

    public static final class OfGas extends ChemicalReturnHandler<Gas, GasStack> implements IGasHandler, ChemicalBridge.OfGas {
        public OfGas(PatternProviderReturnInventory parent) {
            super(MekanismKeyType.GAS, parent);
        }
    }

    public static final class OfInfusion extends ChemicalReturnHandler<InfuseType, InfusionStack> implements IInfusionHandler, ChemicalBridge.OfInfusion {
        public OfInfusion(PatternProviderReturnInventory parent) {
            super(MekanismKeyType.INFUSION, parent);
        }
    }

    public static final class OfPigment extends ChemicalReturnHandler<Pigment, PigmentStack> implements IPigmentHandler, ChemicalBridge.OfPigment {
        public OfPigment(PatternProviderReturnInventory parent) {
            super(MekanismKeyType.PIGMENT, parent);
        }
    }

    public static final class OfSlurry extends ChemicalReturnHandler<Slurry, SlurryStack> implements ISlurryHandler, ChemicalBridge.OfSlurry {
        public OfSlurry(PatternProviderReturnInventory parent) {
            super(MekanismKeyType.SLURRY, parent);
        }
    }

    @Override
    public int getTanks() {
        return parent.size();
    }

    @Override
    public S getChemicalInTank(int tank) {
        GenericStack stack = parent.getStack(tank);

        if (stack != null && stack.what().getType() == type) {
            return withAmount(((MekanismKey<S>) stack.what()).getStack(), stack.amount());
        }

        return getEmptyStack();
    }

    @Override
    public void setChemicalInTank(int tank, S stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTankCapacity(int tank) {
        return 4 * AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public boolean isValid(int tank, S stack) {
        return true;
    }

    @Override
    public S insertChemical(int tank, S stack, Action action) {
        if (((PatternProviderReturnInventoryAccessor) parent).isInjectingIntoNetwork()) {
            // We are pushing out items already, prevent changing the stacks in unexpected ways.
            return stack;
        }

        var what = of(stack);

        if (what == null) {
            return stack;
        }

        long filled = 0;

        for (int i = 0; i < parent.size() && stack.getAmount() - filled > 0; ++i) {
            filled += parent.insert(i, what, stack.getAmount() - filled, GenericStackChemicalStorage.actionable(action));
        }

        return withAmount(stack, stack.getAmount() - filled);
    }

    @Override
    public S extractChemical(int tank, long l, Action action) {
        return getEmptyStack();
    }
}
