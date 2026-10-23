package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class AnimationStyleTest {
    @Test public void recognizesOfficialAndroidPackages() {
        assertEquals(AnimationStyle.CHATGPT, AnimationStyle.fromPackage("com.openai.chatgpt"));
        assertEquals(AnimationStyle.CLAUDE, AnimationStyle.fromPackage("com.anthropic.claude"));
        assertEquals(AnimationStyle.GEMINI, AnimationStyle.fromPackage("com.google.android.apps.bard"));
        assertEquals(AnimationStyle.GEMINI, AnimationStyle.fromPackage("com.google.android.googlequicksearchbox"));
    }
    @Test public void unknownClientsAndLookalikesUseGenericAnimation() {
        assertEquals(AnimationStyle.GENERIC, AnimationStyle.fromPackage("com.example.ai"));
        assertEquals(AnimationStyle.GENERIC, AnimationStyle.fromPackage("com.example.chatgpt"));
        assertEquals(AnimationStyle.GENERIC, AnimationStyle.fromPackage(null));
    }
    @Test public void persistedStyleIdsAreStableAndFailToGeneric() {
        for (AnimationStyle provider : AnimationStyle.values()) assertEquals(provider, AnimationStyle.fromId(provider.id));
        assertEquals(AnimationStyle.GENERIC, AnimationStyle.fromId(null));
        assertEquals(AnimationStyle.GENERIC, AnimationStyle.fromId("unknown"));
    }
}
