package pl.glyphtoy.aiready;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

public class AnimationCatalogTest {
    private final List<AnimationStyle> library = List.of(AnimationStyle.values());

    @Test public void pagesCoverTheLibraryWithoutSkippingOrRepeatingEntries() {
        for (int size : new int[]{1, 2, 4, 7}) {
            List<AnimationStyle> visited = new ArrayList<>();
            AnimationCatalog.Page first = AnimationCatalog.page(library, null, "", 0, size);
            for (int index = 0; index < first.pages(); index++) {
                AnimationCatalog.Page page = AnimationCatalog.page(library, null, "", index, size);
                assertTrue(page.styles().size() <= size);
                assertEquals(library.size(), page.total());
                assertEquals(index, page.index());
                visited.addAll(page.styles());
            }
            assertEquals(library, visited);
        }
    }

    @Test public void categoryAndSearchApplyTogetherBeforePaging() {
        AnimationCatalog.Page messages = AnimationCatalog.page(library, AnimationStyle.Category.MESSAGES, "", 0, 2);
        assertEquals(List.of(AnimationStyle.DISCORD, AnimationStyle.MESSENGER), messages.styles());
        assertEquals(4, messages.total());
        AnimationCatalog.Page sms = AnimationCatalog.page(library, AnimationStyle.Category.MESSAGES, "  sMs  ", 20, 2);
        assertEquals(List.of(AnimationStyle.SMS), sms.styles());
        assertEquals(0, sms.index());
        assertEquals(1, sms.pages());
        assertTrue(AnimationCatalog.page(library, AnimationStyle.Category.SOCIAL, "sms", 0, 4).styles().isEmpty());
    }

    @Test public void searchingUsesEveryWordAndIsIndependentOfSystemLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(List.of(AnimationStyle.INSTAGRAM),
                    AnimationCatalog.page(library, null, "INSTAGRAM", 0, 4).styles());
            assertEquals(List.of(AnimationStyle.GENERIC),
                    AnimationCatalog.page(library, null, "  OTHER   AI  ", 0, 4).styles());
            assertTrue(AnimationCatalog.page(library, null, "other discord", 0, 4).styles().isEmpty());
        } finally { Locale.setDefault(original); }
    }

    @Test public void stalePageNumbersAndEmptyResultsRemainNavigable() {
        AnimationCatalog.Page last = AnimationCatalog.page(library, null, null, Integer.MAX_VALUE, 4);
        assertEquals(2, last.index());
        assertEquals(library.subList(8, 12), last.styles());
        assertEquals(0, AnimationCatalog.page(library, null, "", -100, 4).index());
        AnimationCatalog.Page empty = AnimationCatalog.page(library, null, "missing-symbol", 9, 4);
        assertEquals(List.of(), empty.styles());
        assertEquals(0, empty.index());
        assertEquals(1, empty.pages());
        assertEquals(0, empty.total());
        assertEquals(0, AnimationCatalog.page(List.of(), null, "", 20, 4).total());
    }

    @Test public void pageResultsCannotMutateTheLibrary() {
        AnimationCatalog.Page page = AnimationCatalog.page(library, null, "", 0, 4);
        assertThrows(UnsupportedOperationException.class, () -> page.styles().clear());
        assertEquals(List.of(AnimationStyle.values()), library);
    }

    @Test public void invalidPageSizesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> AnimationCatalog.page(library, null, "", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> AnimationCatalog.page(library, null, "", 0, -1));
    }
}
