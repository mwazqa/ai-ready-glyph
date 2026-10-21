package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationPolicyTest {
    @Test public void acceptsRegularNotificationsWithoutTextOrDetectionSettings() {
        assertEquals(NotificationPolicy.Result.ACCEPTED,
                NotificationPolicy.evaluate(false, false, false));
    }

    @Test public void skipsOngoingNotifications() {
        assertEquals(NotificationPolicy.Result.ONGOING,
                NotificationPolicy.evaluate(true, false, false));
        assertNotEquals(NotificationPolicy.Result.ACCEPTED,
                NotificationPolicy.evaluate(true, false, true));
    }

    @Test public void skipsGroupSummaries() {
        assertEquals(NotificationPolicy.Result.GROUP_SUMMARY,
                NotificationPolicy.evaluate(false, true, false));
        assertNotEquals(NotificationPolicy.Result.ACCEPTED,
                NotificationPolicy.evaluate(false, true, true));
    }

    @Test public void skipsActiveProgress() {
        assertEquals(NotificationPolicy.Result.ACTIVE_PROGRESS,
                NotificationPolicy.evaluate(false, false, true));
    }
}
