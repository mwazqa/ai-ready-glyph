package pl.glyphtoy.aiready;

import java.util.Set;

final class AiAppCatalog {
    static final String GEMINI = "com.google.android.apps.bard";
    static final String GOOGLE = "com.google.android.googlequicksearchbox";
    private static final Set<String> APPS = Set.of(
            "com.openai.chatgpt",
            "com.anthropic.claude",
            GEMINI,
            "com.google.android.apps.labs.language.tailwind",
            "com.google.aiedge.gallery",
            "ai.perplexity.app.android",
            "com.microsoft.copilot",
            "com.microsoft.office.officehubrow",
            "ai.x.grok",
            "com.deepseek.chat",
            "ai.mistral.chat",
            "com.moonshot.kimichat",
            "com.tongyi.intl",
            "ai.qwenlm.chat.android",
            "com.aliyun.tongyi",
            "com.facebook.stella",
            "com.minimax.ai",
            "ai.inflection.pi",
            "com.tencent.hunyuan.app.chat",
            "com.tencent.ima",
            "com.larus.wolf",
            "com.larus.nova",
            "cn.yuewen.ywapp",
            "ai.sarvam.indus",
            "com.yandex.aliceapp",
            "com.sdkit.search.app",
            "com.zhipuai.qingyan",
            "com.zhipu.agent",
            "com.iflytek.spark",
            "com.baidu.newapp",
            "com.singularity.tiangong",
            "ai.skywork.app.android",
            "com.qihoo.namiso",
            "ai.liquid.chatapp",
            "com.skt.nugu.apollo",
            "com.two.chatsutra",
            "ae.tii.falcon.assistant",
            "com.antgroup.leopard.android",
            "com.amazon.dee.app",
            "co.huggingface.chat_ui_android");

    static boolean supports(String packageName, boolean geminiInstalled) {
        if (packageName == null) return false;
        return APPS.contains(packageName) || (geminiInstalled && GOOGLE.equals(packageName));
    }
}
