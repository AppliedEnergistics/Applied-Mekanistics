package me.ramidzkh.mekae2.integration.emi;

import org.jetbrains.annotations.Nullable;

import dev.emi.emi.api.stack.EmiStack;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.IMekanismAccess;
import mekanism.api.chemical.Chemical;

import appeng.api.integrations.emi.EmiStackConverter;
import appeng.api.stacks.GenericStack;

public final class ChemicalIngredientConverter implements EmiStackConverter {

    @Override
    public Class<?> getKeyType() {
        // It doesn't really matter, AE2 only checks that no two converters have the same type.
        return Chemical.class;
    }

    @Override
    public @Nullable EmiStack toEmiStack(GenericStack stack) {
        if (stack.what() instanceof MekanismKey key) {
            return IMekanismAccess.INSTANCE.emiHelper().createEmiStack(key.getStack().getType(), stack.amount());
        }

        return null;
    }

    @Override
    public @Nullable GenericStack toGenericStack(EmiStack stack) {
        var chemical = IMekanismAccess.INSTANCE.emiHelper().asChemicalStack(stack).orElse(null);

        if (chemical != null) {
            var what = MekanismKey.of(chemical);

            if (what != null) {
                return new GenericStack(what, stack.getAmount());
            }
        }

        return null;
    }
}
