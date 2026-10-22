# Privacy

Notify Glyph has no INTERNET permission, backend, analytics, advertisements or crash-reporting service.

Android grants broad notification-listener access. The listener rejects unselected source packages and examines only notification flags, progress metadata and posting timestamps for the apps you select. It does not extract notification titles, message bodies or conversation text.

SharedPreferences store selected AI packages, messaging and social source IDs, animation assignments and settings. SMS uses Android's default SMS package API, with a system-resolved SMS composer fallback when no package is returned. The fallback accepts only an enabled SMS handler and rejects the system chooser; Android can resolve its sole available handler without an explicit default. The app does not read the SMS database, launch the composer, send messages or request SMS permissions. Messaging and social deduplication share at most 512 notification keys and posting timestamps in memory, without extracting sender names or message contents. Older builds may retain unused custom-phrase and detection-mode preferences; this version does not read or use them. App backup is disabled. In-memory diagnostics contain time, an app label and a result, without notification titles, message bodies or conversation history.

Phone recognition reads public manufacturer, brand and model fields. It does not read an IMEI, serial number or Android ID.

Revoke notification access or clear app data in Android Settings. Uninstalling removes app data. These statements describe Notify Glyph, not the apps whose notifications it observes.
