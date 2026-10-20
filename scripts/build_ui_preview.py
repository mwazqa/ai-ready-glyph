"""Create a labelled layout reconstruction, not an Android device screenshot."""
from pathlib import Path
import html
import math
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / 'app/src/main/res'
strings = {s.attrib['name']: s.text or '' for s in ET.parse(RES / 'values/strings.xml').getroot()}


def text(key):
    value = strings[key]
    # The reference device for this layout reconstruction is Phone (3).
    if key == 'device_label':
        value = value.replace('%1$s', 'NOTHING PHONE (3)')
    return html.escape(value.replace('\\n', '\n').replace("\\'", "'")).replace('\n', '<br>')


def button(key, primary=False, label=None):
    return f'<div class="button {"primary" if primary else ""}">{label or text(key)}</div>'


def card(eyebrow, title, content):
    return f'<section><div class="caption">{text(eyebrow)}</div>{f"<h2>{text(title)}</h2>" if title else ""}{content}</section>'


source = (ROOT / 'app/src/main/java/pl/glyphtoy/aiready/ChatGptSymbol.java').read_text(encoding='utf-8')
rows = re.findall(r'"([0-9a-f]{25})"', source)
assert len(rows) == 25
dots = []
for y, row in enumerate(rows):
    for x, value in enumerate(row):
        if math.hypot(x - 12, y - 12) > 12.5:
            continue
        light = int(value, 16) * 15 or 38
        dots.append(f'<circle cx="{13 + x * 7.25}" cy="{13 + y * 7.25}" r="2.175" fill="rgb({light},{light},{light})"/>')
matrix = '<svg viewBox="0 0 200 200" role="img" aria-label="ChatGPT Glyph preview">' + ''.join(dots) + '</svg>'
wordmark_source = (ROOT / 'app/src/main/java/pl/glyphtoy/aiready/DotWordmarkView.java').read_text(encoding='utf-8')
letters = re.findall(r'"([01]{35})"', wordmark_source)
wordmark = '<svg class="wordmark" viewBox="0 0 350 32" aria-label="AI Ready Glyph">'
for index, letter in enumerate(letters):
    for row in range(7):
        for col in range(5):
            if letter[row * 5 + col] == '1':
                wordmark += f'<circle cx="{4 + (index * 6 + col) * 4}" cy="{4 + row * 4}" r="1.12" fill="white"/>'
wordmark += '<circle cx="342" cy="16" r="4.6" fill="#D71920"/></svg>'


def status(configured):
    title = 'ready' if configured else 'setup_needed'
    detail = 'ready_detail' if configured else 'setup_choose_apps'
    content = f'<h2>{text(title)}</h2><p>{text(detail)}</p><hr><div class="small bright">AI apps selected: {3 if configured else 0}</div>'
    content += f'<div class="small">{text("access_on" if configured else "access_off")}</div>'
    content += '<div class="row">' + button('quick_test', True, 'Test ChatGPT') + button('activity_details') + '</div>'
    return card('status_eyebrow', None, content)


def sources(configured):
    content = f'<p>{text("sources_description")}</p><div class="toggle-row">{text("automatic_animations")}<span class="toggle on"></span></div>'
    content += button('choose_apps', True) + button('notification_access')
    if not configured:
        content += f'<p class="small help">{text("access_help")}</p>'
    return card('sources_eyebrow', 'sources_title', content)


def animations():
    content = f'<p>{text("animations_description")}</p><div class="providers"><div class="button selected">ChatGPT</div><div class="button">Claude</div><div class="button">Gemini</div><div class="button">Other AI</div></div>'
    content += f'<div class="matrix">{matrix}</div><p class="small">The knot draws from its inner junctions outwards.</p>'
    content += '<div class="row">' + button('preview') + button('test_on_glyph', True) + '</div>' + button('assign_animations')
    return card('animations_eyebrow', 'animations_title', content)


