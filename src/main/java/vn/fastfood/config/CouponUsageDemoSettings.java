package vn.fastfood.config;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public final class CouponUsageDemoSettings {
    private static final String SAFE = "SAFE";
    private static final String UNSAFE = "UNSAFE";
    private static final AtomicReference<String> mode = new AtomicReference<>(
            normalizeMode(System.getProperty("coupon.usage.mode", SAFE)));

    private CouponUsageDemoSettings() {
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

    public static long getUnsafeDelayMs() {
        return Long.getLong("coupon.usage.demo-delay-ms", 8000L);
    }

    public static long getSafeDelayMs() {
        return Long.getLong("coupon.usage.safe-delay-ms", 5000L);
    }

    private static String normalizeMode(String value) {
        if (value == null) {
            return SAFE;
        }

        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);
        if ("LOST_UPDATE".equals(normalizedValue) || UNSAFE.equals(normalizedValue)) {
            return UNSAFE;
        }

        return SAFE;
    }
}
