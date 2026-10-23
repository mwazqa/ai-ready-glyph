from pathlib import Path
import base64
import html
import json
import math
import os
import re
import shutil
import subprocess
import tempfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / 'app/src/main/java/pl/glyphtoy/aiready'
strings = {s.attrib['name']: s.text or '' for s in ET.parse(ROOT / 'app/src/main/res/values/strings.xml').getroot().findall('string')}
records = re.findall(r'[A-Z]+\("([^"]+)", "([^"]+)", "([^"]+)", Category\.([A-Z]+)\)', (SOURCE / 'AnimationStyle.java').read_text(encoding='utf-8'))
styles = [{'id': key, 'label': label, 'description': description, 'category': category.lower()} for key, label, description, category in records]
assert styles
java_home = os.environ.get('JAVA_HOME')
extension = '.exe' if os.name == 'nt' else ''
javac = str(Path(java_home) / 'bin' / ('javac' + extension)) if java_home else shutil.which('javac')
java = str(Path(java_home) / 'bin' / ('java' + extension)) if java_home else shutil.which('java')
if not javac or not java:
    raise SystemExit('Set JAVA_HOME to a JDK before generating the UI preview.')
with tempfile.TemporaryDirectory(prefix='notify-glyph-ui-') as folder:
    folder = Path(folder)
    exporter = folder / 'UiSymbols.java'
    exporter.write_text('''package pl.glyphtoy.aiready;
import java.util.Base64;
public class UiSymbols {
    public static void main(String[] arguments) {
        for (AnimationStyle style : AnimationStyle.values()) {
            byte[] bytes = new byte[625 * 121];
            for (int time = 0; time <= 6000; time += 50) {
                int[] pixels = ReadyAnimation.frame(style, time);
                for (int i = 0; i < pixels.length; i++) bytes[(time / 50) * 625 + i] = (byte) pixels[i];
            }
            System.out.println(style.id + "\\t" + Base64.getEncoder().encodeToString(bytes));
        }
    }
}
''', encoding='utf-8')
    names = ['AnimationStyle', 'ReadyAnimation', 'AnimationOutro', 'ChatGptTrace', 'ChatGptSymbol', 'ClaudeFrames', 'MessagingAnimation', 'MessagingSymbols', 'SocialAnimation', 'SocialSymbols']
    subprocess.run([javac, '-encoding', 'UTF-8', '-d', str(folder), str(exporter), *[str(SOURCE / (name + '.java')) for name in names]], check=True)
    output = subprocess.check_output([java, '-cp', str(folder), 'pl.glyphtoy.aiready.UiSymbols'], text=True)
animations = dict(line.split('\t') for line in output.splitlines())
frames = {key: list(base64.b64decode(data)[50 * 625:51 * 625]) for key, data in animations.items()}


def text(key):
    return html.escape(strings[key].replace("\\'", "'").replace('\\n', '\n'))


def symbol(key, grid=False):
    dots = []
    for i, light in enumerate(frames[key]):
        x, y = i % 25, i // 25
        if math.hypot(x - 12, y - 12) > 12.5 or not grid and not light:
            continue
        light = light or 38
        dots.append(f'<circle cx="{x + 1.5}" cy="{y + 1.5}" data-pixel="{i}" r="{.3 if grid else .34}" fill="rgb({light},{light},{light})"/>')
    return '<svg viewBox="0 0 27 27" aria-hidden="true"><circle cx="13.5" cy="13.5" r="13.5" fill="black"/>' + ''.join(dots) + '</svg>'


symbols = {style['id']: symbol(style['id']) for style in styles}
matrices = {style['id']: symbol(style['id'], True) for style in styles}


def action(key, name, primary=False):
    return f'<button class="button {"primary" if primary else ""}" type="button" data-action="{name}">{text(key)}</button>'


def card(eyebrow, heading, body, extra=''):
    title = f'<h2>{text(heading)}</h2>' if heading else ''
    return f'<section class="card {extra}"><div class="caption">{text(eyebrow)}</div>{title}{body}</section>'


def tabs(context):
    return f'<div class="tabs {context}-tabs">' + ''.join(f'<button type="button" class="tab" data-{context}-category="{key}" aria-pressed="{str(key=="all").lower()}">{text(label)}</button>' for key,label in [('all','animations_all'),('ai','animations_ai'),('messages','messaging_title'),('social','social_title')]) + '</div>'


