# Messaging sources

Ready Glyph 0.7.0 adds four independent, initially disabled sources alongside the existing AI catalog.

| Source | Android identity | Default animation |
| --- | --- | --- |
| Discord | `com.discord` | Stylized Discord symbol |
| Messenger | `com.facebook.orca` | Messenger bubble and bolt |
| WhatsApp | `com.whatsapp` | WhatsApp phone and bubble |
| SMS | Current default SMS package reported by Android | Message bubble with three dots |

Official distribution identities: [Discord](https://play.google.com/store/apps/details?id=com.discord), [Messenger](https://play.google.com/store/apps/details?id=com.facebook.orca), [WhatsApp](https://play.google.com/store/apps/details?id=com.whatsapp).

The Messages card shows the four sources. Uninstalled or disabled apps cannot be enabled. SMS displays the current app label; its control is unavailable when no enabled default SMS app can be resolved. SMS selection and animation overrides use a logical source ID and follow a change of default SMS app. Saved source choices remain available after reinstalling a messaging app.

Android's [default SMS package API](https://developer.android.com/reference/android/provider/Telephony.Sms#getDefaultSmsPackage(android.content.Context)) requires an SMS_DELIVER package-visibility query. The manifest contains that query and the three explicit messenger packages. It does not request READ_SMS, RECEIVE_SMS, SEND_SMS or QUERY_ALL_PACKAGES. The app does not change the default SMS app or access SMS storage.

These sources observe regular notifications from the selected package. SMS can therefore include RCS and other alerts from the default messaging app. Calls or service alerts can trigger if they are regular notifications; ongoing, progress and group-summary notifications are excluded. No message text, sender, conversation contents or transport type is inspected.

Messaging sources use [notification posting timestamps](https://developer.android.com/reference/android/service/notification/StatusBarNotification#getPostTime()) to distinguish another post on the same notification key from the same callback. A different timestamp permits another animation. This does not establish that the underlying message is new; notification-only updates can also animate. Existing active notifications are seeded when the listener connects, and retained metadata is bounded to 512 keys. Rapid notifications restart the active animation.

AI notification-key deduplication, the 40-identity AI catalog, source preferences and the four existing AI animations retain their prior behavior. Both categories share Automatic animations, Notification access and Do Not Disturb handling. Any selected app can receive an animation override.

The displayed app name is Ready Glyph. The original application ID and service component names remain stable so an APK signed with the same key updates the existing installation and keeps its access grant and preferences.
