package pl.glyphtoy.aiready;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessagingSourceTest {
    @Test public void recognizesOnlyExactApprovedPackages() {
        assertEquals(MessagingSource.DISCORD, MessagingSource.fromPackage("com.discord", null));
        assertEquals(MessagingSource.MESSENGER, MessagingSource.fromPackage("com.facebook.orca", null));
        assertEquals(MessagingSource.WHATSAPP, MessagingSource.fromPackage("com.whatsapp", null));
        for (String name : new String[]{"com.discord.fake", "com.whatsapp.w4b", "com.example.messages", "", "COM.DISCORD"})
            assertNull(MessagingSource.fromPackage(name, null));
        assertNull(MessagingSource.fromPackage(null, null));
    }
    @Test public void eachSourceHasItsOwnDefaultStyle() {
        assertEquals(AnimationStyle.DISCORD, MessagingSource.DISCORD.style);
        assertEquals(AnimationStyle.MESSENGER, MessagingSource.MESSENGER.style);
        assertEquals(AnimationStyle.WHATSAPP, MessagingSource.WHATSAPP.style);
        assertEquals(AnimationStyle.SMS, MessagingSource.SMS.style);
        for (MessagingSource source : MessagingSource.values()) assertTrue(source.style.messaging());
    }
    @Test public void smsSelectionFollowsTheCurrentDefaultAndHandlesNoDefault() {
        Set<String> selected = Set.of("sms", "discord");
        assertEquals(Set.of("com.discord", "com.example.first"), MessagingSource.selectedPackages(selected, "com.example.first"));
        assertEquals(Set.of("com.discord", "com.example.second"), MessagingSource.selectedPackages(selected, "com.example.second"));
        assertEquals(Set.of("com.discord"), MessagingSource.selectedPackages(selected, null));
        assertEquals(Set.of("com.discord"), MessagingSource.selectedPackages(selected, " "));
        assertEquals(MessagingSource.SMS, MessagingSource.fromPackage("com.example.first", "com.example.first"));
        assertNull(MessagingSource.fromPackage("com.example.first", "com.example.second"));
    }
    @Test public void newAndUnknownSelectionsDoNotEnableMessagingApps() {
        assertEquals(Set.of(), MessagingSource.selectedPackages(Set.of(), "com.example.sms"));
        assertEquals(Set.of(), MessagingSource.selectedPackages(Set.of("other", "com.whatsapp"), "com.example.sms"));
        assertEquals(Set.of("com.whatsapp"), MessagingSource.selectedPackages(Set.of("whatsapp"), "com.example.sms"));
    }
}
