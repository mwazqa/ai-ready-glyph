# Notify Glyph

An experimental notification companion for **Nothing Phone (3)**. Selected AI, messaging and social apps can trigger six-second animations on the rear Glyph Matrix.

**Current version: 0.13.4.** Previously Ready Glyph and AI Ready Glyph. Independent project; not affiliated with or endorsed by Nothing, OpenAI, Anthropic, Google, Discord, Meta, TikTok, X or other app providers.

The app uses Android notification metadata. It does not connect to AI accounts, read notification text or observe an AI app's internal task state. An app that sends no notification provides no signal to detect.

## Features

- ChatGPT strokes drawn from the inner junctions, a Claude bloom, a Gemini star growing from one pixel, and generic AI lettering.
- Twelve six-second animations with matched closing motion, on-screen previews and manual Glyph tests.
- A final 1.2-second closing sequence for each style: unwinding strokes, shrinking symbols, separating halves or reverse drawing. [Motion details](docs/animation-motion.md).
- One Apps screen with independent opt-in controls for supported AI, messaging and social sources.
- Discord halves that join and blink, a Messenger bolt that lights its bubble, a WhatsApp phone with signal waves, and bouncing SMS typing dots.
- Support for Instagram, TikTok, X and Facebook, with initially disabled source switches and fixed matching animations.
- An Instagram camera with a growing lens and flash, a TikTok note with echoes, traced X strokes and a drawn Facebook f.
- Fixed matching symbols for supported apps and independent notification switches.
- The rounded cards, dotted wordmark and typography from version 0.9.0, with a compact Apps summary on the home screen.
- Tap a symbol to play its on-screen preview and an immediate physical Glyph test together.
- A searchable animation library with All, AI, Messages and Social categories, showing at most four symbols per page.
- Circular Glyph previews with labels, text tabs with an active underline, and swipe navigation instead of framed tiles and arrow buttons.
- Adaptive two-symbol pages on narrow screens or with large text; page navigation also supports keyboard and accessibility actions.
- Search, categories, one All apps / Enabled only / Disabled only filter and Enable all / Disable all for the current app results, using compact rows with system app icons and full-height switches.
- A prominent Enable Glyph alerts power button: red when enabled, gray when paused. Turning it off dims and locks the other home controls while preserving app selections.
- Immediate source-selection saving and recycled native app rows for longer lists.
- No separate gallery, animation-assignment controls or duplicate preview and test buttons.
- Live notification and playback diagnostics, plus an optional Glyph Toy.
- Automatic animations for regular notifications from selected apps, duplicate suppression and Do Not Disturb handling.
- A six-second pause before automatic playback, followed by the complete six-second animation. Manual tests start immediately.
- A catalog of 40 package identities from 31 model providers, including regional and legacy identities. Only installed, enabled apps are listed.
- Automatic recognition of known Nothing models and a dynamic device label.
- An English interface and the original Spark adaptive icon, with staggered white dot clusters around a red centre.
- Rounded dark dialogs, adaptive action buttons and full-height switches in the app's red, black and white palette.
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

