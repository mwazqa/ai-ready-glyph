package pl.glyphtoy.aiready;

enum AnimationStyle {
    CHATGPT("chatgpt", "ChatGPT", "The knot draws outwards, then unwinds back to its inner junctions."),
    CLAUDE("claude", "Claude", "The symbol blooms, folds in and shrinks to its centre."),
    GEMINI("gemini", "Gemini", "A single pixel grows into the star, then contracts again."),
    GENERIC("generic", "Other AI", "Pixel lettering draws in and erases from right to left."),
    DISCORD("discord", "Discord", "Two halves join and blink, then slide apart."),
    MESSENGER("messenger", "Messenger", "A bolt draws, its bubble grows and folds back into the bolt."),
    WHATSAPP("whatsapp", "WhatsApp", "The phone draws with signal waves; its bubble and handset retract at the end."),
    SMS("sms", "SMS", "The dots bounce in, retreat in reverse order and the bubble closes."),
    INSTAGRAM("instagram", "Instagram", "The camera draws and flashes, then its lens and corners retract."),
    TIKTOK("tiktok", "TikTok", "A note draws upwards with echoes, then un-draws to its base."),
    X("x", "X", "Diagonal strokes meet at the centre, then withdraw to their tips."),
    FACEBOOK("facebook", "Facebook", "The f draws and its crossbar lights up; both retract in reverse.");

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
