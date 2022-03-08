package top.xmbun.auto.car;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
                final List<ApplicationInfo> apps = new ArrayList<>();
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0"));
                final PackageManager pm = preference.getContext().getPackageManager();
                final List<ResolveInfo> preferreds = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : preferreds) {
                    apps.add(resolveInfo.activityInfo.applicationInfo);
                }
                final Comparator<ApplicationInfo> comparator = (l, r) -> {
                    final String ls = pm.getApplicationLabel(l).toString();
                    final String rs = pm.getApplicationLabel(r).toString();
                    return ls.compareTo(rs);
                };
                Collections.sort(apps, comparator);

                // 查询所有软件，以备未找到可用导航软件的情况
                final Map<String, ApplicationInfo> all = new HashMap<>();
                final List<PackageInfo> installeds = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : installeds) {
                    all.put(packageInfo.applicationInfo.packageName, packageInfo.applicationInfo);
                }
                for (ApplicationInfo app : apps) {
                    // 移除先前找到的导航软件
                    all.remove(app.packageName);
                }
                final List<ApplicationInfo> t = new ArrayList<>(all.values());
                Collections.sort(t, comparator);
                apps.addAll(t);

                // 设置列表选项
                final List<String> entries = new ArrayList<>();
                final List<String> entryValues = new ArrayList<>();
                for (ApplicationInfo app : apps) {
                    final boolean isSystemApp = (ApplicationInfo.FLAG_SYSTEM & app.flags) != 0;
                    final String label = pm.getApplicationLabel(app).toString();
                    entries.add(isSystemApp ? "*" + label : label);
                    entryValues.add(app.packageName);
                }
                list.setEntries(entries.toArray(new String[0]));
                list.setEntryValues(entryValues.toArray(new String[0]));
            }

            return super.onPreferenceTreeClick(preference);
        }
    }
}