# Ready Glyph

An experimental notification companion for **Nothing Phone (3)**. Selected AI apps, Discord, Messenger, WhatsApp and your default SMS app can trigger six-second animations on the rear Glyph Matrix.

**Current version: 0.7.2.** Previously AI Ready Glyph. Independent project; not affiliated with or endorsed by Nothing, OpenAI, Anthropic, Google, Discord, Meta or other app providers.

The app uses Android notification metadata. It does not connect to AI accounts, read notification text or observe an AI app's internal task state. An app that sends no notification provides no signal to detect.

## Features

- ChatGPT strokes drawn from the inner junctions, a Claude bloom, a Gemini star growing from one pixel, and generic AI lettering.
- Eight six-second animations, on-screen previews and manual Glyph tests.
- Separate AI apps and Messages sections, with independent opt-in controls for Discord, Messenger, WhatsApp and SMS.
- Discord halves that join and blink, a Messenger bolt that lights its bubble, a WhatsApp phone with signal waves, and bouncing SMS typing dots.
- Per-app animation overrides and an optional Glyph Toy.
- Automatic animations for regular notifications from selected apps, duplicate suppression and Do Not Disturb handling.
- A picker of 40 package identities from 31 model providers, including regional and legacy identities.
- Automatic recognition of known Nothing models and a dynamic device label.
- An English interface, original dot lettering and an original adaptive launcher icon.
- Rounded dark dialogs, pill buttons and full-height switches in the app's red, black and white palette.
- A separate circular 25 x 25 pixel preview with a red notification dot for the Glyph Toys list.

Catalog inclusion is **not confirmation of working completion notifications**. Read the [catalog research](docs/ai-app-catalog-research.md) and [exact identities](docs/ai-app-catalog.json).

## Compatibility

| Device / requirement | Current support |
| --- | --- |
| Nothing Phone (3), A024 | Physical Glyph animations |
| Android | Android 15 / API 35 or newer |
| Nothing OS | Must provide the required Glyph Matrix APIs; Nothing documents app rendering for system builds from 20250801 |
| Other Nothing phones | Known models are identified; physical animations are disabled |
| Phone (4a) Pro | Recognized separately; its smaller Matrix renderer and AOD Toy integration are not implemented |
| Unknown / other phones | Reported identity and unsupported status; on-screen preview remains available |

Recognition does not prove that a compatible OS service is installed or enabled. Custom ROM branding can affect detection. [Device recognition details](docs/device-recognition.md).

The app keeps its original Android application ID, notification-listener component, settings keys and signing identity for in-place updates. The repository URL remains unchanged. [Naming check](docs/name-check.md).

## Build

The repository contains source code and the Gradle wrapper. **No SDK binary, APK, signing key or raw provider reference artwork is distributed here.** This initial publication has no public binary release.

Requirements: JDK 17 or 21, Android SDK Platform 35, Build Tools 35.0.0, PowerShell and Python 3. Android Studio is optional.

1. Clone this repository.
2. Read the [Nothing SDK licence](app/libs/NOTHING-SDK-LICENSE.md), including its redistribution and commercial-use restrictions.
3. Fetch the pinned SDK directly from Nothing:

   ```powershell
   ./scripts/setup.ps1
   ```

4. Set your Android SDK path through `ANDROID_HOME` / `ANDROID_SDK_ROOT` or an ignored local `local.properties` file. If needed, install tools with `sdkmanager "platforms;android-35" "build-tools;35.0.0"`.
5. Check, test and build:

   ```powershell
   python scripts/sync_ai_catalog.py --check
   ./gradlew.bat testDebugUnitTest assembleDebug lintDebug
   ```

On macOS / Linux, use `pwsh ./scripts/setup.ps1` and `bash ./gradlew testDebugUnitTest assembleDebug lintDebug`.

Output: `app/build/outputs/apk/debug/app-debug.apk`.

AGP 8.10.1, Gradle 8.11.1, its distribution checksum and the Nothing SDK commit / checksum are pinned. Builds use your own local debug key. Another developer's APK cannot update an installation signed with a different key.

## First run

