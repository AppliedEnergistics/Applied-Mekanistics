package me.ramidzkh.mekae2.ae2.stack;

import java.util.Map;
import java.util.Objects;

import net.minecraft.network.chat.Component;

import me.ramidzkh.mekae2.ae2.MekanismKey;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import appeng.me.storage.NullInventory;

record CompositeFormStorage(Map<Byte, MEStorage> storages) implements MEStorage {

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return getStorage(what).isPreferredStorageFor(what, source);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        return getStorage(what).insert(what, amount, mode, source);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        return getStorage(what).extract(what, amount, mode, source);
    }

    /**
     * Describes the types of storage represented by this object.
     */
    @Override
    public Component getDescription() {
        return GuiText.ExternalStorage.text(Component.literal("Chemicals"));
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var storage : storages.values()) {
            storage.getAvailableStacks(out);
        }
    }

    private MEStorage getStorage(AEKey key) {
        MEStorage storage = null;

        if (key instanceof MekanismKey mekanismKey) {
            storage = storages.get(mekanismKey.getForm());
        }

        return Objects.requireNonNullElse(storage, NullInventory.of());
    }
}
