package pl.glyphtoy.aiready;

import java.text.DateFormat;
import java.util.Date;

final class LocalStatus {
    static boolean listenerConnected;
    static String lastEvent = "Waiting for a notification from a selected AI app.";
    static String matrix = "Ready for a Glyph test.";
    static void event(String message) {
        lastEvent = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + " · " + message;
    }
}
