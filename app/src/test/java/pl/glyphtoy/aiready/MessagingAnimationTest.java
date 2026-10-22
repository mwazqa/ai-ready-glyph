package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class MessagingAnimationTest {
    @Test public void everyMessagingAnimationSettlesOnItsOriginalSymbol() {
        for (AnimationStyle style : AnimationStyle.values()) if (style.messaging()) {
            int[] pixels = ReadyAnimation.frame(style, 4000);
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
                int expected = Math.hypot(x - 12, y - 12) <= 12.5
                        ? (int) Math.round(MessagingSymbols.coverage(style, x, y) * 225) : 0;
                assertEquals(style.label + " at " + x + "," + y, expected, pixels[y * 25 + x]);
            }
        }
    }

    @Test public void discordJoinsBeforeBlinkingAndRestoresItsOpenEyes() {
        int[] joined = ReadyAnimation.frame(AnimationStyle.DISCORD, 1000);
        int[] closed = ReadyAnimation.frame(AnimationStyle.DISCORD, 1690);
        assertFalse(Arrays.equals(ReadyAnimation.frame(AnimationStyle.DISCORD, 300), joined));
        assertTrue(closed[12 * 25 + 9] > joined[12 * 25 + 9] + 100);
        assertArrayEquals(joined, ReadyAnimation.frame(AnimationStyle.DISCORD, 2100));
    }

    @Test public void messengerDrawsTheBoltBeforeTheBubbleAndReturnsToNegativeSpace() {
        int[] initial = ReadyAnimation.frame(AnimationStyle.MESSENGER, 400);
        int[] complete = ReadyAnimation.frame(AnimationStyle.MESSENGER, 4000);
        assertTrue(initial[11 * 25 + 11] > 100);
        assertEquals(0, initial[5 * 25 + 12]);
        assertEquals(0, complete[11 * 25 + 11]);
        assertTrue(complete[5 * 25 + 12] > 100);
    }

    @Test public void whatsappSendsTwoWavesBeyondTheSettledSymbol() {
        int[] settled = ReadyAnimation.frame(AnimationStyle.WHATSAPP, 4000);
        for (long time : new long[]{2300, 3100}) {
            int[] wave = ReadyAnimation.frame(AnimationStyle.WHATSAPP, time);
            boolean outsideLight = false;
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
                if (Math.hypot(x - 12.3, y - 12) > 10.5 && settled[y * 25 + x] == 0 && wave[y * 25 + x] > 10)
                    outsideLight = true;
            assertTrue("signal wave must spread beyond the logo", outsideLight);
        }
    }

    @Test public void smsDotsArriveInOrderAndBounceWithoutMovingTheBubble() {
        int[] first = ReadyAnimation.frame(AnimationStyle.SMS, 1000);
        assertTrue(first[10 * 25 + 8] > 0);
        for (int y = 8; y <= 15; y++) assertEquals(0, first[y * 25 + 16]);
        int[] moving = ReadyAnimation.frame(AnimationStyle.SMS, 2425);
        int[] settled = ReadyAnimation.frame(AnimationStyle.SMS, 4000);
        assertTrue(moving[10 * 25 + 8] > settled[10 * 25 + 8]);
        assertEquals(settled[5 * 25 + 12], moving[5 * 25 + 12]);
    }
}
