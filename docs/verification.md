# Verification record

Latest verified app build: 0.13.4, versionCode 35.

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

## Home-screen UX and animation gallery 0.10.0

The home screen now keeps AI apps, Messages and Social in a fixed order. Each installed app has an opt-in switch and an inline animation choice. A separate gallery provides style selection, previews and manual Glyph tests. Contextual setup actions replace the always-prominent manual test during setup. Diagnostics and the optional Toy follow the source categories.

- APK assembly and 65 unit tests passed with no failures or errors. Android Lint: 0 errors, the same two existing warnings AndroidGradlePluginVersion and ExportedService.
- Two new unit tests cover countdown rounding, phase deadlines, nonnegative remaining time and availability of Stop animation.
- Eleven controller timing simulations passed using the actual controller with simulated Android and Glyph services. The simulations also check waiting, connecting, playing, completion, cancellation and timeout states.
- Nine preference regression checks passed with the actual AppSettings class and simulated Android preferences: existing selections, old source keys, SMS role changes, explicit/default choices, assignment without enabling a source and gallery choices independent from app assignments.
- The generated layout reconstruction was checked at 390 px and at 320 px with text at 200%. Its visible content had no horizontal overflow; the animation dialog kept its footer inside its bounds while the choices scrolled.
- The reconstruction confirmed separate app assignment and gallery selection flows. This checks the design preview, not the Android event handlers or native rendering.
- Source checks confirm that notification eligibility, existing preference keys, animation sequences, identity artwork and requested permissions are unchanged. The additional gallery Activity is not exported.
- Phone-side native rendering, large-font behavior, TalkBack focus and physical Glyph output remain unverified for this build.

## Graphic previews and fixed symbols 0.11.0

The home screen now uses large source-preview tiles and an independent notification switch per installed app. Tools contains physical tests, diagnostics and the optional Toy. The gallery shows all twelve symbols as large tiles; tapping a symbol opens a preview-only dialog.

- Removed notification-animation assignment APIs, controls and reads of older stored overrides. Existing source selections and the original preference store remain.
- ChatGPT, Claude, Gemini, each supported messaging source and each supported social source use their fixed matching symbols. Other AI providers use generic AI lettering. Gemini via Google now uses Gemini.
- APK assembly and 65 unit tests passed with no failures or errors. Android Lint has zero errors and the same two existing warnings.
- Nineteen checks with actual AppSettings and simulated Android preferences verify fixed symbols despite old overrides, retained source selections, SMS package changes and independent manual previews.
- Eleven controller simulations passed with simulated Android and Glyph services. Automatic delay, full animation timing, cancellation and replacement retain their behavior.
- The generated layout reconstruction uses complete sequences from the actual renderer at 50 ms intervals. Home and gallery were checked at 390 px and at 320 px with text at 200%. The large-text gallery has no horizontal overflow; its preview scrolls while full-width Close and Test on Glyph actions remain inside the dialog.
- Native action buttons stack when measured widths exceed available space. The preview scroll height reserves the measured title and footer. These source changes are not a native-device layout test.
- Dialog previews finish their matched closing motion and remain cleared until replayed. Source thumbnails restore the static symbol after an additional 250 ms blank pause. Physical playback is unchanged.
- Catalog, notification eligibility, requested permissions, physical animation frames and Spark identity artwork remain unchanged.
- Native rendering, TalkBack and physical Glyph output require phone-side confirmation.

## Restored design and direct previews 0.11.1

The visual shell, source controls and configured/setup section order come from 0.9.0. The Animations card now contains twelve direct previews. Tapping plays on screen; holding a symbol tests it on a supported Glyph Matrix. The separate gallery, preview dialogs, assignment controls and redundant playback buttons have been removed.

- APK assembly and 65 unit tests passed with no failures or errors. Android Lint: zero errors and the two existing AndroidGradlePluginVersion and ExportedService warnings.
- Nineteen checks with the actual AppSettings and simulated Android preferences passed, including fixed mappings, ignored old overrides, retained source selections and independent manual previews.
- Eleven controller simulations passed. The automatic six-second wait, full six-second playback, replacement and cancellation behavior are unchanged.
- The notification policy, physical frame renderer, catalog and Spark artwork remain unchanged from the preceding build.
- The browser reconstruction contains actual renderer sequences at 50 ms intervals. It provides configured/setup screens and a 320 px large-text variant. These are design previews, not Android instrumentation.

- Exercised all twelve direct preview tiles in the browser reconstruction: each advanced through actual renderer frames without opening a dialog. The completed preview restored its static symbol.
- At 320 px with text at 200%, the reconstruction uses one preview column and has no horizontal overflow. This does not verify native Android large-font rendering.
- Verified the APK version, original app identity and signature matching 0.11.0. The manifest is identical to 0.9.0, with no additional Activity or permission.
- ADB reported no attached device. Native gestures, accessibility interaction and physical LEDs remain unverified.

## Tap-to-play and paged library 0.12.0

