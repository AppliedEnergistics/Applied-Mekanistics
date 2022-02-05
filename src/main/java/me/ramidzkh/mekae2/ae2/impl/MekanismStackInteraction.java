package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.stacks.GenericStack;
import appeng.menu.me.interaction.EmptyingAction;
import appeng.menu.me.interaction.StackInteractionHandler;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemHandlerHelper;

@SuppressWarnings("ClassCanBeRecord")
public class MekanismStackInteraction implements StackInteractionHandler {

    private final Capability<? extends IChemicalHandler<?, ?>> capability;

    public MekanismStackInteraction(Capability<? extends IChemicalHandler<?, ?>> capability) {
        this.capability = capability;
    }

    @Override
    public EmptyingAction getEmptyingResult(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        var one = ItemHandlerHelper.copyStackWithSize(stack, 1);
        var contained = one.getCapability(capability).map(handler -> handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE));
        ChemicalStack<?> chemical;

        if (contained.isPresent() && !(chemical = contained.get()).isEmpty()) {
            var key = MekanismKey.of(chemical);

            if (key == null) {
                return null;
            }

            var fluidStack = new GenericStack(key, chemical.getAmount());
            var description = fluidStack.what().getDisplayName();
            return new EmptyingAction(description, fluidStack.what(), fluidStack.amount());
        }

        return null;
    }
}
