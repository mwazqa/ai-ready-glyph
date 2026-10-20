package pl.glyphtoy.aiready;

import android.os.Build;
import java.util.Locale;

final class DeviceProfile {
    private enum Model {
        PHONE_1("Phone (1)", "A063", 0),
        PHONE_2("Phone (2)", "A065", 0),
        PHONE_2A("Phone (2a)", "A142", 0),
        PHONE_2A_PLUS("Phone (2a) Plus", "A142P", 0),
        PHONE_3A("Phone (3a)", "A059", 0),
        PHONE_3A_PRO("Phone (3a) Pro", "A059P", 0),
        PHONE_3("Phone (3)", "A024", 25),
        PHONE_4A("Phone (4a)", "A069", 0),
        PHONE_4A_PRO("Phone (4a) Pro", "A069P", 13);

        final String label, code;
        final int matrixLength;
        Model(String label, String code, int matrixLength) {
            this.label = label;
            this.code = code;
            this.matrixLength = matrixLength;
        }
    }

    final String displayName;
    final boolean nothing;
    final int matrixLength;
    private final Model model;

    private DeviceProfile(String displayName, boolean nothing, Model model) {
        this.displayName = displayName;
        this.nothing = nothing;
        this.model = model;
        this.matrixLength = model == null ? 0 : model.matrixLength;
    }

    static DeviceProfile current() {
        return detect(Build.MANUFACTURER, Build.BRAND, Build.MODEL);
    }

    static DeviceProfile detect(String manufacturer, String brand, String rawModel) {
        String maker = clean(manufacturer);
        String productBrand = clean(brand);
        String identity = clean(rawModel);
        boolean nothing = !productBrand.equalsIgnoreCase("CMF")
                && (isNothing(maker) || isNothing(productBrand));
        if (nothing) {
            for (Model candidate : Model.values()) {
                boolean codeMatches = identity.equalsIgnoreCase(candidate.code)
                        || (candidate == Model.PHONE_2 && identity.equalsIgnoreCase("AIN065"));
                String alias = normalize(identity);
                String expected = normalize(candidate.label);
                if (codeMatches || alias.equals(expected) || alias.equals("NOTHING" + expected))
                    return new DeviceProfile("Nothing " + candidate.label, true, candidate);
            }
            return new DeviceProfile(identity.isEmpty() ? "Nothing device"
                    : identity.toLowerCase(Locale.ROOT).startsWith("nothing ") ? identity
                    : "Nothing " + identity, true, null);
        }
        String label = identity.isEmpty() ? "Android device" : identity;
        String vendor = productBrand.equalsIgnoreCase("CMF") ? "CMF" : maker;
        if (!identity.isEmpty() && !vendor.isEmpty()
                && !identity.toLowerCase(Locale.ROOT).startsWith(vendor.toLowerCase(Locale.ROOT)))
            label = vendor + " " + identity;
        return new DeviceProfile(label, false, null);
    }

    boolean hasGlyphMatrix() { return matrixLength > 0; }

    boolean supportsAnimations() { return model == Model.PHONE_3; }

    String matrixTarget() { return supportsAnimations() ? model.code : null; }

    private static boolean isNothing(String value) {
        return value.equalsIgnoreCase("Nothing") || value.equalsIgnoreCase("Nothing Technology Limited");
    }

    private static String clean(String value) {
        if (value == null || value.trim().equalsIgnoreCase("unknown")) return "";
        return value.trim();
    }

    private static String normalize(String value) {
        return value.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }
}
