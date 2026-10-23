package pl.glyphtoy.aiready;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AppsActivity extends GlyphActivity {
    private AppSettings settings;
    private List<AppCatalog.Entry> apps = List.of(), visible = List.of();
    private AnimationStyle.Category category;
    private String query = "";
    private AppCatalog.Filter filter = AppCatalog.Filter.ALL;
    private boolean firstResume = true;
    private TextView summary, resultCount, enabledFilter, enableAll, empty;
    private CategoryTabs tabs;
    private final AppAdapter adapter = new AppAdapter();
    private final LruCache<String, Drawable> icons = new LruCache<>(48);

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        settings = new AppSettings(this);
        if (saved != null) {
            query = saved.getString("query", "");
            try { filter = AppCatalog.Filter.valueOf(saved.getString("appFilter", "ALL")); }
            catch (IllegalArgumentException ignored) { filter = AppCatalog.Filter.ALL; }
            try { category = AnimationStyle.Category.valueOf(saved.getString("category", "")); }
            catch (IllegalArgumentException ignored) { category = null; }
        }
        LinearLayout root = column();
        root.setPadding(dp(20), dp(12), dp(20), dp(16));
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            view.setPadding(dp(20) + bars.left, dp(12) + bars.top, dp(20) + bars.right, dp(16) + bars.bottom);
            return insets;
        });
        setContentView(root);
        TextView back = text(R.string.back, 14, TEXT);
        back.setMinHeight(dp(48));
        back.setGravity(Gravity.CENTER_VERTICAL);
        back.setPadding(dp(4), 0, dp(12), 0);
        back.setOnClickListener(view -> finish());
        back.setFocusable(true);
        buttonRole(back);
        root.addView(back, new LinearLayout.LayoutParams(-2, -2));
        TextView heading = text(R.string.apps_title, 30, TEXT);
        heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        heading.setAccessibilityHeading(true);
        spaced(root, heading, 4);
        spaced(root, text(R.string.apps_description, 14, MUTED), 8);
        summary = text("", 13, MUTED);
        spaced(root, summary, 8);
        EditText search = new EditText(this);
        search.setId(R.id.apps_search);
        search.setSingleLine(true);
        search.setHint(R.string.find_apps);
        search.setContentDescription(getString(R.string.find_apps));
        search.setTextSize(15);
        search.setTextColor(TEXT);
        search.setHintTextColor(MUTED);
        search.setMinHeight(dp(52));
        search.setPadding(dp(16), dp(12), dp(16), dp(12));
        search.setBackgroundTintList(null);
        search.setBackground(shape(android.graphics.Color.rgb(34,34,34), LINE, 18));
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search.setText(query);
        search.setOnEditorActionListener((view, action, event) -> {
            if (action != EditorInfo.IME_ACTION_SEARCH) return false;
            getSystemService(InputMethodManager.class).hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
            return true;
        });
        spaced(root, search, 16);
        tabs = new CategoryTabs(this);
        tabs.setListener(value -> { category = value; refreshList(); });
        spaced(root, tabs, 8);
        resultCount = text("", 12, MUTED);
        resultCount.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        spaced(root, resultCount, 12);
        LinearLayout filters = new LinearLayout(this);
        filters.setGravity(Gravity.CENTER_VERTICAL);
        enableAll = text(R.string.enable_all, 13, RED);
        enableAll.setMinHeight(dp(48));
        enableAll.setPadding(dp(8), 0, dp(8), 0);
        enableAll.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        enableAll.setFocusable(true);
        enableAll.setContentDescription(getString(R.string.enable_all_description));
        buttonRole(enableAll);
        enableAll.setOnClickListener(view -> toggleVisibleApps());
        filters.addView(enableAll, new LinearLayout.LayoutParams(0, -2, 1));
        enabledFilter = text(R.string.all_apps, 13, MUTED);
        enabledFilter.setMinHeight(dp(48));
        enabledFilter.setPadding(dp(8),0,dp(8),0);
        enabledFilter.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        enabledFilter.setFocusable(true);
        buttonRole(enabledFilter);
        enabledFilter.setOnClickListener(view -> { filter = filter.next(); refreshList(); });
        filters.addView(enabledFilter, new LinearLayout.LayoutParams(0, -2, 1));
        spaced(root, filters, 4);
        FrameLayout content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(-1,0,1));
        ListView list = new ListView(this);
        list.setId(R.id.apps_list);
        list.setDivider(null);
        list.setVerticalScrollBarEnabled(false);
        list.setAdapter(adapter);
        content.addView(list, new FrameLayout.LayoutParams(-1,-1));
        empty = text("", 14, MUTED);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(16),dp(24),dp(16),dp(24));
        content.addView(empty, new FrameLayout.LayoutParams(-1,-1));
        list.setEmptyView(empty);
        reloadApps();
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                refreshList();
            }
            @Override public void afterTextChanged(Editable value) { }
        });
    }

    @Override public void onResume() {
        super.onResume();
        if (firstResume) firstResume = false; else reloadApps();
    }

    @Override public void onSaveInstanceState(Bundle saved) {
        saved.putString("query",query);
        saved.putString("category",category == null ? "" : category.name());
        saved.putString("appFilter",filter.name());
        super.onSaveInstanceState(saved);
    }

    private void reloadApps() { apps = InstalledApps.load(this, settings); refreshList(); }

    private void refreshList() {
        visible = AppCatalog.filter(apps, category, query, filter);
        int enabled = (int) apps.stream().filter(AppCatalog.Entry::enabled).count();
        summary.setText(getString(R.string.apps_summary, enabled, apps.size()));
        tabs.setCategory(category);
        resultCount.setText(getResources().getQuantityString(R.plurals.app_results, visible.size(), visible.size()));
        boolean allEnabled = !visible.isEmpty() && visible.stream().allMatch(AppCatalog.Entry::enabled);
        enableAll.setText(allEnabled ? R.string.disable_all : R.string.enable_all);
        enableAll.setContentDescription(getString(allEnabled ? R.string.disable_all_description : R.string.enable_all_description));
        enableAll.setEnabled(!visible.isEmpty());
        enableAll.setTextColor(visible.isEmpty() ? MUTED : RED);
        String filterName = getString(filterLabel(filter));
        enabledFilter.setText(filterName);
        enabledFilter.setSelected(filter != AppCatalog.Filter.ALL);
        enabledFilter.setTextColor(filter == AppCatalog.Filter.ALL ? MUTED : RED);
        enabledFilter.setStateDescription(filterName);
        enabledFilter.setContentDescription(getString(R.string.app_filter_description,
                filterName, getString(filterLabel(filter.next()))));
        empty.setText(apps.isEmpty() ? R.string.no_supported_apps
                : filter == AppCatalog.Filter.ENABLED && query.isBlank() && category == null ? R.string.no_enabled_apps : R.string.no_matching_apps);
        adapter.notifyDataSetChanged();
    }

    private int filterLabel(AppCatalog.Filter value) {
        return switch (value) {
            case ALL -> R.string.all_apps;
            case ENABLED -> R.string.enabled_only;
            case DISABLED -> R.string.disabled_only;
        };
    }

    private void toggleVisibleApps() {
        if (visible.isEmpty()) return;
        boolean enabled = visible.stream().anyMatch(app -> !app.enabled());
        Set<String> changedIds = new HashSet<>();
        for (AppCatalog.Entry app : visible) if (app.enabled() != enabled) {
            InstalledApps.select(settings, app, enabled);
            changedIds.add(app.id());
        }
        apps = apps.stream().map(app -> changedIds.contains(app.id()) ? app.withEnabled(enabled) : app).toList();
        refreshList();
    }

    private void select(AppCatalog.Entry app, boolean enabled) {
        InstalledApps.select(settings, app, enabled);
        apps = apps.stream().map(item -> item.id().equals(app.id()) ? item.withEnabled(enabled) : item).toList();
        refreshList();
    }

    private void buttonRole(View view) {
        view.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host,info);
                info.setClassName(Button.class.getName());
            }
        });
    }

    private Drawable icon(String packageName) {
        Drawable icon = icons.get(packageName);
        if (icon == null) {
            try { icon = getPackageManager().getApplicationIcon(packageName); }
            catch (PackageManager.NameNotFoundException ignored) { icon = getPackageManager().getDefaultActivityIcon(); }
            icons.put(packageName,icon);
        }
        return icon;
    }

    private record Row(ImageView icon, TextView label, TextView help, Switch control) { }

    private final class AppAdapter extends BaseAdapter {
        @Override public int getCount() { return visible.size(); }
        @Override public AppCatalog.Entry getItem(int position) { return visible.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View recycled, ViewGroup parent) {
            LinearLayout row;
            Row holder;
            if (recycled instanceof LinearLayout existing && existing.getTag() instanceof Row existingRow) {
                row=existing; holder=existingRow;
            } else {
                row=new LinearLayout(AppsActivity.this);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setMinimumHeight(dp(72));
                row.setPadding(0,dp(8),0,dp(8));
                row.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                ImageView icon=new ImageView(AppsActivity.this);
                icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                row.addView(icon,new LinearLayout.LayoutParams(dp(36),dp(36)));
                LinearLayout names=column();
                LinearLayout.LayoutParams nameLayout=new LinearLayout.LayoutParams(0,-2,1);
                nameLayout.leftMargin=dp(12); nameLayout.rightMargin=dp(12);
                row.addView(names,nameLayout);
                TextView label=text("",16,TEXT);
                label.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                names.addView(label);
                TextView help=text("",12,MUTED);
                help.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                spaced(names,help,4);
                Switch control=toggle("",false);
                control.setSwitchPadding(0);
                row.addView(control,new LinearLayout.LayoutParams(-2,-2));
                holder=new Row(icon,label,help,control);
                row.setTag(holder);
                Row clickable=holder;
                row.setOnClickListener(view -> clickable.control().toggle());
            }
            AppCatalog.Entry app=getItem(position);
            holder.icon().setImageDrawable(icon(app.packageName()));
            holder.label().setText(app.label());
            String help=app.messaging()==MessagingSource.SMS ? getString(R.string.sms_help)
                    : AiAppCatalog.GOOGLE.equals(app.packageName()) ? getString(R.string.gemini_google_help) : "";
            holder.help().setText(help);
            holder.help().setVisibility(help.isEmpty() ? View.GONE : View.VISIBLE);
            holder.control().setOnCheckedChangeListener(null);
            holder.control().setChecked(app.enabled());
            holder.control().setContentDescription(help.isEmpty() ? app.label() : app.label()+". "+help);
            holder.control().setOnCheckedChangeListener((view,checked) -> select(app,checked));
            return row;
        }
    }
}
