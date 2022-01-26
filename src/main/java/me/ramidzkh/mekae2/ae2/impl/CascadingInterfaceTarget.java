package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.helpers.iface.IInterfaceTarget;
import appeng.me.storage.ExternalStorageFacade;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class CascadingInterfaceTarget implements IInterfaceTarget {

    @Nullable
    private final IInterfaceTarget parent;
    private final Map<AEKeyType, ExternalStorageFacade> facades;
    private final IActionSource actionSource;

    public CascadingInterfaceTarget(@Nullable IInterfaceTarget parent, Map<AEKeyType, ExternalStorageFacade> facades, IActionSource actionSource) {
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

        var facade = facades.get(what.getType());

        if (facade != null) {
            return facade.insert(what, amount, type, actionSource);
        }

        return 0;
    }

    @Override
    public boolean containsPatternInput(Set<AEKey> patternInputs) {
        for (ExternalStorageFacade facade : facades.values()) {
            if (facade.containsAnyFuzzy(patternInputs)) {
                return true;
            }
        }

        return false;
    }
}
