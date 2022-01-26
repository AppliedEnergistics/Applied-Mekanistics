package me.ramidzkh.mekae2.util;

import appeng.api.stacks.AEKey;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

public interface ChemicalBridge<S> {

    AEKey of(S chemical);

    S withAmount(S chemical, long amount);

    interface OfGas extends ChemicalBridge<GasStack> {
        @Override
        default AEKey of(GasStack chemical) {
            return MekanismKey.Gas.of(chemical);
        }

        @Override
        default GasStack withAmount(GasStack chemical, long amount) {
            return new GasStack(chemical, amount);
        }
    }

    interface OfInfusion extends ChemicalBridge<InfusionStack> {
        @Override
        default AEKey of(InfusionStack chemical) {
            return MekanismKey.Infusion.of(chemical);
        }

        @Override
        default InfusionStack withAmount(InfusionStack chemical, long amount) {
            return new InfusionStack(chemical, amount);
        }
    }

    interface OfPigment extends ChemicalBridge<PigmentStack> {
        @Override
        default AEKey of(PigmentStack chemical) {
            return MekanismKey.Pigment.of(chemical);
        }

        @Override
        default PigmentStack withAmount(PigmentStack chemical, long amount) {
            return new PigmentStack(chemical, amount);
        }
    }

    interface OfSlurry extends ChemicalBridge<SlurryStack> {
        @Override
        default AEKey of(SlurryStack chemical) {
            return MekanismKey.Slurry.of(chemical);
        }

        @Override
        default SlurryStack withAmount(SlurryStack chemical, long amount) {
            return new SlurryStack(chemical, amount);
        }
    }
}
