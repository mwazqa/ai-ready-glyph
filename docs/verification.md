# Verification record

Latest verified app build: 0.7.0, versionCode 16.

## Clean public-source build 0.6.4

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

## Glyph Toys preview 0.6.5

The Toy image metadata now references a dedicated Android vector rather than the launcher artwork. Its circular black background contains a monochrome 25 x 25 grid of 489 square pixels and a large white ready check. The area outside the circle is transparent. The system-supplied Toy badge is not baked into the artwork.

- Generated the SVG, Android vector and local PNG with `java scripts/BuildToyPreview.java`; repeat generation produced identical files.
- Inspected the PNG reconstruction and verified the SVG geometry and manifest reference. This is an asset preview, not a Nothing OS screenshot.
- Existing launcher assets and animation sources are unchanged.
- Catalog consistency passed: 40 package identities from 31 model providers.
- Ran `gradlew.bat --no-daemon testDebugUnitTest assembleDebug lintDebug`: assembly passed, 27 tests passed, 0 lint errors and the same two existing warnings.
- ADB reported no connected device. Appearance in the phone's Glyph Toys list still requires confirmation after installing this build.

## Red notification dot 0.6.6

Restored the red notification dot in the upper right of the circular Toy preview. The underlying grid and white ready check have identical geometry to 0.6.5. The dot and its black clearance stay entirely within the circular background.

- Regenerated and visually inspected the asset preview; repeat generation produced identical files.
- APK assembly, 27 unit tests and lint passed, with the same two existing lint warnings.
- Verified the red colour in the packaged Toy vector and the APK signature matching 0.6.5.
- Appearance in Nothing OS has not been checked on a phone for this build.

## Notification diagnostics 0.6.7

The combined skip message could not distinguish an ongoing notification, a group summary, active progress, busy text or a missing completion phrase. Details now reports these reasons separately. Glyph actions receive their own timestamps, including seconds, so a prior manual test can be distinguished from a later skipped notification.

- Existing completion rules, selected packages and opt-in relaxed mode are preserved.
- Added two tests covering structural rejection reasons and the distinction between busy text, a missing phrase and an accepted notification.
- Ran `gradlew.bat --no-daemon testDebugUnitTest assembleDebug lintDebug`: assembly passed, 29 tests passed, 0 lint errors and the same two existing warnings.
- Verified the diagnostic reason strings in the packaged DEX and the APK signature matching 0.6.6.
- Toy artwork, launcher assets, catalog identities and animation frames are unchanged.
- ADB reported no connected device. The exact cause of the reported Codex alert being skipped remains unknown without its fixed completion wording or the new diagnostic result. This build is a diagnostic improvement, not a confirmed physical-device fix.

## Regular notification mode 0.6.8

Regular alerts without a stock completion phrase could previously be rejected. A phone-side diagnostic confirmed the missing-phrase rejection for a ChatGPT / Codex notification. The phrase-dependent decision path is now removed: every regular notification from a selected app is eligible while Automatic animations is enabled.

- Removed the Ready detection card, its mode switch, custom phrases and notification-text extraction.
- Replaced CompletionPolicy with a metadata-only NotificationPolicy. It accepts regular alerts and separately reports ongoing notifications, group summaries and active progress.
- Old detection preferences are not read. Source selections, animation assignments, pause control, duplicate suppression, history seeding and Do Not Disturb handling retain their existing behavior.
- Replaced the obsolete phrase tests with four metadata-decision tests. Total: 28 tests passed, 0 failures and 0 errors.
- APK assembly passed; Android Lint reported 0 errors and the same two existing warnings.
- Checked the generated first-launch and configured layout reconstructions in the browser: the detection card is absent and the source, animation and Toy cards remain. This is a reconstruction, not an Android device screenshot.
- Verified the packaged NotificationPolicy, absence of obsolete phrase-rejection strings and detection controls, and APK signature matching 0.6.7.
- Toy preview with its red dot, launcher artwork, catalog identities and animation frames are unchanged.
- Actual notification delivery and physical Glyph output still need a fresh phone-side test after updating. This build removes the confirmed filter failure; it does not prove the phone rendered a frame.

## Dialogs and switches 0.6.9

All six in-app dialog paths use one themed builder and presentation helper. Dialogs have a dark surface, subtle border, clipped 28 dp corners, consistent titles, muted body text and rounded action buttons. The app picker has a rounded search field and a list height capped for smaller windows.

