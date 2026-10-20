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
}
