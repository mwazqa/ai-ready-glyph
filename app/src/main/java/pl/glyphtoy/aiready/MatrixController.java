package pl.glyphtoy.aiready;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import com.nothing.ketchum.GlyphMatrixManager;

final class MatrixController {
    @SuppressLint("StaticFieldLeak")
    private static MatrixController instance;
    static MatrixController get(Context context) {
        if (instance == null) instance = new MatrixController(context.getApplicationContext());
        return instance;
    }

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private GlyphMatrixManager manager;
    private boolean connected, toyBound, pending, animating;
    private int generation;
    private long start;
    private PowerManager.WakeLock wakeLock;
    private AnimationStyle provider = AnimationStyle.GENERIC;
    private final DeviceProfile device = DeviceProfile.current();

    private MatrixController(Context context) { this.context = context; }

    void play() {
        play(new AppSettings(context).previewProvider());
    }

    void play(AnimationStyle provider) {
        if (!checkDevice()) return;
        this.provider = provider;
        handler.removeCallbacks(timeout);
        handler.removeCallbacks(tick);
        pending = true;
        animating = false;
        acquireWakeLock();
        LocalStatus.glyph("Connecting to Glyph Matrix…");
        if (connected) begin(); else {
            connect();
            if (pending && manager != null) handler.postDelayed(timeout, 4000);
        }
    }

    void attachToy() {
        toyBound = true;
        connect();
        if (connected && !animating && !pending) idle();
    }

    void detachToy() {
        toyBound = false;
        if (!animating && !pending) disconnect();
    }

    void stop() {
        pending = false;
        animating = false;
        handler.removeCallbacks(tick);
        handler.removeCallbacks(timeout);
        releaseWakeLock();
        if (connected && manager != null) {
            try {
                if (toyBound) idle(); else manager.closeAppMatrix();
            } catch (Exception ignored) { }
        }
        if (!toyBound) disconnect();
    }

    private void connect() {
        if (!checkDevice()) return;
        if (manager != null) return;
        final int token = ++generation;
        try {
            manager = GlyphMatrixManager.getInstance(context);
            if (manager == null) { fail("Glyph Matrix unavailable. A Nothing Phone (3) is required."); return; }
            manager.init(new GlyphMatrixManager.Callback() {
                @Override public void onServiceConnected(ComponentName name) {
                    if (token != generation || manager == null) return;
                    try {
                        manager.register(device.matrixTarget());
                        connected = true;
                        if (pending) begin(); else if (toyBound) idle();
                    } catch (RuntimeException error) { fail("Could not register Glyph. Check your Nothing OS settings."); }
                }
                @Override public void onServiceDisconnected(ComponentName name) {
                    if (token == generation) fail("Glyph connection interrupted.");
                }
            });
        } catch (RuntimeException error) { fail("Glyph Matrix access failed. Check Nothing OS and Glyph permissions."); }
    }

    private boolean checkDevice() {
        if (device.supportsAnimations()) return true;
        fail(context.getString(R.string.device_unsupported_detail, device.displayName));
        return false;
    }

    private void begin() {
        handler.removeCallbacks(timeout);
        pending = false;
        animating = true;
        start = SystemClock.uptimeMillis();
        acquireWakeLock();
        LocalStatus.glyph(provider.label + " animation sent to Glyph. Check the matrix on the back of your phone.");
        handler.post(tick);
    }

    private final Runnable timeout = () -> fail("Glyph did not respond. Enable Glyph Interface and try again.");
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!animating || manager == null || !connected) return;
            long elapsed = SystemClock.uptimeMillis() - start;
            if (elapsed >= ReadyAnimation.DURATION_MS) { stop(); return; }
            try {
                int[] frame = ReadyAnimation.frame(provider, elapsed);
                if (toyBound) manager.setMatrixFrame(frame); else manager.setAppMatrixFrame(frame);
                handler.postDelayed(this, 50);
            } catch (Exception error) { fail("Could not display the Glyph animation."); }
        }
    };

    private void idle() {
        int[] pixels = new int[625];
        pixels[12 * 25 + 11] = 35;
        pixels[12 * 25 + 13] = 35;
        try { manager.setMatrixFrame(pixels); }
        catch (Exception error) { fail("Could not display the Glyph Toy."); }
    }

    private void fail(String message) {
        LocalStatus.glyph(message);
        pending = false;
        animating = false;
        handler.removeCallbacks(tick);
        handler.removeCallbacks(timeout);
        releaseWakeLock();
        disconnect();
    }

    private void disconnect() {
        ++generation;
        GlyphMatrixManager previous = manager;
        manager = null;
        connected = false;
        if (previous != null) try { previous.unInit(); } catch (RuntimeException ignored) { }
    }

    private void acquireWakeLock() {
        releaseWakeLock();
        PowerManager power = context.getSystemService(PowerManager.class);
        wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "aiready:animation");
        wakeLock.acquire(11000);
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        wakeLock = null;
    }
}
