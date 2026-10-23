package pl.glyphtoy.aiready;

record PlaybackStatus(Phase phase, AnimationStyle style, long endsAt, String error) {
    enum Phase { IDLE, WAITING, CONNECTING, PLAYING, ERROR }
    static PlaybackStatus idle() { return new PlaybackStatus(Phase.IDLE, AnimationStyle.GENERIC, 0, ""); }
    static PlaybackStatus waiting(AnimationStyle style, long endsAt) { return new PlaybackStatus(Phase.WAITING, style, endsAt, ""); }
    static PlaybackStatus connecting(AnimationStyle style) { return new PlaybackStatus(Phase.CONNECTING, style, 0, ""); }
    static PlaybackStatus playing(AnimationStyle style, long endsAt) { return new PlaybackStatus(Phase.PLAYING, style, endsAt, ""); }
    static PlaybackStatus failed(AnimationStyle style, String error) { return new PlaybackStatus(Phase.ERROR, style, 0, error); }
    boolean active() { return phase == Phase.WAITING || phase == Phase.CONNECTING || phase == Phase.PLAYING; }
    int secondsRemaining(long now) {
        if (phase != Phase.WAITING && phase != Phase.PLAYING) return 0;
        return (int) Math.max(0, (endsAt - now + 999) / 1000);
    }
}
