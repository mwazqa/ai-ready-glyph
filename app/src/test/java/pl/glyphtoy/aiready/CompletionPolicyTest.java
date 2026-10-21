package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class CompletionPolicyTest {
    private boolean match(String text) { return CompletionPolicy.matches(text, false, false, false, false, ""); }
    @Test public void recognizesEnglishAndPolishCompletion() {
        assertTrue(match("Your response is ready."));
        assertTrue(match("ODPOWIEDŹ JEST GOTOWA"));
        assertTrue(match("Zadanie zostało ukończone."));
        assertFalse(match("New feature available"));
        assertFalse(match("task completely unrelated"));
    }
    @Test public void ignoresProgressAndGroupSummariesEvenInRelaxedMode() {
        assertFalse(CompletionPolicy.matches("response ready", true, false, false, true, ""));
        assertFalse(CompletionPolicy.matches("response ready", false, true, false, true, ""));
        assertFalse(CompletionPolicy.matches("response ready", false, false, true, true, ""));
        assertFalse(match("Your response is not ready"));
        assertFalse(match("Odpowiedź nie jest gotowa"));
        assertFalse(CompletionPolicy.matches("Generating your response", false, false, false, true, ""));
    }
    @Test public void customPhrasesAndRelaxedModeAreExplicitOptIns() {
        assertFalse(match("Claude finished"));
        assertTrue(CompletionPolicy.matches("Claude finished", false, false, false, false, "wynik gotowy;Claude finished"));
        assertTrue(CompletionPolicy.matches("New message", false, false, false, true, ""));
        assertFalse(CompletionPolicy.matches("New message", false, false, false, false, "; \n"));
    }

    @Test public void distinguishesStructuralReasonsForSkippingNotifications() {
        assertEquals(CompletionPolicy.Result.ONGOING,
                CompletionPolicy.evaluate("response ready", true, false, false, true, ""));
        assertEquals(CompletionPolicy.Result.GROUP_SUMMARY,
                CompletionPolicy.evaluate("response ready", false, true, false, true, ""));
        assertEquals(CompletionPolicy.Result.ACTIVE_PROGRESS,
                CompletionPolicy.evaluate("response ready", false, false, true, true, ""));
    }

    @Test public void distinguishesBusyTextFromMissingCompletionPhrase() {
        assertEquals(CompletionPolicy.Result.BUSY_TEXT,
                CompletionPolicy.evaluate("Generating your response", false, false, false, true, ""));
        assertEquals(CompletionPolicy.Result.NO_READY_PHRASE,
                CompletionPolicy.evaluate("New message", false, false, false, false, ""));
        assertEquals(CompletionPolicy.Result.READY,
                CompletionPolicy.evaluate("New message", false, false, false, true, ""));
        assertEquals(CompletionPolicy.Result.READY,
                CompletionPolicy.evaluate("Claude finished", false, false, false, false, "Claude finished"));
    }
}
