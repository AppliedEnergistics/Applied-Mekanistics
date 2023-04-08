package me.ramidzkh.mekae2.ae2.stack;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.core.AELog;
import appeng.util.BlockApiCache;

@SuppressWarnings("UnstableApiUsage")
public class MekanismStackImportStrategy implements StackImportStrategy {

    private final List<BlockApiCache<? extends IChemicalHandler>> lookups;
    private final Direction fromSide;

    public MekanismStackImportStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = List.of(BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos));
        this.fromSide = fromSide;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(MekanismKeyType.TYPE)) {
            return false;
        }

        for (var lookup : lookups) {
            var adjacentHandler = lookup.find(fromSide);

            if (adjacentHandler == null) {
                continue;
            }

            var remainingTransferAmount = context.getOperationsRemaining()
                    * (long) MekanismKeyType.TYPE.getAmountPerOperation();

            var inv = context.getInternalStorage();

            // Try to find an extractable resource that fits our filter
            for (var i = 0; i < adjacentHandler.getTanks() && remainingTransferAmount > 0; i++) {
                var stack = adjacentHandler.getChemicalInTank(i);
                var resource = MekanismKey.of(stack);

                if (resource == null
                        // Regard a filter that is set on the bus
                        || !context.isInFilter(resource)) {
                    continue;
                }

                // Check how much of *this* resource we can actually insert into the network, it might be 0
                // if the cells are partitioned or there's not enough types left, etc.
                var amountForThisResource = inv.getInventory().insert(resource, remainingTransferAmount,
                        Actionable.SIMULATE,
                        context.getActionSource());

                // Try to simulate-extract it
                var amount = adjacentHandler
                        .extractChemical(ChemicalBridge.withAmount(stack, amountForThisResource), Action.EXECUTE)
                        .getAmount();

                if (amount > 0) {
                    var inserted = inv.getInventory().insert(resource, amount, Actionable.MODULATE,
                            context.getActionSource());

                    if (inserted < amount) {
                        // Be nice and try to give the overflow back
                        var leftover = amount - inserted;
                        leftover = adjacentHandler
                                .insertChemical(ChemicalBridge.withAmount(stack, leftover), Action.EXECUTE).getAmount();

                        if (leftover > 0) {
                            AELog.warn(
                                    "Extracted %dx%s from adjacent storage and voided it because network refused insert",
                                    leftover, resource);
                        }
                    }

                    var opsUsed = Math.max(1, inserted / MekanismKeyType.TYPE.getAmountPerOperation());
                    context.reduceOperationsRemaining(opsUsed);
                    remainingTransferAmount -= inserted;
                }
            }
        }

        return false;
    }
}
