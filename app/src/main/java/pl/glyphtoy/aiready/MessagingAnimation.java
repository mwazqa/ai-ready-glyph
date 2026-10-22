package pl.glyphtoy.aiready;

final class MessagingAnimation {
    private static final double[] DISCORD_EYES = {8.65, 16.15};
    private static final long[] SIGNAL_WAVES = {2000, 2800};

    static void draw(int[] pixels, AnimationStyle style, long time, double fade) {
        switch (style) {
            case DISCORD -> discord(pixels, time, fade);
            case MESSENGER -> messenger(pixels, time, fade);
            case WHATSAPP -> whatsapp(pixels, time, fade);
            case SMS -> sms(pixels, time, fade);
            default -> { }
        }
    }

    private static void discord(int[] pixels, long time, double fade) {
        int shift = (int) Math.round(7 * (1 - ease((time - 80) / 820.0)));
        double blink = Math.max(pulse(time, 1500, 380), pulse(time, 2450, 380));
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double coverage = MessagingSymbols.coverage(AnimationStyle.DISCORD, x, y);
            int targetX = x + (x < 12 ? -shift : shift);
            put(pixels, targetX, y, coverage * 225 * fade);
            if (blink <= 0) continue;
            for (double eyeX : DISCORD_EYES) {
                double dx = (x - eyeX) / 1.55, dy = y - 12.4;
                double eye = clamp((1 - dx * dx - dy * dy / 4) * 4);
                double lid = clamp((Math.abs(dy) - 2 * (1 - blink)) * 3 + .5);
                put(pixels, targetX, y, (1 - coverage) * eye * lid * 225 * fade);
            }
        }
    }

    private static void messenger(int[] pixels, long time, double fade) {
        double body = ease((time - 500) / 900.0);
        double streak = pulse(time, 1800, 650);
        double streakX = 4 + 17 * clamp((time - 1800) / 650.0);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double coverage = MessagingSymbols.coverage(AnimationStyle.MESSENGER, x, y);
            double reveal = clamp(body * 14 - Math.hypot(x - 12.5, y - 12));
            put(pixels, x, y, coverage * reveal * (225 - 35 * streak) * fade);
            double bolt = (1 - coverage) * clamp((8.2 - Math.hypot(x - 12.5, y - 12)) * 3);
            double draw = clamp(ease(time / 600.0) * 18 - (x - 4));
            double light = Math.max(draw * (1 - body), streak * clamp(1.7 - Math.abs(x - streakX)));
            put(pixels, x, y, bolt * light * 225 * fade);
        }
    }

    private static void whatsapp(int[] pixels, long time, double fade) {
        double phone = ease((time - 100) / 1100.0);
        double bubble = ease((time - 750) / 1050.0);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            double radius = Math.hypot(x - 12.3, y - 12);
            double reveal = radius <= 7.6
                    ? clamp(phone * 26 - (x + y - 12))
                    : clamp(bubble * 25 - (23 - y));
            put(pixels, x, y, MessagingSymbols.coverage(AnimationStyle.WHATSAPP, x, y) * reveal * 225 * fade);
            for (long start : SIGNAL_WAVES) {
                double progress = (time - start) / 650.0;
                if (progress <= 0 || progress >= 1) continue;
                double wave = clamp(1 - Math.abs(radius - (9.5 + 3.5 * progress)) / .8);
                put(pixels, x, y, wave * Math.sin(Math.PI * progress) * 70 * fade);
            }
        }
    }

    private static void sms(int[] pixels, long time, double fade) {
        double outline = ease(time / 1000.0) * 31;
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            if (x >= 6 && x <= 18 && y >= 8 && y <= 15) continue;
            double reveal = clamp(outline - (Math.abs(x - 12) + y - 4));
            put(pixels, x, y, MessagingSymbols.coverage(AnimationStyle.SMS, x, y) * reveal * 225 * fade);
        }
        for (int dot = 0; dot < 3; dot++) {
            long start = 800 + dot * 190L;
            double reveal = ease((time - start) / 230.0);
            double lift = 1.9 * pulse(time, start, 650) + 1.4 * pulse(time, 2100 + dot * 190L, 650);
            for (int y = 7; y <= 15; y++) for (int x = 7 + dot * 4; x <= 9 + dot * 4; x++)
                put(pixels, x, y, sample(AnimationStyle.SMS, x, y + lift) * reveal * 225 * fade);
        }
    }

    private static double sample(AnimationStyle style, int x, double y) {
        int row = (int) Math.floor(y);
        double fraction = y - row;
        return coverage(style, x, row) * (1 - fraction) + coverage(style, x, row + 1) * fraction;
    }

    private static double coverage(AnimationStyle style, int x, int y) {
        return x >= 0 && x < 25 && y >= 0 && y < 25 ? MessagingSymbols.coverage(style, x, y) : 0;
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
