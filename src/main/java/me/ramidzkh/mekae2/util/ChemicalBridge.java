package me.ramidzkh.mekae2.util;

import mekanism.api.chemical.ChemicalStack;

public interface ChemicalBridge {

    static <S extends ChemicalStack<?>> S withAmount(S stack, long amount) {
        var copy = stack.copy();
        copy.setAmount(amount);
        return (S) copy;
    }
}
