package pl.glyphtoy.aiready;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends GlyphActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<AnimationStyle, MatrixPreview> previews = new EnumMap<>(AnimationStyle.class);
    private AppSettings settings;
    private TextView readinessTitle, readinessDetail, accessStatus, selectedStatus, eventStatus, matrixStatus;
    private LinearLayout sections, appsCard, animationCard, statusControls;
    private List<AppCatalog.Entry> installedApps = List.of();
    private TextView appsEnabled, appsInstalled;
    private TextView accessHelp;
    private Button alertsButton;
    private boolean detailsExpanded;
    private AnimationStyle.Category animationCategory;
    private String animationQuery = "";
    private int animationPage;
    private SwipePager animationPager;
    private CategoryTabs animationTabs;
    private TextView animationEmpty, animationPageLabel;
    private Boolean configuredOrder;
    private String announcedReadiness;
    private final DeviceProfile device = DeviceProfile.current();

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        settings = new AppSettings(this);
        detailsExpanded = saved != null && saved.getBoolean("detailsExpanded");
        if (saved != null) {
            animationQuery = saved.getString("animationQuery", "");
            animationPage = saved.getInt("animationPage");
            try { animationCategory = AnimationStyle.Category.valueOf(saved.getString("animationCategory", "")); }
            catch (IllegalArgumentException ignored) { animationCategory = null; }
        }
        ScrollView scroll = new ScrollView(this);
        scroll.setId(R.id.content_scroll);
        scroll.setFillViewport(true);
        LinearLayout root = column();
        root.setPadding(dp(20), dp(24), dp(20), dp(32));
        scroll.addView(root);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            android.graphics.Insets bars = insets.getInsets(android.view.WindowInsets.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        setContentView(scroll);

        root.addView(new DotWordmarkView(this), new LinearLayout.LayoutParams(-1, dp(32)));
        spaced(root, caption(getString(device.hasGlyphMatrix() ? R.string.device_label
                : R.string.device_label_generic, device.displayName).toUpperCase(Locale.ROOT)), 12);
        TextView title = text(R.string.hero_title, 30, TEXT);
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setAccessibilityHeading(true);
        spaced(root, title, 18);
        spaced(root, text(R.string.hero_description, 14, MUTED), 10);
        buildStatus(root);
        sections = column();
        root.addView(sections);
        appsCard = buildApps();
        animationCard = buildAnimations();
        sections.addView(appsCard);
        sections.addView(animationCard);
        configuredOrder = false;
        buildToy(sections);
        spaced(root, caption(R.string.footer), 24);
    }

    private void buildStatus(LinearLayout root) {
        LinearLayout card = card(root, R.string.status_eyebrow, 0);
        alertsButton = button(R.string.enable_glyph_alerts, true);
        alertsButton.setTextSize(18);
        alertsButton.setMinHeight(dp(64));
        alertsButton.setPadding(dp(20), dp(14), dp(20), dp(14));
        alertsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_power, 0, 0, 0);
        alertsButton.setCompoundDrawableTintList(ColorStateList.valueOf(TEXT));
        alertsButton.setCompoundDrawablePadding(dp(12));
        alertsButton.setOnClickListener(view -> {
            boolean enabled = !settings.enabled();
            settings.setEnabled(enabled);
            if (!enabled) {
                MatrixController.get(this).stop();
                for (MatrixPreview preview : previews.values()) preview.stop();
            }
            refreshStatus();
        });
        spaced(card, alertsButton, 12);
        readinessTitle = text("", 24, TEXT);
        readinessTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        spaced(card, readinessTitle, 10);
        readinessDetail = text("", 14, MUTED);
        spaced(card, readinessDetail, 6);
        statusControls = column();
        card.addView(statusControls, new LinearLayout.LayoutParams(-1, -2));
        divider(statusControls, 18);
        selectedStatus = text("", 13, TEXT);
        accessStatus = text("", 13, MUTED);
        spaced(statusControls, selectedStatus, 14);
        spaced(statusControls, accessStatus, 6);
        Button details = button(detailsExpanded ? R.string.hide_details : R.string.activity_details, false);
        spaced(statusControls, details, 16);
        LinearLayout activity = column();
        activity.setVisibility(detailsExpanded ? View.VISIBLE : View.GONE);
        eventStatus = text("", 13, TEXT);
        matrixStatus = text("", 13, MUTED);
        activity.addView(eventStatus);
        spaced(activity, matrixStatus, 8);
        spaced(statusControls, activity, 12);
        details.setOnClickListener(view -> {
            detailsExpanded = !detailsExpanded;
            activity.setVisibility(detailsExpanded ? View.VISIBLE : View.GONE);
            details.setText(detailsExpanded ? R.string.hide_details : R.string.activity_details);
            details.setContentDescription(getString(detailsExpanded ? R.string.hide_activity_description : R.string.show_activity_description));
        });
        details.setContentDescription(getString(detailsExpanded ? R.string.hide_activity_description : R.string.show_activity_description));
        buildNotificationControls(statusControls);
    }

    private LinearLayout buildApps() {
        LinearLayout card = card(null, R.string.apps_eyebrow, R.string.apps_title);
        appsEnabled = text("", 24, TEXT);
        appsEnabled.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        spaced(card, appsEnabled, 12);
        appsInstalled = text("", 13, MUTED);
        spaced(card, appsInstalled, 6);
        Button manage = button(R.string.manage_apps, true);
        spaced(card, manage, 16);
        manage.setOnClickListener(view -> startActivity(new Intent(this, AppsActivity.class)));
        return card;
    }

    private void buildNotificationControls(LinearLayout card) {
        Button grant = button(R.string.notification_access, false);
        spaced(card, grant, 10);
        grant.setOnClickListener(view -> showDialog(dialog(R.string.notification_access)
                .setMessage(R.string.access_explanation)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.open_settings, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                            .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                                    new ComponentName(this, AiNotificationListener.class).flattenToString());
                    try { startActivity(intent); }
                    catch (ActivityNotFoundException error) { startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)); }
                })));
        accessHelp = text(R.string.access_help, 12, MUTED);
        spaced(card, accessHelp, 12);
    }

    private LinearLayout buildAnimations() {
        LinearLayout card = card(null, R.string.animations_eyebrow, R.string.animations_title);
        spaced(card, text(device.supportsAnimations() ? R.string.animations_description
                : R.string.animations_preview_only, 14, MUTED), 8);
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(R.string.search_animations);
        search.setContentDescription(getString(R.string.search_animations));
        search.setTextSize(15);
        search.setTextColor(TEXT);
        search.setHintTextColor(MUTED);
        search.setMinHeight(dp(52));
        search.setPadding(dp(16), dp(12), dp(16), dp(12));
        search.setBackgroundTintList(null);
        search.setBackground(shape(Color.rgb(34, 34, 34), LINE, 18));
        search.setText(animationQuery);
        spaced(card, search, 16);
        animationTabs = new CategoryTabs(this);
        animationTabs.setListener(category -> {
            animationCategory = category;
            animationPage = 0;
            renderAnimations();
        });
        spaced(card, animationTabs, 8);
        animationPager = new SwipePager(this, delta -> {
            animationPage += delta;
            renderAnimations();
        });
        spaced(card, animationPager, 12);
        animationEmpty = text(R.string.no_matching_animations, 14, MUTED);
        spaced(card, animationEmpty, 8);
        animationPageLabel = text("", 12, MUTED);
        animationPageLabel.setGravity(Gravity.CENTER);
        animationPageLabel.setMinHeight(dp(36));
        animationPageLabel.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        spaced(card, animationPageLabel, 8);
        renderAnimations();
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                animationQuery = s.toString();
                animationPage = 0;
                renderAnimations();
            }
            @Override public void afterTextChanged(Editable value) { }
        });
        return card;
    }

    private void renderAnimations() {
        for (MatrixPreview preview : previews.values()) preview.stop();
        previews.clear();
        LinearLayout animationGrid = column();
        int columns = getResources().getConfiguration().screenWidthDp < 360
                || getResources().getConfiguration().fontScale > 1.3f ? 1 : 2;
        AnimationCatalog.Page page = AnimationCatalog.page(List.of(AnimationStyle.values()),
                animationCategory, animationQuery, animationPage, columns * 2);
        animationPage = page.index();
        animationTabs.setCategory(animationCategory);
        animationEmpty.setVisibility(page.total() == 0 ? View.VISIBLE : View.GONE);
        animationPager.setVisibility(page.total() == 0 ? View.GONE : View.VISIBLE);
        for (int row = 0; row < page.styles().size(); row += columns) {
            LinearLayout choices = new LinearLayout(this);
            for (int col = 0; col < columns; col++) {
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(0, -2, 1);
                if (col > 0) layout.leftMargin = dp(8);
                if (row + col < page.styles().size()) choices.addView(animationTile(page.styles().get(row + col), columns), layout);
                else choices.addView(new View(this), layout);
            }
            spaced(animationGrid, choices, row == 0 ? 0 : 8);
        }
        animationPager.setContent(animationGrid, page.index(), page.pages());
        animationPageLabel.setVisibility(page.pages() > 1 ? View.VISIBLE : View.GONE);
        animationPageLabel.setText(getString(R.string.animation_swipe_page, page.index() + 1, page.pages()));
        animationPageLabel.setContentDescription(getString(R.string.animation_page_description, page.index() + 1, page.pages()));
    }

    private View animationTile(AnimationStyle provider, int columns) {
        LinearLayout tile = column();
        tile.setPadding(dp(6), 0, dp(6), 0);
        tile.setGravity(Gravity.CENTER);
        MatrixPreview preview = new MatrixPreview(this);
        preview.setProvider(provider);
        preview.setThumbnail(true);
        preview.setCircular(true);
        preview.setRestoreSymbol(true);
        preview.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        tile.addView(preview, new LinearLayout.LayoutParams(-1, dp(columns == 1 ? 192 : 124)));
        TextView label = text(provider.label, 15, TEXT);
        label.setGravity(Gravity.CENTER);
        label.setMinHeight(dp(36));
        label.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        spaced(tile, label, 8);
        previews.put(provider, preview);
        tile.setFocusable(true);
        tile.setContentDescription(getString(device.supportsAnimations() ? R.string.play_style : R.string.preview_style, provider.label));
        tile.setOnClickListener(view -> playPreview(provider));
        tile.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setClassName(Button.class.getName());
            }
        });
        return tile;
    }

    private void playPreview(AnimationStyle provider) {
        if (!settings.enabled()) return;
        for (MatrixPreview preview : previews.values()) preview.stop();
        settings.setPreviewProvider(provider);
        MatrixPreview preview = previews.get(provider);
        if (preview != null) preview.play();
        if (device.supportsAnimations()) MatrixController.get(this).play(provider);
        refreshStatus();
    }

    private void buildToy(LinearLayout parent) {
        LinearLayout card = card(parent, R.string.toy_eyebrow, R.string.toy_title);
        spaced(card, text(R.string.toy_description, 14, MUTED), 8);
        Button manage = button(R.string.open_toys, false);
        spaced(card, manage, 16);
        manage.setOnClickListener(view -> {
            Intent intent = new Intent().setComponent(new ComponentName("com.nothing.thirdparty",
                    "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"));
            try { startActivity(intent); }
            catch (ActivityNotFoundException | SecurityException error) {
                showDialog(dialog(R.string.toy_title).setMessage(R.string.toy_fallback)
                        .setPositiveButton(R.string.ok, null));
            }
        });
        manage.setEnabled(device.supportsAnimations());
    }

    @Override public void onResume() {
        super.onResume();
        installedApps = InstalledApps.load(this, settings);
        configuredOrder = null;
        statusTick.run();
    }
    @Override public void onPause() {
        handler.removeCallbacks(statusTick);
        for (MatrixPreview preview : previews.values()) preview.stop();
        super.onPause();
    }
    @Override public void onSaveInstanceState(Bundle saved) {
        saved.putBoolean("detailsExpanded", detailsExpanded);
        saved.putString("animationQuery", animationQuery);
        saved.putString("animationCategory", animationCategory == null ? "" : animationCategory.name());
        saved.putInt("animationPage", animationPage);
        super.onSaveInstanceState(saved);
    }
    private final Runnable statusTick = new Runnable() {
        @Override public void run() { refreshStatus(); handler.postDelayed(this, 500); }
    };

    private void refreshStatus() {
        boolean automatic = settings.enabled();
        String alertState = getString(automatic ? R.string.glyph_alerts_on : R.string.glyph_alerts_off);
        if (!alertState.contentEquals(alertsButton.getStateDescription() == null ? "" : alertsButton.getStateDescription())) {
            int fill = automatic ? RED : Color.rgb(58, 58, 60);
            alertsButton.setBackground(shape(fill, fill, 26));
            alertsButton.setStateDescription(alertState);
            statusControls.setAlpha(automatic ? 1f : .4f);
            sections.setAlpha(automatic ? 1f : .4f);
            setControlsEnabled(statusControls, automatic);
            setControlsEnabled(sections, automatic);
        }
        boolean granted = getSystemService(NotificationManager.class).isNotificationListenerAccessGranted(
                new ComponentName(this, AiNotificationListener.class));
        int aiCount = 0, messagingCount = 0, socialCount = 0;
        for (AppCatalog.Entry app : installedApps) if (app.enabled()) {
            switch (app.category()) {
                case AI -> aiCount++;
                case MESSAGES -> messagingCount++;
                case SOCIAL -> socialCount++;
            }
        }
        int enabledCount = aiCount + messagingCount + socialCount;
        boolean hasSources = enabledCount > 0;
        appsEnabled.setText(getResources().getQuantityString(R.plurals.enabled_app_count, enabledCount, enabledCount));
        appsInstalled.setText(installedApps.isEmpty() ? getString(R.string.no_supported_apps)
                : getResources().getQuantityString(R.plurals.installed_app_count, installedApps.size(), installedApps.size()));
        int title, detail;
        if (!automatic) { title = R.string.paused; detail = R.string.paused_detail; }
        else if (!device.supportsAnimations()) { title = R.string.device_unsupported_title; detail = R.string.device_unsupported_detail; }
        else if (!hasSources) { title = R.string.setup_needed; detail = R.string.setup_choose_apps; }
        else if (!granted) { title = R.string.access_needed; detail = R.string.setup_grant_access; }
        else if (!LocalStatus.listenerConnected) { title = R.string.connecting; detail = R.string.connecting_detail; }
        else { title = R.string.ready; detail = R.string.ready_detail; }
        PlaybackStatus playback = LocalStatus.playback;
        if (automatic && device.supportsAnimations() && (playback.active() || playback.phase() == PlaybackStatus.Phase.ERROR)) {
            title = switch (playback.phase()) {
                case WAITING -> R.string.playback_waiting;
                case CONNECTING -> R.string.playback_connecting;
                case PLAYING -> R.string.playback_playing;
                case ERROR -> R.string.playback_error;
                case IDLE -> title;
            };
        }
        String readiness = getString(title);
        if (!readiness.equals(announcedReadiness)) {
            readinessTitle.setText(readiness);
            announcedReadiness = readiness;
        }
        readinessDetail.setText(automatic && device.supportsAnimations()
                && (playback.active() || playback.phase() == PlaybackStatus.Phase.ERROR) ? playbackDetail(playback)
                : detail == R.string.device_unsupported_detail
                ? getString(R.string.device_unsupported_detail, device.displayName) : getString(detail));
        accessStatus.setText(!granted ? R.string.access_off
                : LocalStatus.listenerConnected ? R.string.access_on : R.string.access_connecting);
        accessHelp.setVisibility(granted ? View.GONE : View.VISIBLE);
        selectedStatus.setText(getString(R.string.selected_apps, aiCount, messagingCount, socialCount));
        eventStatus.setText(LocalStatus.lastEvent);
        matrixStatus.setText(device.supportsAnimations() ? LocalStatus.matrix
                : getString(R.string.device_unsupported_detail, device.displayName));
        boolean configured = granted && hasSources;
        if (configuredOrder == null || configuredOrder != configured) {
            sections.removeView(appsCard);
            sections.removeView(animationCard);
            sections.addView(configured ? animationCard : appsCard, 0);
            sections.addView(configured ? appsCard : animationCard, 1);
            configuredOrder = configured;
        }
    }

    private void setControlsEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup group) {
            for (int i = 0; i < group.getChildCount(); i++) setControlsEnabled(group.getChildAt(i), enabled);
        }
    }

    private String playbackDetail(PlaybackStatus playback) {
        return switch (playback.phase()) {
            case WAITING -> getString(R.string.playback_waiting_detail, playback.style().label,
                    playback.secondsRemaining(SystemClock.uptimeMillis()));
            case CONNECTING -> getString(R.string.playback_connecting_detail, playback.style().label);
            case PLAYING -> getString(R.string.playback_playing_detail, playback.style().label,
                    playback.secondsRemaining(SystemClock.uptimeMillis()));
            case ERROR -> playback.error();
            case IDLE -> "";
        };
    }

    private AlertDialog.Builder dialog(int title) { return dialog(getString(title)); }
    private AlertDialog.Builder dialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        TextView heading = new TextView(builder.getContext());
        heading.setId(R.id.dialog_heading);
        heading.setText(title);
        heading.setTextSize(23);
        heading.setTextColor(TEXT);
        heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heading.setPadding(dp(24), dp(24), dp(24), dp(12));
        heading.setAccessibilityHeading(true);
        return builder.setCustomTitle(heading);
    }

    private void showDialog(AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        int dialogWidth = 0, availableHeight = 0;
        if (window != null) {
            window.setBackgroundDrawable(shape(SURFACE, LINE, 28));
            window.getDecorView().setClipToOutline(true);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.68f);
            android.view.WindowMetrics metrics = getWindowManager().getCurrentWindowMetrics();
            android.graphics.Insets bars = metrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            dialogWidth = Math.min(dp(520), metrics.getBounds().width() - bars.left - bars.right - dp(40));
            availableHeight = metrics.getBounds().height() - bars.top - bars.bottom;
            window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setTextColor(MUTED);
            message.setTextSize(15);
            message.setLineSpacing(dp(3), 1);
        }
        if (dialog.getListView() != null) {
            dialog.getListView().setDivider(null);
            dialog.getListView().setPadding(dp(12), 0, dp(12), dp(4));
            dialog.getListView().setSelector(new RippleDrawable(ColorStateList.valueOf(0x22ffffff),
                    null, shape(Color.WHITE, Color.WHITE, 16)));
        }
        ViewGroup actionBar = null;
        for (int which : new int[]{AlertDialog.BUTTON_NEGATIVE, AlertDialog.BUTTON_POSITIVE}) {
            Button action = dialog.getButton(which);
            if (action == null || action.getVisibility() != View.VISIBLE) continue;
            action.setAllCaps(false);
            action.setTextSize(14);
            action.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            action.setMinHeight(dp(48));
            action.setMinimumHeight(dp(48));
            action.setMinWidth(dp(88));
            action.setMinimumWidth(dp(88));
            action.setPadding(dp(16), dp(10), dp(16), dp(10));
            action.setGravity(Gravity.CENTER);
            action.setStateListAnimator(null);
            styleButton(action, which == AlertDialog.BUTTON_POSITIVE, false);
            if (action.getLayoutParams() instanceof ViewGroup.MarginLayoutParams layout) {
                layout.setMarginStart(dp(4));
                layout.setMarginEnd(dp(4));
                layout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layout.bottomMargin = 0;
                action.setLayoutParams(layout);
            }
            if (action.getParent() instanceof ViewGroup parent) actionBar = parent;
        }
        int footerHeight = 0;
        if (actionBar != null && dialogWidth > 0) {
            actionBar.setPadding(dp(12), dp(8), dp(12), dp(12));
            View footer = actionBar.getParent() instanceof View parent ? parent : actionBar;
            int footerWidth = dialogWidth - footer.getPaddingLeft() - footer.getPaddingRight();
            int neededWidth = actionBar.getPaddingLeft() + actionBar.getPaddingRight();
            for (int i = 0; i < actionBar.getChildCount(); i++) {
                View child = actionBar.getChildAt(i);
                if (!(child instanceof Button) || child.getVisibility() != View.VISIBLE) continue;
                child.measure(View.MeasureSpec.makeMeasureSpec(Math.max(1, footerWidth), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                neededWidth += child.getMeasuredWidth();
                if (child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams margin)
                    neededWidth += margin.getMarginStart() + margin.getMarginEnd();
            }
            if (neededWidth > footerWidth && actionBar instanceof LinearLayout bar) {
                bar.setOrientation(LinearLayout.VERTICAL);
                bar.setGravity(Gravity.END);
                for (int i = 0; i < bar.getChildCount(); i++) {
                    View child = bar.getChildAt(i);
                    if (!(child instanceof Button)) { child.setVisibility(View.GONE); continue; }
                    LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) child.getLayoutParams();
                    layout.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layout.weight = 0;
                    layout.topMargin = dp(4);
                    child.setLayoutParams(layout);
                }
            }
            actionBar.measure(View.MeasureSpec.makeMeasureSpec(Math.max(1, footerWidth), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            footerHeight = actionBar.getMeasuredHeight();
            if (footer != actionBar) footerHeight += footer.getPaddingTop() + footer.getPaddingBottom();
            footer.setMinimumHeight(footerHeight);
        }
        if (dialog.getListView() != null && dialogWidth > 0) {
            View heading = dialog.findViewById(R.id.dialog_heading);
            heading.measure(View.MeasureSpec.makeMeasureSpec(dialogWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int limit = Math.max(0, Math.min(dp(440), availableHeight - heading.getMeasuredHeight() - footerHeight - dp(48)));
            android.widget.ListView list = dialog.getListView();
            list.measure(View.MeasureSpec.makeMeasureSpec(dialogWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(limit, View.MeasureSpec.AT_MOST));
            ViewGroup.LayoutParams layout = list.getLayoutParams();
            layout.height = list.getMeasuredHeight();
            list.setLayoutParams(layout);
        }
    }

}
