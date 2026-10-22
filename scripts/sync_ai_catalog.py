import argparse
import json
import re
from pathlib import Path
from urllib.parse import parse_qs, urlparse

ROOT = Path(__file__).resolve().parent.parent
CATALOG = ROOT / "docs/ai-app-catalog.json"
JAVA = ROOT / "app/src/main/java/pl/glyphtoy/aiready/AiAppCatalog.java"
MANIFEST = ROOT / "app/src/main/AndroidManifest.xml"
GEMINI = "com.google.android.apps.bard"
GOOGLE = "com.google.android.googlequicksearchbox"
NOTHING = "com.nothing.thirdparty"


def reviewed_apps():
    data = json.loads(CATALOG.read_text(encoding="utf-8"))
    assert data["schemaVersion"] == 1
    apps = data["apps"]
    names = [app["package"] for app in apps]
    assert len(names) == len(set(names)), "Duplicate catalog package"
    assert GEMINI in names and GOOGLE not in names and NOTHING not in names
    for app in apps:
        name = app["package"]
        assert re.fullmatch(r"[a-zA-Z][a-zA-Z0-9_]*(?:\.[a-zA-Z][a-zA-Z0-9_]*)+", name), name
        assert all(app[key] for key in ("name", "provider", "publisher", "distribution", "sources", "notes"))
        for url in app["sources"]:
            parsed = urlparse(url)
            assert parsed.scheme == "https" and parsed.netloc, url
            if parsed.netloc in ("play.google.com", "app.mi.com"):
                assert parse_qs(parsed.query).get("id") == [name], (name, url)
            if parsed.netloc == "www.rustore.ru":
                assert parsed.path == f"/catalog/app/{name}", (name, url)
    return apps


def java_source(apps):
    entries = "\n".join(
        "            " + ("GEMINI" if app["package"] == GEMINI else json.dumps(app["package"]))
        + ("," if index < len(apps) - 1 else ");")
        for index, app in enumerate(apps)
    )
    return '''package pl.glyphtoy.aiready;

import java.util.Set;

final class AiAppCatalog {
    static final String GEMINI = "com.google.android.apps.bard";
    static final String GOOGLE = "com.google.android.googlequicksearchbox";
    private static final Set<String> APPS = Set.of(
''' + entries + '''

    static boolean supports(String packageName, boolean geminiInstalled) {
        if (packageName == null) return false;
        return APPS.contains(packageName) || (geminiInstalled && GOOGLE.equals(packageName));
    }
}
'''


def main():
    parser = argparse.ArgumentParser(description=(
        "Generate the exact package allowlist and Android visibility from reviewed identities.\n\n"
        "Run normally after editing docs/ai-app-catalog.json; run --check before building.\n"
        "The catalog is bundled as Java constants and never fetched at runtime.\n"
    ))
    parser.add_argument("--check", action="store_true", help="Fail if generated files differ; do not edit.")
    args = parser.parse_args()
    apps = reviewed_apps()
    manifest = MANIFEST.read_text(encoding="utf-8")
    queries = "    <queries>\n"
    queries += "".join(f'        <package android:name="{app["package"]}" />\n' for app in apps)
    queries += f'        <package android:name="{GOOGLE}" />\n'
    queries += f'        <package android:name="{NOTHING}" />\n'
    for name in ('com.discord', 'com.facebook.orca', 'com.whatsapp'):
        queries += f'        <package android:name="{name}" />\n'
    queries += '        <intent>\n            <action android:name="android.provider.Telephony.SMS_DELIVER" />\n        </intent>\n'
    queries += '        <intent>\n            <action android:name="android.intent.action.SENDTO" />\n            <data android:scheme="smsto" />\n        </intent>\n    </queries>'
    manifest, count = re.subn(r"    <queries>.*?</queries>", lambda _: queries, manifest, flags=re.S)
    assert count == 1, "Expected one package visibility section"
    for path, expected in ((JAVA, java_source(apps)), (MANIFEST, manifest)):
        if args.check:
            assert path.read_text(encoding="utf-8") == expected, f"Catalog drift: {path.relative_to(ROOT)}"
        else:
            path.write_text(expected, encoding="utf-8", newline="\n")
    print(f'{len(apps)} reviewed packages, {len({app["provider"] for app in apps})} model providers; catalog and visibility agree.')


if __name__ == "__main__":
    main()