The [experimental v0.13.4 release](https://github.com/mwazqa/ai-ready-glyph/releases/tag/v0.13.4) provides a debug-signed APK for testing, a source archive, verification notes, interface reconstructions and SHA-256 checksums. This is a prerelease, not a production signing setup.

The Git source tree and source archive contain the Gradle wrapper but exclude the SDK AAR, signing keys, APKs and raw provider reference artwork. The APK includes Nothing SDK runtime components under the [separate SDK licence](app/libs/NOTHING-SDK-LICENSE.md); project MIT terms do not override those terms or the [third-party notices](THIRD_PARTY_NOTICES.md).

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

1. Install the experimental release APK or your own build, open it and enable Glyph Interface in Nothing OS. Enable Glyph alerts if the home controls are paused.
2. Open Manage apps and enable installed sources. Search, category tabs and the All apps / Enabled only / Disabled only filter help manage longer lists. Fresh installs select none; newly added sources also start disabled after an update.
3. Grant Notification access in Android Settings.
4. If sideloading blocks access, use App info's menu to allow restricted settings, then return to Notification access.
5. Enable notifications in the selected apps and initially turn Do Not Disturb off.
6. In Animations, choose a category or search, then tap a symbol to play it on screen and immediately on Glyph. Swipe horizontally for more symbols.
7. Trigger a new notification from a selected app. Automatic playback waits six seconds, then plays its full six-second animation.

Notification symbols are fixed. ChatGPT, Claude and Gemini use their own symbols; other AI providers use generic AI lettering. Discord, Messenger, WhatsApp, SMS, Instagram, TikTok, X and Facebook each use their matching symbol. Playing a preview does not change this mapping. Earlier custom assignments are ignored.

The optional **Gemini via Google** source appears when Gemini is installed and enabled. It covers the Google app as a whole, so other regular Google alerts also trigger the Gemini animation.

## Notifications and animations

Every regular notification from an app you select is eligible for an animation when Glyph alerts are enabled. This is the only notification mode: there is no phrase filter, custom-phrase field or Every regular notification switch. Old phrase and detection-mode preferences no longer affect behavior after an update.

Ongoing notifications, active progress and group summaries are skipped using Android metadata. Notification text and language are not analyzed. An ordinary alert unrelated to a completed AI task can also trigger an animation. Existing notifications are not replayed when the listener connects. AI sources retain their existing behavior: a notification key alerts once until removed or new progress appears. Messaging and social sources also accept a different posting timestamp on the same notification key. This detects posts, not unique message content; an app can post an update without a new message. A newer accepted alert replaces the pending or active animation and restarts the six-second pause rather than queueing a backlog.

SMS uses Android's default SMS package API. If that returns no package, the app falls back to the system-resolved SMS composer and checks that it is an enabled SMS handler. A system chooser is rejected; Android can resolve the sole available handler even without an explicit default. Its selection and fixed SMS symbol follow the SMS source when the resolved app changes. This covers that app's regular notifications, including RCS or other alerts; it does not inspect SMS messages or distinguish message transport. No SMS permissions are requested and the default SMS app is not changed. [Messaging sources](docs/messaging-sources.md).

Social supports the standard Instagram, TikTok, X and Facebook packages. Enable each app separately; all its regular notifications can trigger, including likes, mentions and direct messages. Lite and other regional editions are outside the initial list. [Social sources and verified identities](docs/social-sources.md).

Details reports whether a notification was ongoing, a group summary or actively progressing. Notification events and Glyph actions have independent timestamps including seconds. A previous manual test's Glyph status does not confirm that a later notification triggered an animation. These diagnostics do not store notification text.

Automatic alerts wait six seconds before starting, allowing time for the system notification effect. This is a fixed margin, not a measured bell duration or a callback from Nothing OS. The animation clock begins only after the wait and SDK connection, preserving its full six seconds including the matched closing sequence. Pause, source selection and Do Not Disturb are checked again before playback. Stopping or disconnecting the listener cancels pending playback. Manual tests and Glyph Button tests start immediately and replace a pending alert. Other AI apps use generic AI lettering. Messaging and social sources always use their matching symbols. Saved overrides from earlier versions are not read.

For the optional Toy, use Open Glyph Toys and add Notify Glyph. A short Glyph Button press cycles toys; holding it tests the selected style. Idle shows two dim dots. Automatic alerts also work without selecting the Toy. System effects and higher-priority Toys can override frames. An SDK call is not a physical LED reading.

## Privacy

No INTERNET permission, backend, analytics or account system. Android grants broad notification access, but unselected packages are rejected and the app does not extract notification titles or message bodies. Flags, progress metadata and messaging and social post timestamps are used for notification decisions.

Preferences store selected packages, source IDs, the automatic-alert setting and the last previewed style. Older phrase, mode and animation-override values may remain from previous builds but are not read. Diagnostics contain time, an app label and a result. Phone recognition reads public build fields, not unique device identifiers. [Privacy details](PRIVACY.md).

## Verification and development

Local 0.13.4 verification: **81 unit tests passed**, APK assembly passed, Android Lint had **0 errors and 2 existing warnings**. The warnings concern the pinned Gradle version and exported Glyph Toy service. Eleven controller simulations verify timing and playback states; nineteen settings checks verify fixed symbols, ignored old overrides, retained source selections, SMS role changes and independent manual previews. Catalog and gesture tests cover filtering, page boundaries, immutable selection changes, logical SMS identity, horizontal intent, direction and distance thresholds. Layout reconstructions use the actual six-second renderer sequences and support visual checks at 390 px and at 320 px with text at 200%. These are not Android instrumentation or phone screenshots. Native large-font rendering, TalkBack and physical notification-triggered LED output still need phone-side checks. [Interface flow](docs/interface.md) and [verification record](docs/verification.md).

GitHub Actions checks the catalog, wrapper, tests, build and lint. It retrieves the SDK from Nothing and does not upload APK artifacts.

For catalog changes, edit the JSON and run `python scripts/sync_ai_catalog.py`; commit the generated Java and manifest together. No catalog is fetched at runtime.

See [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md) and [CHANGELOG.md](CHANGELOG.md).

## Licensing

Original project code and original app identity artwork use [MIT](LICENSE), with [third-party exclusions](THIRD_PARTY_NOTICES.md).

Nothing's SDK has separate restrictive terms, including a commercial-use restriction. Provider marks and reference-derived visual data are not relicensed under MIT. The Claude reference's upstream redistribution licence has not been established. No blanket permission or brand approval is claimed. Review the notices before reusing or distributing these visuals.

- [Artwork provenance](artwork/README.md)
- [Nothing Glyph Matrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
- [Android notification listener](https://developer.android.com/reference/android/service/notification/NotificationListenerService)
