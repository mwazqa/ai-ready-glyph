package pl.glyphtoy.aiready;

import java.util.LinkedHashMap;
import java.util.Map;

final class PostedNotificationGate {
    private final Map<String, Long> delivered = new LinkedHashMap<>();
    void seed(String key, long postTime) {
        delivered.put(key, postTime);
        if (delivered.size() > 512) delivered.remove(delivered.keySet().iterator().next());
    }
    void remove(String key) { delivered.remove(key); }
    boolean accept(String key, long postTime) {
        Long previous = delivered.get(key);
        if (previous != null && previous == postTime) return false;
        seed(key, postTime);
        return true;
    }
}
