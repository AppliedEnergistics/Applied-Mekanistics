package me.ramidzkh.mekae2.ae2.stack;

import com.google.common.primitives.Ints;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;

import me.ramidzkh.mekae2.AMText;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;

public record ChemicalHandlerFacade(@Nullable IChemicalHandler[] handlers, boolean extractableOnly,
        Runnable changeListener) implements MEStorage {

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof MekanismKey key)) {
            return 0;
        }

        var handler = handlers[key.getForm()];

        if (handler == null) {
            return 0;
        }

        var inserted = amount - handler.insertChemical(key.withAmount(amount),
                Action.fromFluidAction(mode.getFluidAction())).getAmount();

        if (inserted > 0 && mode == Actionable.MODULATE) {
            if (this.changeListener != null) {
                this.changeListener.run();
            }
        }

        return inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof MekanismKey key)) {
            return 0;
        }

        var handler = handlers[key.getForm()];

        if (handler == null) {
            return 0;
        }

        var extracted = handler.extractChemical(key.withAmount(Ints.saturatedCast(amount)),
                Action.fromFluidAction(mode.getFluidAction())).getAmount();

        if (extracted > 0 && mode == Actionable.MODULATE) {
            if (this.changeListener != null) {
                this.changeListener.run();
            }
        }

        return extracted;
    }

    @Override
    public Component getDescription() {
        return GuiText.ExternalStorage.text(AMText.CHEMICAL);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var handler : handlers) {
            if (handler == null) {
                continue;
            }

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
    }
}
