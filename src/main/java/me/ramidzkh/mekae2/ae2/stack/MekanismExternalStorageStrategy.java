package me.ramidzkh.mekae2.ae2.stack;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.storage.MEStorage;
import appeng.util.BlockApiCache;

public class MekanismExternalStorageStrategy implements ExternalStorageStrategy {

    private final Map<Byte, BlockApiCache<? extends IChemicalHandler>> lookups;
    private final Direction fromSide;

    public MekanismExternalStorageStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = Map.of(
                MekanismKey.GAS, BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.INFUSION, BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.PIGMENT, BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                MekanismKey.SLURRY, BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos));
        this.fromSide = fromSide;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var handlers = new IChemicalHandler[4];
        var empty = true;

        for (var entry : lookups.entrySet()) {
            var storage = entry.getValue().find(fromSide);

            if (storage == null) {
                continue;
            }

            handlers[entry.getKey()] = storage;
            empty = false;
        }

        if (empty) {
            return null;
        }

        return new ChemicalHandlerFacade(handlers, extractableOnly, injectOrExtractCallback);
    }
}
