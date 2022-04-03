package me.ramidzkh.mekae2.qio;

import com.google.common.primitives.Ints;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import me.ramidzkh.mekae2.AMText;
import mekanism.common.content.qio.QIODriveData;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.lib.security.SecurityMode;
import mekanism.common.tile.qio.TileEntityQIODashboard;

import appeng.api.config.Actionable;
import appeng.api.features.IPlayerRegistry;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;

public class QioStorageAdapter implements MEStorage {
    private final TileEntityQIODashboard dashboard;
    @Nullable
    private final Direction queriedSide;
    private final IActionSource querySrc;

    public QioStorageAdapter(TileEntityQIODashboard dashboard, @Nullable Direction queriedSide,
            IActionSource querySrc) {
        this.dashboard = dashboard;
        this.queriedSide = queriedSide;
        this.querySrc = querySrc;
    }

    @Nullable
    public QIOFrequency getFrequency() {
        // Check dashboard facing.
        if (dashboard.getBlockState().getValue(BlockStateProperties.FACING).getOpposite() != queriedSide) {
            return null;
        }
        // Check that it has a frequency.
        var freq = dashboard.getFrequency();
        if (freq == null) {
            return null;
        }
        // Check security.
        var securityMode = dashboard.getSecurityMode();
        if (securityMode != SecurityMode.PUBLIC) {
            // Private or trusted: the player who placed the storage bus must be the owner of the dashboard.
            var host = querySrc.machine().map(IActionHost::getActionableNode).orElse(null);
            if (host == null) {
                return null;
            }
            var storageBusOwner = host.getOwningPlayerId();
            var dashboardOwner = IPlayerRegistry.getMapping(dashboard.getLevel()).getPlayerId(dashboard.getOwnerUUID());

            if (storageBusOwner != dashboardOwner) {
                return null;
            }
        }
        return freq;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            // Insert only accepts int, so clamp to that.
            int intAmount = Ints.saturatedCast(amount);
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }

            if (mode == Actionable.MODULATE) {
                return intAmount - freq.addItem(itemKey.toStack(intAmount)).getCount();
            } else {
                // Need to simulate by hand, quite bad :(
                int canFit = 0;
                var hashedItem = HashedItem.raw(itemKey.toStack());

                for (var driveData : freq.getAllDrives()) {
                    canFit += howMuchCanFit(driveData, hashedItem, intAmount - canFit);

                    if (canFit == intAmount) {
                        return canFit;
                    }
                }

                return canFit;
            }
        }
        return 0;
    }

    private static int howMuchCanFit(QIODriveData driveData, HashedItem item, int upTo) {
        long stored = driveData.getStored(item);
        long totalCountLeft = driveData.getCountCapacity() - driveData.getTotalCount();
        if (totalCountLeft == 0 || (stored == 0 && driveData.getTotalTypes() == driveData.getTypeCapacity())) {
            // No room left
            return 0;
        }
        // Ok
        return (int) Math.min(upTo, totalCountLeft);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            // Extract only accepts int, so clamp to that.
            int intAmount = Ints.saturatedCast(amount);
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }
            var hashedItem = HashedItem.raw(itemKey.toStack());

            if (mode == Actionable.MODULATE) {
                return freq.removeByType(hashedItem, intAmount).getCount();
            } else {
                return Math.min(intAmount, Ints.saturatedCast(freq.getStored(hashedItem)));
            }
        }
        return MEStorage.super.extract(what, amount, mode, source);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        var freq = getFrequency();
        if (freq == null) {
            return;
        }
        for (var entry : freq.getItemDataMap().entrySet()) {
            out.add(AEItemKey.of(entry.getKey().getStack()), entry.getValue().getCount());
        }
    }

    @Override
    public Component getDescription() {
        var freq = getFrequency();
        if (freq == null) {
            throw new IllegalStateException("Unexpected null frequency!");
        }
        return AMText.QIO_FREQUENCY.formatted(freq.getName());
    }
}
