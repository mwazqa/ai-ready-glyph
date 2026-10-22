# Changelog

These versions describe local experimental builds, not public binary releases.

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
