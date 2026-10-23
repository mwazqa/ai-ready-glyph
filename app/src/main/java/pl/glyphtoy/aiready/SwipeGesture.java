package pl.glyphtoy.aiready;

final class SwipeGesture {
    static boolean horizontal(float dx, float dy, float slop) {
        return Math.abs(dx) > slop && Math.abs(dx) > Math.abs(dy) * 1.2f;
    }

    static int pageDelta(float dx, float dy, float width, float minimum, boolean rtl) {
        if (!horizontal(dx, dy, Math.max(minimum, width * .16f))) return 0;
        int delta = dx < 0 ? 1 : -1;
        return rtl ? -delta : delta;
    }
}
