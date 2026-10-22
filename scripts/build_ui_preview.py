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
wordmark = '<svg class="wordmark" viewBox="0 0 350 32" aria-label="Ready Glyph">'
for index, letter in enumerate(letters):
    for row in range(7):
        for col in range(5):
            if letter[row * 5 + col] == '1':
                wordmark += f'<circle cx="{4 + (index * 6 + col) * 4}" cy="{4 + row * 4}" r="1.12" fill="white"/>'
wordmark += '<circle cx="342" cy="16" r="4.6" fill="#D71920"/></svg>'


def status(configured):
    title = 'ready' if configured else 'setup_needed'
    detail = 'ready_detail' if configured else 'setup_choose_apps'
    selected = text('selected_apps').replace('%1$d', str(1 if configured else 0)).replace('%2$d', str(2 if configured else 0))
    content = f'<h2>{text(title)}</h2><p>{text(detail)}</p><hr><div class="small bright">{selected}</div>'
    content += f'<div class="small">{text("access_on" if configured else "access_off")}</div>'
    content += '<div class="row">' + button('quick_test', True, 'Test ChatGPT') + button('activity_details') + '</div>'
    content += f'<div class="toggle-row">{text("automatic_animations")}<span class="toggle on"></span></div>' + button('notification_access')
    if not configured:
        content += f'<p class="small help">{text("access_help")}</p>'
    return card('status_eyebrow', None, content)


def sources(configured):
    content = f'<p>{text("sources_description")}</p>' + button('choose_apps', True)
    return card('sources_eyebrow', 'sources_title', content)


def messaging(configured):
    content = f'<p>{text("messaging_description")}</p><div class="messaging-options">'
    for label, enabled in [('Discord', configured), ('Messenger', False), ('WhatsApp', configured), ('SMS (Messages)', False)]:
        content += f'<div class="toggle-row"><span>{label}</span><span class="toggle {"on" if enabled else ""}"></span></div>'
    content += f'</div><p class="small help">{text("messaging_help")}</p>'
    return card('messaging_eyebrow', 'messaging_title', content)


def animations():
    content = f'<p>{text("animations_description")}</p><div class="caption style-group">{text("ai_styles")}</div><div class="providers"><div class="button selected">ChatGPT</div><div class="button">Claude</div><div class="button">Gemini</div><div class="button">Other AI</div></div>'
    content += f'<div class="caption style-group">{text("messaging_styles")}</div><div class="providers"><div class="button">Discord</div><div class="button">Messenger</div><div class="button">WhatsApp</div><div class="button">SMS</div></div>'
    content += f'<div class="matrix">{matrix}</div><p class="small">The knot draws from its inner junctions outwards.</p>'
    content += '<div class="row">' + button('preview') + button('test_on_glyph', True) + '</div>' + button('assign_animations')
    return card('animations_eyebrow', 'animations_title', content)


