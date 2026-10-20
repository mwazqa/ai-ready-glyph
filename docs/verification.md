# Verification record

Date: 21 October 2026. App version: 0.6.4, versionCode 10.

## Clean public-source build

An archive of the initial Git source tree was extracted into a fresh verification directory. It contained no local.properties, SDK AAR, signing key or raw provider reference files. The setup script fetched the pinned Nothing SDK from its official source and verified its SHA-256. Android SDK paths and the local development signing key were supplied outside the source tree.

Commands checked:

```text
scripts/setup.ps1
python scripts/sync_ai_catalog.py --check
gradlew.bat --no-daemon testDebugUnitTest assembleDebug lintDebug
```

- APK assembly passed.
- 27 unit tests passed, with 0 failures and 0 errors.
- Android Lint: 0 errors, 2 existing warnings: AndroidGradlePluginVersion and ExportedService.
- Catalog consistency passed: 40 package identities from 31 model providers.
- The Gradle wrapper JAR matched the official Gradle 8.11.1 wrapper checksum.
- The reviewed public file list excluded SDK binaries, local configuration, signing material, APKs, raw provider references and development caches.
- Text checks found no local user paths, common credential patterns, disallowed long dashes or broken local Markdown links in the reviewed files. This is a scoped check, not a security certification.

Unit cases cover notification matching and duplicate handling, catalog eligibility, animation frame boundaries and timing, known phone identities, regional and Pro variants, unknown devices, lookalikes and locale-independent matching.

## Physical-device boundary

No ADB device was attached for the current verification. Reported Build values, native UI rendering, current Nothing OS binding and physical LEDs have not been checked on a phone for this build. Catalog inclusion does not confirm completion-notification behavior for every AI app.

On Phone (3), verify the detected device label, retained settings, a manual Glyph test and a real completion notification. Test screen-off operation and system restrictions separately. Unknown or unsupported phones should show a compatibility status without contacting the Glyph SDK.

Recognition identifies reported hardware, not the availability of a compatible OS service. Phone (4a) Pro recognition does not add its renderer or AOD Toy support. See [device recognition](device-recognition.md) and the [catalog review](ai-app-catalog-research.md).
