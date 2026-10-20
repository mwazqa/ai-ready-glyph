package pl.glyphtoy.aiready;

/** Package identity, never notification text or a look-alike app label. */
enum AiProvider {
    CHATGPT("chatgpt", "ChatGPT", "The knot draws from its inner junctions outwards."),
    CLAUDE("claude", "Claude", "The symbol blooms and folds back in."),
    GEMINI("gemini", "Gemini", "A single pixel grows into the Gemini star."),
    GENERIC("generic", "Other AI", "Pixel lettering with a blinking signal dot.");

    final String id, label, description;
    AiProvider(String id, String label, String description) {
        this.id = id; this.label = label; this.description = description;
    }
    static AiProvider fromPackage(String packageName) {
        if ("com.openai.chatgpt".equals(packageName)) return CHATGPT;
        if ("com.anthropic.claude".equals(packageName)) return CLAUDE;
        if ("com.google.android.apps.bard".equals(packageName)) return GEMINI;
        return GENERIC;
    }
    static AiProvider fromId(String value) {
        for (AiProvider provider : values()) if (provider.id.equals(value)) return provider;
        return GENERIC;
    }
}
