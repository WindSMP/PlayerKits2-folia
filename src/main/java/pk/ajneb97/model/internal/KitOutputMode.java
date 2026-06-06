package pk.ajneb97.model.internal;

import java.util.Locale;

public enum KitOutputMode {
    ARMOR,
    SHULKER;

    public static KitOutputMode fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return KitOutputMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public String toCommandValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
