package pl.glyphtoy.aiready;

import java.text.DateFormat;
import java.util.Date;

final class LocalStatus {
    static boolean listenerConnected;
    static String lastEvent = "Waiting for a notification from a selected app.";
    static String matrix = "Ready for a Glyph test.";
    static void event(String message) {
        lastEvent = timestamp(message);
    }
    static void glyph(String message) { matrix = timestamp(message); }
    private static String timestamp(String message) {
        return DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) + " Â· " + message;
    }
}
