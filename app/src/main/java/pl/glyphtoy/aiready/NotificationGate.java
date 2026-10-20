package pl.glyphtoy.aiready;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class NotificationGate {
    private final Map<String, Boolean> delivered = new LinkedHashMap<>();
    void seed(String key) { remember(key, true); }
    void remove(String key) { delivered.remove(key); }
    void progress(String key) { delivered.remove(key); }
    boolean accept(String key, boolean complete) {
        if (!complete) return false;
        if (delivered.containsKey(key)) return false;
        remember(key, true);
        return true;
    }
    private void remember(String key, boolean value) {
        delivered.put(key, value);
        if (delivered.size() > 512) {
            Iterator<String> keys = delivered.keySet().iterator();
            keys.next(); keys.remove();
        }
    }
}
