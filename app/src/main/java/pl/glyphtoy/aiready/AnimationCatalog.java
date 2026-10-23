package pl.glyphtoy.aiready;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class AnimationCatalog {
    record Page(List<AnimationStyle> styles, int index, int pages, int total) { }

    static Page page(List<AnimationStyle> library, AnimationStyle.Category category,
                     String query, int requestedPage, int pageSize) {
        if (pageSize < 1) throw new IllegalArgumentException("Page size must be positive.");
        String normalized = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        String[] terms = normalized.isEmpty() ? new String[0] : normalized.split("\\s+");
        List<AnimationStyle> matches = new ArrayList<>();
        for (AnimationStyle style : library) {
            if (category != null && category != style.category) continue;
            String searchable = (style.id + " " + style.label + " " + style.category.name()).toLowerCase(Locale.ROOT);
            boolean include = true;
            for (String term : terms) if (!searchable.contains(term)) { include = false; break; }
            if (include) matches.add(style);
        }
        int pages = Math.max(1, (matches.size() + pageSize - 1) / pageSize);
        int index = Math.max(0, Math.min(requestedPage, pages - 1));
        int start = index * pageSize;
        return new Page(List.copyOf(matches.subList(start, Math.min(start + pageSize, matches.size()))),
                index, pages, matches.size());
    }
}
