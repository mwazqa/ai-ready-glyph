package pl.glyphtoy.aiready;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

abstract class GlyphActivity extends Activity {
    static final int TEXT = Color.rgb(245, 245, 245);
    static final int MUTED = Color.rgb(170, 170, 170);
    static final int RED = Color.rgb(215, 25, 32);
    static final int SURFACE = Color.rgb(24, 24, 24);
    static final int LINE = Color.rgb(51, 51, 51);
    final LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    final LinearLayout card(LinearLayout parent, int eyebrow, int heading) {
        LinearLayout card = column();
        card.setPadding(dp(18), dp(20), dp(18), dp(20));
        card.setBackground(shape(SURFACE, LINE, 28));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, -2);
        layout.topMargin = dp(16);
        card.setLayoutParams(layout);
        if (parent != null) parent.addView(card);
        card.addView(caption(eyebrow));
        if (heading != 0) {
            TextView title = text(heading, 23, TEXT);
            title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            title.setAccessibilityHeading(true);
            spaced(card, title, 10);
        }
        return card;
    }

    final TextView caption(int resource) {
        return caption(getString(resource));
    }

    final TextView caption(String content) {
        TextView view = text(content, 11, MUTED);
        view.setTypeface(Typeface.MONOSPACE);
        view.setLetterSpacing(.08f);
        return view;
    }

    final TextView text(int resource, int size, int color) { return text(getString(resource), size, color); }
    final TextView text(String content, int size, int color) {
        TextView view = new TextView(this);
        view.setText(content);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setLineSpacing(dp(3), 1);
        return view;
    }

    final Switch toggle(int resource, boolean checked) { return toggle(getString(resource), checked); }
    final Switch toggle(String title, boolean checked) {
        Switch control = new Switch(this);
        control.setText(title);
        control.setTextColor(TEXT);
        control.setTextSize(15);
        control.setChecked(checked);
        control.setMinHeight(dp(56));
        control.setGravity(Gravity.CENTER_VERTICAL);
        control.setSwitchPadding(dp(14));
        control.setSwitchMinWidth(dp(54));
        control.setShowText(false);
        control.setSplitTrack(false);
        control.setThumbTintList(null);
        control.setTrackTintList(null);
        GradientDrawable track = new GradientDrawable();
        track.setSize(dp(54), dp(32));
        track.setCornerRadius(dp(16));
        track.setPadding(dp(5), dp(2), dp(5), dp(2));
        track.setColor(new ColorStateList(new int[][]{{android.R.attr.state_checked}, {}},
                new int[]{RED, Color.rgb(58, 58, 60)}));
        control.setTrackDrawable(track);
        GradientDrawable thumb = new GradientDrawable();
        thumb.setSize(dp(28), dp(28));
        thumb.setCornerRadius(dp(14));
        thumb.setColor(TEXT);
        thumb.setPadding(dp(3), 0, dp(3), 0);
        control.setThumbDrawable(new InsetDrawable(thumb, 0, dp(2), 0, dp(2)));
        return control;
    }

    final Button button(int resource, boolean primary) { return button(getString(resource), primary); }
    final Button button(String title, boolean primary) {
        Button control = new Button(this);
        control.setText(title);
        control.setAllCaps(false);
        control.setTextSize(14);
        control.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        control.setMinHeight(dp(52));
        control.setMinimumWidth(0);
        control.setPadding(dp(12), dp(10), dp(12), dp(10));
        control.setGravity(Gravity.CENTER);
        control.setStateListAnimator(null);
        styleButton(control, primary, false);
        return control;
    }

    final void styleButton(Button control, boolean primary, boolean selected) {
        int fill = primary ? RED : selected ? TEXT : Color.TRANSPARENT;
        control.setBackgroundTintList(null);
        control.setBackground(shape(fill, primary || selected ? fill : LINE, 26));
        control.setTextColor(selected ? Color.BLACK : TEXT);
    }

    final GradientDrawable shape(int fill, int stroke, int radius) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fill);
        background.setCornerRadius(dp(radius));
        background.setStroke(dp(1), stroke);
        return background;
    }

    final void divider(LinearLayout parent, int margin) {
        View line = new View(this);
        line.setBackgroundColor(LINE);
        spaced(parent, line, margin, dp(1));
    }

    final void spaced(LinearLayout parent, View child, int margin) { spaced(parent, child, margin, -2); }
    final void spaced(LinearLayout parent, View child, int margin, int height) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, height);
        layout.topMargin = dp(margin);
        parent.addView(child, layout);
    }
    final int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
