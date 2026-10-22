package pl.glyphtoy.aiready;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class SocialAnimationTest {
    @Test public void allSocialAnimationsSettleOnTheirOriginalSymbolsAndKeepSixSeconds() {
        assertEquals(6000, ReadyAnimation.DURATION_MS);
        for (AnimationStyle style : AnimationStyle.values()) if (style.social()) {
            assertTrue(Arrays.stream(ReadyAnimation.frame(style, 0)).allMatch(pixel -> pixel == 0));
            int[] settled = ReadyAnimation.frame(style, 4000);
            assertArrayEquals(settled, ReadyAnimation.frame(style, 4500));
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
                assertEquals((int) Math.round(SocialSymbols.coverage(style, x, y) * 225), settled[y * 25 + x]);
        }
    }

    @Test public void instagramBuildsItsLensAndIntroducesTheFlashLater() {
        int[] initial = ReadyAnimation.frame(AnimationStyle.INSTAGRAM, 400);
        int[] lens = ReadyAnimation.frame(AnimationStyle.INSTAGRAM, 1100);
        int[] settled = ReadyAnimation.frame(AnimationStyle.INSTAGRAM, 4000);
        assertEquals(0, initial[6 * 25 + 18]);
        assertTrue(settled[6 * 25 + 18] > 100);
        assertFalse(Arrays.equals(lens, settled));
        assertTrue(lens[12 * 25 + 16] > 0);
    }

    @Test public void tiktokEchoesExtendBeyondTheCompletedNoteThenDisappear() {
        int[] echo = ReadyAnimation.frame(AnimationStyle.TIKTOK, 2300);
        int[] settled = ReadyAnimation.frame(AnimationStyle.TIKTOK, 4000);
        boolean outsideLight = false;
        for (int i = 0; i < echo.length; i++) if (settled[i] == 0 && echo[i] > 10) outsideLight = true;
        assertTrue(outsideLight);
    }

    @Test public void xDrawsTheTipsBeforeTheJunctionAndRestoresItsBrightness() {
        int[] initial = ReadyAnimation.frame(AnimationStyle.X, 150);
        int[] settled = ReadyAnimation.frame(AnimationStyle.X, 4000);
        assertEquals(0, initial[12 * 25 + 12]);
        assertTrue(Arrays.stream(initial).sum() > 0);
        assertTrue(settled[12 * 25 + 12] > 0);
        assertFalse(Arrays.equals(settled, ReadyAnimation.frame(AnimationStyle.X, 1950)));
    }

    @Test public void facebookIntroducesTheCrossbarAfterTheStemAndSettles() {
        int[] stem = ReadyAnimation.frame(AnimationStyle.FACEBOOK, 500);
        int[] settled = ReadyAnimation.frame(AnimationStyle.FACEBOOK, 4000);
        assertTrue(stem[19 * 25 + 12] > 100);
        assertEquals(0, stem[11 * 25 + 8]);
        assertTrue(settled[11 * 25 + 8] > 100);
    }
}
