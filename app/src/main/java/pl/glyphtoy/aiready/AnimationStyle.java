package pl.glyphtoy.aiready;

enum AnimationStyle {
    CHATGPT("chatgpt", "ChatGPT", "The knot draws outwards, then unwinds back to its inner junctions.", Category.AI),
    CLAUDE("claude", "Claude", "The symbol blooms, folds in and shrinks to its centre.", Category.AI),
    GEMINI("gemini", "Gemini", "A single pixel grows into the star, then contracts again.", Category.AI),
    GENERIC("generic", "Other AI", "Pixel lettering draws in and erases from right to left.", Category.AI),
    DISCORD("discord", "Discord", "Two halves join and blink, then slide apart.", Category.MESSAGES),
    MESSENGER("messenger", "Messenger", "A bolt draws, its bubble grows and folds back into the bolt.", Category.MESSAGES),
    WHATSAPP("whatsapp", "WhatsApp", "The phone draws with signal waves; its bubble and handset retract at the end.", Category.MESSAGES),
    SMS("sms", "SMS", "The dots bounce in, retreat in reverse order and the bubble closes.", Category.MESSAGES),
    INSTAGRAM("instagram", "Instagram", "The camera draws and flashes, then its lens and corners retract.", Category.SOCIAL),
    TIKTOK("tiktok", "TikTok", "A note draws upwards with echoes, then un-draws to its base.", Category.SOCIAL),
    X("x", "X", "Diagonal strokes meet at the centre, then withdraw to their tips.", Category.SOCIAL),
    FACEBOOK("facebook", "Facebook", "The f draws and its crossbar lights up; both retract in reverse.", Category.SOCIAL);

    enum Category { AI, MESSAGES, SOCIAL }
    final String id, label, description;
    final Category category;
    AnimationStyle(String id, String label, String description, Category category) {
        this.id = id; this.label = label; this.description = description; this.category = category;
    }
    boolean messaging() { return category == Category.MESSAGES; }
    boolean social() { return category == Category.SOCIAL; }
    static AnimationStyle fromPackage(String packageName) {
        if ("com.openai.chatgpt".equals(packageName)) return CHATGPT;
        if ("com.anthropic.claude".equals(packageName)) return CLAUDE;
        if ("com.google.android.apps.bard".equals(packageName)
                || "com.google.android.googlequicksearchbox".equals(packageName)) return GEMINI;
        return GENERIC;
    }
    static AnimationStyle fromId(String value) {
        for (AnimationStyle provider : values()) if (provider.id.equals(value)) return provider;
        return GENERIC;
    }
}
