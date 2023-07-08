package me.ramidzkh.mekae2.qio;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import me.ramidzkh.mekae2.AMText;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.inventory.IHashedItem;
import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.api.inventory.qio.IQIOFrequency;
import mekanism.api.security.SecurityMode;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;

/**
 * This generic trick allows us to capture both the BE and the IQIOComponent without depending on the actual Mekanism
 * block entity class.
 */
public class QioStorageAdapter<DASHBOARD extends BlockEntity & IQIOComponent> implements MEStorage {
    private static final Map<IHashedItem, AEItemKey> CACHE = new WeakHashMap<>();
    private final DASHBOARD dashboard;
    private final @Nullable Direction queriedSide;
    private final @Nullable UUID owner;

    public QioStorageAdapter(DASHBOARD dashboard, @Nullable Direction queriedSide, @Nullable UUID owner) {
        this.dashboard = dashboard;
        this.queriedSide = queriedSide;
        this.owner = owner;
    }

    @Nullable
    public IQIOFrequency getFrequency() {
        // Check dashboard facing.
        if (dashboard.getBlockState().getValue(BlockStateProperties.FACING).getOpposite() != queriedSide) {
            return null;
        }
        // Check that it has a frequency.
        var freq = dashboard.getQIOFrequency();
        if (freq == null || !freq.isValid()) {
            return null;
        }
        // Check security.
        var utils = MekanismAPI.getSecurityUtils();
        var securityMode = utils.getSecurityMode(dashboard, dashboard.getLevel().isClientSide());
        if (securityMode != SecurityMode.PUBLIC) {
            // Private or trusted: the player who placed the storage bus must have dashboard access.
            if (!utils.canAccess(owner, dashboard, dashboard.getLevel().isClientSide())) {
                return null;
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

        // Fixes #19
        freq.forAllHashedStored((type, count) -> {
            // noinspection ConstantConditions
            out.add(CACHE.computeIfAbsent(type, it -> AEItemKey.of(it.getInternalStack())), count);
        });
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
