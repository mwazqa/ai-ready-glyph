package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class ReadyAnimationTest {
    @Test public void everyFrameFitsTheCircularMatrixAndBrightnessRange() {
        for (AiProvider provider : AiProvider.values()) for (long time = 0; time <= ReadyAnimation.DURATION_MS; time += 50) {
            int[] pixels = ReadyAnimation.frame(provider, time);
            assertEquals(625, pixels.length);
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
                assertTrue(pixels[y * 25 + x] >= 0 && pixels[y * 25 + x] <= 255);
                if (Math.hypot(x - 12, y - 12) > 12.5) assertEquals(0, pixels[y * 25 + x]);
            }
        }
    }
    @Test public void endsWithAllPixelsOff() {
        for (AiProvider provider : AiProvider.values()) {
            for (long time : new long[]{-1, ReadyAnimation.DURATION_MS, ReadyAnimation.DURATION_MS + 50})
                for (int pixel : ReadyAnimation.frame(provider, time)) assertEquals(0, pixel);
            if (provider == AiProvider.CHATGPT || provider == AiProvider.GENERIC)
                for (int pixel : ReadyAnimation.frame(provider, 0)) assertEquals(0, pixel);
        }
    }
    @Test public void eachProviderHasADistinctVisibleSymbolAndMotion() {
        for (AiProvider provider : AiProvider.values()) {
            int[] settled = ReadyAnimation.frame(provider, 2500);
            assertTrue(Arrays.stream(settled).sum() > 4000);
            assertFalse(Arrays.equals(settled, ReadyAnimation.frame(provider, 150)));
            for (AiProvider other : AiProvider.values()) if (provider != other)
                assertFalse(Arrays.equals(settled, ReadyAnimation.frame(other, 2500)));
        }
    }

    @Test public void geminiStartsAtOnePixelAndGrowsInPlaceWithoutShrinking() {
        int[] first = ReadyAnimation.frame(AiProvider.GEMINI, 0);
        assertEquals(1, Arrays.stream(first).filter(pixel -> pixel > 0).count());
        assertTrue(first[12 * 25 + 12] > 0);
        long previousCount = 0;
        for (long time = 0; time <= 1800; time += 100) {
            int[] frame = ReadyAnimation.frame(AiProvider.GEMINI, time);
            long count = Arrays.stream(frame).filter(pixel -> pixel > 0).count();
            assertTrue(count >= previousCount);
            previousCount = count;
            double total = 0, centerX = 0, centerY = 0;
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
                double light = frame[y * 25 + x];
                total += light; centerX += x * light; centerY += y * light;
            }
            assertEquals(12, centerX / total, .001);
            assertEquals(12, centerY / total, .001);
        }
        assertTrue(previousCount > 60);
    }

    @Test public void chatGptDrawsFixedStrokesProgressivelyThenHoldsTheCompleteLogo() {
        int[] previous = new int[625];
        for (long time = 0; time <= 2300; time += 100) {
            int[] frame = ReadyAnimation.frame(AiProvider.CHATGPT, time);
            for (int i = 0; i < frame.length; i++) assertTrue("drawn ink must stay in place", frame[i] >= previous[i]);
            previous = frame;
        }
        int[] complete = ReadyAnimation.frame(AiProvider.CHATGPT, 2300);
        long partialCount = Arrays.stream(ReadyAnimation.frame(AiProvider.CHATGPT, 300)).filter(p -> p > 0).count();
        long fullCount = Arrays.stream(complete).filter(p -> p > 0).count();
        assertTrue(partialCount > 0 && partialCount < fullCount);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
            assertEquals((int) Math.round(ChatGptSymbol.coverage(x, y) * 225), complete[y * 25 + x]);
        assertArrayEquals(complete, ReadyAnimation.frame(AiProvider.CHATGPT, 4000));
    }

    @Test public void chatGptStartsAtTheInnerJunctionsAndDrawsOutward() {
        int[] initial = ReadyAnimation.frame(AiProvider.CHATGPT, 150);
        int[] complete = ReadyAnimation.frame(AiProvider.CHATGPT, 2300);
        double initialRadius = 0, initialLight = 0, completeRadius = 0, completeLight = 0;
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double radius = Math.hypot(x - 12, y - 12);
            int p = y * 25 + x;
            if (radius >= 5.5) assertEquals("outer strokes must not start first", 0, initial[p]);
            initialRadius += radius * initial[p]; initialLight += initial[p];
            completeRadius += radius * complete[p]; completeLight += complete[p];
        }
        assertTrue(initialLight > 0);
        assertTrue(completeRadius / completeLight > initialRadius / initialLight + 2);
    }

    @Test public void claudeFollowsTheReferenceGrowthContractionAndOneSecondTiming() {
        assertEquals(1000, Arrays.stream(ClaudeFrames.DELAYS).sum());
        int initial = Arrays.stream(ReadyAnimation.frame(AiProvider.CLAUDE, 0)).sum();
        int expanded = Arrays.stream(ReadyAnimation.frame(AiProvider.CLAUDE, 400)).sum();
        int contracted = Arrays.stream(ReadyAnimation.frame(AiProvider.CLAUDE, 930)).sum();
        assertTrue(initial > 0);
        assertTrue(expanded > initial * 2);
        assertTrue(contracted < expanded);
        assertArrayEquals(ReadyAnimation.frame(AiProvider.CLAUDE, 250), ReadyAnimation.frame(AiProvider.CLAUDE, 1250));
    }
}
