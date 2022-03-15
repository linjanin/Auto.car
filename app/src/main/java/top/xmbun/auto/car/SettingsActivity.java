package top.xmbun.auto.car;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.xmbun.auto.car.preference.TimePickerDialogFragmentCompat;
import top.xmbun.auto.car.preference.TimePickerPreference;

/**
 * 设置页.
 *
 * @author bun
 * @since 2022-02-18
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * 网络是否开机自启.
     */
    public static final String WLAN_AUTOLOAD_ON_BOOT = "wlan_autoload_on_boot";

    /**
     * 网络随指定蓝牙设备开启.
     */
    public static final String WLAN_FOLLOW_BLUETOOTH = "wlan_follow_bluetooth";

    /**
     * 蓝牙是否开机自启.
     */
    public static final String BLUETOOTH_AUTOLOAD_ON_BOOT = "bluetooth_autoload_on_boot";

    /**
     * 导航是否开机自启.
     */
    public static final String NAVIGATION_AUTOLOAD_ON_BOOT = "navigation_autoload_on_boot";

    /**
     * 默认导航应用.
     */
    public static final String NAVIGATION_APP_INFO = "navigation_app_info";

    /**
     * 导航启动延时.
     * <p>
     * 秒制。
     */
    public static final String NAVIGATION_DELAY_INTERVAL = "navigation_delay_interval";

    /**
     * 音乐是否开机自启.
     */
    public static final String MUSIC_AUTOLOAD_ON_BOOT = "music_autoload_on_boot";

    /**
     * 默认音乐应用.
     */
    public static final String MUSIC_APP_INFO = "music_app_info";

    /**
     * 音乐启动延时.
     * <p>
     * 秒制。
     */
    public static final String MUSIC_DELAY_INTERVAL = "music_delay_interval";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            if (preference instanceof TimePickerPreference) {
                final TimePickerDialogFragmentCompat f =
                        TimePickerDialogFragmentCompat.newInstance(preference.getKey());
                f.setTargetFragment(this, 0);
                f.show(getParentFragmentManager(), TimePickerDialogFragmentCompat.TAG);
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (WLAN_FOLLOW_BLUETOOTH.equals(preference.getKey())) {
                final MultiSelectListPreference list = (MultiSelectListPreference) preference;

                // 准备已配对蓝牙设备列表
                final Set<String> entries = new HashSet<>();
                final Set<BluetoothDevice> devices = BluetoothUtils.getBondedDevices(preference.getContext());
                for (BluetoothDevice device : devices) {
                    final String name = device.getName();
                    if (!TextUtils.isEmpty(name)) {
                        entries.add(name);
                    }
                }

                // 设备列表选项
                final String[] array = entries.toArray(new String[0]);
                list.setEntries(array);
                list.setEntryValues(array);

                // 移除先前的失效配置
                final Set<String> values = list.getValues();
                if (values != null && values.retainAll(entries)) {
                    list.setValues(values);
                }
            } else if (NAVIGATION_APP_INFO.equals(preference.getKey())) {
                final ListPreference list = (ListPreference) preference;

                // 查询已安装导航软件
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0"));
                final List<App> apps = listAllWithPreferredApps(intent);

                // 设置列表选项
                final List<String> entries = new ArrayList<>();
                final List<String> entryValues = new ArrayList<>();
                for (App app : apps) {
                    entries.add(app.systemApp ? "*" + app.label : app.label);
                    entryValues.add(app.packageName);
                }
                list.setEntries(entries.toArray(new String[0]));
                list.setEntryValues(entryValues.toArray(new String[0]));
            } else if (MUSIC_APP_INFO.equals(preference.getKey())) {
                final ListPreference list = (ListPreference) preference;

                // 查询已安装音乐软件
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://"), "audio/*");
                final List<App> apps = listAllWithPreferredApps(intent);

                // 设置列表选项
                final List<String> entries = new ArrayList<>();
                final List<String> entryValues = new ArrayList<>();
                for (App app : apps) {
                    entries.add(app.systemApp ? "*" + app.label : app.label);
                    entryValues.add(app.packageName);
                }
                list.setEntries(entries.toArray(new String[0]));
                list.setEntryValues(entryValues.toArray(new String[0]));
            }

            return super.onPreferenceTreeClick(preference);
        }

        /**
         * 列出所有软件.
         * <p>
         * 优选在前。
         *
         * @param preferred 优选意图
         * @return 软件集
         */
        protected List<App> listAllWithPreferredApps(Intent preferred) {
            final Context context = getContext();
            assert context != null;
            final PackageManager pm = context.getPackageManager();

            // 根据软件名称排序
            final Comparator<App> comparator;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                comparator = (l, r) -> {
                    return l.label.compareTo(r.label);
                };
            } else {
                comparator = Comparator.comparing(t -> t.label);
            }

            // 优选软件在前
            final List<App> apps = new ArrayList<>();
            final List<ResolveInfo> preferreds = pm.queryIntentActivities(preferred,
                    PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : preferreds) {
                apps.add(App.from(resolveInfo.activityInfo.applicationInfo, pm));
            }
            Collections.sort(apps, comparator);

            // 其他软件在后，以备未找到可用软件的情况
            final Map<String, App> all = new HashMap<>();
            final List<PackageInfo> installeds = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : installeds) {
                final App app = App.from(packageInfo.applicationInfo, pm);
                all.put(app.packageName, app);
            }
            for (App app : apps) {
                // 移除优选软件
                all.remove(app.packageName);
            }
            final List<App> t = new ArrayList<>(all.values());
            Collections.sort(t, comparator);
            apps.addAll(t);

            return apps;
        }

        /**
         * 软件信息.
         */
        protected static class App {
            private String packageName;
            private String label;
            private boolean systemApp;

            public static App from(ApplicationInfo info, PackageManager packageManager) {
                final String packageName = info.packageName;
                final String label = info.loadLabel(packageManager).toString();
                final boolean isSystemApp = (ApplicationInfo.FLAG_SYSTEM & info.flags) != 0;
                return new App(packageName, label, isSystemApp);
            }

            public App() {
            }

            public App(String packageName, String label, boolean systemApp) {
                this.packageName = packageName;
                this.label = label;
                this.systemApp = systemApp;
            }

            public String getPackageName() {
                return packageName;
            }

            public void setPackageName(String packageName) {
                this.packageName = packageName;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public boolean isSystemApp() {
                return systemApp;
            }

            public void setSystemApp(boolean systemApp) {
                this.systemApp = systemApp;
            }
        }
    }
}