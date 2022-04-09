package me.ramidzkh.mekae2.qio;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import me.ramidzkh.mekae2.AMText;
import mekanism.api.Action;
import mekanism.common.content.qio.QIOFrequency;
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
            // Private or trusted: the player who placed the storage bus must have dashboard access.
            var host = querySrc.machine().map(IActionHost::getActionableNode).orElse(null);
            if (host == null) {
                return null;
            }
            var storageBusOwner = IPlayerRegistry.getMapping(dashboard.getLevel())
                    .getProfileId(host.getOwningPlayerId());
            var dashboardOwner = dashboard.getOwnerUUID();

            if (!Objects.equals(dashboardOwner, storageBusOwner)) {
                var securityFreq = dashboard.getSecurity().getFrequency();

                if (securityMode == SecurityMode.PRIVATE) {
                    return null;
                } else if (securityMode == SecurityMode.TRUSTED
                        && !securityFreq.getTrustedUUIDs().contains(storageBusOwner)) {
                    return null;
                }
            }
        }
        return freq;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }
            return freq.massInsert(itemKey.toStack(), amount, Action.fromFluidAction(mode.getFluidAction()));
        }
        return 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof AEItemKey itemKey && amount > 0) {
            var freq = getFrequency();
            if (freq == null) {
                return 0;
            }
            return freq.massExtract(itemKey.toStack(), amount, Action.fromFluidAction(mode.getFluidAction()));
        }
        return 0;
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
