package me.ramidzkh.mekae2.ae2;

import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

@SuppressWarnings("UnstableApiUsage")
public class ChemicalContainerItemStrategy
        implements ContainerItemStrategy<MekanismKey, ChemicalContainerItemStrategy.Context> {

    @Override
    @Nullable
    public GenericStack getContainedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        for (var capability : List.of(MekCapabilities.GAS_HANDLER_CAPABILITY,
                MekCapabilities.INFUSION_HANDLER_CAPABILITY, MekCapabilities.PIGMENT_HANDLER_CAPABILITY,
                MekCapabilities.SLURRY_HANDLER_CAPABILITY)) {
            var contained = stack.getCapability(capability)
                    .map(handler -> handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE));
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

        if (Stream
                .of(MekCapabilities.GAS_HANDLER_CAPABILITY, MekCapabilities.INFUSION_HANDLER_CAPABILITY,
                        MekCapabilities.PIGMENT_HANDLER_CAPABILITY, MekCapabilities.SLURRY_HANDLER_CAPABILITY)
                .anyMatch(capability -> carried.getCapability(capability).isPresent())) {
            return new CarriedContext(menu);
        }

        return null;
    }

    @Override
    public @Nullable ChemicalContainerItemStrategy.Context findPlayerSlotContext(Player player, int slot) {
        var carried = player.getInventory().getItem(slot);

        if (Stream
                .of(MekCapabilities.GAS_HANDLER_CAPABILITY, MekCapabilities.INFUSION_HANDLER_CAPABILITY,
                        MekCapabilities.PIGMENT_HANDLER_CAPABILITY, MekCapabilities.SLURRY_HANDLER_CAPABILITY)
                .anyMatch(capability -> carried.getCapability(capability).isPresent())) {
            return new PlayerInvContext(player, slot);
        }

        return null;
    }

    @Override
    public long extract(Context context, MekanismKey what, long amount, Actionable mode) {
        var stack = ChemicalBridge.withAmount(what.getStack(), amount);
        var action = Action.fromFluidAction(mode.getFluidAction());

        if (stack instanceof GasStack gas) {
            return context.getStack().getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY)
                    .map(handler -> handler.extractChemical(gas, action).getAmount()).orElse(0L);
        } else if (stack instanceof InfusionStack infusion) {
            return context.getStack().getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY)
                    .map(handler -> handler.extractChemical(infusion, action).getAmount()).orElse(0L);
        } else if (stack instanceof PigmentStack pigment) {
            return context.getStack().getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY)
                    .map(handler -> handler.extractChemical(pigment, action).getAmount()).orElse(0L);
        } else if (stack instanceof SlurryStack slurry) {
            return context.getStack().getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY)
                    .map(handler -> handler.extractChemical(slurry, action).getAmount()).orElse(0L);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public long insert(Context context, MekanismKey what, long amount, Actionable mode) {
        var stack = ChemicalBridge.withAmount(what.getStack(), amount);
        var action = Action.fromFluidAction(mode.getFluidAction());

        if (stack instanceof GasStack gas) {
            return context.getStack().getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY)
                    .map(handler -> amount - handler.insertChemical(gas, action).getAmount()).orElse(0L);
        } else if (stack instanceof InfusionStack infusion) {
            return context.getStack().getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY)
                    .map(handler -> amount - handler.insertChemical(infusion, action).getAmount()).orElse(0L);
        } else if (stack instanceof PigmentStack pigment) {
            return context.getStack().getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY)
                    .map(handler -> amount - handler.insertChemical(pigment, action).getAmount()).orElse(0L);
        } else if (stack instanceof SlurryStack slurry) {
            return context.getStack().getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY)
                    .map(handler -> amount - handler.insertChemical(slurry, action).getAmount()).orElse(0L);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void playFillSound(Player player, MekanismKey what) {
        player.playNotifySound(SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void playEmptySound(Player player, MekanismKey what) {
        player.playNotifySound(SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    @Nullable
    public GenericStack getExtractableContent(Context context) {
        var held = context.getStack();

        for (var capability : List.of(MekCapabilities.GAS_HANDLER_CAPABILITY,
                MekCapabilities.INFUSION_HANDLER_CAPABILITY, MekCapabilities.PIGMENT_HANDLER_CAPABILITY,
                MekCapabilities.SLURRY_HANDLER_CAPABILITY)) {
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

    interface Context {

        ItemStack getStack();
    }

    private record CarriedContext(AbstractContainerMenu menu) implements Context {

        @Override
        public ItemStack getStack() {
            return menu.getCarried();
        }
    }

    private record PlayerInvContext(Player player, int slot) implements Context {

        @Override
        public ItemStack getStack() {
            return player.getInventory().getItem(slot);
        }
    }
}