- Replaced thin switch tracks with 54 x 32 dp capsules and 28 dp white thumbs. Enabled tracks are red; disabled tracks are gray. Native Switch handles checked state, dragging, animation, focus and accessibility.
- Ran `gradlew.bat --no-daemon testDebugUnitTest assembleDebug lintDebug`: assembly passed, 28 tests passed, 0 lint errors and the same two existing warnings.
- Checked the shared dialog theme in packaged resources and all dialog call sites in source.
- Visually checked browser reconstructions of notification access, app selection and animation choices. The preview's switch states and search filter were exercised. These checks cover the reconstruction, not Android rendering or app behavior on a phone.
- Verified that notification handling, saved settings, the catalog, Glyph animations and artwork are unchanged.
- APK signature matches 0.6.8 for an in-place update; permissions are unchanged.
- ADB reported no connected device. Native dialog rendering, keyboard resizing, large font sizes and switch interaction on Nothing OS still require a phone-side check.

## Messaging expansion and Ready Glyph 0.7.0

The displayed app and Toy name is now Ready Glyph. Android application ID, notification-listener component, signing identity, AI preferences and existing artwork remain stable. Messaging selections start disabled and are stored separately from AI package selections.

- Added Discord, Messenger, WhatsApp and the current default SMS app, with exact package identities and a targeted SMS_DELIVER visibility query. No SMS permissions or all-packages visibility are requested.
- SMS selection and overrides follow its logical source ID when the default SMS app changes; unavailable or disabled apps cannot be selected in the Messages card.
- Messaging posts with different posting timestamps can reuse a notification key. Exact repeated timestamps and seeded history are suppressed; AI key deduplication remains unchanged. No message content is extracted.
- Added eight tests for source eligibility, source defaults, SMS role changes, initially empty selections, repost handling, clock changes and bounded metadata retention, plus one animation reveal/hold test. Total: 37 tests passed, no failures or errors.
- APK assembly passed; Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Compared all four AI animation sequences against 0.6.9 at 50 ms intervals: sampled pixel frames match exactly.
- Generated and inspected messaging symbol coverage and animation stages from the Java renderer. The symbol generator reproduces its Java coverage data and SVG atlas.
- Checked browser reconstructions of source cards, first-launch/configured layouts and animation choices. They are design reconstructions, not Android screenshots.
- Verified packaged version, app label, unchanged permission set, source visibility and APK signature matching 0.6.9.
- ADB reported no attached phone. Native rendering, retained settings/access on upgrade and real notification-triggered physical output still require device-side checks. Posting timestamps identify posts, not unique message contents.

## Messaging animation motion 0.7.1

The four messaging styles now use separate motion sequences. Discord halves join and blink, Messenger introduces the bolt before its bubble, WhatsApp draws its handset and sends two waves, and SMS uses staggered bouncing dots. All four settle on the original symbol before the final fade. Duration remains six seconds.

- Added five tests for final symbol fidelity, Discord eye restoration, Messenger bolt timing, WhatsApp waves and SMS dot ordering and movement. Total: 42 tests passed, no failures or errors.
- APK assembly passed; Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Compared all four AI animation sequences against 0.6.9 at 50 ms intervals: sampled pixel frames match exactly.
- Generated and inspected messaging stages and a GIF from the same Java renderer used by the app. The GIF adds a blank pause between six-second cycles.
- Verified the packaged version and permission set, with the APK signature matching 0.7.0 for an in-place update.
- Notification handling, source selection, catalog identities, static symbol coverage and app artwork are unchanged.
- No phone was attached for this verification. Motion quality and physical LED output still require a Phone (3) check.

## Dialog action clipping 0.7.2

A phone screenshot showed the rounded Close button clipped above its label in the animation assignment dialog. Action spacing now belongs to the button bar, and the outer footer reserves the full measured bar height. Native list height is capped using the available screen height, measured title and measured footer, leaving a vertical margin. Longer lists remain scrollable.

- Kept the existing rounded theme, labels, assignment behavior and button actions.
- Ran `gradlew.bat --no-daemon testDebugUnitTest assembleDebug lintDebug`: assembly passed, 42 tests passed, 0 lint errors and the same two existing warnings.
- Checked the assignment and animation-choice dialog reconstructions in the browser. Their action outlines and spacing are visible. This verifies the preview layout, not Nothing OS rendering.
- Verified the packaged version and APK signature matching 0.7.1. Notification behavior, preferences, artwork and all eight animation sources are unchanged.
- ADB reported no attached phone. Confirm the Close button on Nothing OS after updating, including with larger text or a longer app list.

## SMS detection fallback 0.7.3

Phone screenshots showed Google Messages selected in Android's default SMS settings while Ready Glyph reported no enabled default SMS app. The manifest already contained the SMS_DELIVER visibility query documented for the Telephony API. The exact failing API or package lookup was not captured on the phone.

