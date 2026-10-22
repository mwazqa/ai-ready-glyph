package pl.glyphtoy.aiready;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class SocialSourceTest {
    @Test public void recognizesTheFourStandardOfficialPackages() {
        assertEquals(SocialSource.INSTAGRAM, SocialSource.fromPackage("com.instagram.android"));
        assertEquals(SocialSource.TIKTOK, SocialSource.fromPackage("com.zhiliaoapp.musically"));
        assertEquals(SocialSource.X, SocialSource.fromPackage("com.twitter.android"));
        assertEquals(SocialSource.FACEBOOK, SocialSource.fromPackage("com.facebook.katana"));
    }

    @Test public void excludesLiteRegionalAndLookalikePackages() {
        for (String name : new String[]{null, "", "com.instagram.lite", "com.facebook.lite",
                "com.ss.android.ugc.trill", "com.example.instagram", "COM.TWITTER.ANDROID"})
            assertNull(SocialSource.fromPackage(name));
    }

    @Test public void onlyExplicitSocialSelectionsEnablePackages() {
        assertEquals(Set.of(), SocialSource.selectedPackages(Set.of()));
        assertEquals(Set.of(), SocialSource.selectedPackages(Set.of("discord", "com.instagram.android", "unknown")));
        assertEquals(Set.of("com.instagram.android", "com.twitter.android"),
                SocialSource.selectedPackages(Set.of("instagram", "x")));
    }

    @Test public void eachSourceKeepsItsOwnStableStyleAndId() {
        for (SocialSource source : SocialSource.values()) {
            assertTrue(source.style.social());
            assertEquals(source.id, source.style.id);
            assertEquals(source.style, AnimationStyle.fromId(source.id));
            assertEquals(Set.of(source.packageName), SocialSource.selectedPackages(Set.of(source.id)));
        }
    }
}
