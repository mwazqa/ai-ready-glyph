package pl.glyphtoy.aiready;

/** Monochrome symbols designed for a circular 25x25 LED matrix. */
final class ReadyAnimation {
    static final int SIZE = 25;
    static final long DURATION_MS = 6000;
    private static final String[] LETTER_A = {"01110", "11011", "11011", "11111", "11011", "11011", "11011"};
    private static final String[] LETTER_I = {"111", "010", "010", "010", "010", "010", "111"};

    static int[] frame(AiProvider provider, long elapsed) {
        int[] pixels = new int[SIZE * SIZE];
        if (elapsed < 0 || elapsed >= DURATION_MS) return pixels;
        double fadeOut = Math.min(1, (DURATION_MS - elapsed) / 700.0);
        double fade = Math.min(1, elapsed / 220.0) * fadeOut;
        switch (provider) {
            case CHATGPT -> knot(pixels, elapsed, fade);
            case CLAUDE -> claudeReference(pixels, elapsed, fadeOut);
            case GEMINI -> star(pixels, elapsed, fadeOut);
            case GENERIC -> letters(pixels, elapsed, fade);
        }
        return pixels;
    }

    private static void knot(int[] pixels, long time, double fade) {
        double progress = clamp(time / 2200.0);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) {
            double drawn = clamp((progress - ChatGptTrace.arrival(x, y)) * 18);
            put(pixels, x, y, ChatGptSymbol.coverage(x, y) * 225 * drawn * fade);
        }
    }

    private static void claudeReference(int[] pixels, long time, double fade) {
        int[] reference = ClaudeFrames.at(time);
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++)
            put(pixels, x, y, reference[y * SIZE + x] * fade);
    }

    private static void star(int[] pixels, long time, double fade) {
        double progress = clamp((time - 130) / 1500.0);
        double growth = progress * progress * (3 - 2 * progress);
        double radius = .35 + 10.35 * growth;
        for (int y = 0; y < SIZE; y++) for (int x = 0; x < SIZE; x++) {
            double dx = Math.abs(x - 12.0), dy = Math.abs(y - 12.0);
            double field = Math.pow(dx / radius, 2.0 / 3) + Math.pow(dy / radius, 2.0 / 3);
            double coverage = clamp((1.07 - field) * 7);
            put(pixels, x, y, coverage * fade * 225);
        }
    }

    private static void letters(int[] pixels, long time, double fade) {
        double reveal = ease(time / 1000.0) * 20;
        drawLetter(pixels, LETTER_A, 3, reveal, fade, time);
        drawLetter(pixels, LETTER_I, 15, reveal, fade, time);
        if (time > 1100 && (time / 550) % 2 == 0) dot(pixels, 12, 21, .8, fade * 220);
    }

    private static void drawLetter(int[] pixels, String[] letter, int offset,
                                   double reveal, double fade, long time) {
        for (int row = 0; row < letter.length; row++) for (int col = 0; col < letter[row].length(); col++) {
            if (letter[row].charAt(col) != '1') continue;
            for (int sy = 0; sy < 2; sy++) for (int sx = 0; sx < 2; sx++) {
                int x = offset + col * 2 + sx, y = 5 + row * 2 + sy;
                double light = 205 + 20 * Math.cos(time / 700.0);
                put(pixels, x, y, light * fade * clamp(reveal - (x - 3)));
            }
        }
    }

    private static void dot(int[] pixels, double cx, double cy, double width, double light) {
        for (int y = (int) Math.floor(cy - width - 1); y <= Math.ceil(cy + width + 1); y++)
            for (int x = (int) Math.floor(cx - width - 1); x <= Math.ceil(cx + width + 1); x++)
                put(pixels, x, y, light * clamp(width + .5 - Math.hypot(x - cx, y - cy)));
    }
    private static void put(int[] pixels, int x, int y, double brightness) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && Math.hypot(x - 12, y - 12) <= 12.5)
            pixels[y * SIZE + x] = Math.max(pixels[y * SIZE + x], (int) Math.round(Math.max(0, Math.min(255, brightness))));
    }
    private static double clamp(double value) { return Math.max(0, Math.min(1, value)); }
    private static double ease(double value) { double t = clamp(value); return 1 - Math.pow(1 - t, 3); }
}
