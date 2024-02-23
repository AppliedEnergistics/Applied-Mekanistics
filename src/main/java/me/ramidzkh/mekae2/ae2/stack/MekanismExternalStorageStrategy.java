package me.ramidzkh.mekae2.ae2.stack;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import me.ramidzkh.mekae2.MekCapabilities;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.storage.MEStorage;

public class MekanismExternalStorageStrategy implements ExternalStorageStrategy {

    private final BlockCapabilityCache<? extends IChemicalHandler, Direction>[] lookups;

    public MekanismExternalStorageStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = new BlockCapabilityCache[] {
                BlockCapabilityCache.create(MekCapabilities.GAS.block(), level, fromPos, fromSide),
                BlockCapabilityCache.create(MekCapabilities.INFUSION.block(), level, fromPos, fromSide),
                BlockCapabilityCache.create(MekCapabilities.PIGMENT.block(), level, fromPos, fromSide),
                BlockCapabilityCache.create(MekCapabilities.SLURRY.block(), level, fromPos, fromSide) };
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var handlers = new IChemicalHandler[4];
        var empty = true;

        for (var i = 0; i < 4; i++) {
            var storage = lookups[i].getCapability();

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