def screen(configured):
    content = wordmark + f'<div class="caption device">{text("device_label")}</div><h1>{text("hero_title")}</h1><p class="intro">{text("hero_description")}</p>'
    content += status(configured)
    content += animations() + sources(True) + messaging(True) if configured else sources(False) + messaging(False) + animations()
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
.messaging-options{margin-top:12px}.messaging-options .toggle-row{margin-top:0}
.toggle {flex:none;width:54px;height:32px;background:#3a3a3c;border:0;padding:0;border-radius:16px;position:relative;display:inline-block}
.toggle:after{content:"";position:absolute;left:2px;top:2px;width:28px;height:28px;border-radius:50%;background:#f5f5f5;transition:left .25s}.toggle.on {background:#d71920}.toggle.on:after {left:24px}
.help{font-size:12px!important;margin-top:12px!important}.style-group{margin-top:16px}.providers{display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-top:8px}.providers .button{margin:0}
.matrix{background:#000;border:1px solid #333;border-radius:24px;height:224px;margin-top:16px;display:flex;justify-content:center;align-items:center}.matrix svg{height:196px;max-width:100%}.footer{margin-top:24px}
@media(max-width:880px){body{padding:20px}.previews{flex-wrap:wrap}.variant{max-width:100%;width:390px}} @media(prefers-reduced-motion:reduce){*{scroll-behavior:auto}}
'''
version = strings['footer']
page = f'<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Ready Glyph {version} - UI layout preview</title><style>' + css + '</style>'
page += f'<header><h1>Ready Glyph / {version}</h1><p>Layout reconstruction from the Android source. This is a design preview, not a device screenshot.<br>Scroll each panel to see the complete screen. Settings and controls are illustrative.</p></header><main class="previews">'
page += '<div class="variant"><div class="label">01 / FIRST LAUNCH</div><div class="screen">' + screen(False) + '</div></div>'
page += '<div class="variant"><div class="label">02 / CONFIGURED</div><div class="screen">' + screen(True) + '</div></div></main></html>'
destination = ROOT / f'dist/Ready-Glyph-{version}-ui-design.html'
destination.write_text(page, encoding='utf-8')
print(destination)

dialog_css = '''
.previews {flex-wrap:wrap}.dialog-stage {position:relative;border-radius:28px;overflow:hidden}
.dialog-stage .screen {height:780px;overflow:hidden;pointer-events:none}
.overlay {position:absolute;inset:0;background:rgba(0,0,0,.68);display:flex;align-items:center;justify-content:center;padding:20px;color:#f5f5f5}
.dialog {width:100%;max-width:520px;background:#181818;border:1px solid #333;border-radius:28px;overflow:hidden;box-shadow:0 20px 64px #0006}
.dialog h2 {font-size:23px;font-weight:500;line-height:1.3;padding:24px 24px 12px;margin:0}
.dialog p {color:#aaa;font-size:15px;line-height:1.5;margin:0;padding:0 24px 20px}
.dialog-actions {display:flex;justify-content:flex-end;gap:8px;padding:8px 12px 16px}
.dialog-actions .button {min-height:48px;min-width:88px;padding:10px 16px;margin:0;font-family:inherit;background:transparent;cursor:pointer}
.dialog-actions .primary {background:#d71920}.dialog button:focus-visible,.dialog input:focus-visible {outline:2px solid #f5f5f5;outline-offset:3px}
.picker {padding:0 24px 8px}.picker p {padding:0;font-size:13px;line-height:1.45}
.picker input {width:100%;height:52px;background:#222;border:1px solid #333;border-radius:18px;color:#f5f5f5;padding:12px 16px;margin:16px 0 8px;font:15px Arial,sans-serif}
.picker input::placeholder {color:#aaa;opacity:1}.picker .toggle-row {margin:0;gap:14px;min-height:56px;cursor:pointer}
.picker .toggle {cursor:pointer}.picker-list {height:260px;overflow:auto;scrollbar-width:thin;scrollbar-color:#555 transparent}.picker-list .hidden {display:none}
.dialog-choices {padding:0 12px 4px}.dialog-choice {min-height:56px;padding:16px;color:#f5f5f5;font-size:16px;display:flex;align-items:center}
@media(prefers-reduced-motion:reduce){.toggle:after {transition:none}}
'''


def dialog_stage(label, title, content, actions):
    heading = text(title) if title in strings else html.escape(title)
    dialog = f'<div class="dialog" role="group" aria-label="{heading}"><h2>{heading}</h2>{content}<div class="dialog-actions">{actions}</div></div>'
    return f'<div class="variant"><div class="label">{label}</div><div class="dialog-stage"><div class="screen">{screen(True)}</div><div class="overlay">{dialog}</div></div></div>'


def dialog_button(key, primary=False):
    return f'<button type="button" class="button {"primary" if primary else ""}">{text(key)}</button>'


dialog_page = f'<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Ready Glyph {version} - Dialog design preview</title><style>{css}{dialog_css}</style>'
dialog_page += f'<header><h1>Ready Glyph / {version} / Dialogs</h1><p>Design reconstruction from Android source, not a device screenshot.<br>Try the search field and switches in the app picker. Example app selections are illustrative.</p></header><main class="previews">'
dialog_page += dialog_stage('01 / NOTIFICATION ACCESS', 'notification_access', f'<p>{text("access_explanation")}</p>', dialog_button('cancel') + dialog_button('open_settings', True))
picker = f'<div class="picker"><p>{text("ai_picker_help")}</p><input type="search" aria-label="Search AI apps" placeholder="{text("search_apps")}"><div class="picker-list">'
for label, enabled in [('ChatGPT', True), ('Claude', False), ('Gemini', True), ('Kimi', False), ('Qwen', False)]:
    picker += f'<div class="toggle-row" data-app="{label.lower()}"><span>{label}</span><button class="toggle {"on" if enabled else ""}" type="button" role="switch" aria-label="{label}" aria-checked="{str(enabled).lower()}"></button></div>'
picker += f'<p class="hidden" id="picker-empty">{text("no_matching_apps")}</p></div></div>'
dialog_page += dialog_stage('02 / CHOOSE AI APPS', 'choose_apps', picker, dialog_button('done', True))
choices = '<div class="dialog-choices">' + ''.join(f'<div class="dialog-choice">{label}</div>' for label in ['Automatic (Discord)', 'ChatGPT', 'Claude', 'Gemini', 'Other AI', 'Discord', 'Messenger', 'WhatsApp', 'SMS']) + '</div>'
dialog_page += dialog_stage('03 / ANIMATION CHOICES', 'Discord', choices, dialog_button('cancel'))
dialog_page += '''</main><script>
document.querySelectorAll('button.toggle').forEach(control => control.addEventListener('click', () => {
    const enabled = control.getAttribute('aria-checked') !== 'true';
    control.setAttribute('aria-checked', String(enabled));
    control.classList.toggle('on', enabled);
}));
document.querySelector('input[type="search"]').addEventListener('input', event => {
    const query = event.target.value.toLowerCase();
    let visible = 0;
    document.querySelectorAll('[data-app]').forEach(row => {
        const matches = row.dataset.app.includes(query);
        row.classList.toggle('hidden', !matches);
        if (matches) visible++;
    });
    document.getElementById('picker-empty').classList.toggle('hidden', visible !== 0);
});
</script></html>'''
dialog_destination = ROOT / f'dist/Ready-Glyph-{version}-dialogs-design.html'
dialog_destination.write_text(dialog_page, encoding='utf-8')
print(dialog_destination)

detail_page = f'<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Ready Glyph {version} - Source sections</title><style>{css}</style>'
detail_page += f'<header><h1>Ready Glyph / {version}</h1><p>Source-card and animation-card reconstructions. Example selections; not Android screenshots.</p></header><main class="previews">'
detail_page += '<div class="variant"><div class="label">AI APPS + MESSAGES</div><div class="screen">' + wordmark + sources(True) + messaging(True) + '</div></div>'
detail_page += '<div class="variant"><div class="label">EIGHT ANIMATION STYLES</div><div class="screen">' + wordmark + animations() + '</div></div></main></html>'
detail_destination = ROOT / f'dist/Ready-Glyph-{version}-sources-design.html'
detail_destination.write_text(detail_page, encoding='utf-8')
print(detail_destination)
