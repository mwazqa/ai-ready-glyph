package pl.glyphtoy.aiready;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class InstalledApps {
    static List<AppCatalog.Entry> load(Context context, AppSettings settings) {
        PackageManager manager = context.getPackageManager();
        boolean gemini;
        try { gemini = manager.getApplicationInfo(AiAppCatalog.GEMINI, 0).enabled; }
        catch (PackageManager.NameNotFoundException ignored) { gemini = false; }
        List<AppCatalog.Entry> apps = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Set<String> selectedAi = settings.packages();
        for (ResolveInfo resolved : manager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)) {
            if (resolved.activityInfo == null || !resolved.activityInfo.enabled
                    || resolved.activityInfo.applicationInfo == null || !resolved.activityInfo.applicationInfo.enabled) continue;
            String name = resolved.activityInfo.packageName;
            if (!AiAppCatalog.supports(name, gemini) || !seen.add(name)) continue;
            String label = AiAppCatalog.GOOGLE.equals(name) ? context.getString(R.string.gemini_google_source)
                    : manager.getApplicationLabel(resolved.activityInfo.applicationInfo).toString();
            apps.add(new AppCatalog.Entry(name, label, AnimationStyle.Category.AI, null, null, selectedAi.contains(name)));
        }
        for (MessagingApps.Entry entry : MessagingApps.installed(context)) {
            String label = entry.source() == MessagingSource.SMS
                    ? context.getString(R.string.sms_source, entry.label()) : entry.source().label;
            apps.add(new AppCatalog.Entry(entry.packageName(), label, AnimationStyle.Category.MESSAGES,
                    entry.source(), null, settings.messagingSelected(entry.source())));
        }
        for (SocialApps.Entry entry : SocialApps.installed(context)) {
            apps.add(new AppCatalog.Entry(entry.packageName(), entry.source().label, AnimationStyle.Category.SOCIAL,
                    null, entry.source(), settings.socialSelected(entry.source())));
        }
        apps.sort(Comparator.comparing(AppCatalog.Entry::label, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(AppCatalog.Entry::id));
        return List.copyOf(apps);
    }

    static void select(AppSettings settings, AppCatalog.Entry app, boolean value) {
        if (app.messaging() != null) settings.selectMessaging(app.messaging(), value);
        else if (app.social() != null) settings.selectSocial(app.social(), value);
        else settings.select(app.packageName(), value);
    }
}
