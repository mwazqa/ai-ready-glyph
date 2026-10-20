# Model-provider Android app catalog research

Research: 20-21 October 2026. Finalized: 21 October 2026.

The picker contains **40 exact package identities from 31 model providers**, plus the existing conditional Google host for Gemini. This includes regional variants, one legacy Copilot identity and one official source-only HuggingChat identity. It is an auditable snapshot, not a claim that 40 current apps are downloadable in Poland or that each sends completion notifications.

## Scope and method

The user's final scope is official apps from model providers. Eligible products are conversational assistants, research tools and task agents published by a model creator or its product subsidiary. A provider may also offer other companies' models. Creating or further training its own model family qualifies; merely wrapping another provider's API does not.

Searches covered global products and regional products in China, India, South Korea, Russia and the UAE. English and local-language searches were cross-checked against publisher pages, Google Play, Xiaomi's official app store, RuStore and source code in official GitHub organizations. Store developer names, support domains, package identities, regional variants and renamed products were checked. Third-party APK mirrors and directory articles were discovery leads, not the sole evidence used to add a new identity.

The JSON file records publisher, region, distribution and primary URLs for every entry. Java constants and manifest visibility declarations are generated from that JSON by `scripts/sync_ai_catalog.py`. Exact package matching avoids name-based guesses. It does not validate signing certificates or prove that a sideloaded package is authentic.

## Reviewed identities

Every URL in this table supports the package identity or the official Android source. Additional product and provider evidence is in [the JSON catalog](ai-app-catalog.json). Names shown by the Android picker come from the installed application.

