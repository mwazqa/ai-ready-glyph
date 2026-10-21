package pl.glyphtoy.aiready;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public final class AiNotificationListener extends NotificationListenerService {
    private final NotificationGate gate = new NotificationGate();

    @Override public void onListenerConnected() {
        LocalStatus.listenerConnected = true;
        StatusBarNotification[] existing = getActiveNotifications();
        if (existing != null) for (StatusBarNotification item : existing) {
            if (!item.isOngoing() && !hasProgress(item.getNotification())) gate.seed(item.getKey());
        }
    }

    @Override public void onListenerDisconnected() {
        LocalStatus.listenerConnected = false;
        MatrixController.get(this).stop();
    }

    @Override public void onNotificationPosted(StatusBarNotification item) {
        AppSettings settings = new AppSettings(this);
        if (!settings.enabled() || !settings.packages().contains(item.getPackageName())) return;
        Notification notification = item.getNotification();
        boolean progress = hasProgress(notification);
        if (progress || item.isOngoing()) gate.progress(item.getKey());
        NotificationPolicy.Result result = NotificationPolicy.evaluate(item.isOngoing(),
                (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0, progress);
        String label = label(item.getPackageName());
        if (result != NotificationPolicy.Result.ACCEPTED) {
            LocalStatus.event(label + " · skipped: " + result.reason + ".");
            return;
        }
        if (!gate.accept(item.getKey(), true)) return;
        NotificationManager system = getSystemService(NotificationManager.class);
        if (system.getCurrentInterruptionFilter() != NotificationManager.INTERRUPTION_FILTER_ALL) {
            LocalStatus.event(label + " · skipped: Do Not Disturb.");
            return;
        }
        AiProvider provider = settings.providerFor(item.getPackageName());
        LocalStatus.event(label + " · notification · animation: " + provider.label + ".");
        MatrixController.get(this).play(provider);
    }

    @Override public void onNotificationRemoved(StatusBarNotification item) { gate.remove(item.getKey()); }

    @Override public void onDestroy() {
        LocalStatus.listenerConnected = false;
        MatrixController.get(this).stop();
        super.onDestroy();
    }

    private static boolean hasProgress(Notification notification) {
        Bundle extras = notification.extras;
        int maximum = extras.getInt(Notification.EXTRA_PROGRESS_MAX, 0);
        return extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false)
                || (maximum > 0 && extras.getInt(Notification.EXTRA_PROGRESS, 0) < maximum);
    }

    private String label(String packageName) {
        try { return getPackageManager().getApplicationLabel(
                getPackageManager().getApplicationInfo(packageName, 0)).toString(); }
        catch (PackageManager.NameNotFoundException ignored) { return packageName; }
    }
}
