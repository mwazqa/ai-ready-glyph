package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Telephony;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class MessagingApps {
    record Entry(MessagingSource source, String packageName, String label) { }
    static String defaultSmsPackage(Context context) {
        String telephony = Telephony.Sms.getDefaultSmsPackage(context);
        if (telephony != null && !telephony.isBlank()) return telephony;
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", "", null));
        ResolveInfo resolved = manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Set<String> handlers = new HashSet<>();
        for (ResolveInfo handler : manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)) {
            if (handler.activityInfo != null && handler.activityInfo.enabled
                    && handler.activityInfo.applicationInfo != null && handler.activityInfo.applicationInfo.enabled)
                handlers.add(handler.activityInfo.packageName);
        }
        String name = resolved == null || resolved.activityInfo == null ? null : resolved.activityInfo.packageName;
        return SmsPackageResolver.fallback(name, handlers);
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
