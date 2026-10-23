# Changelog

Versions earlier than 0.13.4 describe local experimental builds. Version 0.13.4 is the first GitHub prerelease with a debug-signed APK, source archive, checksums and verification notes.

## 0.13.4 - 2026-10-23

- Replace the enabled-only toggle with one cycling All apps / Enabled only / Disabled only filter.
- Combine both filtered modes with search, categories and bulk selection while preserving hidden source choices.
- Dim secondary home controls and cards to 40% opacity while Glyph alerts are paused.
- Disable those controls while keeping the power button and pause explanation readable.
- Restore controls without clearing source selections, search filters or page state when alerts resume.
- Stop on-screen previews when paused and prevent disabled pager gestures and accessibility scrolling.

## 0.13.3 - 2026-10-23

- Toggle the filtered bulk action between Enable all and Disable all based on the current results.
- Keep hidden source choices unchanged and disable the bulk action only for empty results.
- Keep the main button label Enable Glyph alerts in both states.
- Show a red fill while alerts are enabled and a gray fill while paused, retaining accessible on/off state.
- Keep playback, notification timing, source mappings and other interface controls unchanged.

## 0.13.2 - 2026-10-23

- Replace the automatic-animation switch with a prominent red power button at the top of the status card.
- Label its action Pause Glyph alerts or Enable Glyph alerts and expose the current state to accessibility services.
- Add Enable all for disabled apps in the current filtered results, preserving hidden source choices.
- Keep Enabled only free of a checkmark and show its label in red while active.
- Draw the selected category's red underline through its background on both screens.
- Remove the animation-count label and its extra spacing from the library.
- Remove ripple feedback from action buttons, including Details and dialog actions.
- Keep app result counts, filtering, source choices, paging and playback unchanged.

## 0.13.1 - 2026-10-23

- Fix category-tab measurement on both screens so All no longer expands to hide the remaining categories.
- Keep All as the combined category view, alongside AI, Messages and Social.
- Remove pressed backgrounds and ripples from animation symbols and category tabs.
- Preserve tap playback on screen and on Glyph Matrix, source choices, swiping and notification behavior.

## 0.13.0 - 2026-10-23

- Replace framed animation tiles with circular matrix faces and labels.
- Replace equal-width category pills with naturally sized text tabs and an active underline.
- Replace arrow buttons with horizontal paging gestures, a compact page indicator, keyboard support and accessibility actions.
- Keep direct tap playback on screen and on supported Glyph Matrix hardware.
- Move source switches into one private Apps screen with installed app icons, search, categories and Enabled only filtering.
- Show enabled and installed source counts in a compact home-screen Apps card.
- Save source choices immediately with their original preference keys; use recycled rows and a bounded icon cache for longer lists.
- Preserve filters through Activity recreation and preserve logical SMS identity across package changes.
- Keep notification eligibility, fixed source symbols, physical animation frames, automatic timing and Spark artwork unchanged.
- Add five app-filter tests and four gesture-intent tests; the local total is 80 unit tests.

## 0.12.0 - 2026-10-23

- Play on-screen and physical Glyph animations together with one tap on a supported phone.
- Replace the long animation grid with a searchable catalog and All, AI, Messages and Social categories.
- Show at most four symbols per page, or two in the single-column layout for small screens and large text.
- Add result counts, page navigation and an empty-results message.
- Preserve the active category, query and page across Activity recreation.
- Categorize styles explicitly so pagination and grouping do not depend on enum positions or four styles per category.
- Keep the 0.9.0 visual shell, source selections, fixed notification symbols, automatic delay, physical motion and Spark artwork.
- Add six catalog behavior tests; the local unit-test total is 71.

## 0.11.1 - 2026-10-22

- Restore the 0.9.0 visual design: dotted wordmark, hero text, rounded cards, pill buttons and original source-selection controls.
- Put all twelve animation symbols directly inside the home-screen Animations card.
- Play a symbol's full on-screen sequence when tapped; hold it for an immediate physical Glyph test.
- Remove the separate gallery, preview dialogs, duplicate Preview, Replay and Test controls, and the status-card quick test.
- Keep notification symbols fixed to their apps, without any assignment controls or reads of old overrides.
- Keep notification timing, source selections, animation frames, closing motion and Spark artwork unchanged.

