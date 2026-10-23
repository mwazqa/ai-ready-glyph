package pl.glyphtoy.aiready;

import java.util.List;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;
import static pl.glyphtoy.aiready.AppCatalog.Filter.*;

public class AppCatalogTest {
    private final AppCatalog.Entry chatgpt = new AppCatalog.Entry("com.openai.chatgpt","ChatGPT",
            AnimationStyle.Category.AI,null,null,true);
    private final AppCatalog.Entry claude = new AppCatalog.Entry("com.anthropic.claude","Claude",
            AnimationStyle.Category.AI,null,null,false);
    private final AppCatalog.Entry discord = new AppCatalog.Entry("com.discord","Discord",
            AnimationStyle.Category.MESSAGES,MessagingSource.DISCORD,null,true);
    private final AppCatalog.Entry sms = new AppCatalog.Entry("sms.old","SMS (Messages)",
            AnimationStyle.Category.MESSAGES,MessagingSource.SMS,null,true);
    private final AppCatalog.Entry instagram = new AppCatalog.Entry("com.instagram.android","Instagram",
            AnimationStyle.Category.SOCIAL,null,SocialSource.INSTAGRAM,false);
    private final List<AppCatalog.Entry> apps = List.of(chatgpt,claude,discord,sms,instagram);

    @Test public void enabledFilterWorksAcrossAllCategories() {
        assertEquals(List.of(chatgpt,discord,sms),AppCatalog.filter(apps,null,"",ENABLED));
        assertEquals(List.of(chatgpt),AppCatalog.filter(apps,AnimationStyle.Category.AI,"",ENABLED));
        assertTrue(AppCatalog.filter(apps,AnimationStyle.Category.SOCIAL,"",ENABLED).isEmpty());
        assertEquals(apps,AppCatalog.filter(apps,null,null,ALL));
    }

    @Test public void disabledResultsCombineWithSearchCategoriesAndSelectionChanges() {
        assertEquals(List.of(claude,instagram),AppCatalog.filter(apps,null,"",DISABLED));
        assertEquals(List.of(claude),AppCatalog.filter(apps,AnimationStyle.Category.AI,"anthropic",DISABLED));
        assertTrue(AppCatalog.filter(apps,AnimationStyle.Category.MESSAGES,"",DISABLED).isEmpty());
        AppCatalog.Entry changedDiscord=discord.withEnabled(false);
        assertEquals(List.of(claude,changedDiscord,instagram),
                AppCatalog.filter(List.of(chatgpt,claude,changedDiscord,sms,instagram),null,"",DISABLED));
        assertEquals(List.of(),AppCatalog.filter(List.of(),null,"",DISABLED));
        assertTrue(discord.enabled());
    }

    @Test public void searchAndCategoriesCombineWithoutEnablingAnApp() {
        assertEquals(List.of(sms),AppCatalog.filter(apps,AnimationStyle.Category.MESSAGES," SMS   messages ",ALL));
        assertEquals(List.of(claude),AppCatalog.filter(apps,null,"anthropic",ALL));
        assertTrue(AppCatalog.filter(apps,AnimationStyle.Category.AI,"discord",ALL).isEmpty());
        assertTrue(AppCatalog.filter(apps,null,"chatgpt discord",ALL).isEmpty());
        assertFalse(claude.enabled());
    }

    @Test public void searchIgnoresSystemCasingRules() {
        Locale original=Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(List.of(instagram),AppCatalog.filter(apps,null,"INSTAGRAM",ALL));
        } finally { Locale.setDefault(original); }
    }

    @Test public void changedSelectionsDoNotMutateOtherAppsOrCatalogEntries() {
        AppCatalog.Entry disabled=discord.withEnabled(false);
        assertEquals(discord.id(),disabled.id());
        assertTrue(discord.enabled());
        assertFalse(disabled.enabled());
        assertEquals(List.of(chatgpt,sms),AppCatalog.filter(List.of(chatgpt,claude,disabled,sms,instagram),null,"",ENABLED));
        assertThrows(UnsupportedOperationException.class,()->AppCatalog.filter(apps,null,"",ALL).clear());
    }

    @Test public void smsIdentityFollowsItsLogicalSourceAcrossPackageChanges() {
        AppCatalog.Entry newSms=new AppCatalog.Entry("sms.new","SMS (New app)",
                AnimationStyle.Category.MESSAGES,MessagingSource.SMS,null,true);
        assertEquals(sms.id(),newSms.id());
        assertNotEquals(sms.packageName(),newSms.packageName());
        assertNotEquals(chatgpt.id(),claude.id());
        assertEquals(List.of(),AppCatalog.filter(List.of(),null,"",ALL));
    }
}
