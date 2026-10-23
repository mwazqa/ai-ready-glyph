package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlaybackStatusTest {
    @Test public void countdownUsesTheCurrentPhaseDeadlineAndRoundsUp() {
        PlaybackStatus waiting = PlaybackStatus.waiting(AnimationStyle.DISCORD, 16000);
        assertEquals(6, waiting.secondsRemaining(10000));
        assertEquals(6, waiting.secondsRemaining(10001));
        assertEquals(1, waiting.secondsRemaining(15999));
        assertEquals(0, waiting.secondsRemaining(16000));
        assertEquals(0, waiting.secondsRemaining(30000));
        PlaybackStatus playing = PlaybackStatus.playing(AnimationStyle.DISCORD, 24000);
        assertEquals(6, playing.secondsRemaining(18000));
        assertEquals(4, playing.secondsRemaining(20000));
        assertEquals(0, playing.secondsRemaining(24000));
    }
    @Test public void onlyActivePhasesOfferStopAndUntimedPhasesHaveNoCountdown() {
        assertTrue(PlaybackStatus.waiting(AnimationStyle.GEMINI, 6000).active());
        assertTrue(PlaybackStatus.connecting(AnimationStyle.GEMINI).active());
        assertTrue(PlaybackStatus.playing(AnimationStyle.GEMINI, 12000).active());
        assertFalse(PlaybackStatus.idle().active());
        PlaybackStatus failure = PlaybackStatus.failed(AnimationStyle.GEMINI, "Glyph did not respond.");
        assertFalse(failure.active());
        assertEquals("Glyph did not respond.", failure.error());
        assertEquals(AnimationStyle.GEMINI, failure.style());
        assertEquals(0, PlaybackStatus.connecting(AnimationStyle.GEMINI).secondsRemaining(1));
        assertEquals(0, failure.secondsRemaining(1));
    }
}
