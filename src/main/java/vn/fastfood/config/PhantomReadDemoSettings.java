package vn.fastfood.config;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public final class PhantomReadDemoSettings {
    private static final String SAFE   = "SAFE";
    private static final String UNSAFE = "UNSAFE";

    private static final AtomicReference<String> mode = new AtomicReference<>(
            normalize(System.getProperty("phantom.read.mode", SAFE)));

    private PhantomReadDemoSettings() {}

    public static String getMode() { return mode.get(); }

    public static String setMode(String nextMode) {
        String v = normalize(nextMode);
        mode.set(v);
        return v;
    }

    public static boolean isUnsafeMode() { return UNSAFE.equals(mode.get()); }

    private static String normalize(String value) {
        if (value == null) return SAFE;
        return UNSAFE.equals(value.trim().toUpperCase(Locale.ROOT)) ? UNSAFE : SAFE;
    }
}
