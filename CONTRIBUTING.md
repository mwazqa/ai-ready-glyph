# Contributing

Keep changes focused. Preserve saved settings unless a migration is explicitly required.

Follow README build steps and review Nothing's SDK licence. Run:

```text
python scripts/sync_ai_catalog.py --check
./gradlew.bat testDebugUnitTest assembleDebug lintDebug
```

On macOS / Linux, use bash gradlew. State exactly what was tested on physical hardware; unit tests and SDK success do not establish LED output.

For catalog additions, verify the official publisher and applicationId with primary sources. Update JSON and research notes, run scripts/sync_ai_catalog.py, and commit generated Java and manifest changes.

Keep the interface English. Notification decisions use metadata rather than text or language. Recognizing a phone does not add its hardware renderer.

Do not commit SDK binaries, APKs, signing material, private notifications, local paths or unverified artwork. Read THIRD_PARTY_NOTICES.md before changing assets.

Bug reports should include app version, model, Android / Nothing OS version, steps, source AI app and detection mode. Distinguish preview, manual Glyph test and real notification. Use fabricated or redacted phrases instead of private conversations. Report sensitive security problems through SECURITY.md.
