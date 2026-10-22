package pl.glyphtoy.aiready;

import java.util.HashSet;
import java.util.Set;

enum SocialSource {
    INSTAGRAM("instagram", "Instagram", "com.instagram.android", AnimationStyle.INSTAGRAM),
    TIKTOK("tiktok", "TikTok", "com.zhiliaoapp.musically", AnimationStyle.TIKTOK),
    X("x", "X", "com.twitter.android", AnimationStyle.X),
    FACEBOOK("facebook", "Facebook", "com.facebook.katana", AnimationStyle.FACEBOOK);

    final String id, label, packageName;
    final AnimationStyle style;
    SocialSource(String id, String label, String packageName, AnimationStyle style) {
        this.id = id; this.label = label; this.packageName = packageName; this.style = style;
    }
    static SocialSource fromPackage(String packageName) {
        for (SocialSource source : values()) if (source.packageName.equals(packageName)) return source;
        return null;
    }
    static Set<String> selectedPackages(Set<String> ids) {
        Set<String> packages = new HashSet<>();
        for (SocialSource source : values()) if (ids.contains(source.id)) packages.add(source.packageName);
        return packages;
    }
}
