# Messaging sources

Ready Glyph 0.7.0 adds four independent, initially disabled sources alongside the existing AI catalog.

| Source | Android identity | Default animation |
| --- | --- | --- |
| Discord | `com.discord` | Stylized Discord symbol |
| Messenger | `com.facebook.orca` | Messenger bubble and bolt |
| WhatsApp | `com.whatsapp` | WhatsApp phone and bubble |
| SMS | Android default SMS package, with a system-resolved SMS composer fallback | Message bubble with three dots |

Official distribution identities: [Discord](https://play.google.com/store/apps/details?id=com.discord), [Messenger](https://play.google.com/store/apps/details?id=com.facebook.orca), [WhatsApp](https://play.google.com/store/apps/details?id=com.whatsapp).

In 0.7.1, Discord joins from two halves and blinks. Messenger draws its bolt before lighting the bubble, then sends a light streak through the bolt. WhatsApp draws its handset, closes the bubble from the bottom upwards and sends two soft signal waves. SMS draws a bubble and introduces three bouncing typing dots. Each sequence settles on its existing symbol and fades within the same six-second duration.

The Messages card shows the four sources. Uninstalled or disabled apps cannot be enabled. SMS displays the current app label; its control is unavailable when no enabled SMS app can be resolved. SMS selection and animation overrides use a logical source ID and follow a change of resolved SMS app. Saved source choices remain available after reinstalling a messaging app.

Android's [default SMS package API](https://developer.android.com/reference/android/provider/Telephony.Sms#getDefaultSmsPackage(android.content.Context)) requires an SMS_DELIVER package-visibility query. Since 0.7.3, a null or blank result triggers a [resolveActivity](https://developer.android.com/reference/android/content/pm/PackageManager#resolveActivity(android.content.Intent,%20int)) fallback for ACTION_SENDTO with a smsto URI. The resolved package must also appear among enabled SMS handlers. The system chooser and an unresolved result are rejected instead of choosing the first installed app. Android may resolve the sole available handler even without an explicitly selected default; this fallback is not a direct SMS-role-holder lookup.

The manifest contains SMS_DELIVER and SENDTO / smsto visibility queries and the three explicit messenger packages. It does not request READ_SMS, RECEIVE_SMS, SEND_SMS or QUERY_ALL_PACKAGES. Resolution does not launch the composer, send a message, change the default SMS app or access SMS storage. No SMS app package is hardcoded as a fallback.

These sources observe regular notifications from the selected package. SMS can therefore include RCS and other alerts from the default messaging app. Calls or service alerts can trigger if they are regular notifications; ongoing, progress and group-summary notifications are excluded. No message text, sender, conversation contents or transport type is inspected.

Messaging sources use [notification posting timestamps](https://developer.android.com/reference/android/service/notification/StatusBarNotification#getPostTime()) to distinguish another post on the same notification key from the same callback. A different timestamp permits another animation. This does not establish that the underlying message is new; notification-only updates can also animate. Existing active notifications are seeded when the listener connects, and retained metadata is bounded to 512 keys. Automatic playback waits six seconds, then starts its full six-second animation. A newer accepted notification replaces pending or active playback and restarts the wait. Manual tests remain immediate.

AI notification-key deduplication, the 40-identity AI catalog, source preferences and the four existing AI animations retain their prior behavior. AI, Messages and [Social](social-sources.md) share Automatic animations, Notification access and Do Not Disturb handling. Any selected app can receive an animation override.

The displayed app name is Ready Glyph. The original application ID and service component names remain stable so an APK signed with the same key updates the existing installation and keeps its access grant and preferences.