def wordmark():
    glyphs = re.findall(r'"([01]{35})"', (SOURCE/'DotWordmarkView.java').read_text(encoding='utf-8'))
    dots=[]
    for letter,glyph in enumerate(glyphs):
        for i,pixel in enumerate(glyph):
            if pixel=='1':
                dots.append(f'<circle cx="{1+letter*6+i%5}" cy="{1+i//5}" r=".28" fill="white"/>')
    return '<div class="wordmark"><svg viewBox="0 0 80 8" role="img" aria-label="Notify Glyph">' + ''.join(dots) + '<circle cx="78" cy="4" r="1.15" fill="#d71920"/></svg></div>'


def animation_card():
    body=f'<p>{text("animations_description")}</p><input class="search animation-search" type="search" aria-label="{text("search_animations")}" placeholder="{text("search_animations")}">'
    body+=tabs('animation')
    body+='<div class="animation-pager" tabindex="0" role="group" aria-label="Animation pages"><div class="animation-grid"></div></div>'
    body+=f'<p class="empty-results" hidden>{text("no_matching_animations")}</p><div class="page-label" aria-live="polite"></div>'
    return card('animations_eyebrow','animations_title',body,'animations')


def apps_summary():
    body='<div class="enabled-count apps-enabled"></div><p class="apps-installed"></p>'+action('manage_apps','apps',True)
    return card('apps_eyebrow','apps_title',body,'apps-summary')


def home():
    body=wordmark()+'<div class="caption device">NOTHING PHONE (3) / GLYPH MATRIX</div>'
    body+=f'<h1>{text("hero_title")}</h1><p class="hero-description">{text("hero_description")}</p>'
    power='<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3V11M6.34 5.34A8 8 0 1 0 17.66 5.34" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>'
    status=f'<button type="button" class="button primary power-button" data-action="alerts" aria-pressed="true" aria-description="{text("glyph_alerts_on")}">{power}<span>{text("enable_glyph_alerts")}</span></button>'
    status+=f'<h2 class="readiness">{text("ready")}</h2><p class="readiness-detail">{text("ready_detail")}</p><div class="status-controls"><hr><p class="status-selected"></p><p class="small">{text("access_on")}</p>'
    status+=action('activity_details','details')+'<div class="details" hidden><p>Waiting for a notification from a selected app.</p><p>Ready for a Glyph test.</p></div>'
    status+=action('notification_access','access')+'</div>'
    body+=card('status_eyebrow',None,status)
    body+='<div class="home-sections">'+animation_card()+apps_summary()
    body+=card('toy_eyebrow','toy_title',f'<p>{text("toy_description")}</p>'+action('open_toys','toy'))
    return body+f'</div><div class="caption footer">{text("footer")}</div>'


def apps_screen():
    return f'''<div class="apps-header"><button type="button" class="back" data-action="back">{text("back")}</button><h1>{text("apps_title")}</h1><p>{text("apps_description")}</p><p class="apps-summary-count small"></p><input class="search app-search" type="search" aria-label="{text("find_apps")}" placeholder="{text("find_apps")}">{tabs("app")}<p class="app-result-count" aria-live="polite"></p><div class="app-filters"><button type="button" class="enable-all" aria-label="{text("enable_all_description")}">{text("enable_all")}</button><button type="button" class="enabled-filter" data-mode="all">{text("all_apps")}</button></div></div><div class="app-list"></div>'''