def screen(configured):
    content = wordmark + f'<div class="caption device">{text("device_label")}</div><h1>{text("hero_title")}</h1><p class="intro">{text("hero_description")}</p>'
    content += status(configured)
    content += animations() + sources(True) if configured else sources(False) + animations()
    detection = f'<p>{text("detection_description")}</p><div class="toggle-row">{text("every_notification")}<span class="toggle"></span></div><p class="small">{text("every_notification_help")}</p>' + button('custom_phrases')
    content += card('detection_eyebrow', 'detection_title', detection)
    content += card('toy_eyebrow', 'toy_title', f'<p>{text("toy_description")}</p>' + button('open_toys'))
    return content + f'<div class="caption footer">{text("footer")}</div>'


css = '''
* {box-sizing:border-box} body {margin:0;background:#e8e8e8;color:#151515;font-family:Arial,sans-serif;padding:32px}
header {max-width:820px;margin:0 auto 24px} header h1 {font-size:24px;letter-spacing:-.6px;margin:0 0 8px}
header p {font-size:13px;color:#555;line-height:1.5;margin:0}.previews {display:flex;justify-content:center;gap:24px;align-items:flex-start}
.variant {width:390px;flex:none}.label {font:11px monospace;letter-spacing:1px;margin-bottom:10px}
.screen {height:1000px;overflow:auto;background:#080808;color:#f5f5f5;padding:24px 20px 32px;border-radius:28px;scrollbar-width:none}
.wordmark {width:100%;height:32px;display:block}.caption {font:11px/1.45 monospace;color:#aaa;letter-spacing:.8px}.device{margin-top:12px}
.screen h1 {font-size:30px;line-height:1.18;font-weight:500;letter-spacing:-.6px;margin:18px 0 10px}.screen p {font-size:14px;line-height:1.45;color:#aaa;margin:8px 0 0}
section {margin-top:16px;background:#181818;border:1px solid #333;border-radius:28px;padding:20px 18px}.screen h2{font-size:23px;line-height:1.3;font-weight:500;letter-spacing:-.4px;margin:10px 0 0}
hr {border:0;height:1px;background:#333;margin:18px 0 14px}.small {font-size:13px!important;line-height:1.45;color:#aaa;margin-top:6px}.bright{color:#f5f5f5}
.button {min-height:52px;border:1px solid #333;border-radius:26px;padding:10px 12px;text-align:center;font-size:14px;line-height:1.3;color:#f5f5f5;display:flex;align-items:center;justify-content:center;margin-top:10px}
.button.primary {background:#d71920;border-color:#d71920}.button.selected{background:#f5f5f5;color:#000;border-color:#f5f5f5}
.row {display:flex;gap:8px;margin-top:16px}.row .button {flex:1;min-width:0;margin-top:0}
.toggle-row {display:flex;align-items:center;justify-content:space-between;gap:12px;min-height:56px;font-size:15px;margin-top:14px}
.toggle {flex:none;width:36px;height:16px;background:#333;border-radius:20px;position:relative;display:inline-block}
.toggle:after{content:"";position:absolute;left:0;top:-3px;width:22px;height:22px;border-radius:50%;background:#aaa}.toggle.on {background:#d71920}.toggle.on:after {left:17px;background:#f5f5f5}
.help{font-size:12px!important;margin-top:12px!important}.providers{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:16px}.providers .button{margin:0}
.matrix{background:#000;border:1px solid #333;border-radius:24px;height:224px;margin-top:16px;display:flex;justify-content:center;align-items:center}.matrix svg{height:196px;max-width:100%}.footer{margin-top:24px}
@media(max-width:880px){body{padding:20px}.previews{flex-wrap:wrap}.variant{max-width:100%;width:390px}} @media(prefers-reduced-motion:reduce){*{scroll-behavior:auto}}
'''
page = '<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>AI Ready Glyph 0.6 - UI layout preview</title><style>' + css + '</style>'
page += '<header><h1>AI Ready Glyph / 0.6.0</h1><p>Layout reconstruction from the Android source. This is a design preview, not a device screenshot.<br>Scroll each panel to see the complete screen. Settings and controls are illustrative.</p></header><main class="previews">'
page += '<div class="variant"><div class="label">01 / FIRST LAUNCH</div><div class="screen">' + screen(False) + '</div></div>'
page += '<div class="variant"><div class="label">02 / CONFIGURED</div><div class="screen">' + screen(True) + '</div></div></main></html>'
destination = ROOT / 'dist/AI-Ready-Glyph-0.6.0-ui-design.html'
destination.write_text(page, encoding='utf-8')
print(destination)
