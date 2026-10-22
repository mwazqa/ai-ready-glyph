package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class PostedNotificationGateTest {
    @Test public void skipsExistingHistoryButAcceptsANewerPost() {
        PostedNotificationGate gate = new PostedNotificationGate();
        gate.seed("conversation", 100);
        assertFalse(gate.accept("conversation", 100));
        assertTrue(gate.accept("conversation", 200));
    }
    @Test public void acceptsRepeatedConversationIdsWithNewPostTimes() {
        PostedNotificationGate gate = new PostedNotificationGate();
        assertTrue(gate.accept("conversation", 100));
        assertFalse(gate.accept("conversation", 100));
        assertTrue(gate.accept("conversation", 101));
        assertFalse(gate.accept("conversation", 101));
        assertTrue(gate.accept("other", 101));
    }
    @Test public void permitsRemovalAndWallClockChanges() {
        PostedNotificationGate gate = new PostedNotificationGate();
        assertTrue(gate.accept("conversation", 100));
        assertTrue(gate.accept("conversation", 50));
        gate.remove("conversation");
        assertTrue(gate.accept("conversation", 50));
    }
    @Test public void boundsRetainedNotificationMetadata() {
        PostedNotificationGate gate = new PostedNotificationGate();
        for (int i = 0; i < 513; i++) gate.seed("key-" + i, 100);
        assertFalse(gate.accept("key-512", 100));
        assertTrue(gate.accept("key-0", 100));
    }
}
