package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import java.util.ArrayList;
import java.util.List;

final class MessagingApps {
    record Entry(MessagingSource source, String packageName, String label) { }
    static String defaultSmsPackage(Context context) {
        return Telephony.Sms.getDefaultSmsPackage(context);
    }
    static List<Entry> installed(Context context) {
        List<Entry> entries = new ArrayList<>();
        String sms = defaultSmsPackage(context);
        PackageManager manager = context.getPackageManager();
        for (MessagingSource source : MessagingSource.values()) {
            String name = source.packageFor(sms);
            if (name == null) continue;
            try {
                ApplicationInfo info = manager.getApplicationInfo(name, 0);
                if (info.enabled) entries.add(new Entry(source, name, manager.getApplicationLabel(info).toString()));
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return entries;
    }
}
