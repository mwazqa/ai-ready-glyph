package pl.glyphtoy.aiready;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

final class DotWordmarkView extends View {
    private static final String[] GLYPHS = {
            "01110100011000111111100011000110001",
            "11111001000010000100001000010011111",
            "00000000000000000000000000000000000",
            "11110100011000111110101001001010001",
            "11111100001000011110100001000011111",
            "01110100011000111111100011000110001",
            "11110100011000110001100011000111110",
            "10001100010101000100001000010000100"
    };
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    DotWordmarkView(Context context) {
        super(context);
        setContentDescription("AI Ready Glyph");
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float step = Math.min(getHeight() / 8f, getWidth() / 56f);
        float top = (getHeight() - step * 6) / 2;
        paint.setColor(Color.WHITE);
        for (int letter = 0; letter < GLYPHS.length; letter++) {
            String pixels = GLYPHS[letter];
            for (int row = 0; row < 7; row++) for (int col = 0; col < 5; col++) {
                if (pixels.charAt(row * 5 + col) == '1')
                    canvas.drawCircle(step + (letter * 6 + col) * step, top + row * step, step * .28f, paint);
            }
        }
        paint.setColor(Color.rgb(215, 25, 32));
        canvas.drawCircle(getWidth() - step * 2, getHeight() / 2f, step * 1.15f, paint);
    }
}