- Keep the non-empty Telephony default result authoritative. When it is null or blank, resolve ACTION_SENDTO / smsto and validate that the resulting package belongs to an enabled SMS handler.
- Add the corresponding targeted package-visibility query to the manifest generator. Do not hardcode Google Messages or choose an arbitrary candidate. Reject the system chooser; Android can resolve a sole handler without an explicit default.
- Change the unavailable message to describe detection failure rather than assert that no default app exists.
- Added four tests for Google Messages and other handlers, switching between resolved handlers, chooser rejection and missing or disabled candidates. Total: 46 tests passed, no failures or errors.
- APK assembly passed; Android Lint reported 0 errors and the same two existing warnings. Catalog consistency passed.
- Verified the packaged visibility queries, fallback class and unchanged permission set. The APK signature matches 0.7.2.
- Existing SMS preferences, animation assignments, notification gates, dialog layout and all eight animation sources are unchanged. No SMS read, receive or send permissions are requested.
- ADB reported no attached phone. The revised detection path and a real SMS notification still require a Phone (3) check after updating; this is not a confirmed device-side result.

## Delayed automatic playback 0.7.4

The user accepts Nothing OS's bell effect but reported that it masks the beginning of the custom animation. Automatic requests now wait six seconds before starting playback. This is a conservative fixed margin, not a measurement of the system effect or a system completion callback. The animation clock starts only after the delay and SDK connection.

- Manual and Glyph Button tests remain immediate. A newer accepted notification replaces pending or active playback and restarts the wait.
- Stop, listener disconnect and failure cancel delayed callbacks. Pause, source selection and Do Not Disturb are checked again before starting, including after a late SDK connection.
- The wake-lock timeout covers the wait and connection phase; playback renews it for the animation. All stop and failure paths release it.
- Ran the actual controller and renderers in a local JVM simulation with simulated Android clock, Handler, settings, wake locks and Glyph service. Eleven scenarios passed: complete 120-frame playback after the wait, late connection, rapid replacement, immediate manual override, stop, pause, source removal, Do Not Disturb, Toy binding during the wait, replacement during connection and connection timeout. This harness is a local development check, not Android instrumentation or part of GitHub Actions.
- APK assembly and 46 existing unit tests passed. Android Lint reported 0 errors and the same two existing warnings.
- Verified the packaged delay/status text and unchanged permission set, with APK signature matching 0.7.3. Animation sources, SMS detection, catalog, assignment keys and notification eligibility are unchanged.
- ADB reported no attached phone. Confirm that the six-second margin outlasts the bell on the user's Nothing OS build and that the complete animation is visible. New system effects can still override Matrix frames.

## Social sources and animations 0.8.0

Added a separate Social card for the standard Instagram, TikTok, X and Facebook Android apps. Selections start disabled and use independent preferences. Existing AI and messaging selections and assignment keys are retained. The four targeted visibility queries add no permissions.

- Added four source tests for exact package identities, excluded variants, explicit selections and stable source/style IDs, plus five animation tests for reveal order, distinct motion and final symbol fidelity. Total: 55 tests passed, no failures or errors.
- APK assembly passed; Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Compared all eight existing animation sequences against 0.7.4 at 50 ms intervals: sampled pixel frames match exactly. The social symbol generator reproduces its Java coverage data and SVG atlas.
- Inspected social symbol coverage, animation stages and a GIF generated by the app's Java renderer. Instagram joins its camera corners and grows its lens; TikTok draws its note and sends two echoes; X joins diagonal strokes; Facebook draws its f and lights its crossbar.
- Checked browser reconstructions of the Social card, all twelve animation choices and scrollable assignment dialogs with complete action buttons. These previews are design reconstructions, not Android screenshots.
- Social alerts use the existing bounded messaging timestamp gate. AI deduplication, metadata exclusions, Do Not Disturb handling and the unchanged controller's six-second wait followed by six-second playback are retained. The local controller simulation still passes all eleven scenarios described for 0.7.4.
- Verified packaged version, social classes and queries, unchanged permissions and the APK signature matching 0.7.4 for an in-place update. The source ZIP excludes private references, caches, SDK binaries and signing keys.
- ADB reported no attached phone. Native layout, settings retention and real social notification-triggered physical output still require a Phone (3) check.

## Notify Glyph identity 0.8.1

The displayed app and Glyph Toy name is now Notify Glyph. The dot wordmark spells NOTIFY GLYPH. Launcher artwork, its monochrome variant and the circular Toys preview use an original N monogram with the red notification dot retained.

- APK assembly and all 55 unit tests passed. Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Checked the launcher and circular Toy PNGs generated alongside their Android vectors and SVGs. Both generators reproduce all five committed artwork assets exactly.
- Visually checked the new wordmark and labels in the browser layout reconstruction. This is not an Android screenshot.
- Verified the packaged app label, listener label, Toy strings, drawable resources and version. The APK signature matches 0.8.0 for an in-place update.
- All Java sources except DotWordmarkView, notification handling, source preferences, assignment keys, catalog, Android identity, manifest, theme and all twelve animation implementations are unchanged from 0.8.0. The six-second automatic wait and full six-second playback remain.
- ADB reported no attached phone. Launcher masking, themed icons, the system Glyph Toys list and retained settings still require a device-side check after updating.

