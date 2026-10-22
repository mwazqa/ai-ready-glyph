package pl.glyphtoy.aiready;

final class AnimationOutro {
    static final long START_MS = 4800;

    static int[] frame(AnimationStyle style, long elapsed) {
        if (elapsed <= START_MS) return ReadyAnimation.openingFrame(style, elapsed);
        double progress = clamp((elapsed - START_MS) / 1200.0);
        int[] pixels = switch (style) {
            case CHATGPT -> reverse(style, 2200, progress);
            case CLAUDE -> claude(elapsed);
            case GEMINI -> ReadyAnimation.openingFrame(style, Math.round(130 + 1500 * (1 - progress)));
            case GENERIC -> reverse(style, 1000, progress);
            case DISCORD -> discord(progress);
            case MESSENGER -> reverse(style, 1400, progress);
            case WHATSAPP, INSTAGRAM -> reverse(style, 1800, progress);
            case SMS -> sms(elapsed);
            case TIKTOK, X -> reverse(style, 1500, progress);
            case FACEBOOK -> reverse(style, 1700, progress);
        };
        if (style != AnimationStyle.CLAUDE && style != AnimationStyle.DISCORD && style != AnimationStyle.SMS) {
            int[] settled = ReadyAnimation.openingFrame(style, START_MS);
            double blend = clamp((elapsed - START_MS) / 120.0);
            for (int i = 0; i < pixels.length; i++)
                pixels[i] = (int) Math.round(settled[i] * (1 - blend) + pixels[i] * blend);
        }
        if (style == AnimationStyle.GEMINI || style == AnimationStyle.CLAUDE) {
            double dot = clamp((ReadyAnimation.DURATION_MS - elapsed) / 100.0);
            for (int i = 0; i < pixels.length; i++) pixels[i] = (int) Math.round(pixels[i] * dot);
        }
        return pixels;
    }

    private static int[] reverse(AnimationStyle style, long openingDuration, double progress) {
        return ReadyAnimation.openingFrame(style, Math.round(openingDuration * (1 - ease(progress))));
    }

    private static int[] claude(long elapsed) {
        if (elapsed < 5000) return ReadyAnimation.openingFrame(AnimationStyle.CLAUDE, elapsed);
        int[] source = ReadyAnimation.openingFrame(AnimationStyle.CLAUDE, 5000);
        int[] pixels = new int[625];
        double scale = 1 - ease((elapsed - 5000) / 1000.0);
        if (scale <= 0) return pixels;
        if (scale >= 1) return source;
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
            put(pixels, x, y, sample(source, 12 + (x - 12) / scale, 12 + (y - 12) / scale, -1));
        return pixels;
    }

    private static int[] discord(double progress) {
        int[] source = ReadyAnimation.openingFrame(AnimationStyle.DISCORD, START_MS);
        int[] pixels = new int[625];
        double shift = 14 * ease(progress);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
            put(pixels, x, y, sample(source, x + shift, y, 0) + sample(source, x - shift, y, 1));
        return pixels;
    }

    private static int[] sms(long elapsed) {
        int[] source = ReadyAnimation.openingFrame(AnimationStyle.SMS, START_MS);
        double closing = ease((elapsed - 5400) / 600.0);
        int[] outline = ReadyAnimation.openingFrame(AnimationStyle.SMS, Math.round(1000 * (1 - closing)));
        int[] pixels = new int[625];
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++)
            if (!(x >= 6 && x <= 18 && y >= 8 && y <= 15)) pixels[y * 25 + x] = outline[y * 25 + x];
        for (int dot = 0; dot < 3; dot++) {
            double progress = clamp((elapsed - START_MS - (2 - dot) * 150) / 300.0);
            if (progress >= 1) continue;
            double radius = 3 * (1 - ease(progress));
            for (int y = 8; y <= 15; y++) for (int x = 7 + dot * 4; x <= 9 + dot * 4; x++)
                put(pixels, x, y, source[y * 25 + x] * clamp(radius + .5 - Math.hypot(x - 8 - dot * 4, y - 11.5)));
        }
        return pixels;
    }

    private static double sample(int[] source, double x, double y, int half) {
        if (x < -1 || x > 25 || y < -1 || y > 25) return 0;
        int column = (int) Math.floor(x), row = (int) Math.floor(y);
        double light = 0;
        for (int dy = 0; dy <= 1; dy++) for (int dx = 0; dx <= 1; dx++) {
            int sx = column + dx, sy = row + dy;
            if (sx < 0 || sx >= 25 || sy < 0 || sy >= 25 || half == 0 && sx >= 12 || half == 1 && sx < 12) continue;
            double weight = (dx == 0 ? 1 - x + column : x - column) * (dy == 0 ? 1 - y + row : y - row);
            light += source[sy * 25 + sx] * weight;
        }
        return light;
    }

    private static void put(int[] pixels, int x, int y, double brightness) {
        if (Math.hypot(x - 12, y - 12) <= 12.5)
            pixels[y * 25 + x] = (int) Math.round(Math.max(0, Math.min(255, brightness)));
    }

    private static double clamp(double value) { return Math.max(0, Math.min(1, value)); }
    private static double ease(double value) { double t = clamp(value); return t * t * (3 - 2 * t); }
}
