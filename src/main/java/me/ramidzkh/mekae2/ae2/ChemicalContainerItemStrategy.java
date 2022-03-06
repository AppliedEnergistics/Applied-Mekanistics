package me.ramidzkh.mekae2.ae2;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class ChemicalContainerItemStrategy implements ContainerItemStrategy<MekanismKey, ChemicalContainerItemStrategy.Context> {

    @Override
    @Nullable
    public GenericStack getContainedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        for (var capability : List.of(MekCapabilities.GAS_HANDLER_CAPABILITY, MekCapabilities.INFUSION_HANDLER_CAPABILITY, MekCapabilities.PIGMENT_HANDLER_CAPABILITY, MekCapabilities.SLURRY_HANDLER_CAPABILITY)) {
            var contained = stack.getCapability(capability).map(handler -> handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE));
            ChemicalStack<?> chemical;

            if (contained.isPresent() && !(chemical = contained.get()).isEmpty()) {
                var key = MekanismKey.of(chemical);

                if (key == null) {
                    return null;
                }

                return new GenericStack(key, chemical.getAmount());
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ChemicalContainerItemStrategy.Context findCarriedContext(Player player, AbstractContainerMenu menu) {
        var carried = menu.getCarried();

        if (Stream.of(MekCapabilities.GAS_HANDLER_CAPABILITY, MekCapabilities.INFUSION_HANDLER_CAPABILITY, MekCapabilities.PIGMENT_HANDLER_CAPABILITY, MekCapabilities.SLURRY_HANDLER_CAPABILITY)
                .anyMatch(capability -> carried.getCapability(capability).isPresent())) {
            return new Context(player, menu, carried);
        }

        return null;
    }

    @Override
    public long extract(Context context, MekanismKey what, long amount, Actionable mode) {
        var stack = ChemicalBridge.withAmount(what.getStack(), amount);

        if (stack instanceof GasStack gas) {
            return context.stack().getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY).map(handler -> handler.extractChemical(gas, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof InfusionStack infusion) {
            return context.stack().getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY).map(handler -> handler.extractChemical(infusion, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof PigmentStack pigment) {
            return context.stack().getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY).map(handler -> handler.extractChemical(pigment, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof SlurryStack slurry) {
            return context.stack().getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY).map(handler -> handler.extractChemical(slurry, action(mode)).getAmount()).orElse(0L);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public long insert(Context context, MekanismKey what, long amount, Actionable mode) {
        var stack = ChemicalBridge.withAmount(what.getStack(), amount);

        if (stack instanceof GasStack gas) {
            return context.stack().getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY).map(handler -> amount - handler.insertChemical(gas, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof InfusionStack infusion) {
            return context.stack().getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY).map(handler -> amount - handler.insertChemical(infusion, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof PigmentStack pigment) {
            return context.stack().getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY).map(handler -> amount - handler.insertChemical(pigment, action(mode)).getAmount()).orElse(0L);
        } else if (stack instanceof SlurryStack slurry) {
            return context.stack().getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY).map(handler -> amount - handler.insertChemical(slurry, action(mode)).getAmount()).orElse(0L);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void playFillSound(Player player, MekanismKey what) {
    }

    @Override
    public void playEmptySound(Player player, MekanismKey what) {
    }

    @Override
    @Nullable
    public GenericStack getExtractableContent(Context context) {
        var held = context.menu.getCarried();

        for (var capability : List.of(MekCapabilities.GAS_HANDLER_CAPABILITY, MekCapabilities.INFUSION_HANDLER_CAPABILITY, MekCapabilities.PIGMENT_HANDLER_CAPABILITY, MekCapabilities.SLURRY_HANDLER_CAPABILITY)) {
            var handler = held.getCapability(capability).resolve().orElse(null);

            if (handler == null) {
                continue;
            }

            var chemical = handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE);
            var key = MekanismKey.of(chemical);

            if (key != null) {
                return new GenericStack(key, chemical.getAmount());
            }
        }

        return null;
    }

    record Context(Player player, AbstractContainerMenu menu, ItemStack stack) {
    }

    public static Action action(Actionable actionable) {
        return switch (actionable) {
            case MODULATE -> Action.EXECUTE;
            case SIMULATE -> Action.SIMULATE;
        };
    }
}
