$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$taskLibs = Join-Path $taskRoot 'app\libs'
New-Item -ItemType Directory -Force -Path $taskLibs | Out-Null
$taskTarget = Join-Path $taskLibs 'glyph-matrix-sdk-2.0.aar'
$taskExpected = 'BE00EE9CD7115F6B11984C6E31FE98E298FB726940D1555063610685EF3BBF29'
if (!(Test-Path -LiteralPath $taskTarget)) {
    Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit/999b1143a6bff0dd79db11d21b92c15c56a346b1/glyph-matrix-sdk-2.0.aar' -OutFile $taskTarget
}
if ((Get-FileHash -LiteralPath $taskTarget -Algorithm SHA256).Hash -ne $taskExpected) {
    throw 'Nothing SDK checksum mismatch. Download the original SDK again.'
}
Write-Output 'Nothing SDK ready. Open the project in Android Studio or run .\gradlew.bat assembleDebug.'
