package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationGateTest {
    @Test public void skipsHistoryAndRepeatedUpdates() {
        NotificationGate gate = new NotificationGate();
        gate.seed("old");
        assertFalse(gate.accept("old", true));
        assertFalse(gate.accept("new", false));
        assertTrue(gate.accept("new", true));
        assertFalse(gate.accept("new", true));
        assertTrue(gate.accept("other", true));
    }
    @Test public void permitsNewResponseReusingAnIdAfterProgressOrRemoval() {
        NotificationGate gate = new NotificationGate();
        assertTrue(gate.accept("id", true));
        gate.progress("id");
        assertTrue(gate.accept("id", true));
        gate.remove("id");
        assertTrue(gate.accept("id", true));
    }
}
