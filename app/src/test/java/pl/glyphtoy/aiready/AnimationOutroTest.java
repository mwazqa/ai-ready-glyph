package pl.glyphtoy.aiready;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class AnimationOutroTest {
    @Test public void allStylesCloseByChangingTheirShapeAndFinishAtSixSeconds() {
        assertEquals(6000, ReadyAnimation.DURATION_MS);
        for (AnimationStyle style : AnimationStyle.values()) {
            int[] full = ReadyAnimation.frame(style, 4800);
            boolean shapeChanges = false;
            for (long time : new long[]{5000, 5300, 5600, 5800}) {
                int[] closing = ReadyAnimation.frame(style, time);
                double low = 10, high = -1;
                for (int i = 0; i < full.length; i++) {
                    if (full[i] >= 80) {
                        double ratio = closing[i] / (double) full[i];
                        low = Math.min(low, ratio); high = Math.max(high, ratio);
                    }
                    if (full[i] < 8 && closing[i] > 50) shapeChanges = true;
                }
                if (high - low > .25) shapeChanges = true;
            }
            assertTrue(style.label + " must change geometry rather than only dim uniformly", shapeChanges);
            assertTrue(style.label + " must leave at most a tiny final point", sum(ReadyAnimation.frame(style, 5999)) <= 10);
            assertEquals(0, sum(ReadyAnimation.frame(style, 6000)));
        }
    }

    @Test public void chatGptErasesItsOuterStrokesBackTowardsTheInnerJunctions() {
        int[] full = ReadyAnimation.frame(AnimationStyle.CHATGPT, 4800);
        int[] retreat = ReadyAnimation.frame(AnimationStyle.CHATGPT, 5700);
        assertTrue(sum(retreat) > 0);
        assertTrue(radius(retreat) < radius(full) - 1);
        for (int i = 0; i < full.length; i++) if (full[i] == 0) assertEquals(0, retreat[i]);
    }

    @Test public void geminiAndClaudeContractToTheirCentresWithoutRestarting() {
        for (AnimationStyle style : new AnimationStyle[]{AnimationStyle.GEMINI, AnimationStyle.CLAUDE}) {
            assertTrue(radius(ReadyAnimation.frame(style, 5200)) > radius(ReadyAnimation.frame(style, 5800)) + .3);
            assertTrue(sum(ReadyAnimation.frame(style, 5900)) > 0);
        }
        for (long time : new long[]{4800, 4850, 4900, 4950})
            assertArrayEquals(ReadyAnimation.openingFrame(AnimationStyle.CLAUDE, time), ReadyAnimation.frame(AnimationStyle.CLAUDE, time));
    }

    @Test public void discordHalvesSeparateAndTravelCompletelyOffTheMatrix() {
        int[] full = ReadyAnimation.frame(AnimationStyle.DISCORD, 4800);
        int[] split = ReadyAnimation.frame(AnimationStyle.DISCORD, 5300);
        assertTrue(radius(split) > radius(full) + 1);
        assertTrue(sum(split) > 0);
        for (int y = 0; y < 25; y++) for (int x = 9; x <= 15; x++)
            assertEquals(0, ReadyAnimation.frame(AnimationStyle.DISCORD, 5500)[y * 25 + x]);
        assertEquals(0, sum(ReadyAnimation.frame(AnimationStyle.DISCORD, 5950)));
    }

    @Test public void messengerRetractsItsBubbleLeavingTheBoltBeforeItErases() {
        int[] full = ReadyAnimation.frame(AnimationStyle.MESSENGER, 4800);
        int[] bolt = ReadyAnimation.frame(AnimationStyle.MESSENGER, 5500);
        assertEquals(0, full[11 * 25 + 11]);
        assertTrue(bolt[11 * 25 + 11] > 100);
        assertTrue(full[5 * 25 + 12] > 100);
        assertEquals(0, bolt[5 * 25 + 12]);
    }

    @Test public void smsDotsRetreatRightToLeftBeforeTheBubbleCloses() {
        int[] dots = ReadyAnimation.frame(AnimationStyle.SMS, 5150);
        assertTrue(region(dots, 7, 9, 8, 15) > 100);
        assertEquals(0, region(dots, 15, 17, 8, 15));
        int[] outline = ReadyAnimation.frame(AnimationStyle.SMS, 5400);
        assertEquals(0, region(outline, 6, 18, 8, 15));
        assertTrue(outline[5 * 25 + 12] > 100);
        assertEquals(ReadyAnimation.frame(AnimationStyle.SMS, 4800)[5 * 25 + 12], outline[5 * 25 + 12]);
    }

    @Test public void genericLettersEraseFromRightToLeft() {
        int[] closing = ReadyAnimation.frame(AnimationStyle.GENERIC, 5700);
        assertTrue(region(closing, 3, 12, 5, 18) > 0);
        assertEquals(0, region(closing, 15, 21, 5, 18));
    }

    @Test public void socialOutrosReverseTheDistinctiveDrawingOrder() {
        int[] camera = ReadyAnimation.frame(AnimationStyle.INSTAGRAM, 5300);
        assertEquals(0, camera[6 * 25 + 18]);
        assertTrue(region(camera, 3, 21, 3, 5) > 100);
        int[] note = ReadyAnimation.frame(AnimationStyle.TIKTOK, 5750);
        assertEquals(0, region(note, 0, 24, 0, 8));
        assertTrue(region(note, 0, 24, 17, 24) > 100);
        int[] cross = ReadyAnimation.frame(AnimationStyle.X, 5600);
        assertEquals(0, cross[12 * 25 + 12]);
        assertTrue(sum(cross) > 0);
        int[] letter = ReadyAnimation.frame(AnimationStyle.FACEBOOK, 5500);
        assertEquals(0, letter[11 * 25 + 8]);
        assertTrue(letter[19 * 25 + 12] > 100);
    }

    private static int sum(int[] pixels) { return Arrays.stream(pixels).sum(); }
    private static int region(int[] pixels, int left, int right, int top, int bottom) {
        int light = 0;
        for (int y = top; y <= bottom; y++) for (int x = left; x <= right; x++) light += pixels[y * 25 + x];
        return light;
    }
    private static double radius(int[] pixels) {
        double radius = 0, light = 0;
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            int value = pixels[y * 25 + x];
            radius += Math.hypot(x - 12, y - 12) * value; light += value;
        }
        return light == 0 ? 0 : radius / light;
    }
}
