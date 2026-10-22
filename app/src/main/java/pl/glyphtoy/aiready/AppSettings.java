package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

final class AppSettings {
    private final Context context;
    private final SharedPreferences data;
    AppSettings(Context context) {
        this.context = context.getApplicationContext();
        data = this.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }
    boolean enabled() { return data.getBoolean("enabled", true); }
    void setEnabled(boolean value) { data.edit().putBoolean("enabled", value).apply(); }
    Set<String> packages() { return new HashSet<>(data.getStringSet("packages", Set.of())); }
    void select(String name, boolean selected) {
        Set<String> names = packages();
        if (selected) names.add(name); else names.remove(name);
        data.edit().putStringSet("packages", names).apply();
    }
    Set<String> messagingSources() { return new HashSet<>(data.getStringSet("messaging_sources", Set.of())); }
    boolean messagingSelected(MessagingSource source) { return messagingSources().contains(source.id); }
    void selectMessaging(MessagingSource source, boolean selected) {
        Set<String> ids = messagingSources();
        if (selected) ids.add(source.id); else ids.remove(source.id);
        data.edit().putStringSet("messaging_sources", ids).apply();
    }
    Set<String> monitoredPackages() {
        Set<String> names = packages();
        for (MessagingApps.Entry entry : MessagingApps.installed(context))
            if (messagingSelected(entry.source())) names.add(entry.packageName());
        return names;
    }
    MessagingSource messagingSource(String packageName) {
        return MessagingSource.fromPackage(packageName, MessagingApps.defaultSmsPackage(context));
    }
    boolean monitors(String packageName) {
        if (packages().contains(packageName)) return true;
        return MessagingSource.selectedPackages(messagingSources(), MessagingApps.defaultSmsPackage(context)).contains(packageName);
    }
    AnimationStyle defaultStyle(String packageName) {
        MessagingSource source = messagingSource(packageName);
        return source == null ? AnimationStyle.fromPackage(packageName) : source.style;
    }
    AnimationStyle previewProvider() { return AnimationStyle.fromId(data.getString("preview_provider", "chatgpt")); }
    void setPreviewProvider(AnimationStyle provider) { data.edit().putString("preview_provider", provider.id).apply(); }
    AnimationStyle providerFor(String packageName) {
        String override = data.getString(assignmentKey(packageName), null);
        return override == null ? defaultStyle(packageName) : AnimationStyle.fromId(override);
    }
    void setProvider(String packageName, AnimationStyle provider) {
        if (provider == null) data.edit().remove(assignmentKey(packageName)).apply();
        else data.edit().putString(assignmentKey(packageName), provider.id).apply();
    }
    private String assignmentKey(String packageName) {
        MessagingSource source = messagingSource(packageName);
        return "provider:" + (source == null ? packageName : "messaging:" + source.id);
    }
}
