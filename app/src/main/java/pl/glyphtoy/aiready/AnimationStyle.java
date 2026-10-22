package pl.glyphtoy.aiready;

enum AnimationStyle {
    CHATGPT("chatgpt", "ChatGPT", "The knot draws from its inner junctions outwards."),
    CLAUDE("claude", "Claude", "The symbol blooms and folds back in."),
    GEMINI("gemini", "Gemini", "A single pixel grows into the Gemini star."),
    GENERIC("generic", "Other AI", "Pixel lettering with a blinking signal dot."),
    DISCORD("discord", "Discord", "Two halves join, then the Discord eyes blink."),
    MESSENGER("messenger", "Messenger", "A bolt draws first, then its bubble lights up."),
    WHATSAPP("whatsapp", "WhatsApp", "The phone draws, its bubble closes and signal waves spread."),
    SMS("sms", "SMS", "A bubble draws around three bouncing typing dots."),
    INSTAGRAM("instagram", "Instagram", "Camera corners join, the lens grows and its flash lights up."),
    TIKTOK("tiktok", "TikTok", "A musical note draws upwards and sends two soft echoes."),
    X("x", "X", "Diagonal strokes draw from their tips and meet at the centre."),
    FACEBOOK("facebook", "Facebook", "The f draws upwards, then its crossbar lights up.");

    final String id, label, description;
    AnimationStyle(String id, String label, String description) {
        this.id = id; this.label = label; this.description = description;
    }
    boolean messaging() { return this == DISCORD || this == MESSENGER || this == WHATSAPP || this == SMS; }
    boolean social() { return this == INSTAGRAM || this == TIKTOK || this == X || this == FACEBOOK; }
    static AnimationStyle fromPackage(String packageName) {
        if ("com.openai.chatgpt".equals(packageName)) return CHATGPT;
        if ("com.anthropic.claude".equals(packageName)) return CLAUDE;
        if ("com.google.android.apps.bard".equals(packageName)) return GEMINI;
        return GENERIC;
    }
    static AnimationStyle fromId(String value) {
        for (AnimationStyle provider : values()) if (provider.id.equals(value)) return provider;
        return GENERIC;
    }
}
