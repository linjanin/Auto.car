package top.xmbun.auto.car;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 开机启动服务.
 *
 * @author bun
 * @since 2022-03-04
 */
public class StartupService extends IntentService {

    private static final String TAG = "StartupService";

    public StartupService() {
        super("StartupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 网络
        wlan();

        // 蓝牙
        bluetooth();

        // 导航
        navigation();
    }


    /**
     * 网络自启.
     */
    private void wlan() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean autoload = sp.getBoolean(SettingsActivity.WLAN_AUTOLOAD_ON_BOOT, false);
        Log.d(TAG, String.format("%s = %b", SettingsActivity.WLAN_AUTOLOAD_ON_BOOT, autoload));
        if (autoload) {
            WifiUtils.tureOn(this);
        }
    }

    /**
     * 蓝牙自启.
     */
    private void bluetooth() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean autoload = sp.getBoolean(SettingsActivity.BLUETOOTH_AUTOLOAD_ON_BOOT, false);
        Log.d(TAG, String.format("%s = %b", SettingsActivity.BLUETOOTH_AUTOLOAD_ON_BOOT, autoload));
        if (autoload) {
            BluetoothUtils.tureOn(this);
        }
    }

    /**
     * 导航自启.
     */
    private void navigation() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean autoload = sp.getBoolean(SettingsActivity.NAVIGATION_AUTOLOAD_ON_BOOT, false);
        final String packageName = sp.getString(SettingsActivity.NAVIGATION_APP_INFO, null);
        Log.d(TAG, String.format("%s = %b, %s = %s",
                SettingsActivity.NAVIGATION_AUTOLOAD_ON_BOOT, autoload,
                SettingsActivity.NAVIGATION_APP_INFO, packageName));
        if (autoload && !TextUtils.isEmpty(packageName)) {
            final Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(intent);
        }
    }
}