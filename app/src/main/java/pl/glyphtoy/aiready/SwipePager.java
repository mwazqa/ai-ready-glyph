package pl.glyphtoy.aiready;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import java.util.function.IntConsumer;

final class SwipePager extends FrameLayout {
    private final IntConsumer listener;
    private final int slop;
    private float startX, startY;
    private boolean horizontal, decided, dragging;
    private int index, pages = 1;
    private View content;

    SwipePager(Context context) { this(context, delta -> { }); }

    SwipePager(Context context, IntConsumer listener) {
        super(context);
        this.listener = listener;
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFocusable(true);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        setClipChildren(true);
    }

    void setContent(View view, int index, int pages) {
        if (content != null) content.animate().cancel();
        removeAllViews();
        content = view;
        this.index = index;
        this.pages = pages;
        dragging = false;
        addView(view, new LayoutParams(-1, -2));
        setStateDescription(getContext().getString(R.string.animation_page_description, index + 1, pages));
    }

    private boolean canChange(int delta) { return isEnabled() && delta != 0 && index + delta >= 0 && index + delta < pages; }

    private boolean change(int delta) {
        if (!canChange(delta)) return false;
        listener.accept(delta);
        if (content != null) {
            float direction = delta * (getLayoutDirection() == LAYOUT_DIRECTION_RTL ? -1 : 1);
            content.setTranslationX(direction * dp(24));
            content.setAlpha(.5f);
            content.animate().translationX(0).alpha(1).setDuration(160).start();
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SCROLLED);
        return true;
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            startX = event.getX();
            startY = event.getY();
            decided = horizontal = dragging = false;
            if (pages > 1) getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && !decided) {
            float dx = event.getX() - startX, dy = event.getY() - startY;
            if (Math.max(Math.abs(dx), Math.abs(dy)) > slop) {
                decided = true;
                horizontal = SwipeGesture.horizontal(dx, dy, slop);
                if (!horizontal) getParent().requestDisallowInterceptTouchEvent(false);
            }
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        if (pages < 2) return false;
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE && decided) {
            if (horizontal) dragging = true;
            return true;
        }
        return dragging;
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        if (pages < 2) return super.onTouchEvent(event);
        float dx = event.getX() - startX, dy = event.getY() - startY;
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE && dragging && content != null) {
            int direction = dx < 0 ? 1 : -1;
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) direction = -direction;
            content.setTranslationX(dx * (canChange(direction) ? .35f : .12f));
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            boolean wasDragging = dragging;
            int delta = wasDragging ? SwipeGesture.pageDelta(dx, dy, getWidth(), dp(40),
                    getLayoutDirection() == LAYOUT_DIRECTION_RTL) : 0;
            if (!change(delta) && content != null) content.animate().translationX(0).setDuration(120).start();
            if (!wasDragging) performClick();
            dragging = false;
        } else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            dragging = false;
            if (content != null) content.animate().translationX(0).setDuration(120).start();
        }
        return true;
    }

    @Override public boolean performClick() { super.performClick(); return true; }

    @Override public boolean canScrollHorizontally(int direction) {
        return canChange(getLayoutDirection() == LAYOUT_DIRECTION_RTL ? -direction : direction);
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) return change(1);
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) return change(-1);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            int delta = keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1;
            return change(getLayoutDirection() == LAYOUT_DIRECTION_RTL ? -delta : delta);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(HorizontalScrollView.class.getName());
        info.setScrollable(isEnabled() && pages > 1);
        if (canChange(1)) info.addAction(new AccessibilityNodeInfo.AccessibilityAction(
                AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, getContext().getString(R.string.next_animation_page)));
        if (canChange(-1)) info.addAction(new AccessibilityNodeInfo.AccessibilityAction(
                AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, getContext().getString(R.string.previous_animation_page)));
    }

    @Override public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) return change(1);
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) return change(-1);
        return super.performAccessibilityAction(action, arguments);
    }

    @Override public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(HorizontalScrollView.class.getName());
        event.setScrollable(pages > 1);
        event.setItemCount(pages);
        event.setFromIndex(index);
        event.setToIndex(index);
        event.setScrollX(index * getWidth());
        event.setMaxScrollX(Math.max(0, pages - 1) * getWidth());
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
