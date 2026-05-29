package vn.fastfood.config;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public final class OrderConcurrencyDemoSettings {
    private static final String SAFE = "SAFE";
    private static final String UNSAFE = "UNSAFE";
    private static final AtomicReference<String> mode = new AtomicReference<>(
            normalizeMode(System.getProperty("order.concurrency.mode", SAFE)));

    private OrderConcurrencyDemoSettings() {
    }

    public static String getMode() {
        return mode.get();
    }

    public static String setMode(String nextMode) {
        String normalizedMode = normalizeMode(nextMode);
        mode.set(normalizedMode);
        return normalizedMode;
    }

    public static boolean isUnsafeMode() {
        return UNSAFE.equals(mode.get());
    }

    private static String normalizeMode(String value) {
        if (value == null) {
            return SAFE;
        }

        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);
        if (UNSAFE.equals(normalizedValue)) {
            return UNSAFE;
        }

        return SAFE;
    }
}
