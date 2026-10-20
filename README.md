# AI Ready Glyph

An experimental notification companion for **Nothing Phone (3)**. A matching notification from a selected AI app triggers a six-second animation on the rear Glyph Matrix.

**Current version: 0.6.4.** Independent project; not affiliated with or endorsed by Nothing, OpenAI, Anthropic, Google or other AI providers.

The app reads Android notifications. It does not connect to AI accounts or observe an AI app's internal task state. An app that sends no completion notification provides no signal to detect.

## Features

- ChatGPT strokes drawn from the inner junctions, a Claude bloom, a Gemini star growing from one pixel, and generic AI lettering.
- Four six-second animations, on-screen previews and manual Glyph tests.
- Per-app animation overrides and an optional Glyph Toy.
- English / Polish completion phrases, custom phrases, duplicate suppression and Do Not Disturb handling.
- A picker of 40 package identities from 31 model providers, including regional and legacy identities.
- Automatic recognition of known Nothing models and a dynamic device label.
- An English interface, original dot lettering and an original adaptive launcher icon.

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
2. Choose the installed AI apps to monitor. Fresh installs select none.
3. Grant Notification access in Android Settings.
4. If sideloading blocks access, use App info's menu to allow restricted settings, then return to Notification access.
5. Enable notifications in the selected AI apps and initially turn Do Not Disturb off.
6. Run a manual Glyph test.
7. Start an AI task, leave the AI app and wait for an actual ready notification.

The optional **Gemini via Google** source appears when Gemini is installed and enabled. It covers the Google app as a whole, so unrelated Google alerts can also match. Assign the Gemini style manually if desired.

## Detection and animations

Default rules match phrases such as `response is ready`, `task completed` and `odpowiedź jest gotowa`. Enter phrases from a real ready notification under Custom phrases, separated by semicolons or new lines.

**Every regular notification** also accepts answer-only notifications, but may react to unrelated alerts. Neither mode interprets answers semantically. Ongoing work, active progress, group summaries and recognized busy phrases are skipped. Existing notifications are not replayed when the listener connects. A notification key alerts once until removed or new progress appears.

Automatic alerts respect Do Not Disturb. Manual tests run on demand. Every animation lasts six seconds including a final fade. Other apps use generic AI lettering unless assigned another style.

For the optional Toy, use Open Glyph Toys and add AI Ready Glyph. A short Glyph Button press cycles toys; holding it tests the selected style. Idle shows two dim dots. Automatic alerts also work without selecting the Toy. System effects and higher-priority Toys can override frames. An SDK call is not a physical LED reading.

## Privacy

No INTERNET permission, backend, analytics or account system. Android grants broad notification access, but unselected packages are rejected before text extraction. Selected text is matched in memory and not stored or sent.

Preferences store selected packages, custom phrases, assignments and settings. Diagnostics contain time, an app label and a result. Phone recognition reads public build fields, not unique device identifiers. [Privacy details](PRIVACY.md).

## Verification and development

Local 0.6.4 verification: **27 unit tests passed**, APK assembly passed, Android Lint had **0 errors and 2 existing warnings**. The warnings concern the pinned Gradle version and exported Glyph Toy service. Current recognition, native UI and real LED output still need physical-device checks. [Verification record](docs/verification.md).

GitHub Actions checks the catalog, wrapper, tests, build and lint. It retrieves the SDK from Nothing and does not upload APK artifacts.

For catalog changes, edit the JSON and run `python scripts/sync_ai_catalog.py`; commit the generated Java and manifest together. No catalog is fetched at runtime.

See [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md) and [CHANGELOG.md](CHANGELOG.md).

## Licensing

Original project code and original app identity artwork use [MIT](LICENSE), with [third-party exclusions](THIRD_PARTY_NOTICES.md).

Nothing's SDK has separate restrictive terms, including a commercial-use restriction. Provider marks and reference-derived visual data are not relicensed under MIT. The Claude reference's upstream redistribution licence has not been established. No blanket permission or brand approval is claimed. Review the notices before reusing or distributing these visuals.

- [Artwork provenance](artwork/README.md)
- [Nothing Glyph Matrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
- [Android notification listener](https://developer.android.com/reference/android/service/notification/NotificationListenerService)
