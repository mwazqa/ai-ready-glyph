package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class AiAppCatalogTest {
    @Test public void acceptsOfficialProviderClientsAndRegionalVariants() {
        for (String name : new String[]{"com.openai.chatgpt", "com.anthropic.claude",
                "com.google.android.apps.bard", "ai.perplexity.app.android", "com.microsoft.copilot",
                "com.microsoft.office.officehubrow", "ai.x.grok", "com.deepseek.chat",
                "ai.mistral.chat", "com.moonshot.kimichat", "com.tongyi.intl",
                "ai.qwenlm.chat.android", "com.aliyun.tongyi", "com.larus.wolf", "com.larus.nova",
                "com.zhipuai.qingyan", "com.zhipu.agent", "com.sdkit.search.app",
                "ae.tii.falcon.assistant", "ai.sarvam.indus", "com.google.aiedge.gallery",
                "co.huggingface.chat_ui_android"}) assertTrue(name, AiAppCatalog.supports(name, false));
    }

    @Test public void rejectsOrdinaryAppsAndAiLookalikes() {
        for (String name : new String[]{"com.whatsapp", "com.android.chrome", "com.example.chatgpt",
                "com.openai.chatgpt.clone", "com.finance.ai", "", "COM.OPENAI.CHATGPT"})
            assertFalse(name, AiAppCatalog.supports(name, true));
        assertFalse(AiAppCatalog.supports(null, true));
    }

    @Test public void googleHostIsEligibleOnlyWhenGeminiIsInstalled() {
        assertFalse(AiAppCatalog.supports(AiAppCatalog.GOOGLE, false));
        assertTrue(AiAppCatalog.supports(AiAppCatalog.GOOGLE, true));
    }

    @Test public void excludesThirdPartyWrappersAndEmbeddedHosts() {
        for (String name : new String[]{"com.poe.android", "im.monica.app.monica",
                "ai.sider.ChatGPT.android", "tech.butterfly.app", "com.abacusai.chatllm",
                "com.whatsapp", "com.microsoft.emmx", "com.android.chrome"})
            assertFalse(name, AiAppCatalog.supports(name, true));
    }

    @Test public void namespaceAndSimilarBrandNamesDoNotGrantEligibility() {
        assertFalse(AiAppCatalog.supports("com.google.ai.edge.gallery", false));
        assertFalse(AiAppCatalog.supports("com.gigachat.app", false));
        assertFalse(AiAppCatalog.supports("com.huggingface.chat", false));
        assertFalse(AiAppCatalog.supports("com.tongyi.intl.clone", false));
    }

}