Tapping a symbol now starts its on-screen preview and requests an immediate physical Glyph test on supported hardware. The 0.9.0 visual shell is retained. The Animations card uses search, explicit categories and bounded pages in place of the long grid.

- APK assembly and 71 unit tests passed with no failures or errors. Android Lint: zero errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Six new catalog tests cover complete page traversal, combined category/search filtering, locale-independent queries, stale page indices, empty results, immutable pages and invalid capacities.
- Nineteen existing fixed-symbol/settings checks and eleven controller timing scenarios passed with simulated Android services.
- The browser reconstruction was exercised through all three pages, category changes, combined search, empty results and page navigation after filtering. Renderer frames advance directly after a tile click, without opening a dialog.
- At 320 px with text at 200%, the reconstruction shows two symbols per page in one column and has no horizontal overflow. These results are not native Android layout tests.
- The native tap handler uses the existing immediate-play path. Automatic notification timing and fixed source-to-symbol mappings are retained.
- The renderer, notification policy, permission set and Spark artwork are unchanged. Category metadata replaces enum-position assumptions.
- The actual renderer exports match 0.11.1 for all twelve styles at every 50 ms sample from 0 to 6000 ms.
- Verified packaged version 0.12.0 / code 30 and signature matching 0.11.1. The manifest remains identical to 0.9.0, with no additional permission or Activity.
- ADB reported no attached device. Native layout, keyboard, state restoration, TalkBack and physical LEDs remain phone-side checks.

## Circular previews and unified Apps 0.13.0

The home screen retains its visual shell while animation tiles use circular faces and underline tabs. Swipe paging replaces arrow buttons. A compact Apps card opens a private source-management screen with native installed app icons, search, categories, Enabled only and recycled source-switch rows.

- 80 unit tests passed. Five new app-catalog tests cover combined filters, locale-independent search, immutable enabled-state changes, empty results and logical SMS identity.
- Four gesture-intent tests cover taps, vertical and diagonal movement, reading direction and distance thresholds. They do not simulate Android MotionEvent routing.
- The browser reconstruction was exercised with an actual horizontal drag: the page changed and no tile started playback. A subsequent tile tap advanced renderer frames.
- Enabled-only filtering, source changes, combined search/category empty results and counts after returning home were exercised in the reconstruction.
- At 320 px with text at 200%, page content has no horizontal overflow, category labels remain single-line in their own horizontal strip, and Apps retains a scrollable list below its header.
- APK assembly passed. Android Lint: zero errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- Nineteen existing settings/fixed-symbol checks and eleven controller timing scenarios passed with simulated Android services.
- All twelve actual renderer sequences match 0.12.0 at every 50 ms sample from 0 through 6000 ms.
- AppSettings, notification handling, the physical controller and Spark artwork match 0.12.0. Requested permissions and visibility queries are unchanged; AppsActivity is not exported.
- Verified packaged 0.13.0 / code 31 and signature matching 0.12.0. ADB reported no attached device.
- Native gesture routing, layout, state restoration, actual app icons, row recycling, keyboard, TalkBack and physical Glyph output remain phone-side checks.

## Category sizing and touch feedback 0.13.1

Phone screenshots of 0.13.0 showed All stretching across the full strip and hiding the other categories. They also showed a gray pressed background around an animation symbol. The browser reconstruction did not expose the native sizing issue.

- Both screens now use individual wrap-content TextViews for category tabs, with 48 dp minimum touch targets and a selected underline drawn within each tab.
- All remains the combined view; AI, Messages and Social narrow the results. Large-text tab strips can scroll horizontally.
- Animation tiles and category tabs have no ripple or pressed background.
- The tap playback path is unchanged, including the on-screen preview and physical Glyph request.
- All 80 existing unit tests passed with no failures, errors or skips. APK assembly passed. Lint reported zero errors and the same two existing warnings.
- All other Java sources, physical renderers, notification timing, preferences, artwork and manifest are byte-identical to the 0.13.0 source archive.
- Packaged version 0.13.1 / code 32 and the unchanged signing certificate were verified.
- No ADB device was attached. Native measurement, appearance and physical output of this correction still require a phone-side check. No browser reconstruction is presented as native verification.

## Alert controls and filter feedback 0.13.2

