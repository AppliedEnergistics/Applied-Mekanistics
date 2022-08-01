package me.ramidzkh.mekae2.ae2.stack;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.me.storage.ExternalStorageFacade;

class HandlerStrategy {

    public static ExternalStorageFacade getFacade(IChemicalHandler handler) {
        return new ExternalStorageFacade() {
            @Override
            public int getSlots() {
                return handler.getTanks();
            }

            @Nullable
            @Override
            public GenericStack getStackInSlot(int slot) {
                var stack = handler.getChemicalInTank(slot);
                var key = MekanismKey.of(stack);

                if (key == null) {
                    return null;
                }

                return new GenericStack(key, stack.getAmount());
            }

            @Override
            public AEKeyType getKeyType() {
                return MekanismKeyType.TYPE;
            }

            @Override
            public void getAvailableStacks(KeyCounter out) {
                for (var i = 0; i < handler.getTanks(); i++) {
                    // Skip resources that cannot be extracted if that filter was enabled
                    var stack = handler.getChemicalInTank(i);
                    var key = MekanismKey.of(stack);

                    if (key == null) {
                        continue;
                    }

                    if (extractableOnly && handler.extractChemical(stack, Action.SIMULATE).isEmpty()) {
                        continue;
                    }

                    out.add(key, stack.getAmount());
                }
            }

            @Override
            protected int insertExternal(AEKey what, int amount, Actionable mode) {
                return (int) HandlerStrategy.insert(handler, what, amount, mode);
            }

            @Override
            public boolean containsAnyFuzzy(Set<AEKey> keys) {
                for (var i = 0; i < handler.getTanks(); i++) {
                    var what = MekanismKey.of(handler.getChemicalInTank(i));

                    if (what != null && keys.contains(what.dropSecondary())) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            protected int extractExternal(AEKey what, int amount, Actionable mode) {
                return (int) HandlerStrategy.extract(handler, what, amount, mode);
            }
        };
    }

    public static long extract(IChemicalHandler handler, AEKey what, long amount, Actionable mode) {
        if (!(what instanceof MekanismKey key)) {
            return 0;
        }

        var stack = ChemicalBridge.withAmount(key.getStack(), amount);
        return handler.extractChemical(stack, Action.fromFluidAction(mode.getFluidAction())).getAmount();
    }

    @Nullable
    public static AEKey getStackInTank(int slot, IChemicalHandler handler) {
        var stack = handler.getChemicalInTank(slot);
        return MekanismKey.of(stack);
    }

    public static long insert(IChemicalHandler handler, AEKey what, long amount, Actionable mode) {
        if (what instanceof MekanismKey key) {
            return amount - handler.insertChemical(ChemicalBridge.withAmount(key.getStack(), amount),
                    Action.fromFluidAction(mode.getFluidAction())).getAmount();
        }

        return 0;
    }
}
