package me.ramidzkh.mekae2.integration.emi;

import com.mojang.logging.LogUtils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiStack;
import dev.emi.emi.jemi.JemiUtil;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;

import appeng.api.integrations.emi.EmiStackConverter;
import appeng.api.stacks.GenericStack;

public final class ChemicalIngredientConverter implements EmiStackConverter {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public Class<?> getKeyType() {
        // It doesn't really matter, AE2 only checks that no two converters have the same type.
        return Chemical.class;
    }

    @Override
    public @Nullable EmiStack toEmiStack(GenericStack stack) {
        try {
            if (stack.what() instanceof MekanismKey key) {
                return JemiUtil.getStack(key.withAmount(Math.max(1, stack.amount())));
            }
        } catch (Exception e) { // catch error in case JEMI internals change
            LOGGER.error("Failed to convert GenericStack to EmiStack", e);
        }
        return null;
    }

    @Override
    public @Nullable GenericStack toGenericStack(EmiStack stack) {
        try {
            if (stack instanceof JemiStack<?> jemiStack) {
                var ing = jemiStack.ingredient;
                if (ing instanceof ChemicalStack<?> chemicalStack) {
                    var mekKey = MekanismKey.of(chemicalStack);
                    if (mekKey != null) {
                        return new GenericStack(mekKey, stack.getAmount());
                    }
                }
            }
        } catch (Exception e) { // catch error in case JEMI internals change
            LOGGER.error("Failed to convert EmiStack to GenericStack", e);
        }
        return null;
    }
}
