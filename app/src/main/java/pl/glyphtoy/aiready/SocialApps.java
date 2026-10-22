package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

final class SocialApps {
    record Entry(SocialSource source, String packageName) { }
    static List<Entry> installed(Context context) {
        List<Entry> entries = new ArrayList<>();
        PackageManager manager = context.getPackageManager();
        for (SocialSource source : SocialSource.values()) {
            try {
                ApplicationInfo info = manager.getApplicationInfo(source.packageName, 0);
                if (info.enabled) entries.add(new Entry(source, source.packageName));
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return entries;
    }
}