| Application / variant | Model provider | Exact Android package | Primary evidence |
| --- | --- | --- | --- |
| ChatGPT | OpenAI | `com.openai.chatgpt` | [Google Play](https://play.google.com/store/apps/details?id=com.openai.chatgpt) |
| Claude | Anthropic | `com.anthropic.claude` | [Google Play](https://play.google.com/store/apps/details?id=com.anthropic.claude) |
| Gemini | Google | `com.google.android.apps.bard` | [Google Play](https://play.google.com/store/apps/details?id=com.google.android.apps.bard) |
| Gemini Notebook / NotebookLM | Google | `com.google.android.apps.labs.language.tailwind` | [Google Play](https://play.google.com/store/apps/details?id=com.google.android.apps.labs.language.tailwind) |
| Google AI Edge Gallery | Google | `com.google.aiedge.gallery` | [GitHub releases](https://github.com/google-ai-edge/gallery/blob/1.0.20/Android/src/app/build.gradle.kts) |
| Perplexity | Perplexity | `ai.perplexity.app.android` | [Google Play](https://play.google.com/store/apps/details?id=ai.perplexity.app.android) |
| Microsoft Copilot | Microsoft | `com.microsoft.copilot` | [Google Play](https://play.google.com/store/apps/details?id=com.microsoft.copilot) |
| Microsoft 365 Copilot | Microsoft | `com.microsoft.office.officehubrow` | [Google Play](https://play.google.com/store/apps/details?id=com.microsoft.office.officehubrow) |
| Grok | xAI | `ai.x.grok` | [Google Play](https://play.google.com/store/apps/details?id=ai.x.grok) |
| DeepSeek | DeepSeek | `com.deepseek.chat` | [Google Play](https://play.google.com/store/apps/details?id=com.deepseek.chat) |
| Mistral / Le Chat / Vibe | Mistral AI | `ai.mistral.chat` | [Google Play](https://play.google.com/store/apps/details?id=ai.mistral.chat) |
| Kimi | Moonshot AI | `com.moonshot.kimichat` | [Google Play](https://play.google.com/store/apps/details?id=com.moonshot.kimichat) |
| Qwen | Alibaba | `com.tongyi.intl` | [Google Play](https://play.google.com/store/apps/details?id=com.tongyi.intl) |
| Qwen Studio | Alibaba | `ai.qwenlm.chat.android` | [Google Play](https://play.google.com/store/apps/details?id=ai.qwenlm.chat.android) |
| Qwen / Tongyi (China) | Alibaba | `com.aliyun.tongyi` | [Xiaomi app store](https://app.mi.com/details?id=com.aliyun.tongyi) |
| Meta AI | Meta | `com.facebook.stella` | [Google Play](https://play.google.com/store/apps/details?id=com.facebook.stella) |
| MiniMax Agent | MiniMax | `com.minimax.ai` | [Google Play](https://play.google.com/store/apps/details?id=com.minimax.ai) |
| Pi | Inflection AI | `ai.inflection.pi` | [Google Play](https://play.google.com/store/apps/details?id=ai.inflection.pi) |
| Tencent Yuanbao | Tencent | `com.tencent.hunyuan.app.chat` | [Google Play](https://play.google.com/store/apps/details?id=com.tencent.hunyuan.app.chat) |
| ima | Tencent | `com.tencent.ima` | [Xiaomi app store](https://app.mi.com/details?id=com.tencent.ima) |
| Dola / Cici | ByteDance | `com.larus.wolf` | [Google Play](https://play.google.com/store/apps/details?gl=JP&hl=JA&id=com.larus.wolf) |
| Doubao | ByteDance | `com.larus.nova` | [Xiaomi app store](https://app.mi.com/details?id=com.larus.nova) |
| StepFun / Yuewen | StepFun | `cn.yuewen.ywapp` | [Google Play](https://play.google.com/store/apps/details?id=cn.yuewen.ywapp) |
| Indus by Sarvam | Sarvam AI | `ai.sarvam.indus` | [Google Play](https://play.google.com/store/apps/details?id=ai.sarvam.indus) |
| Alice AI | Yandex | `com.yandex.aliceapp` | [Google Play](https://play.google.com/store/apps/details?id=com.yandex.aliceapp) |
| GigaChat | Sber | `com.sdkit.search.app` | [RuStore](https://www.rustore.ru/catalog/app/com.sdkit.search.app) |
| Qingyan / ChatGLM | Zhipu AI | `com.zhipuai.qingyan` | [Xiaomi app store](https://app.mi.com/details?id=com.zhipuai.qingyan) |
| AutoGLM | Zhipu AI | `com.zhipu.agent` | [Xiaomi app store](https://app.mi.com/details?id=com.zhipu.agent) |
| iFlytek Spark | iFlytek | `com.iflytek.spark` | [Xiaomi app store](https://app.mi.com/details?id=com.iflytek.spark) |
| Wenxin / ERNIE / Wenxiaoyan | Baidu | `com.baidu.newapp` | [Xiaomi app store](https://app.mi.com/details?id=com.baidu.newapp) |
| Tiangong | Kunlun / Skywork | `com.singularity.tiangong` | [Xiaomi app store](https://app.mi.com/details?id=com.singularity.tiangong) |
| Skywork | Kunlun / Skywork | `ai.skywork.app.android` | [Google Play](https://play.google.com/store/apps/details?id=ai.skywork.app.android) |
| Nami AI | 360 | `com.qihoo.namiso` | [Xiaomi app store](https://app.mi.com/details?id=com.qihoo.namiso) |
| Liquid Apollo | Liquid AI | `ai.liquid.chatapp` | [Google Play](https://play.google.com/store/apps/details?id=ai.liquid.chatapp) |
| A.dot | SK Telecom | `com.skt.nugu.apollo` | [Google Play](https://play.google.com/store/apps/details?id=com.skt.nugu.apollo) |
| ChatSUTRA | TWO AI | `com.two.chatsutra` | [Google Play](https://play.google.com/store/apps/details?id=com.two.chatsutra) |
| Falcon.AI | TII | `ae.tii.falcon.assistant` | [Google Play](https://play.google.com/store/apps/details?id=ae.tii.falcon.assistant) |
| Lingguang | Ant Group | `com.antgroup.leopard.android` | [Xiaomi app store](https://app.mi.com/details?id=com.antgroup.leopard.android) |
| Amazon Alexa / Alexa+ | Amazon | `com.amazon.dee.app` | [Google Play](https://play.google.com/store/apps/details?id=com.amazon.dee.app) |
| HuggingChat (official Android source) | Hugging Face | `co.huggingface.chat_ui_android` | [Official GitHub source](https://github.com/huggingface/chat-ui-android/blob/main/app/build.gradle.kts) |

## Classification and identity details

- **Perplexity stays:** its [Sonar announcement](https://www.perplexity.ai/en-GB/hub/blog/meet-new-sonar) describes an in-house model further trained from Llama. It is not treated as a provider solely because its app exposes other models.
- **Microsoft stays:** it publishes its own [MAI models](https://microsoft.ai/models/). The old `com.microsoft.copilot` identity remains eligible for existing installations, but direct Play requests returned 404. Current distribution for that identity is unconfirmed. The separate `com.microsoft.office.officehubrow` listing is currently titled Microsoft Copilot.
- **Qwen has three verified identities:** international Qwen, Qwen Studio and the mainland Qwen/Tongyi app. They are separate packages, not three spellings of one ID.
- **Dola and Cici share one identity:** `com.larus.wolf`. The Japanese Play listing was readable while the unqualified URL returned 404. Mainland Doubao uses `com.larus.nova`. The [ByteDance Seed site](https://seed.bytedance.com/en/) links its products to Dola.
- **Google AI Edge Gallery:** [release 1.0.20 source](https://github.com/google-ai-edge/gallery/blob/1.0.20/Android/src/app/build.gradle.kts) declares `applicationId = "com.google.aiedge.gallery"`. The similar Kotlin namespace `com.google.ai.edge.gallery` is not its installed package ID.
- **HuggingChat:** the official Hugging Face repository confirms `co.huggingface.chat_ui_android`. The inspected [v1.4 release](https://github.com/huggingface/chat-ui-android/releases/tag/v1.4) contains no APK assets. The identity is included for source-built or existing installations; no current store availability or server compatibility is claimed. Hugging Face publishes its own [SmolLM models](https://huggingface.co/blog/smollm).
- **GigaChat:** the [Sber listing in RuStore](https://www.rustore.ru/catalog/app/com.sdkit.search.app) uses `com.sdkit.search.app`. A similarly named `com.gigachat.app` search result does not establish an official Sber identity.
- **NotebookLM / Gemini Notebook, Le Chat / Vibe, Yuewen / StepFun and Wenxiaoyan / Wenxin / ERNIE:** aliases preserve discovery across renamed products; the package identity, rather than the visible name, determines eligibility.
- **Amazon Alexa:** included as Amazon's conversational assistant. [Alexa+](https://alexa.amazon.com/about) supports typed and spoken conversations; access varies by region. The same app also posts device or shopping alerts, which may match broad detection mode.
- **Google host:** `com.google.android.googlequicksearchbox` remains an explicit optional source only when Gemini is installed and enabled. It is not counted as another AI product. Other Google alerts can also match its notification rules.

## Excluded products

These are scope decisions, not claims that the products are unofficial or poor quality.

| Product / group | Reason |
| --- | --- |
| Poe, Monica, Sider, Merlin, You.com, Liner, ChatLLM, Genspark, Ninja AI, Luzia, ChatOn, Ask AI, Genie and Nova | Third-party clients, aggregators or agent products without a verified model-provider basis in this review. The user selected the narrower provider scope. Poe is removed from the picker. |
| Manus | Third-party agent product outside the selected model-provider-client scope; not added on the strength of corporate affiliation or ownership alone. |
| Character.AI, Replika, Talkie, PolyBuzz, Chai, Nomi and Kindroid | Roleplay / companion-first products outside this work-and-research assistant catalog. This is a product-scope exclusion; it does not imply they lack their own models. |
| Obywatel Bielik | The [official Play description](https://play.google.com/store/apps/details?id=pl.obywatel.bielik) describes contributing and annotating photographs for model training, rather than chatting with an assistant. |
| Baixiaoyi, formerly Baixiaoying | The [official Baichuan store listing](https://app.mi.com/details?id=com.baichuan.baiying) is now a dedicated AI medical assistant. Specialized medical apps are outside this catalog. |
| Sora, Hailuo, Seedance, Dreamina, Kling, Runway, Suno and other media-only products | Video, image or music creation without the general conversational / research assistant role covered here. |
| Bing, Edge, Comet, Quark, Opera / Aria, Brave / Leo, Duck.ai in a browser, GitHub, WhatsApp, Messenger, Instagram and ordinary productivity suites | General browser, social or productivity hosts. Their unrelated notifications cannot be distinguished by package identity alone. The existing conditional Gemini host is the explicit exception. |
| Samsung Bixby, Huawei Xiaoyi and other OEM-integrated system assistants | Device-specific system integrations, not standalone assistant clients intended for installation on Nothing Phone (3). |

## Candidates without enough evidence for inclusion

Search failure is not evidence that an app does not exist. These candidates remain visible here so a future review can close the gaps without guessing package IDs.

| Provider / product | Review outcome and next evidence needed |
| --- | --- |
| SenseTime SenseChat | [Official product page](https://www.sensetime.com/cn/product-detail?categoryId=51134632&gioNav=1) confirms the assistant. Third-party sites report `com.sensetime.chat`, but a primary package listing or official APK identity was not verified. Do not substitute the guessed `com.sensetime.sensechat`. |
| Z.ai international app | [Official site](https://z.ai/) and Chinese Qingyan / AutoGLM were checked. No separate international Android package was verified. Chinese official packages are included. |
| Krutrim / Kruti | The candidate Play URL for `com.app.krutrim.prod` returned 404. No current primary download and publisher chain was established. [Official Krutrim site](https://www.olakrutrim.com/). |
| Hanooman | Results exposed conflicting product and package identities. Neither current distribution nor a reliable primary Android package chain was established. [Publisher product site reviewed](https://hanooman.ai/about-us). |
| SK Telecom Aster | [SKT announcement](https://news.sktelecom.com/208264) confirms the product; no current Android package was verified. A.dot is included separately. |
| 01.AI / Wanzhi | No current official Android package was verified. [01.AI site](https://www.lingyiwanwu.com/). |
| Core42 / Jais | Official model and [Compass web documentation](https://www.core42.ai/compass/documentation/web) were found. No current official Android chat package was verified. |
| MBZUAI / K2 Think | No current official Android package was verified. Do not add unrelated apps named Think AI. |
| LG AI Research / ChatEXAONE | [Official LG announcement](https://lg.co.kr/media/release/28438) confirms the enterprise product. No public Android package was verified. |
| Cohere / North | [Official North page](https://cohere.com/blog/north-ga) describes the enterprise platform. No public Android package was verified. |
| Nous Research / Hermes | [Official documentation](https://hermes-agent.nousresearch.com/docs) includes Termux installation. Termux support is not a distinct official Android assistant package. |
| Nexa AI | [Official Android SDK documentation](https://docs.nexa.ai/en/nexa-sdk-android/overview) confirms an SDK, not a separately verified consumer chat application. Similarly named Play publishers do not prove ownership. |
| AI21, Reka, Arcee, Inception, Allen AI, IBM, NVIDIA, Upstage and Sakana AI | No qualifying standalone official Android assistant package was established in this review. This does not rule out web apps, enterprise clients, SDK demos or later releases. |

## Coverage and runtime limits

This is a deliberately broad provider catalog, not a guarantee of worldwide completeness. Listings change, regional stores differ, and closed enterprise distributions may not be searchable. The JSON records the reviewed state and should be checked again before a public release.

Catalog inclusion confirms a package identity, not end-to-end Glyph support. No newly added app was installed or used to send a real completion notification in this review. On-device clients may never post one. Regional wording may need a custom phrase. Existing English / Polish detection, manual animation assignments and all four six-second animations are unchanged.

Only installed, launchable catalog entries appear on the phone. The app does not fetch the catalog or request INTERNET / QUERY_ALL_PACKAGES. Saved selections are not migrated or cleared; this remains a local test build.
