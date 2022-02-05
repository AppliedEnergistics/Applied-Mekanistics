package me.ramidzkh.mekae2.util;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

public interface ChemicalBridge {

    static <S extends ChemicalStack<?>> S withAmount(S stack, long amount) {
        if (stack instanceof GasStack gas) {
            return (S) new GasStack(gas, amount);
        } else if (stack instanceof InfusionStack infusion) {
            return (S) new InfusionStack(infusion, amount);
        } else if (stack instanceof PigmentStack pigment) {
            return (S) new PigmentStack(pigment, amount);
        } else if (stack instanceof SlurryStack slurry) {
            return (S) new SlurryStack(slurry, amount);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
