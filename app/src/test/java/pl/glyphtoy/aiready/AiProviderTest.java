package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class AiProviderTest {
    @Test public void recognizesOfficialAndroidPackages() {
        assertEquals(AiProvider.CHATGPT, AiProvider.fromPackage("com.openai.chatgpt"));
        assertEquals(AiProvider.CLAUDE, AiProvider.fromPackage("com.anthropic.claude"));
        assertEquals(AiProvider.GEMINI, AiProvider.fromPackage("com.google.android.apps.bard"));
    }
    @Test public void unknownClientsAndLookalikesUseGenericAnimation() {
        assertEquals(AiProvider.GENERIC, AiProvider.fromPackage("com.example.ai"));
        assertEquals(AiProvider.GENERIC, AiProvider.fromPackage("com.example.chatgpt"));
        assertEquals(AiProvider.GENERIC, AiProvider.fromPackage("com.google.android.googlequicksearchbox"));
        assertEquals(AiProvider.GENERIC, AiProvider.fromPackage(null));
    }
    @Test public void persistedStyleIdsAreStableAndFailToGeneric() {
        for (AiProvider provider : AiProvider.values()) assertEquals(provider, AiProvider.fromId(provider.id));
        assertEquals(AiProvider.GENERIC, AiProvider.fromId(null));
        assertEquals(AiProvider.GENERIC, AiProvider.fromId("unknown"));
    }
}