- A prominent red power button at the top of the status card replaces the Automatic animations switch. Its action changes between Pause Glyph alerts and Enable Glyph alerts, while its accessible state reports on/off.
- The control keeps the original enabled preference. Pausing still stops the controller; manual previews remain available.
- Enable all selects disabled sources in the current filtered results. Hidden source choices are preserved. The action is disabled for empty or fully enabled results.
- Enabled only keeps a plain label without a checkmark and turns red while active.
- Category underlines now use a selected-state background, independent of TextView content drawing. Tab widths remain intrinsic and strips can scroll at large text sizes.
- The animation-count label and extra row were removed. App counts and page indicators remain.
- Action buttons retain their static shape without ripple feedback. Animation tiles remain free of pressed backgrounds.
- All 80 existing unit tests passed with no failures, errors or skips. APK assembly passed. Lint reported zero errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- The browser reconstruction exercised pause/resume and filtered Enable all. Enabling five AI results increased the overall selected count from four to eight without enabling hidden sources.
- Active filter text and tab lines were verified as rgb(215,25,32) in the reconstruction. No animation-count element remains.
- At 320 px and 200% text, the reconstruction had no page-level horizontal overflow. The action labels fit their columns and the Apps list retained 233 px of height. Empty results disabled Enable all.
- The generated renderer sequences match 0.13.0 at every 50 ms sample. Notification handling, controller, settings, source resolution, existing artwork and manifest are unchanged.
- Packaged 0.13.2 / code 33, permissions and the unchanged signing certificate were verified.
- ADB reported no attached device. Browser checks do not verify native button drawing, underline placement, bulk-save UI routing or physical LEDs. These remain phone-side checks.

## Two-way bulk selection and fixed power label 0.13.3

- Enable all changes to Disable all when every current result is selected. Mixed results offer Enable all; empty results disable the action.
- Both directions use the original source preferences, preserve hidden choices and refresh the current filters and counts.
- With Enabled only active, Disable all removes the affected rows. The empty-result action is disabled until matching rows are visible again.
- The power button label remains Enable Glyph alerts in both states. Its fill is red rgb(215,25,32) while enabled and gray rgb(58,58,60) while paused. The button stays clickable in either state and retains the accessible on/off description.
- All 80 existing unit tests passed with no failures, errors or skips. APK assembly passed. Lint reported zero errors and the same two existing warnings.
- The browser reconstruction verified mixed-result enabling, fully selected-result disabling, empty results and the Enabled only interaction. Selecting the five AI results raised the total from four to eight; disabling them reduced the total to three, preserving the three hidden enabled sources.
- The fixed power label and both fill colors were read from the reconstruction. The saved screenshot shows the gray paused state and the Disable all action.
- Actual renderer exports match 0.13.2. Java sources outside MainActivity and AppsActivity, including settings and notification handling, match the prior source archive.
- Packaged 0.13.3 / code 34, permissions and the signing certificate matching 0.13.2 were verified.
- ADB reported no attached device. Browser checks do not confirm native UI routing, drawing, preference persistence through process death or physical Glyph output.

## Paused controls and one app-state filter 0.13.4

- Secondary home status controls and the Apps, Animations and Toy cards remain visible at 40% opacity while alerts are paused. Their child controls are disabled.
- The power button and pause explanation remain fully readable. Resuming restores controls without clearing source choices, searches, detail expansion or page state.
- Pausing stops on-screen previews as well as the controller. Manual home playback is guarded while paused. The custom pager respects its disabled state for touch, keyboard and accessibility scrolling.
- One app-state control cycles All apps, Enabled only and Disabled only. Active filtered modes are red and combine with search, categories and the existing bulk action.
- The current filter is saved in Activity state. Accessibility descriptions identify the current and next mode.
- 81 unit tests passed with no failures, errors or skips. The additional regression covers disabled results combined with categories, search and changed source selections.
- APK assembly passed. Lint reported zero errors and the same AndroidGradlePluginVersion and ExportedService warnings.
- In the browser reconstruction, both paused groups had opacity 0.4 and zero enabled child controls. The power button and pause heading remained at opacity 1.
- Resuming restored opacity 1 and enabled controls while preserving the search value and expanded details.
- The app filter cycled through all three modes. The fixture showed four enabled and nine disabled apps. Enabling four disabled AI results left no disabled AI results, raised the total to eight and preserved hidden choices.
- At 320 px and 200% text, Disabled only wrapped within its 135 px column. No page-level horizontal overflow was present; the app list retained 233 px of height.
- Actual renderer exports, source preferences, controller, notification handling, fixed source mappings, manifest and artwork match 0.13.3.
- Packaged 0.13.4 / code 35, permissions and signing certificate matching 0.13.3 were verified.
- No ADB device was attached. Native disabled-input routing, Activity restoration, dimmed rendering, TalkBack and physical LEDs remain phone-side checks. Browser inert regions are an approximation of disabled native view groups, not an accessibility-equivalence test.

## Physical-device boundary

No ADB device was attached for the current verification. Reported Build values, native UI rendering, current Nothing OS binding and physical LEDs have not been checked on a phone for this build. Catalog inclusion does not confirm completion-notification behavior for every AI app.

On Phone (3), verify the detected device label, retained settings, a manual Glyph test and a real completion notification. Test screen-off operation and system restrictions separately. Unknown or unsupported phones should show a compatibility status without contacting the Glyph SDK.

Recognition identifies reported hardware, not the availability of a compatible OS service. Phone (4a) Pro recognition does not add its renderer or AOD Toy support. See [device recognition](device-recognition.md) and the [catalog review](ai-app-catalog-research.md).
