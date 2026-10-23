package pl.glyphtoy.aiready;

import java.util.List;
import java.util.Locale;

final class AppCatalog {
    enum Filter {
        ALL, ENABLED, DISABLED;

        Filter next() {
            return switch (this) {
                case ALL -> ENABLED;
                case ENABLED -> DISABLED;
                case DISABLED -> ALL;
            };
        }
    }

    record Entry(String packageName, String label, AnimationStyle.Category category,
                 MessagingSource messaging, SocialSource social, boolean enabled) {
        String id() {
            return messaging != null ? "messages:" + messaging.id
                    : social != null ? "social:" + social.id : "ai:" + packageName;
        }
        Entry withEnabled(boolean value) { return new Entry(packageName, label, category, messaging, social, value); }
    }

    static List<Entry> filter(List<Entry> apps, AnimationStyle.Category category, String query, Filter filter) {
        String normalized = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        String[] terms = normalized.isEmpty() ? new String[0] : normalized.split("\\s+");
        return apps.stream().filter(app -> {
            if (category != null && app.category() != category) return false;
            if (filter == Filter.ENABLED && !app.enabled() || filter == Filter.DISABLED && app.enabled()) return false;
            String searchable = (app.label() + " " + app.packageName() + " " + app.category().name()).toLowerCase(Locale.ROOT);
            for (String term : terms) if (!searchable.contains(term)) return false;
            return true;
        }).toList();
    }
}