examples=[
 {'id':'chatgpt','label':'ChatGPT','package':'com.openai.chatgpt','category':'ai','enabled':True,'color':'#343434','letter':'C'},
 {'id':'claude','label':'Claude','package':'com.anthropic.claude','category':'ai','enabled':False,'color':'#c76c4b','letter':'C'},
 {'id':'discord','label':'Discord','package':'com.discord','category':'messages','enabled':True,'color':'#5865f2','letter':'D'},
 {'id':'facebook','label':'Facebook','package':'com.facebook.katana','category':'social','enabled':False,'color':'#0866ff','letter':'f'},
 {'id':'gemini','label':'Gemini','package':'com.google.android.apps.bard','category':'ai','enabled':False,'color':'#427fe8','letter':'G'},
 {'id':'google','label':'Gemini via Google','package':'com.google.android.googlequicksearchbox','category':'ai','enabled':False,'color':'#4285f4','letter':'G','help':strings['gemini_google_help']},
 {'id':'instagram','label':'Instagram','package':'com.instagram.android','category':'social','enabled':True,'color':'#ca367e','letter':'I'},
 {'id':'kimi','label':'Kimi','package':'com.moonshot.kimichat','category':'ai','enabled':False,'color':'#232327','letter':'K'},
 {'id':'messenger','label':'Messenger','package':'com.facebook.orca','category':'messages','enabled':False,'color':'#923bf3','letter':'M'},
 {'id':'sms','label':'SMS (Messages)','package':'com.google.android.apps.messaging','category':'messages','enabled':True,'color':'#2075ed','letter':'S','help':strings['sms_help']},
 {'id':'tiktok','label':'TikTok','package':'com.zhiliaoapp.musically','category':'social','enabled':False,'color':'#252529','letter':'T'},
 {'id':'whatsapp','label':'WhatsApp','package':'com.whatsapp','category':'messages','enabled':False,'color':'#1da851','letter':'W'},
 {'id':'x','label':'X','package':'com.twitter.android','category':'social','enabled':False,'color':'#252529','letter':'X'}
]
css=r'''
*{box-sizing:border-box}body{margin:0;padding:24px;background:#101010;color:#f5f5f5;font-family:Arial,sans-serif}header{max-width:1200px;margin:0 auto 24px}header h1{font-size:22px;margin:0 0 10px}header p{font-size:14px;color:#aaa;line-height:1.5;margin:0}.previews{display:flex;gap:24px;justify-content:center;align-items:flex-start;flex-wrap:wrap}.variant{width:390px;max-width:100%}.variant>h2{font-size:14px;font-weight:400;color:#aaa;margin:0 0 10px}.phone{height:880px;background:#000;border:1px solid #333;border-radius:28px;overflow:hidden;position:relative}.screen{height:100%;overflow-y:auto;padding:24px 20px 32px;scrollbar-width:none}.screen::-webkit-scrollbar,.tabs::-webkit-scrollbar,.app-list::-webkit-scrollbar{display:none}.wordmark{height:32px;display:flex;align-items:center}.wordmark svg{width:100%;height:100%}.caption{font:calc(11px * var(--font-scale,1))/1.4 monospace;letter-spacing:.08em;color:#aaa}.device{margin-top:12px}h1{font-size:calc(30px * var(--font-scale,1));font-weight:500;line-height:1.24;margin:18px 0 0;white-space:pre-line;overflow-wrap:anywhere}p{font-size:calc(14px * var(--font-scale,1));line-height:1.45;color:#aaa;margin:8px 0 0;overflow-wrap:anywhere}.hero-description{margin-top:10px}.card{border:1px solid #333;background:#181818;border-radius:28px;padding:20px 18px;margin-top:16px}.card h2{font-size:calc(23px * var(--font-scale,1));font-weight:500;line-height:1.3;margin:10px 0 0}.card .readiness{font-size:calc(24px * var(--font-scale,1))}.card hr{border:0;border-top:1px solid #333;margin:18px 0 14px}.status-selected{font-size:calc(13px * var(--font-scale,1));color:#f5f5f5}.small{font-size:calc(12px * var(--font-scale,1));line-height:1.45}.button{display:block;width:100%;min-height:52px;border:1px solid #333;border-radius:26px;background:transparent;color:#f5f5f5;font:500 calc(14px * var(--font-scale,1))/1.35 Arial,sans-serif;padding:10px 12px;cursor:pointer;margin-top:16px;overflow-wrap:anywhere}.button.primary{background:#d71920;border-color:#d71920}.power-button{position:relative;min-height:64px;font-size:calc(18px * var(--font-scale,1));padding:14px 44px;margin-top:12px}.power-button[aria-pressed=false]{background:#3a3a3c;border-color:#3a3a3c}.power-button svg{position:absolute;left:20px;top:50%;transform:translateY(-50%);width:24px;height:24px}.source-row input{position:absolute;right:0;width:54px;height:32px;opacity:0;cursor:pointer}.switch{width:54px;height:32px;border-radius:16px;background:#3a3a3c;flex:none;pointer-events:none;position:relative}.switch:after{content:"";position:absolute;left:2px;top:2px;width:28px;height:28px;border-radius:50%;background:#f5f5f5}input:checked+.switch{background:#d71920}input:checked+.switch:after{left:24px}.search{display:block;width:100%;background:#222;border:1px solid #333;border-radius:18px;padding:12px 16px;min-height:52px;margin-top:16px;color:#f5f5f5;font:calc(15px * var(--font-scale,1)) Arial,sans-serif;min-width:0}.tabs{display:flex;gap:0;overflow-x:auto;scrollbar-width:none;margin-top:8px;flex:none}.tab{flex:none;white-space:nowrap;min-width:48px;min-height:48px;padding:0 8px;background:transparent;color:#aaa;border:0;border-bottom:2px solid transparent;font:500 calc(14px * var(--font-scale,1))/1.2 Arial,sans-serif;cursor:pointer}.tab[aria-pressed=true]{color:#f5f5f5;border-bottom-color:#d71920}.animation-pager{margin-top:12px;overflow:hidden;touch-action:pan-y;user-select:none}.animation-grid{display:grid;grid-template-columns:minmax(0,1fr) minmax(0,1fr);gap:8px}.animation{width:100%;min-width:0;padding:0 6px;background:transparent;border:0;border-radius:0;color:#f5f5f5;cursor:pointer}.matrix{height:124px;display:flex;align-items:center;justify-content:center;pointer-events:none}.matrix svg{width:100%;height:100%;pointer-events:none}.animation-label{font:calc(15px * var(--font-scale,1))/1.35 Arial,sans-serif;min-height:36px;display:flex;align-items:center;justify-content:center;margin-top:8px;overflow-wrap:anywhere}.page-label{font-size:calc(12px * var(--font-scale,1));color:#aaa;min-height:36px;display:flex;align-items:center;justify-content:center;margin-top:8px}.empty-results{margin-top:8px}.enabled-count{font-size:calc(24px * var(--font-scale,1));margin-top:12px;line-height:1.3}.apps-installed{font-size:calc(13px * var(--font-scale,1));margin-top:6px}.footer{margin-top:24px}.details{margin-top:12px}.apps-screen{height:100%;display:flex;flex-direction:column;padding:12px 20px 16px}.apps-header{flex:none;min-width:0}.back{display:block;min-height:48px;background:transparent;color:#f5f5f5;border:0;padding:0 12px 0 4px;font:calc(14px * var(--font-scale,1)) Arial,sans-serif;cursor:pointer}.apps-header h1{margin-top:4px}.apps-header>p{margin-top:8px}.app-filters{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-top:4px;min-width:0}.app-result-count{font-size:calc(12px * var(--font-scale,1));color:#aaa;margin-top:12px}.enable-all,.enabled-filter{flex:1;min-width:0;min-height:48px;background:transparent;color:#aaa;border:0;padding:0 8px;font:calc(13px * var(--font-scale,1)) Arial,sans-serif;cursor:pointer}.enable-all{color:#d71920;text-align:left}.enable-all:disabled{color:#aaa;cursor:default}.enabled-filter{text-align:right}.enabled-filter[data-mode=enabled],.enabled-filter[data-mode=disabled]{color:#d71920}.app-list{flex:1;min-height:0;overflow-y:auto;scrollbar-width:none}.source-row{display:flex;align-items:center;gap:12px;min-height:72px;padding:8px 0;position:relative;cursor:pointer}.app-icon{width:36px;height:36px;border-radius:10px;display:flex;align-items:center;justify-content:center;color:#fff;font:600 22px Arial,sans-serif;flex:none}.app-name{flex:1;min-width:0;font-size:calc(16px * var(--font-scale,1));line-height:1.35;overflow-wrap:anywhere}.app-help{display:block;color:#aaa;font-size:calc(12px * var(--font-scale,1));line-height:1.4;margin-top:4px}.empty-apps{padding:24px 16px;text-align:center;font-size:calc(14px * var(--font-scale,1))}.button:focus-visible,.animation:focus-visible,.animation-pager:focus-visible,.tab:focus-visible,.enable-all:focus-visible,.enabled-filter:focus-visible,.back:focus-visible,.search:focus-visible,input:focus-visible+.switch{outline:2px solid #f5f5f5;outline-offset:2px}[hidden]{display:none!important}@media(max-width:450px){body{padding:12px}.variant{width:100%}}.compact .animation-grid{grid-template-columns:1fr}.compact .matrix{height:192px}
'''
script=r'''
const frames=FRAMES,styles=STYLES,matrices=MATRICES,examples=EXAMPLES,ui=UI_STRINGS,decoded={},running=new WeakMap();
const escapeText=value=>String(value).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
function animate(container,id){
 const screen=container.closest('.screen');
 screen.querySelectorAll('.animation').forEach(other=>{if(other!==container&&running.has(other)){running.delete(other);drawFrame(other,other.dataset.animation,50)}});
 const token={};running.set(container,token);
 const start=performance.now();let previous=-1;
 function step(now){
  if(running.get(container)!==token||!container.isConnected)return;
  const elapsed=now-start,index=Math.min(120,Math.floor(elapsed/50));
  if(index!==previous){drawFrame(container,id,index);previous=index}
  if(elapsed<6000)requestAnimationFrame(step);
  else setTimeout(()=>{if(running.get(container)===token&&container.isConnected){drawFrame(container,id,50);running.delete(container)}},250);
 }
 requestAnimationFrame(step);
}
function drawFrame(container,id,index){
 const data=decoded[id]||(decoded[id]=Uint8Array.from(atob(frames[id]),c=>c.charCodeAt(0)));
 for(const circle of container.querySelectorAll('circle[data-pixel]')){const value=data[index*625+Number(circle.dataset.pixel)]||38;circle.setAttribute('fill','rgb('+value+','+value+','+value+')')}
 container.dataset.frame=String(index);
}
function renderCatalog(card){
 const terms=card.querySelector('.animation-search').value.trim().toLowerCase().split(/\s+/).filter(Boolean);
 const category=card.dataset.category||'all',size=card.closest('.phone').classList.contains('compact')?2:4;
 const matches=styles.filter(style=>(category==='all'||category===style.category)&&terms.every(term=>(style.id+' '+style.label+' '+style.category).toLowerCase().includes(term)));
 const pages=Math.max(1,Math.ceil(matches.length/size)),page=Math.max(0,Math.min(Number(card.dataset.page||0),pages-1));
 card.dataset.page=page;card.dataset.pages=pages;
 card.querySelectorAll('button[data-animation-category]').forEach(button=>button.setAttribute('aria-pressed',String(button.dataset.animationCategory===category)));
 card.querySelector('.empty-results').hidden=matches.length!==0;
 const pager=card.querySelector('.animation-pager'),grid=card.querySelector('.animation-grid');
 pager.hidden=matches.length===0;
 grid.style.transform='';
 grid.innerHTML=matches.slice(page*size,(page+1)*size).map(style=>'<button class="animation" type="button" data-animation="'+style.id+'" aria-label="Play '+style.label+' on screen and Glyph"><span class="matrix">'+matrices[style.id]+'</span><span class="animation-label">'+style.label+'</span></button>').join('');
 const label=card.querySelector('.page-label');label.hidden=pages<=1;label.textContent='Swipe · '+(page+1)+' / '+pages;
 pager.setAttribute('aria-label','Animation pages, '+(page+1)+' of '+pages);
}
document.querySelectorAll('.phone').forEach(phone=>{
 const data=examples.map(app=>({...app})),home=phone.querySelector('[data-home]'),apps=phone.querySelector('[data-apps]');
 let category='all',appFilter='all',alertsEnabled=true,ignoreClicksUntil=0;
 function updateCounts(){
  const enabled=data.filter(app=>app.enabled);
  phone.querySelectorAll('.apps-enabled').forEach(el=>el.textContent=enabled.length+' enabled');
  phone.querySelectorAll('.apps-installed').forEach(el=>el.textContent=data.length+' installed apps');
  phone.querySelector('.apps-summary-count').textContent='Enabled: '+enabled.length+' / Installed: '+data.length;
  phone.querySelectorAll('.status-selected').forEach(el=>el.textContent='Selected sources: AI '+enabled.filter(a=>a.category==='ai').length+' / Messages '+enabled.filter(a=>a.category==='messages').length+' / Social '+enabled.filter(a=>a.category==='social').length);
 }
 function filteredApps(){
  const terms=apps.querySelector('.app-search').value.trim().toLowerCase().split(/\s+/).filter(Boolean);
  return data.filter(app=>(category==='all'||category===app.category)&&(appFilter==='all'||(appFilter==='enabled'?app.enabled:!app.enabled))&&terms.every(term=>(app.label+' '+app.package+' '+app.category).toLowerCase().includes(term)));
 }
 function renderApps(){
  const terms=apps.querySelector('.app-search').value.trim().toLowerCase().split(/\s+/).filter(Boolean);
  const visible=filteredApps();
  const allEnabled=visible.length>0&&visible.every(app=>app.enabled),bulk=apps.querySelector('.enable-all');
  bulk.disabled=!visible.length;
  bulk.textContent=allEnabled?ui.disable_all:ui.enable_all;
  bulk.setAttribute('aria-label',allEnabled?ui.disable_all_description:ui.enable_all_description);
  apps.querySelectorAll('button[data-app-category]').forEach(button=>button.setAttribute('aria-pressed',String(button.dataset.appCategory===category)));
  apps.querySelector('.app-result-count').textContent=visible.length+' '+(visible.length===1?'app':'apps');
  const filter=apps.querySelector('.enabled-filter'),labels={all:ui.all_apps,enabled:ui.enabled_only,disabled:ui.disabled_only},next={all:'enabled',enabled:'disabled',disabled:'all'};
  filter.dataset.mode=appFilter;filter.textContent=labels[appFilter];
  filter.setAttribute('aria-label',ui.app_filter_description.replace('%1$s',labels[appFilter]).replace('%2$s',labels[next[appFilter]]));
  apps.querySelector('.app-list').innerHTML=visible.length?visible.map(app=>'<label class="source-row"><span class="app-icon" style="background:'+app.color+'" aria-hidden="true">'+app.letter+'</span><span class="app-name">'+escapeText(app.label)+(app.help?'<span class="app-help">'+escapeText(app.help)+'</span>':'')+'</span><input class="source-toggle" type="checkbox" role="switch" aria-label="'+escapeText(app.label)+'" data-source="'+app.id+'" '+(app.enabled?'checked':'')+'><span class="switch"></span></label>').join(''):'<p class="empty-apps">'+escapeText(appFilter==='enabled'&&!terms.length&&category==='all'?ui.no_enabled_apps:ui.no_matching_apps)+'</p>';
  updateCounts();
 }
 phone.querySelectorAll('.animations').forEach(card=>{
  renderCatalog(card);
  card.querySelector('.animation-search').addEventListener('input',()=>{card.dataset.page=0;renderCatalog(card)});
  const pager=card.querySelector('.animation-pager');
  let start=null,horizontal=false;
  function change(delta){
   const page=Number(card.dataset.page||0),pages=Number(card.dataset.pages||1);
   if(page+delta<0||page+delta>=pages)return;
   card.dataset.page=page+delta;renderCatalog(card);
   card.querySelector('.animation-grid').animate([{transform:'translateX('+(delta*24)+'px)',opacity:.5},{transform:'translateX(0)',opacity:1}],{duration:160});
  }
  pager.addEventListener('keydown',event=>{if(['ArrowRight','PageDown','ArrowLeft','PageUp'].includes(event.key)){event.preventDefault();change(['ArrowRight','PageDown'].includes(event.key)?1:-1)}});
  pager.addEventListener('pointerdown',event=>{if(event.button!==0)return;start={x:event.clientX,y:event.clientY};horizontal=false});
  pager.addEventListener('pointermove',event=>{
   if(!start)return;
   const dx=event.clientX-start.x,dy=event.clientY-start.y;
   if(!horizontal&&Math.abs(dx)>8&&Math.abs(dx)>Math.abs(dy)*1.2){horizontal=true;pager.setPointerCapture(event.pointerId)}
   if(horizontal){event.preventDefault();card.querySelector('.animation-grid').style.transform='translateX('+(dx*.35)+'px)'}
  });
  pager.addEventListener('pointerup',event=>{
   if(!start)return;
   const dx=event.clientX-start.x,dy=event.clientY-start.y;
   if(horizontal){ignoreClicksUntil=performance.now()+300;if(Math.abs(dx)>Math.max(40,pager.clientWidth*.16)&&Math.abs(dx)>Math.abs(dy)*1.2)change(dx<0?1:-1)}
   card.querySelector('.animation-grid').style.transform='';start=null;horizontal=false;
  });
  pager.addEventListener('pointercancel',()=>{start=null;horizontal=false;card.querySelector('.animation-grid').style.transform=''});
 });
 apps.querySelector('.app-search').addEventListener('input',renderApps);
 phone.addEventListener('change',event=>{
  if(!event.target.matches('.source-toggle'))return;
  data.find(app=>app.id===event.target.dataset.source).enabled=event.target.checked;renderApps();
 });
 phone.addEventListener('click',event=>{
  const tile=event.target.closest('[data-animation]');if(tile){if(performance.now()>=ignoreClicksUntil)animate(tile,tile.dataset.animation);return}
  const animationTab=event.target.closest('button[data-animation-category]');if(animationTab){const card=animationTab.closest('.animations');card.dataset.category=animationTab.dataset.animationCategory;card.dataset.page=0;renderCatalog(card);return}
  const appTab=event.target.closest('button[data-app-category]');if(appTab){category=appTab.dataset.appCategory;renderApps();return}
  if(event.target.closest('.enable-all')){const targets=filteredApps(),enabled=targets.some(app=>!app.enabled);targets.forEach(app=>app.enabled=enabled);renderApps();return}
  if(event.target.closest('.enabled-filter')){appFilter={all:'enabled',enabled:'disabled',disabled:'all'}[appFilter];renderApps();return}
  const control=event.target.closest('[data-action]');if(!control)return;
  if(control.dataset.action==='alerts'){
   alertsEnabled=!alertsEnabled;
   control.setAttribute('aria-pressed',String(alertsEnabled));
   control.setAttribute('aria-description',alertsEnabled?ui.glyph_alerts_on:ui.glyph_alerts_off);
   home.querySelectorAll('.status-controls,.home-sections').forEach(group=>{
    group.style.opacity=alertsEnabled?'1':'.4';
    group.inert=!alertsEnabled;
    group.querySelectorAll('button,input').forEach(item=>item.disabled=!alertsEnabled);
   });
   if(!alertsEnabled)home.querySelectorAll('.animation').forEach(tile=>{running.delete(tile);drawFrame(tile,tile.dataset.animation,50)});
   phone.querySelector('.readiness').textContent=alertsEnabled?ui.ready:ui.paused;
   phone.querySelector('.readiness-detail').textContent=alertsEnabled?ui.ready_detail:ui.paused_detail;
  }
  if(control.dataset.action==='details'){const details=phone.querySelector('.details');details.hidden=!details.hidden;control.textContent=details.hidden?'Details':'Hide details'}
  if(control.dataset.action==='apps'){category='all';appFilter='all';apps.querySelector('.app-search').value='';renderApps();home.hidden=true;apps.hidden=false}
  if(control.dataset.action==='back'){apps.hidden=true;home.hidden=false;updateCounts()}
 });
 renderApps();updateCounts();
});
'''
script=script.replace('UI_STRINGS',json.dumps(strings)).replace('EXAMPLES',json.dumps(examples)).replace('STYLES',json.dumps(styles)).replace('MATRICES',json.dumps(matrices)).replace('FRAMES',json.dumps(animations))
version=strings['footer']
header=f'<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Notify Glyph {version} - UI layout preview</title><style>{css}</style><header><h1>Notify Glyph / {version}</h1><p>Native-interface reconstruction, not Android screenshots. Frames come from the actual renderer.<br>App icons are illustrative monograms. The Android app uses installed app icons. Browser previews do not control a phone.</p></header><main class="previews">'
variants=[]
for title,content,mode in [('Home',home(),'home'),('Animations',animation_card()+apps_summary(),'animations'),('Apps',home(),'apps')]:
    variants.append(f'<div class="variant"><h2>{title}</h2><div class="phone"><div class="screen" data-home {"hidden" if mode=="apps" else ""}>{content}</div><div class="apps-screen" data-apps {"" if mode=="apps" else "hidden"}>{apps_screen()}</div></div></div>')
large=''.join(variants[1:]).replace('class="phone"','class="phone compact"')
for name,content,extra in [('ui-design',''.join(variants),''),('ui-review',''.join(variants[1:]),''),('ui-controls',variants[0]+variants[2],''),('ui-large-text',large,'<style>.variant{width:320px}.phone{--font-scale:2;height:800px}.app-filters{gap:8px}</style>')]:
    path=ROOT/f'dist/Notify-Glyph-{version}-{name}.html'
    path.parent.mkdir(exist_ok=True)
    path.write_text(header+content+f'</main>{extra}<script>{script}</script></html>',encoding='utf-8')
    print(path)
