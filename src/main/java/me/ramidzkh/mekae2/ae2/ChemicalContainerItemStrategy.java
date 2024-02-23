package me.ramidzkh.mekae2.ae2;

import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import me.ramidzkh.mekae2.MekCapabilities;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;

@SuppressWarnings("UnstableApiUsage")
public class ChemicalContainerItemStrategy implements ContainerItemStrategy<MekanismKey, ItemStack> {

    @Override
    @Nullable
    public GenericStack getContainedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        for (var capability : MekCapabilities.HANDLERS) {
            var handler = stack.getCapability(capability.item());

            if (handler != null) {
                var chemical = handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE);
                var key = MekanismKey.of(chemical);

                if (key != null) {
                    return new GenericStack(key, chemical.getAmount());
                }
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ItemStack findCarriedContext(Player player, AbstractContainerMenu menu) {
        var carried = menu.getCarried();

        for (var capability : MekCapabilities.HANDLERS) {
            if (carried.getCapability(capability.item()) != null) {
                return carried;
            }
        }

        return null;
    }

    @Override
    public @Nullable ItemStack findPlayerSlotContext(Player player, int slot) {
        var carried = player.getInventory().getItem(slot);

        for (var capability : MekCapabilities.HANDLERS) {
            if (carried.getCapability(capability.item()) != null) {
                return carried;
            }
        }

        return null;
    }

    @Override
    public long extract(ItemStack context, MekanismKey what, long amount, Actionable mode) {
        var stack = what.withAmount(amount);
        var action = Action.fromFluidAction(mode.getFluidAction());

        if (stack instanceof GasStack gas) {
            var handler = context.getCapability(MekCapabilities.GAS.item());

            if (handler != null) {
                return handler.extractChemical(gas, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof InfusionStack infusion) {
            var handler = context.getCapability(MekCapabilities.INFUSION.item());

            if (handler != null) {
                return handler.extractChemical(infusion, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof PigmentStack pigment) {
            var handler = context.getCapability(MekCapabilities.PIGMENT.item());

            if (handler != null) {
                return handler.extractChemical(pigment, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof SlurryStack slurry) {
            var handler = context.getCapability(MekCapabilities.SLURRY.item());

            if (handler != null) {
                return handler.extractChemical(slurry, action).getAmount();
            } else {
                return 0L;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public long insert(ItemStack context, MekanismKey what, long amount, Actionable mode) {
        var stack = what.withAmount(amount);
        var action = Action.fromFluidAction(mode.getFluidAction());

        if (stack instanceof GasStack gas) {
            var handler = context.getCapability(MekCapabilities.GAS.item());

            if (handler != null) {
                return amount - handler.insertChemical(gas, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof InfusionStack infusion) {
            var handler = context.getCapability(MekCapabilities.INFUSION.item());

            if (handler != null) {
                return amount - handler.insertChemical(infusion, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof PigmentStack pigment) {
            var handler = context.getCapability(MekCapabilities.PIGMENT.item());

            if (handler != null) {
                return amount - handler.insertChemical(pigment, action).getAmount();
            } else {
                return 0L;
            }
        } else if (stack instanceof SlurryStack slurry) {
            var handler = context.getCapability(MekCapabilities.SLURRY.item());

            if (handler != null) {
                return amount - handler.insertChemical(slurry, action).getAmount();
            } else {
                return 0L;
            }
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
    public GenericStack getExtractableContent(ItemStack context) {
        return getContainedStack(context);
    }
}
