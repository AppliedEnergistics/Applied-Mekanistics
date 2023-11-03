package me.ramidzkh.mekae2.ae2.stack;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import me.ramidzkh.mekae2.MekCapabilities;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.storage.MEStorage;
import appeng.util.BlockApiCache;

public class MekanismExternalStorageStrategy implements ExternalStorageStrategy {

    private final BlockApiCache<? extends IChemicalHandler>[] lookups;
    private final Direction fromSide;

    public MekanismExternalStorageStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = new BlockApiCache[] {
                BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos) };
        this.fromSide = fromSide;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var handlers = new IChemicalHandler[4];
        var empty = true;

        for (int i = 0; i < 4; i++) {
            var storage = lookups[i].find(fromSide);

            if (storage == null) {
                continue;
            }

            handlers[i] = storage;
            empty = false;
        }

        if (empty) {
            return null;
        }

        return new ChemicalHandlerFacade(handlers, extractableOnly, injectOrExtractCallback);
    }
}
