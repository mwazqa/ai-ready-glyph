package pl.glyphtoy.aiready;

final class SocialAnimation {
    static void draw(int[] pixels, AnimationStyle style, long time, double fade) {
        switch (style) {
            case INSTAGRAM -> instagram(pixels, time, fade);
            case TIKTOK -> tiktok(pixels, time, fade);
            case X -> x(pixels, time, fade);
            case FACEBOOK -> facebook(pixels, time, fade);
            default -> { }
        }
    }

    private static void instagram(int[] pixels, long time, double fade) {
        double frame = ease(time / 1000.0) * 13;
        double lens = ease((time - 600) / 1100.0);
        double flash = clamp((time - 1500) / 250.0);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double coverage = SocialSymbols.coverage(AnimationStyle.INSTAGRAM, x, y);
            double radius = Math.hypot(x - 12, y - 12);
            if (radius <= 6) {
                double ring = lens >= 1 ? coverage
                        : clamp(1 - Math.abs(radius - (.35 + 4.15 * lens)) / .95) * clamp((time - 600) / 150.0);
                put(pixels, x, y, ring * 225 * fade);
            } else if (Math.hypot(x - 18, y - 6) <= 1.6) {
                put(pixels, x, y, coverage * flash * (225 + 25 * pulse(time, 1900, 600)) * fade);
            } else {
                double corner = Math.min(Math.min(Math.hypot(x - 5, y - 5), Math.hypot(x - 19, y - 5)),
                        Math.min(Math.hypot(x - 5, y - 19), Math.hypot(x - 19, y - 19)));
                put(pixels, x, y, coverage * clamp(frame - corner) * 225 * fade);
            }
        }
    }

    private static void tiktok(int[] pixels, long time, double fade) {
        double reveal = ease(time / 1500.0) * 26;
        double echo = pulse(time, 1700, 1200);
        double shift = 2.2 * clamp((time - 1700) / 1200.0);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double coverage = SocialSymbols.coverage(AnimationStyle.TIKTOK, x, y);
            put(pixels, x, y, coverage * clamp(reveal - (23 - y)) * 225 * fade);
            if (echo > 0) {
                put(pixels, x, y, sample(AnimationStyle.TIKTOK, x - shift, y) * echo * 75 * fade);
                put(pixels, x, y, sample(AnimationStyle.TIKTOK, x + shift, y) * echo * 75 * fade);
            }
        }
    }

    private static void x(int[] pixels, long time, double fade) {
        double reach = ease(time / 1500.0) * 16;
        double joined = pulse(time, 1550, 800);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double tip = Math.min(Math.min(Math.hypot(x - 4, y - 3), Math.hypot(x - 20, y - 3)),
                    Math.min(Math.hypot(x - 4, y - 21), Math.hypot(x - 20, y - 21)));
            double junction = clamp(4 - Math.hypot(x - 12, y - 12));
            double light = 225 + 25 * joined * junction;
            put(pixels, x, y, SocialSymbols.coverage(AnimationStyle.X, x, y) * clamp(reach - tip) * light * fade);
        }
    }

    private static void facebook(int[] pixels, long time, double fade) {
        double stem = ease(time / 1400.0) * 26;
        double crossbar = ease((time - 800) / 900.0) * 10;
        double signal = pulse(time, 1800, 800);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            boolean bar = y >= 9 && y <= 13;
            double reveal = bar && (x < 10 || x > 14)
                    ? clamp(crossbar - Math.abs(x - 12)) : clamp(stem - (23 - y));
            double light = 225 + (bar ? 25 * signal : 0);
            put(pixels, x, y, SocialSymbols.coverage(AnimationStyle.FACEBOOK, x, y) * reveal * light * fade);
        }
    }

    private static double sample(AnimationStyle style, double x, int y) {
        int column = (int) Math.floor(x);
        double fraction = x - column;
        return coverage(style, column, y) * (1 - fraction) + coverage(style, column + 1, y) * fraction;
    }

    private static double coverage(AnimationStyle style, int x, int y) {
        return x >= 0 && x < 25 && y >= 0 && y < 25 ? SocialSymbols.coverage(style, x, y) : 0;
    }

    private static void put(int[] pixels, int x, int y, double brightness) {
        if (x >= 0 && x < 25 && y >= 0 && y < 25 && Math.hypot(x - 12, y - 12) <= 12.5)
            pixels[y * 25 + x] = Math.max(pixels[y * 25 + x], (int) Math.round(Math.max(0, Math.min(255, brightness))));
    }

    private static double pulse(long time, long start, long duration) {
        double progress = (time - start) / (double) duration;
        return progress <= 0 || progress >= 1 ? 0 : Math.sin(Math.PI * progress);
    }

    private static double clamp(double value) { return Math.max(0, Math.min(1, value)); }
    private static double ease(double value) { double t = clamp(value); return 1 - Math.pow(1 - t, 3); }
}
