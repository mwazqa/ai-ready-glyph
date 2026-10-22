package pl.glyphtoy.aiready;

import java.util.Set;

final class SmsPackageResolver {
    static String fallback(String resolvedPackage, Set<String> enabledHandlers) {
        if ("android".equals(resolvedPackage) || "com.android.intentresolver".equals(resolvedPackage)) return null;
        return resolvedPackage != null && !resolvedPackage.isBlank() && enabledHandlers.contains(resolvedPackage)
                ? resolvedPackage : null;
    }
}
