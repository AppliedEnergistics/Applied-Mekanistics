package me.ramidzkh.mekae2.util;

import me.ramidzkh.mekae2.MekCapabilities;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

@SuppressWarnings("ClassCanBeRecord")
public final class ChemicalUtil<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>> {

    public static final ChemicalUtil<Gas, GasStack, IGasHandler> GAS = new ChemicalUtil<>(MekCapabilities.GAS_HANDLER_CAPABILITY);
    public static final ChemicalUtil<InfuseType, InfusionStack, IInfusionHandler> INFUSION = new ChemicalUtil<>(MekCapabilities.INFUSION_HANDLER_CAPABILITY);
    public static final ChemicalUtil<Pigment, PigmentStack, IPigmentHandler> PIGMENT = new ChemicalUtil<>(MekCapabilities.PIGMENT_HANDLER_CAPABILITY);
    public static final ChemicalUtil<Slurry, SlurryStack, ISlurryHandler> SLURRY = new ChemicalUtil<>(MekCapabilities.SLURRY_HANDLER_CAPABILITY);

    private final Capability<H> capability;

    private ChemicalUtil(Capability<H> capability) {
        this.capability = capability;
    }

    /**
     * {@link net.minecraftforge.fluids.FluidUtil#tryFillContainer(ItemStack, IFluidHandler, int, Player, boolean)}
     *
     * @param container
     * @param chemicalSource
     * @param maxAmount
     * @param player
     * @param doFill
     * @return
     */
    public FluidActionResult tryFillContainer(ItemStack container, H chemicalSource, long maxAmount, @Nullable Player player, boolean doFill) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);

        return getChemicalHandler(containerCopy).map(containerChemicalHandler -> {
            S simulatedTransfer = tryChemicalTransfer(containerChemicalHandler, chemicalSource, maxAmount, false);

            if (!simulatedTransfer.isEmpty()) {
                if (doFill) {
                    tryChemicalTransfer(containerChemicalHandler, chemicalSource, maxAmount, true);
                }

                return new FluidActionResult(containerCopy);
            }

            return FluidActionResult.FAILURE;
        }).orElse(FluidActionResult.FAILURE);
    }

    public FluidActionResult tryEmptyContainer(ItemStack container, H chemicalDestination, long maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);

        return getChemicalHandler(containerCopy).map(containerChemicalHandler -> {
            S transfer = tryChemicalTransfer(chemicalDestination, containerChemicalHandler, maxAmount, true);

            if (transfer.isEmpty()) {
                return FluidActionResult.FAILURE;
            }

            return new FluidActionResult(containerCopy);
        }).orElse(FluidActionResult.FAILURE);
    }

    public FluidActionResult tryFillContainerAndStow(ItemStack container, H chemicalSource, IItemHandler inventory, long maxAmount, @Nullable Player player, boolean doFill) {
        if (container.isEmpty()) {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.getAbilities().instabuild) {
            FluidActionResult filledReal = tryFillContainer(container, chemicalSource, maxAmount, player, doFill);

            if (filledReal.isSuccess()) {
                return new FluidActionResult(container);
            }
        } else if (container.getCount() == 1) {
            FluidActionResult filledReal = tryFillContainer(container, chemicalSource, maxAmount, player, doFill);

            if (filledReal.isSuccess()) {
                return filledReal;
            }
        } else {
            FluidActionResult filledSimulated = tryFillContainer(container, chemicalSource, maxAmount, player, false);

            if (filledSimulated.isSuccess() && (ItemHandlerHelper.insertItemStacked(inventory, filledSimulated.getResult(), true).isEmpty() || player != null)) {
                FluidActionResult filledReal = tryFillContainer(container, chemicalSource, maxAmount, player, doFill);
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);

                if (!remainder.isEmpty() && player != null && doFill) {
                    ItemHandlerHelper.giveItemToPlayer(player, remainder);
                }

                ItemStack containerCopy = container.copy();
                containerCopy.shrink(1);

                return new FluidActionResult(containerCopy);
            }
        }

        return FluidActionResult.FAILURE;
    }

    public FluidActionResult tryEmptyContainerAndStow(ItemStack container, H chemicalDestination, IItemHandler inventory, long maxAmount, @Nullable Player player, boolean doDrain) {
        if (container.isEmpty()) {
            return FluidActionResult.FAILURE;
        }

        if (player != null && player.getAbilities().instabuild) {
            FluidActionResult emptiedReal = tryEmptyContainer(container, chemicalDestination, maxAmount, player, doDrain);

            if (emptiedReal.isSuccess()) {
                return new FluidActionResult(container);
            }
        } else if (container.getCount() == 1) {
            FluidActionResult emptiedReal = tryEmptyContainer(container, chemicalDestination, maxAmount, player, doDrain);

            if (emptiedReal.isSuccess()) {
                return emptiedReal;
            }
        } else {
            FluidActionResult emptiedSimulated = tryEmptyContainer(container, chemicalDestination, maxAmount, player, false);

            if (emptiedSimulated.isSuccess() && (ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true).isEmpty() || player != null)) {
                FluidActionResult emptiedReal = tryEmptyContainer(container, chemicalDestination, maxAmount, player, doDrain);
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                if (!remainder.isEmpty() && player != null && doDrain) {
                    ItemHandlerHelper.giveItemToPlayer(player, remainder);
                }

                ItemStack containerCopy = container.copy();
                containerCopy.shrink(1);
                return new FluidActionResult(containerCopy);
            }
        }

        return FluidActionResult.FAILURE;
    }

    public S tryChemicalTransfer(H chemicalDestination, H chemicalSource, long maxAmount, boolean doTransfer) {
        S drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
        if (!drainable.isEmpty()) {
            return tryFluidTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
        }
        return chemicalDestination.getEmptyStack();
    }

    private S tryFluidTransfer_Internal(H chemicalDestination, H chemicalSource, S drainable, boolean doTransfer) {
        long fillableAmount = drainable.getAmount() - chemicalDestination.insertChemical(drainable, Action.SIMULATE).getAmount();

        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);

            if (doTransfer) {
                S drained = chemicalSource.extractChemical(drainable, Action.EXECUTE);

                if (!drained.isEmpty()) {
                    drained.setAmount(drained.getAmount() - chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount());
                    return drained;
                }
            } else {
                return drainable;
            }
        }

        return chemicalDestination.getEmptyStack();
    }

    public LazyOptional<H> getChemicalHandler(ItemStack itemStack) {
        return itemStack.getCapability(capability);
    }
}
