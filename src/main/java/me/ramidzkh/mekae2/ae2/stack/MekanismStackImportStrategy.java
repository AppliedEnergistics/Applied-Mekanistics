package me.ramidzkh.mekae2.ae2.stack;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.core.AELog;
import appeng.util.BlockApiCache;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

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
            for (var i = 0; i < adjacentHandler.getTanks(); i++) {
                var resource = HandlerStrategy.getStackInTank(i, adjacentHandler);

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
                var amount = HandlerStrategy.extract(adjacentHandler, resource, amountForThisResource, Actionable.MODULATE);

                if (amount > 0) {
                    var inserted = inv.getInventory().insert(resource, amount, Actionable.MODULATE,
                            context.getActionSource());

                    if (inserted < amount) {
                        // Be nice and try to give the overflow back
                        AELog.warn("Extracted %dx%s from adjacent storage and voided it because network refused insert",
                                amount - inserted, resource);
                    }

                    var opsUsed = Math.max(1, inserted / MekanismKeyType.TYPE.getAmountPerOperation());
                    context.reduceOperationsRemaining(opsUsed);
                }
            }

            return false;
        }

        return false;
    }
}