## 0.11.0 - 2026-10-22

- Replace per-app assignment buttons with large matrix-preview tiles and independent notification switches.
- Remove custom notification-animation assignments, including reads of older saved overrides.
- Use the matching symbol for every supported messaging or social source; Gemini via Google now uses Gemini.
- Keep generic AI lettering for AI providers without a dedicated symbol.
- Replace the gallery selector with a grid of large symbol tiles and preview-only dialogs.
- Keep physical tests, diagnostics and the optional Toy in a compact Tools list.
- Adapt dialog actions to available width and let large headings wrap.
- Clear dialog previews after the closing sequence; restore source thumbnails after a brief blank pause.
- Preserve source selections, notification gates, six-second wait, full six-second animation and Spark artwork.

## 0.10.0 - 2026-10-22

- Simplify the home screen and keep AI apps, Messages and Social in a fixed order.
- Show installed, enabled apps with the same opt-in switch and inline animation choice in every category.
- Replace separate source and assignment dialogs with a single-choice animation dialog under each app.
- Move animation previews and manual tests to a separate gallery with a compact style selector.
- Show waiting and playback countdowns, connection errors and a Stop animation action.
- Use contextual setup actions and move diagnostics below app categories.
- Preserve stored selections, per-source assignments, animation frames, automatic timing and Spark artwork.

## 0.9.0 - 2026-10-22

- Add matched closing motion during the final 1.2 seconds of every animation instead of relying on whole-symbol dimming.
- Unwind ChatGPT, contract Claude and Gemini, separate Discord halves and reverse the other symbols' distinctive drawing order.
- Withdraw SMS dots from right to left before closing its bubble.
- Preserve all twelve opening/hold sequences through 4.8 seconds, the total six-second playback, automatic start delay, settings and Spark identity artwork.

## 0.8.4 - 2026-10-22

- Use the selected Spark concept A: staggered white dot clusters around a red centre.
- Add the matching monochrome icon and circular 25 x 25 Glyph Toys preview.
- Preserve the Notify Glyph name, settings, all twelve animation sequences and notification behavior.

## 0.8.3 - 2026-10-22

- Replace the signal arcs with a notification card, two short content lines and a red unread dot.
- Update adaptive colour, monochrome and circular Glyph Toys artwork with the same symbol.
- Preserve the Notify Glyph name, settings, all twelve animation sequences and notification behavior.

## 0.8.2 - 2026-10-22

- Replace the N monogram with a signal symbol: three white arcs spreading from a red source dot.
- Use smooth strokes for adaptive launcher icons and a matching pixel version in Glyph Toys.
- Preserve the Notify Glyph name, settings, all twelve animation sequences and notification behavior.

## 0.8.1 - 2026-10-22

- Rename the displayed app and Glyph Toy to Notify Glyph, including notification access labels, in-app text and the dot wordmark.
- Replace the ready check with an original N monogram in adaptive colour, monochrome and circular Glyph Toys artwork. Keep the red notification dot.
- Preserve Android identity, signing identity, existing settings, notification handling, all twelve animation sequences and the six-second delay followed by full six-second playback.

## 0.8.0 - 2026-10-22

- Add a separate Social section for the standard Instagram, TikTok, X and Facebook apps. New source switches start disabled.
- Add four matching monochrome pixel animations, previews and per-app assignments, bringing the total to twelve styles.
- Include Social in readiness, selected-source counts and first-launch or configured section ordering.
- Apply existing regular-notification filtering, repost handling and the six-second start delay to enabled social sources.
- Preserve previous source selections, assignment keys, SMS detection and all eight previous animation sequences.

## 0.7.4 - 2026-10-22

- Wait six seconds before automatic Glyph playback so the system notification effect has time to finish. This delay is a fixed margin, not measured system timing.
- Start the animation clock after the wait and SDK connection, keeping the full six-second animation.
- Replace pending alerts with the latest accepted notification; manual tests remain immediate.
- Cancel delayed work on stop, disconnect or failure, and recheck pause, source selection and Do Not Disturb before starting.
- Preserve animation frames, source preferences, notification eligibility and SMS detection.

## 0.7.3 - 2026-10-22

