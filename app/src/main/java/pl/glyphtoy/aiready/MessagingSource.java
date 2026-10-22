package pl.glyphtoy.aiready;

import java.util.HashSet;
import java.util.Set;

enum MessagingSource {
    DISCORD("discord", "Discord", "com.discord", AnimationStyle.DISCORD),
    MESSENGER("messenger", "Messenger", "com.facebook.orca", AnimationStyle.MESSENGER),
    WHATSAPP("whatsapp", "WhatsApp", "com.whatsapp", AnimationStyle.WHATSAPP),
    SMS("sms", "SMS", null, AnimationStyle.SMS);

    final String id, label, packageName;
    final AnimationStyle style;
    MessagingSource(String id, String label, String packageName, AnimationStyle style) {
        this.id = id; this.label = label; this.packageName = packageName; this.style = style;
    }
    String packageFor(String defaultSmsPackage) {
        String name = this == SMS ? defaultSmsPackage : packageName;
        return name == null || name.isBlank() ? null : name;
    }
    static MessagingSource fromPackage(String packageName, String defaultSmsPackage) {
        if (packageName == null || packageName.isBlank()) return null;
        for (MessagingSource source : values())
            if (packageName.equals(source.packageFor(defaultSmsPackage))) return source;
        return null;
    }
    static Set<String> selectedPackages(Set<String> ids, String defaultSmsPackage) {
        Set<String> names = new HashSet<>();
        for (MessagingSource source : values()) {
            String name = source.packageFor(defaultSmsPackage);
            if (ids.contains(source.id) && name != null) names.add(name);
        }
        return names;
    }
}
