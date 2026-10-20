package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

final class AppSettings {
    private final SharedPreferences data;
    AppSettings(Context context) { data = context.getSharedPreferences("settings", Context.MODE_PRIVATE); }
    boolean enabled() { return data.getBoolean("enabled", true); }
    void setEnabled(boolean value) { data.edit().putBoolean("enabled", value).apply(); }
    Set<String> packages() { return new HashSet<>(data.getStringSet("packages", Set.of())); }
    void select(String name, boolean selected) {
        Set<String> names = packages();
        if (selected) names.add(name); else names.remove(name);
        data.edit().putStringSet("packages", names).apply();
    }
    boolean relaxed() { return data.getBoolean("relaxed", false); }
    void setRelaxed(boolean value) { data.edit().putBoolean("relaxed", value).apply(); }
    String phrases() { return data.getString("phrases", ""); }
    void setPhrases(String value) { data.edit().putString("phrases", value).apply(); }
    AiProvider previewProvider() { return AiProvider.fromId(data.getString("preview_provider", "chatgpt")); }
    void setPreviewProvider(AiProvider provider) { data.edit().putString("preview_provider", provider.id).apply(); }
    AiProvider providerFor(String packageName) {
        String override = data.getString("provider:" + packageName, null);
        return override == null ? AiProvider.fromPackage(packageName) : AiProvider.fromId(override);
    }
    void setProvider(String packageName, AiProvider provider) {
        if (provider == null) data.edit().remove("provider:" + packageName).apply();
        else data.edit().putString("provider:" + packageName, provider.id).apply();
    }
}
