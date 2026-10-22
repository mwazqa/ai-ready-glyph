package pl.glyphtoy.aiready;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

final class MatrixPreview extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long start = -1;
    private AnimationStyle provider = AnimationStyle.CHATGPT;
    MatrixPreview(Context context) {
        super(context);
        setProvider(AnimationStyle.CHATGPT);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }
    void play() { start = SystemClock.uptimeMillis(); invalidate(); }
    void setProvider(AnimationStyle provider) {
        this.provider = provider;
        start = -1;
        setContentDescription(provider.label + " animation preview. " + provider.description);
        invalidate();
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight()) - 28 * getResources().getDisplayMetrics().density;
        float step = size / 27;
        float offsetX = (getWidth() - step * 24) / 2;
        float offsetY = (getHeight() - step * 24) / 2;
        long elapsed = start < 0 ? 2500 : SystemClock.uptimeMillis() - start;
        boolean active = start >= 0 && elapsed < ReadyAnimation.DURATION_MS;
        int[] frame = ReadyAnimation.frame(provider, active ? elapsed : 2500);
        for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
            if (Math.hypot(x - 12, y - 12) > 12.5) continue;
            int light = frame[y * 25 + x];
            paint.setColor(light > 0 ? Color.rgb(light, light, light) : Color.rgb(38, 38, 38));
            canvas.drawCircle(offsetX + x * step, offsetY + y * step, step * .30f, paint);
        }
        if (active) postInvalidateDelayed(50);
    }
}
