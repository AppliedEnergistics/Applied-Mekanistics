package me.ramidzkh.mekae2.ae2.stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.IChemicalHandler;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.util.BlockApiCache;

public class MekanismStackExportStrategy implements StackExportStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(MekanismStackExportStrategy.class);
    private final BlockApiCache<? extends IChemicalHandler>[] lookups;
    private final Direction fromSide;

    public MekanismStackExportStrategy(ServerLevel level,
            BlockPos fromPos,
            Direction fromSide) {
        this.lookups = new BlockApiCache[] {
                BlockApiCache.create(MekCapabilities.GAS_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.INFUSION_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, level, fromPos),
                BlockApiCache.create(MekCapabilities.SLURRY_HANDLER_CAPABILITY, level, fromPos) };
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long amount) {
        if (!(what instanceof MekanismKey mekanismKey)) {
            return 0;
        }

        var storage = lookups[mekanismKey.getForm()].find(fromSide);

        if (storage == null) {
            return 0;
        }

        var inv = context.getInternalStorage();

        var extracted = StorageHelper.poweredExtraction(
                context.getEnergySource(),
                inv.getInventory(),
                what,
                amount,
                context.getActionSource(),
                Actionable.SIMULATE);

        var wasInserted = extracted
                - storage.insertChemical(mekanismKey.withAmount(extracted),
                        Action.SIMULATE).getAmount();

        if (wasInserted > 0) {
            extracted = StorageHelper.poweredExtraction(
                    context.getEnergySource(),
                    inv.getInventory(),
                    what,
                    wasInserted,
                    context.getActionSource(),
                    Actionable.MODULATE);

            wasInserted = extracted
                    - storage.insertChemical(mekanismKey.withAmount(extracted),
                            Action.EXECUTE).getAmount();

            if (wasInserted < extracted) {
                LOGGER.error("Storage export issue, voided {}x{}", extracted - wasInserted, what);
            }

            return wasInserted;
        }

        return 0;
    }

    @Override
    public long push(AEKey what, long amount, Actionable mode) {
        if (!(what instanceof MekanismKey mekanismKey)) {
            return 0;
        }

        var storage = lookups[mekanismKey.getForm()].find(fromSide);

        if (storage == null) {
            return 0;
        }

        return amount - storage.insertChemical(mekanismKey.withAmount(amount),
                Action.fromFluidAction(mode.getFluidAction())).getAmount();
    }
}
