package me.ramidzkh.mekae2;

import net.minecraft.network.chat.TranslatableComponent;

public enum AMText {
    QIO_FREQUENCY("qio_frequency"),
    ;

    public final String key;

    AMText(String key) {
        this.key = "text.%s.%s".formatted(AppliedMekanistics.ID, key);
    }

    public TranslatableComponent formatted(Object... params) {
        return new TranslatableComponent(this.key, params);
    }
}
