# Social sources

Ready Glyph 0.8.0 adds a separate Social section with four initially disabled sources. Only installed, enabled standard apps can be selected.

| Source | Android package | Default animation | Official distribution |
| --- | --- | --- | --- |
| Instagram | `com.instagram.android` | Camera outline, growing lens and flash | [Google Play](https://play.google.com/store/apps/details?id=com.instagram.android) |
| TikTok | `com.zhiliaoapp.musically` | Musical note drawn upwards with soft echoes | [Google Play](https://play.google.com/store/apps/details?id=com.zhiliaoapp.musically) |
| X | `com.twitter.android` | Diagonal strokes meeting at the centre | [Google Play](https://play.google.com/store/apps/details?id=com.twitter.android) |
| Facebook | `com.facebook.katana` | Drawn f with a crossbar pulse | [Google Play](https://play.google.com/store/apps/details?id=com.facebook.katana) |

Package identities were checked against these publisher listings on 2026-10-22. Lite editions, other regional TikTok identities and unofficial clients are outside this initial list. Package matching does not verify an app's signing identity.

All regular notifications from a selected app are eligible, including direct messages, likes, mentions and other alerts. Ongoing notifications, active progress and group summaries are excluded using metadata. Notification text and language are not read or matched. Availability of an app in this list does not guarantee that every activity in that app produces a notification.

Social and messaging sources share the bounded posting-timestamp gate: a different posting time can trigger another animation on the same notification key. Repeated timestamps and existing notifications seeded at listener connection are suppressed. This identifies posts, not unique messages, and notification-only updates can also trigger playback.

Automatic playback waits six seconds, then starts the complete six-second animation. A newer accepted alert replaces pending or active playback and restarts the wait. Manual previews and tests remain immediate. Notification access, Automatic animations and Do Not Disturb handling are shared with the other sections.

Social selections use a separate `social_sources` preference and start empty, including on upgrade. Existing AI and messaging choices are retained. Animation overrides use `provider:social:<source-id>` keys. Missing or disabled apps cannot be enabled; saved choices remain available if an app is reinstalled. The manifest adds four explicit package queries without all-package visibility or new permissions.

These are project-drawn brand-identifying pixel adaptations with the same brightness range, circular 25 x 25 boundary and final fade as the existing styles. All twelve styles are available as overrides. See [artwork provenance](../artwork/README.md) and [third-party notices](../THIRD_PARTY_NOTICES.md).
