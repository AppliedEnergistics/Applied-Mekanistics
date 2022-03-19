package me.ramidzkh.mekae2.ae2.stack;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class MekanismStackExportStrategy implements StackExportStrategy {

    private final List<BlockApiCache<? extends IChemicalHandler>> lookups;
    private final Direction fromSide;

    public MekanismStackExportStrategy(ServerLevel level,
                                       BlockPos fromPos,
                                       Direction fromSide) {
        this.lookups = List.of(BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos));
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount, Actionable mode) {
        if (what.getType() != MekanismKeyType.TYPE) {
            return 0;
        }

        for (var lookup : lookups) {
            var adjacentStorage = lookup.find(fromSide);
            if (adjacentStorage == null) {
                continue;
            }

            var inv = context.getInternalStorage();

            var extracted = StorageHelper.poweredExtraction(
                    context.getEnergySource(),
                    inv.getInventory(),
                    what,
                    amount,
                    context.getActionSource(),
                    Actionable.SIMULATE);

            var wasInserted = HandlerStrategy.insert(adjacentStorage, what, extracted, mode);

            if (wasInserted > 0) {
                if (mode == Actionable.MODULATE) {
                    StorageHelper.poweredExtraction(
                            context.getEnergySource(),
                            inv.getInventory(),
                            what,
                            wasInserted,
                            context.getActionSource(),
                            Actionable.MODULATE);
                }

                return wasInserted;
            }

            return 0;
        }

        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (what.getType() != MekanismKeyType.TYPE) {
            return 0;
        }

        for (var lookup : lookups) {
            var adjacentStorage = lookup.find(fromSide);

            if (adjacentStorage == null) {
                continue;
            }

            return HandlerStrategy.insert(adjacentStorage, what, amount, mode);
        }

        return 0;
    }
}
