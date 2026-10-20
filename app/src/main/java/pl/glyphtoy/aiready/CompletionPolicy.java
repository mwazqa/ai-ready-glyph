package pl.glyphtoy.aiready;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Heuristic only: third-party apps do not expose a universal completion event. */
final class CompletionPolicy {
    private static final String[] READY = {
        "response is ready", "response ready", "answer is ready", "answer ready",
        "reply is ready", "finished responding", "has responded", "has replied", "task complete",
        "task completed", "task is complete", "task is done", "response complete",
        "response is complete", "odpowiedz jest gotowa", "odpowiedz gotowa",
        "gotowa odpowiedz", "zakonczono zadanie", "zadanie zakonczone",
        "zadanie zostalo ukonczone", "odpowiedz zostala wygenerowana"
    };
    private static final String[] BUSY = {
        "not ready", "isn't ready", "not complete", "still working", "still thinking",
        "generating response", "generating a response", "generating your response",
        "thinking...", "thinking…", "przetwarzanie", "generowanie odpowiedzi",
        "jeszcze nie", "nie jest gotowa", "niegotowa", "w trakcie"
    };

    static boolean matches(String text, boolean ongoing, boolean summary, boolean progress,
                           boolean relaxed, String customPhrases) {
        if (ongoing || summary || progress) return false;
        String value = normalize(text);
        for (String phrase : BUSY) if (containsPhrase(value, normalize(phrase))) return false;
        if (relaxed) return true;
        for (String phrase : READY) if (containsPhrase(value, phrase)) return true;
        for (String phrase : customPhrases.split("[;\\n]")) {
            String normalized = normalize(phrase).trim();
            if (!normalized.isEmpty() && containsPhrase(value, normalized)) return true;
        }
        return false;
    }

    private static boolean containsPhrase(String text, String phrase) {
        return Pattern.compile("(?<![\\p{L}\\p{N}])" + Pattern.quote(phrase)
                + "(?![\\p{L}\\p{N}])").matcher(text).find();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
                .replace('ł', 'l').replaceAll("\\s+", " ").trim();
    }
}