1. Install your build, open it and enable Glyph Interface in Nothing OS.
2. Choose installed AI apps and enable messaging sources under Messages. Fresh installs select none; messaging sources also start disabled after an update.
3. Grant Notification access in Android Settings.
4. If sideloading blocks access, use App info's menu to allow restricted settings, then return to Notification access.
5. Enable notifications in the selected apps and initially turn Do Not Disturb off.
6. Run a manual Glyph test.
7. Send a new message to a selected messaging app, or start an AI task and wait for its notification with that app in the background.

The optional **Gemini via Google** source appears when Gemini is installed and enabled. It covers the Google app as a whole, so other regular Google alerts also trigger animations. Assign the Gemini style manually if desired.

## Notifications and animations

Every regular notification from an app you select is eligible for an animation when Automatic animations is enabled. This is the only notification mode: there is no phrase filter, custom-phrase field or Every regular notification switch. Old phrase and detection-mode preferences no longer affect behavior after an update.

Ongoing notifications, active progress and group summaries are skipped using Android metadata. Notification text and language are not analyzed. An ordinary alert unrelated to a completed AI task can also trigger an animation. Existing notifications are not replayed when the listener connects. AI sources retain their existing behavior: a notification key alerts once until removed or new progress appears. Messaging sources also accept a different posting timestamp on the same notification key, so later posts for a conversation can trigger another animation. This detects posts, not unique message content; an app can post an update without a new message. Rapid alerts restart the current animation rather than queueing a backlog.

SMS follows the phone's currently enabled default SMS app. Its selection and animation assignment follow the SMS source when the default app changes. This covers that app's regular notifications, including RCS or other alerts; it does not inspect SMS messages or distinguish message transport. No SMS permissions are requested and the default SMS app is not changed. [Messaging sources](docs/messaging-sources.md).

Details reports whether a notification was ongoing, a group summary or actively progressing. Notification events and Glyph actions have independent timestamps including seconds. A previous manual test's Glyph status does not confirm that a later notification triggered an animation. These diagnostics do not store notification text.

Automatic alerts respect Do Not Disturb. Manual tests run on demand. Every animation lasts six seconds including a final fade. Other AI apps use generic AI lettering unless assigned another style. The four messaging sources have their own defaults; any selected source can use an animation override.

For the optional Toy, use Open Glyph Toys and add Ready Glyph. A short Glyph Button press cycles toys; holding it tests the selected style. Idle shows two dim dots. Automatic alerts also work without selecting the Toy. System effects and higher-priority Toys can override frames. An SDK call is not a physical LED reading.

## Privacy

No INTERNET permission, backend, analytics or account system. Android grants broad notification access, but unselected packages are rejected and the app does not extract notification titles or message bodies. Flags, progress metadata and messaging post timestamps are used for notification decisions.

Preferences store selected packages, assignments and settings. Older unused phrase and mode values may remain from previous builds but are not read. Diagnostics contain time, an app label and a result. Phone recognition reads public build fields, not unique device identifiers. [Privacy details](PRIVACY.md).

## Verification and development

Local 0.7.2 verification: **42 unit tests passed**, APK assembly passed, Android Lint had **0 errors and 2 existing warnings**. The warnings concern the pinned Gradle version and exported Glyph Toy service. Existing AI animation sequences match the 0.6.9 baseline at 50 ms samples. Messaging routing, repost handling and animation phases have unit coverage. Current native UI and real notification-triggered LED output still need physical-device checks. [Verification record](docs/verification.md).

GitHub Actions checks the catalog, wrapper, tests, build and lint. It retrieves the SDK from Nothing and does not upload APK artifacts.

For catalog changes, edit the JSON and run `python scripts/sync_ai_catalog.py`; commit the generated Java and manifest together. No catalog is fetched at runtime.

See [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md) and [CHANGELOG.md](CHANGELOG.md).

## Licensing

Original project code and original app identity artwork use [MIT](LICENSE), with [third-party exclusions](THIRD_PARTY_NOTICES.md).

Nothing's SDK has separate restrictive terms, including a commercial-use restriction. Provider marks and reference-derived visual data are not relicensed under MIT. The Claude reference's upstream redistribution licence has not been established. No blanket permission or brand approval is claimed. Review the notices before reusing or distributing these visuals.

- [Artwork provenance](artwork/README.md)
- [Nothing Glyph Matrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
- [Android notification listener](https://developer.android.com/reference/android/service/notification/NotificationListenerService)
