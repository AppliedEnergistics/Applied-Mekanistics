package me.ramidzkh.mekae2.ae2.stack;

import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import me.ramidzkh.mekae2.ae2.MekanismKey;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;

public record CompositeFormStorage(Map<Byte, MEStorage> storages) implements MEStorage {

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (!(what instanceof MekanismKey mekanismKey))
            return false;
        var storage = storages.get(mekanismKey.getForm());
        return storage != null && storage.isPreferredStorageFor(what, source);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof MekanismKey mekanismKey))
            return 0;
        var storage = storages.get(mekanismKey.getForm());
        return storage != null ? storage.insert(what, amount, mode, source) : 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof MekanismKey mekanismKey))
            return 0;
        var storage = storages.get(mekanismKey.getForm());
        return storage != null ? storage.extract(what, amount, mode, source) : 0;
    }

    /**
     * Describes the types of storage represented by this object.
     */
    @Override
    public Component getDescription() {
        var types = new TextComponent("Chemicals");
        return GuiText.ExternalStorage.text(types);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (var storage : storages.values()) {
            storage.getAvailableStacks(out);
        }
    }
}