- Fall back to the system-resolved SMS composer when the default SMS package API returns no package.
- Add targeted SENDTO / smsto package visibility and accept only an enabled handler, excluding the system chooser.
- Report an SMS detection failure without claiming that no default app exists.
- Preserve SMS source choices, animation assignments, notification rules and all animation frames. No SMS permissions are added.

## 0.7.2 - 2026-10-22

- Reserve the full measured height of dialog actions, including padding, to prevent rounded buttons from being clipped.
- Limit native dialog lists to the space left below the title and above the actions; longer lists remain scrollable.
- Preserve animation frames, notification handling, source selections and existing dialog styling.

## 0.7.1 - 2026-10-22

- Give each messaging animation its own motion: joining and blinking Discord halves, a Messenger bolt followed by its bubble, a drawn WhatsApp phone with two signal waves, and bouncing SMS typing dots.
- Settle on the existing messaging symbols before the final fade and keep the six-second duration.
- Preserve existing AI animation frames, notification handling, source selections and artwork.

## 0.7.0 - 2026-10-22

- Rename the displayed app and Glyph Toy to Ready Glyph while preserving the application ID, service identities and existing settings.
- Add a Messages card for Discord, Messenger, WhatsApp and the current default SMS app. New sources start disabled.
- Add four distinct messaging symbols with six-second centre-out reveal animations, previews and per-app overrides.
- Follow the current SMS app without reading SMS storage, requesting SMS permissions or changing the default app.
- Accept new messaging notification posts that reuse a conversation key, using posting timestamps without extracting message text.
- Preserve the AI catalog, AI notification behavior and existing AI animation frames.
- Move shared notification controls into Live status and retain the rounded Nothing-inspired theme.

## 0.6.9 - 2026-10-21

- Apply one dark dialog style with 28 dp corners, consistent typography and pill-shaped buttons to every in-app dialog.
- Give the app picker a rounded, filled search field and responsive list height.
- Replace thin switches with 54 x 32 dp tracks and 28 dp white thumbs, using red for enabled and gray for disabled.
- Preserve native switch interaction, accessibility, selected apps, notification behavior and Glyph animations.

## 0.6.8 - 2026-10-21

- Always accept regular notifications from selected apps while Automatic animations is enabled.
- Remove Ready detection, the Every regular notification switch, custom phrases and text-based matching.
- Stop extracting notification titles and message bodies.
- Keep ongoing, progress and group-summary exclusions, duplicate suppression and Do Not Disturb handling.
- Old phrase and mode preferences no longer affect behavior; source selections and animation assignments are preserved.

## 0.6.7 - 2026-10-21

- Show the exact reason a notification is skipped instead of a combined message.
- Timestamp Glyph actions separately from notification events, including seconds.
- Preserve detection rules and add two notification-decision tests.
- This diagnostic update does not establish why the reported Codex alert was skipped or confirm a physical-device fix.

## 0.6.6 - 2026-10-21

- Restore the red notification dot in the circular Glyph Toys preview.

## 0.6.5 - 2026-10-21

- Give Glyph Toys a dedicated circular 25 x 25 pixel preview with a white ready check.
- Replace the square launcher artwork in the Toy list and enlarge the symbol.
- Add a reproducible SVG and Android vector generator for the Toy preview.

## 0.6.4 - 2026-10-21

- Detect known Nothing models and update the device label.
- Separate model recognition from physical animation support.
- Guard SDK access on unsupported models.
- Add seven device tests; total 27 tests.

## 0.6.3 - 2026-10-21

- Show only the version in the footer.

## 0.6.2 - 2026-10-21

- Expand the provider catalog to 40 identities from 31 providers.
- Record sources, regional variants and unresolved candidates.
- Generate eligibility and visibility from JSON.
- Exclude aggregators from the picker without clearing saved choices.

## 0.6.1 - 2026-10-20

- Restrict source selection to installed catalog entries.
- Add the optional Google source for Gemini.

## 0.6.0 - 2026-10-20

- Introduce the English interface, current design and original icon.
- Order sections according to setup state.

## Initial public source repository - 2026-10-21

- Add public documentation, CI and separate licensing notices.
- Exclude local binaries, signing material and raw references.