## Signal icon 0.8.2

Replaced the N monogram with three white signal arcs spreading from a red source dot. Adaptive launcher artwork uses smooth rounded strokes; the circular Glyph Toys preview uses a corresponding 25 x 25 pixel symbol. The Notify Glyph name and header remain.

- APK assembly and all 55 existing unit tests passed. Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings. No icon-specific unit tests were added for this artwork change.
- Visually checked generated launcher and Toy previews. Both generators reproduce all five committed Android vector and SVG assets exactly.
- Verified the new vector path data in the packaged APK, adaptive colour and monochrome resources, version and unchanged permission set. The APK signature matches 0.8.1 for an in-place update.
- All runtime Java sources, manifest, theme, catalog, selected-source logic and all twelve animation implementations remain byte-identical to 0.8.1. The six-second wait and full six-second automatic playback are unchanged.
- Native launcher masks, themed icons and the system Glyph Toys list still require a device-side visual check.

## Notification-card icon 0.8.3

Replaced the signal arcs with a rounded notification card, two short content lines and a red unread dot. Adaptive colour and monochrome icons use smooth strokes; the circular Glyph Toys preview uses the same card as a pixel symbol.

- APK assembly and all 55 existing unit tests passed. Android Lint reported 0 errors and the same two existing warnings.
- Visually inspected generated launcher and Toy previews. Both generators reproduce all five committed Android vector and SVG assets exactly.
- Verified the actual path data of all three vectors inside the APK, version, labels and unchanged permissions. The APK signature matches 0.8.2 for an in-place update.
- Runtime Java sources, manifest, theme and catalog remain byte-identical to 0.8.2. Existing settings, source assignments, notification handling and all twelve animation implementations are unchanged.
- Native launcher masks, themed icons and the system Glyph Toys list still require a device-side visual check.

## Spark icon 0.8.4

Implemented the selected original concept A, with 24 staggered white dots around a red centre. The adaptive colour preview matches the selected concept PNG byte for byte. Monochrome artwork retains the same geometry; the circular Glyph Toys preview rasterizes the mark onto its 25 x 25 pixel grid.

- Visually checked launcher and Toy previews. Both generators reproduce all five committed Android vector and SVG assets exactly.
- APK assembly and all 55 existing unit tests passed. Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Verified the actual path data of all three vectors inside the APK, packaged version, labels and unchanged permissions. The APK signature matches 0.8.3 for an in-place update.
- Runtime Java sources, manifest, theme, catalog and all twelve animation implementations remain byte-identical to 0.8.3. Source choices, assignments, notification handling and the six-second wait followed by full six-second playback are retained.
- Native launcher masks, themed icons and the system Glyph Toys list still require a device-side visual check.

## Matched closing motion 0.9.0

Every style now closes during the final 1200 ms of its existing six-second playback. The initial 4800 ms remain unchanged. Closing paths reuse the symbols' drawing order, with dedicated contraction for Claude, separating Discord halves and staged SMS dot withdrawal.

- All twelve opening/hold sequences match 0.8.4 at every 50 ms sample through 4800 ms.
- Added eight tests covering non-uniform geometric change for every style, the final central point, ChatGPT's inward retreat, Claude/Gemini contraction, Discord separation, Messenger's bolt, SMS ordering, generic lettering and social drawing order. Total: 63 tests passed, no failures or errors.
- APK assembly passed. Android Lint reported 0 errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Inspected renderer snapshots and generated GIFs of the full cycles and closing sections. These are renderer previews, not phone recordings.
- The local simulation of the unchanged controller passed all eleven timing scenarios with the new renderer. This is not Android instrumentation or part of GitHub Actions.
- Verified the packaged closing class, version, unchanged permission set and signature matching 0.8.4. Notification handling, source settings, catalog, symbol coverage and Spark artwork remain unchanged.
- Physical LED motion, native rendering and competition with Nothing OS effects still need a phone-side check.

## Physical-device boundary

No ADB device was attached for the current verification. Reported Build values, native UI rendering, current Nothing OS binding and physical LEDs have not been checked on a phone for this build. Catalog inclusion does not confirm completion-notification behavior for every AI app.

On Phone (3), verify the detected device label, retained settings, a manual Glyph test and a real completion notification. Test screen-off operation and system restrictions separately. Unknown or unsupported phones should show a compatibility status without contacting the Glyph SDK.

Recognition identifies reported hardware, not the availability of a compatible OS service. Phone (4a) Pro recognition does not add its renderer or AOD Toy support. See [device recognition](device-recognition.md) and the [catalog review](ai-app-catalog-research.md).
