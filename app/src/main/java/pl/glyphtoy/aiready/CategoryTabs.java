package pl.glyphtoy.aiready;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class CategoryTabs extends HorizontalScrollView {
    private record Tab(AnimationStyle.Category category, TextView view) { }
    private final List<Tab> tabs = new ArrayList<>();
    private Consumer<AnimationStyle.Category> listener;

    CategoryTabs(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(false);
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        addView(row, new LayoutParams(-2, -2));
        addTab(row, null, R.string.animations_all);
        for (AnimationStyle.Category category : AnimationStyle.Category.values()) {
            int label = switch (category) {
                case AI -> R.string.animations_ai;
                case MESSAGES -> R.string.messaging_title;
                case SOCIAL -> R.string.social_title;
            };
            addTab(row, category, label);
        }
    }

    private void addTab(LinearLayout row, AnimationStyle.Category category, int resource) {
        TextView tab = new TextView(getContext());
        LayerDrawable underline = new LayerDrawable(new Drawable[] {
                new ColorDrawable(Color.rgb(215,25,32))
        });
        underline.setLayerHeight(0, dp(2));
        underline.setLayerGravity(0, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        underline.setLayerInset(0, dp(8), 0, dp(8), 0);
        StateListDrawable background = new StateListDrawable();
        background.addState(new int[] {android.R.attr.state_selected}, underline);
        background.addState(new int[0], new ColorDrawable(Color.TRANSPARENT));
        tab.setBackground(background);
        tab.setText(resource);
        tab.setTextSize(14);
        tab.setSingleLine(true);
        tab.setGravity(Gravity.CENTER);
        tab.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        tab.setMinWidth(dp(48));
        tab.setMinHeight(dp(48));
        tab.setPadding(dp(8), 0, dp(8), dp(2));
        tab.setFocusable(true);
        tab.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setClassName(Button.class.getName());
            }
        });
        tab.setOnClickListener(view -> { if (listener != null) listener.accept(category); });
        row.addView(tab, new LinearLayout.LayoutParams(-2, -2));
        tabs.add(new Tab(category, tab));
    }

    void setListener(Consumer<AnimationStyle.Category> listener) { this.listener = listener; }

    void setCategory(AnimationStyle.Category category) {
        for (Tab tab : tabs) {
            boolean selected = tab.category() == category;
            tab.view().setSelected(selected);
            tab.view().setTextColor(selected ? Color.rgb(245,245,245) : Color.rgb(170,170,170));
            if (selected) post(() -> smoothScrollTo(Math.max(0, tab.view().getLeft() - dp(8)), 0));
        }
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
