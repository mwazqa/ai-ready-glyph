package pl.glyphtoy.aiready;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MainActivity extends Activity {
    private static final int TEXT = Color.rgb(245, 245, 245);
    private static final int MUTED = Color.rgb(170, 170, 170);
    private static final int RED = Color.rgb(215, 25, 32);
    private static final int SURFACE = Color.rgb(24, 24, 24);
    private static final int LINE = Color.rgb(51, 51, 51);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Button> providerButtons = new ArrayList<>();
    private AppSettings settings;
    private TextView readinessTitle, readinessDetail, accessStatus, selectedStatus, eventStatus, matrixStatus;
    private LinearLayout sections, sourceCard, animationCard;
    private MatrixPreview preview;
    private TextView animationDescription;
    private TextView accessHelp;
    private Button quickTest;
    private Boolean configuredOrder;
    private String announcedReadiness;
    private final DeviceProfile device = DeviceProfile.current();

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        settings = new AppSettings(this);
        ScrollView scroll = new ScrollView(this);
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
        sourceCard = buildSources();
        animationCard = buildAnimations();
        sections.addView(sourceCard);
        sections.addView(animationCard);
        configuredOrder = false;
        buildToy(sections);
        spaced(root, caption(R.string.footer), 24);
    }

    private void buildStatus(LinearLayout root) {
        LinearLayout card = card(root, R.string.status_eyebrow, 0);
        readinessTitle = text("", 24, TEXT);
        readinessTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        spaced(card, readinessTitle, 10);
        readinessDetail = text("", 14, MUTED);
        spaced(card, readinessDetail, 6);
        divider(card, 18);
        selectedStatus = text("", 13, TEXT);
        accessStatus = text("", 13, MUTED);
        spaced(card, selectedStatus, 14);
        spaced(card, accessStatus, 6);
        LinearLayout actions = new LinearLayout(this);
        quickTest = button(getString(R.string.quick_test, settings.previewProvider().label), true);
        actions.addView(quickTest, new LinearLayout.LayoutParams(0, -2, 1));
        quickTest.setOnClickListener(view -> testGlyph());
        quickTest.setEnabled(device.supportsAnimations());
        Button details = button(R.string.activity_details, false);
        LinearLayout.LayoutParams detailsLayout = new LinearLayout.LayoutParams(0, -2, 1);
        detailsLayout.leftMargin = dp(8);
        actions.addView(details, detailsLayout);
        spaced(card, actions, 16);
        LinearLayout activity = column();
        activity.setVisibility(View.GONE);
        eventStatus = text("", 13, TEXT);
        matrixStatus = text("", 13, MUTED);
        activity.addView(eventStatus);
        spaced(activity, matrixStatus, 8);
        spaced(card, activity, 12);
        details.setOnClickListener(view -> {
            boolean showing = activity.getVisibility() == View.VISIBLE;
            activity.setVisibility(showing ? View.GONE : View.VISIBLE);
            details.setText(showing ? R.string.activity_details : R.string.hide_details);
            details.setContentDescription(getString(showing ? R.string.show_activity_description : R.string.hide_activity_description));
        });
        details.setContentDescription(getString(R.string.show_activity_description));
    }

    private LinearLayout buildSources() {
        LinearLayout card = card(null, R.string.sources_eyebrow, R.string.sources_title);
        spaced(card, text(R.string.sources_description, 14, MUTED), 8);
        Switch enabled = toggle(R.string.automatic_animations, settings.enabled());
        spaced(card, enabled, 14);
        enabled.setOnCheckedChangeListener((view, checked) -> {
            settings.setEnabled(checked);
            if (!checked) MatrixController.get(this).stop();
            refreshStatus();
        });
        Button select = button(R.string.choose_apps, true);
        spaced(card, select, 12);
        select.setOnClickListener(view -> showAppPicker());
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
        return card;
    }

    private LinearLayout buildAnimations() {
        LinearLayout card = card(null, R.string.animations_eyebrow, R.string.animations_title);
        spaced(card, text(R.string.animations_description, 14, MUTED), 8);
        AiProvider[] providers = AiProvider.values();
        for (int row = 0; row < 2; row++) {
            LinearLayout choices = new LinearLayout(this);
            for (int col = 0; col < 2; col++) {
                AiProvider provider = providers[row * 2 + col];
                Button choice = button(provider.label, false);
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(0, -2, 1);
                if (col > 0) layout.leftMargin = dp(8);
                choices.addView(choice, layout);
                providerButtons.add(choice);
                choice.setOnClickListener(view -> selectProvider(provider, true));
            }
            spaced(card, choices, row == 0 ? 16 : 8);
        }
        preview = new MatrixPreview(this);
        preview.setBackground(shape(Color.BLACK, LINE, 24));
        preview.setProvider(settings.previewProvider());
        spaced(card, preview, 16, dp(224));
        animationDescription = text(settings.previewProvider().description, 13, MUTED);
        spaced(card, animationDescription, 10);
        LinearLayout actions = new LinearLayout(this);
        Button previewButton = button(R.string.preview, false);
        Button testButton = button(R.string.test_on_glyph, true);
        actions.addView(previewButton, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams testLayout = new LinearLayout.LayoutParams(0, -2, 1);
        testLayout.leftMargin = dp(8);
        actions.addView(testButton, testLayout);
        spaced(card, actions, 16);
        previewButton.setOnClickListener(view -> preview.play());
        testButton.setOnClickListener(view -> testGlyph());
        testButton.setEnabled(device.supportsAnimations());
        Button assignments = button(R.string.assign_animations, false);
        spaced(card, assignments, 10);
        assignments.setOnClickListener(view -> showAnimationAssignments());
        selectProvider(settings.previewProvider(), false);
        return card;
    }

    private void selectProvider(AiProvider provider, boolean play) {
        settings.setPreviewProvider(provider);
        quickTest.setText(getString(R.string.quick_test, provider.label));
        preview.setProvider(provider);
        animationDescription.setText(provider.description);
        for (AiProvider item : AiProvider.values()) {
            Button button = providerButtons.get(item.ordinal());
            boolean selected = item == provider;
            styleButton(button, false, selected);
            button.setSelected(selected);
            button.setContentDescription(getString(selected ? R.string.animation_selected : R.string.animation_select, item.label));
        }
        if (play) preview.play();
    }

    private void testGlyph() {
        preview.play();
        MatrixController.get(this).play(settings.previewProvider());
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

    @Override public void onResume() { super.onResume(); statusTick.run(); }
    @Override public void onPause() { handler.removeCallbacks(statusTick); super.onPause(); }
    private final Runnable statusTick = new Runnable() {
        @Override public void run() { refreshStatus(); handler.postDelayed(this, 1000); }
    };

    private void refreshStatus() {
        boolean granted = getSystemService(NotificationManager.class).isNotificationListenerAccessGranted(
                new ComponentName(this, AiNotificationListener.class));
        boolean hasSources = !settings.packages().isEmpty();
        int title, detail;
        if (!device.supportsAnimations()) { title = R.string.device_unsupported_title; detail = R.string.device_unsupported_detail; }
        else if (!hasSources) { title = R.string.setup_needed; detail = R.string.setup_choose_apps; }
        else if (!granted) { title = R.string.access_needed; detail = R.string.setup_grant_access; }
        else if (!settings.enabled()) { title = R.string.paused; detail = R.string.paused_detail; }
        else if (!LocalStatus.listenerConnected) { title = R.string.connecting; detail = R.string.connecting_detail; }
        else { title = R.string.ready; detail = R.string.ready_detail; }
        String readiness = getString(title);
        if (!readiness.equals(announcedReadiness)) {
            readinessTitle.setText(readiness);
            announcedReadiness = readiness;
        }
        readinessDetail.setText(detail == R.string.device_unsupported_detail
                ? getString(R.string.device_unsupported_detail, device.displayName) : getString(detail));
        accessStatus.setText(!granted ? R.string.access_off
                : LocalStatus.listenerConnected ? R.string.access_on : R.string.access_connecting);
        accessHelp.setVisibility(granted ? View.GONE : View.VISIBLE);
        selectedStatus.setText(getString(R.string.selected_apps, settings.packages().size()));
        eventStatus.setText(LocalStatus.lastEvent);
        matrixStatus.setText(device.supportsAnimations() ? LocalStatus.matrix
                : getString(R.string.device_unsupported_detail, device.displayName));
        boolean configured = granted && hasSources;
        if (configuredOrder == null || configuredOrder != configured) {
            sections.removeView(sourceCard);
            sections.removeView(animationCard);
            sections.addView(configured ? animationCard : sourceCard, 0);
            sections.addView(configured ? sourceCard : animationCard, 1);
            configuredOrder = configured;
        }
    }

    private void showAppPicker() {
        boolean geminiInstalled;
        try { geminiInstalled = getPackageManager().getApplicationInfo(AiAppCatalog.GEMINI, 0).enabled; }
        catch (android.content.pm.PackageManager.NameNotFoundException ignored) { geminiInstalled = false; }
        List<ResolveInfo> applications = getPackageManager().queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        applications.sort(Comparator.comparing(info -> applicationLabel(info.activityInfo.packageName), String.CASE_INSENSITIVE_ORDER));
        List<ResolveInfo> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (ResolveInfo info : applications) {
            String name = info.activityInfo.packageName;
            if (AiAppCatalog.supports(name, geminiInstalled) && seen.add(name)) unique.add(info);
        }
        LinearLayout container = column();
        container.setPadding(dp(24), 0, dp(24), dp(8));
        container.addView(text(R.string.ai_picker_help, 13, MUTED));
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(R.string.search_apps);
        search.setTextSize(15);
        search.setTextColor(TEXT);
        search.setHintTextColor(MUTED);
        search.setMinHeight(dp(52));
        search.setPadding(dp(16), dp(12), dp(16), dp(12));
        search.setBackgroundTintList(null);
        search.setBackground(shape(Color.rgb(34, 34, 34), LINE, 18));
        spaced(container, search, 16);
        ScrollView scroll = new ScrollView(this);
        LinearLayout list = column();
        scroll.addView(list);
        spaced(container, scroll, 8, Math.min(dp(350), getWindowManager()
                .getCurrentWindowMetrics().getBounds().height() / 3));
        List<Switch> switches = new ArrayList<>();
        for (ResolveInfo info : unique) {
            String name = info.activityInfo.packageName;
            String label = applicationLabel(name);
            Switch option = toggle(label, settings.packages().contains(name));
            option.setTag((label + " " + name).toLowerCase(java.util.Locale.ROOT));
            option.setOnCheckedChangeListener((view, checked) -> settings.select(name, checked));
            list.addView(option);
            switches.add(option);
            if (AiAppCatalog.GOOGLE.equals(name)) {
                TextView help = text(R.string.gemini_google_help, 12, MUTED);
                list.addView(help);
                option.setTag(R.id.gemini_source_help, help);
            }
        }
        TextView empty = text(unique.isEmpty() ? R.string.no_apps : R.string.no_matching_apps, 14, MUTED);
        empty.setVisibility(unique.isEmpty() ? View.VISIBLE : View.GONE);
        list.addView(empty);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase(java.util.Locale.ROOT);
                int visible = 0;
                for (Switch option : switches) {
                    boolean matches = option.getTag().toString().contains(query);
                    option.setVisibility(matches ? View.VISIBLE : View.GONE);
                    View help = (View) option.getTag(R.id.gemini_source_help);
                    if (help != null) help.setVisibility(matches ? View.VISIBLE : View.GONE);
                    if (matches) visible++;
                }
                empty.setText(unique.isEmpty() ? R.string.no_apps : R.string.no_matching_apps);
                empty.setVisibility(visible == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable value) { }
        });
        showDialog(dialog(R.string.choose_apps)
                .setView(container).setPositiveButton(R.string.done, (dialog, which) -> refreshStatus())
                .setOnDismissListener(dialog -> refreshStatus()));
    }

    private void showAnimationAssignments() {
        List<String> packages = new ArrayList<>(settings.packages());
        packages.sort(Comparator.comparing(this::applicationLabel, String.CASE_INSENSITIVE_ORDER));
        if (packages.isEmpty()) {
            showDialog(dialog(R.string.assign_animations).setMessage(R.string.select_apps_first)
                    .setPositiveButton(R.string.ok, null));
            return;
        }
        String[] labels = new String[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            String name = packages.get(i);
            labels[i] = applicationLabel(name) + " · " + settings.providerFor(name).label;
        }
        showDialog(dialog(R.string.assign_animations)
                .setItems(labels, (dialog, which) -> {
                    String packageName = packages.get(which);
                    String[] choices = new String[AiProvider.values().length + 1];
                    choices[0] = getString(R.string.automatic_assignment, AiProvider.fromPackage(packageName).label);
                    for (AiProvider provider : AiProvider.values()) choices[provider.ordinal() + 1] = provider.label;
                    showDialog(dialog(applicationLabel(packageName))
                            .setItems(choices, (picker, selection) -> {
                                settings.setProvider(packageName, selection == 0 ? null : AiProvider.values()[selection - 1]);
                                LocalStatus.event(getString(R.string.assignment_saved, settings.providerFor(packageName).label,
                                        applicationLabel(packageName)));
                                refreshStatus();
                            }).setNegativeButton(R.string.cancel, null));
                }).setNegativeButton(R.string.close, null));
    }

    private String applicationLabel(String packageName) {
        if (AiAppCatalog.GOOGLE.equals(packageName)) return getString(R.string.gemini_google_source);
        try { return getPackageManager().getApplicationLabel(
                getPackageManager().getApplicationInfo(packageName, 0)).toString(); }
        catch (android.content.pm.PackageManager.NameNotFoundException ignored) { return packageName; }
    }

    private AlertDialog.Builder dialog(int title) { return dialog(getString(title)); }
    private AlertDialog.Builder dialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        TextView heading = new TextView(builder.getContext());
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
        if (window != null) {
            window.setBackgroundDrawable(shape(SURFACE, LINE, 28));
            window.getDecorView().setClipToOutline(true);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.68f);
            android.view.WindowMetrics metrics = getWindowManager().getCurrentWindowMetrics();
            android.graphics.Insets bars = metrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            window.setLayout(Math.min(dp(520), metrics.getBounds().width() - bars.left - bars.right - dp(40)),
                    WindowManager.LayoutParams.WRAP_CONTENT);
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
            action.setStateListAnimator(null);
            styleButton(action, which == AlertDialog.BUTTON_POSITIVE, false);
            if (action.getLayoutParams() instanceof ViewGroup.MarginLayoutParams layout) {
                layout.setMarginStart(dp(4));
                layout.setMarginEnd(dp(4));
                layout.bottomMargin = dp(8);
                action.setLayoutParams(layout);
            }
        }
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout card(LinearLayout parent, int eyebrow, int heading) {
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

    private TextView caption(int resource) {
        return caption(getString(resource));
    }

    private TextView caption(String content) {
        TextView view = text(content, 11, MUTED);
        view.setTypeface(Typeface.MONOSPACE);
        view.setLetterSpacing(.08f);
        return view;
    }

    private TextView text(int resource, int size, int color) { return text(getString(resource), size, color); }
    private TextView text(String content, int size, int color) {
        TextView view = new TextView(this);
        view.setText(content);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setLineSpacing(dp(3), 1);
        return view;
    }

    private Switch toggle(int resource, boolean checked) { return toggle(getString(resource), checked); }
    private Switch toggle(String title, boolean checked) {
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

    private Button button(int resource, boolean primary) { return button(getString(resource), primary); }
    private Button button(String title, boolean primary) {
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

    private void styleButton(Button control, boolean primary, boolean selected) {
        int fill = primary ? RED : selected ? TEXT : Color.TRANSPARENT;
        control.setBackgroundTintList(null);
        control.setBackground(new RippleDrawable(ColorStateList.valueOf(selected ? 0x22000000 : 0x33ffffff),
                shape(fill, primary || selected ? fill : LINE, 26), null));
        control.setTextColor(selected ? Color.BLACK : TEXT);
    }

    private GradientDrawable shape(int fill, int stroke, int radius) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fill);
        background.setCornerRadius(dp(radius));
        background.setStroke(dp(1), stroke);
        return background;
    }

    private void divider(LinearLayout parent, int margin) {
        View line = new View(this);
        line.setBackgroundColor(LINE);
        spaced(parent, line, margin, dp(1));
    }

    private void spaced(LinearLayout parent, View child, int margin) { spaced(parent, child, margin, -2); }
    private void spaced(LinearLayout parent, View child, int margin, int height) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, height);
        layout.topMargin = dp(margin);
        parent.addView(child, layout);
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
