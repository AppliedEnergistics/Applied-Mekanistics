package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.helpers.iface.IInterfaceTarget;
import appeng.me.storage.ExternalStorageFacade;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("ClassCanBeRecord")
public class CascadingInterfaceTarget implements IInterfaceTarget {

    @Nullable
    private final IInterfaceTarget parent;
    private final Map<Predicate<AEKey>, ExternalStorageFacade> facades;
    private final IActionSource actionSource;

    public CascadingInterfaceTarget(@Nullable IInterfaceTarget parent, Map<Predicate<AEKey>, ExternalStorageFacade> facades, IActionSource actionSource) {
        this.parent = parent;
        this.facades = facades;
        this.actionSource = actionSource;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable type) {
        long inserted;

        if (parent != null && (inserted = parent.insert(what, amount, type)) != 0) {
            return inserted;
        }

        for (var entry : facades.entrySet()) {
            if (entry.getKey().test(what)) {
                return entry.getValue().insert(what, amount, type, actionSource);
            }
        }

        return 0;
    }

    @Override
    public boolean containsPatternInput(Set<AEKey> patternInputs) {
        for (var facade : facades.values()) {
            if (facade.containsAnyFuzzy(patternInputs)) {
                return true;
            }
        }

        return false;
    }
}
