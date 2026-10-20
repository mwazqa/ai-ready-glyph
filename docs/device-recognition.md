# Nothing device recognition

Introduced in AI Ready Glyph 0.6.4, 21 October 2026.

The app reads Android's public `Build.MANUFACTURER`, `Build.BRAND` and `Build.MODEL` fields. It does not read an IMEI, serial number, Android ID or other unique device identifier. No new permission or network request is needed.

`DeviceProfile` resolves those fields into a display name, the known Matrix size and the target permitted for the current animations. Nothing branding must match exactly (case-insensitive, with surrounding whitespace ignored). A code from another manufacturer's device does not qualify it as Nothing. CMF branding is kept separate. Exact known model codes and equivalent marketing names are accepted; a substring such as `A024-clone` is not treated as Phone (3).

## Known identities

| Android model code | Detected model | Known Glyph Matrix | Current animation support |
| --- | --- | --- | --- |
| A063 | Nothing Phone (1) | No | No |
| A065, AIN065 | Nothing Phone (2) | No | No |
| A142 | Nothing Phone (2a) | No | No |
| A142P | Nothing Phone (2a) Plus | No | No |
| A059 | Nothing Phone (3a) | No | No |
| A059P | Nothing Phone (3a) Pro | No | No |
| A024 | Nothing Phone (3) | 25 x 25 | Yes |
| A069 | Nothing Phone (4a) | No | No |
| A069P | Nothing Phone (4a) Pro | 13 x 13 | Not implemented |

Known code values and sizes were checked against the project's pinned official `glyph-matrix-sdk-2.0.aar` (SHA-256 `BE00EE9CD7115F6B11984C6E31FE98E298FB726940D1555063610685EF3BBF29`) and the official kit documentation. The SDK's `Common.is24111()` groups the Phone (3a) family; the app distinguishes its base and Pro model labels. The SDK's `Common.is22111()` covers both Phone (2) regional codes.

## Behavior

- The header now shows the detected model. The Glyph Matrix label appears for recognized Matrix devices.
- Phone (3) registers its detected `A024` target. This is the value of Nothing's `Glyph.DEVICE_23112`.
- The existing 25 x 25 animations and six-second timing are unchanged.
- Unsupported devices show the detected identity and a compatibility message in Live status. Glyph test and Toy-manager buttons are disabled; on-screen animation previews remain available.
- The controller checks compatibility before acquiring a wake lock or binding the Glyph SDK, including requests from the notification listener and Toy service.
- An unknown Nothing model keeps its reported identity and is not assumed to support this renderer. Missing or unknown build values have a safe generic label.
- Saved selections and settings are not modified.

Detection identifies the reported hardware family; it does not prove that a compatible Nothing OS service is installed, enabled or responsive. The existing SDK connection and timeout handling still apply. Custom ROMs that alter build branding or model codes may be unrecognized. Phone (4a) Pro recognition does not add its 13 x 13 renderer or AOD Toy integration.

## Verification

Unit cases cover Phone (3) codes and marketing aliases, regional Phone (2), older and Pro models, the smaller Matrix, missing / future models, manufacturer mismatches, lookalike codes, CMF and locale-independent matching. APK compilation and Android Lint check integration. Physical-device recognition and LED output remain separate verification steps.

## Primary references

- [Pinned official SDK](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit/tree/999b1143a6bff0dd79db11d21b92c15c56a346b1)
- [Official Glyph Matrix kit: supported devices, sizes and registration](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit/blob/main/README.md)
- [Official Glyph kit: model helpers](https://github.com/Nothing-Developer-Programme/Glyph-Developer-Kit/blob/main/README.md)
- [Android Build reference](https://developer.android.com/reference/android/os/Build)
