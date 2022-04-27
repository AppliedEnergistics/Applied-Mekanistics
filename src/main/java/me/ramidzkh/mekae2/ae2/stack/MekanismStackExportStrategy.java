package me.ramidzkh.mekae2.ae2.stack;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;

public class MekanismStackExportStrategy implements StackExportStrategy {

    private final Map<Byte, BlockApiCache<? extends IChemicalHandler>> lookups;
    private final Direction fromSide;

    public MekanismStackExportStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = Map.of(
                MekanismKey.GAS, BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.INFUSION, BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.PIGMENT, BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.SLURRY, BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos));
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount, Actionable mode) {
        if (!(what instanceof MekanismKey mekanismKey)) {
            return 0;
        }

        for (var entry : lookups.entrySet()) {
            if (entry.getKey() != mekanismKey.getForm()) {
                continue;
            }

            var storage = entry.getValue().find(fromSide);

            if (storage == null) {
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

            var wasInserted = HandlerStrategy.insert(storage, what, extracted, mode);

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
        if (!(what instanceof MekanismKey mekanismKey)) {
            return 0;
        }

        for (var entry : lookups.entrySet()) {
            if (entry.getKey() != mekanismKey.getForm()) {
                continue;
            }

            var storage = entry.getValue().find(fromSide);

            if (storage == null) {
                continue;
            }

            return HandlerStrategy.insert(storage, what, amount, mode);
        }

        return 0;
    }
}
