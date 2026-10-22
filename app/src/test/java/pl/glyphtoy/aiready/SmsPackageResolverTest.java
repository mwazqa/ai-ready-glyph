package pl.glyphtoy.aiready;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class SmsPackageResolverTest {
    @Test public void acceptsTheResolvedEnabledHandlerIncludingGoogleMessages() {
        String google = "com.google.android.apps.messaging";
        assertEquals(google, SmsPackageResolver.fallback(google, Set.of(google)));
        assertEquals("com.example.sms", SmsPackageResolver.fallback("com.example.sms", Set.of("com.example.sms")));
    }

    @Test public void followsTheResolvedHandlerWhenSeveralSmsAppsAreInstalled() {
        Set<String> handlers = Set.of("com.example.first", "com.example.second");
        assertEquals("com.example.second", SmsPackageResolver.fallback("com.example.second", handlers));
        assertEquals("com.example.first", SmsPackageResolver.fallback("com.example.first", handlers));
    }

    @Test public void rejectsAChooserRatherThanPickingAnArbitraryInstalledApp() {
        Set<String> handlers = Set.of("com.example.first", "com.example.second");
        assertNull(SmsPackageResolver.fallback("android", handlers));
        assertNull(SmsPackageResolver.fallback("com.android.intentresolver", handlers));
        assertNull(SmsPackageResolver.fallback("android", Set.of("android")));
        assertNull(SmsPackageResolver.fallback("com.android.intentresolver", Set.of("com.android.intentresolver")));
        assertNull(SmsPackageResolver.fallback(null, handlers));
        assertNull(SmsPackageResolver.fallback("", handlers));
    }

    @Test public void rejectsMissingDisabledOrUnrelatedPackages() {
        assertNull(SmsPackageResolver.fallback("com.example.disabled", Set.of()));
        assertNull(SmsPackageResolver.fallback("com.example.other", Set.of("com.example.sms")));
        assertNull(SmsPackageResolver.fallback(" ", Set.of("com.example.sms")));
    }
}
