package me.ramidzkh.mekae2.ae2.stack;

import java.util.List;

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

    private final List<BlockApiCache<? extends IChemicalHandler>> lookups;
    private final Direction fromSide;

    public MekanismExternalStorageStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = List.of(BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos));
        this.fromSide = fromSide;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        for (var lookup : lookups) {
            var storage = lookup.find(fromSide);

            if (storage == null) {
                continue;
            }

            var result = HandlerStrategy.getFacade(storage);
            result.setChangeListener(injectOrExtractCallback);
            result.setExtractableOnly(extractableOnly);
            return result;
        }

        return null;
    }
}
