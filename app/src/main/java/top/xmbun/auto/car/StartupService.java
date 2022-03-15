package top.xmbun.auto.car;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.time.Instant;
import java.util.Calendar;

/**
 * 开机启动服务.
 *
 * @author bun
 * @since 2022-03-04
 */
public class StartupService extends IntentService {

    private static final String TAG = "StartupService";

    /**
     * 导航启动请求码.
     */
    public static final int NAVIGATION_REQUEST_CODE = 1;

    /**
     * 音乐启动请求码.
     */
    public static final int MUSIC_REQUEST_CODE = 2;

    public StartupService() {
        super("StartupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 准备 UI 线程环境
        final Context context = this;
        final Handler handler = new Handler(Looper.getMainLooper());

        // 网络
        try {
            wlan();
        } catch (Throwable e) {
            Log.e(TAG, "操作网络失败", e);
        }

        // 蓝牙
        try {
            bluetooth();
        } catch (Throwable e) {
            Log.e(TAG, "操作蓝牙失败", e);
        }

        // 导航
        try {
            navigation();
        } catch (Throwable e) {
            Log.e(TAG, "操作导航失败", e);
            handler.post(() -> {
                Toast.makeText(context,
                        "开启导航失败: " + e.getLocalizedMessage(), Toast.LENGTH_LONG)
                        .show();
            });
        }

        // 音乐
        try {
            music();
        } catch (Throwable e) {
            Log.e(TAG, "操作音乐失败", e);
            handler.post(() -> {
                Toast.makeText(context,
                        "开启音乐失败: " + e.getLocalizedMessage(), Toast.LENGTH_LONG)
                        .show();
            });
        }
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
        final int delay = sp.getInt(SettingsActivity.NAVIGATION_DELAY_INTERVAL, 0);
        Log.d(TAG, String.format("%s = %b, %s = %s, %s = %d",
                SettingsActivity.NAVIGATION_AUTOLOAD_ON_BOOT, autoload,
                SettingsActivity.NAVIGATION_APP_INFO, packageName,
                SettingsActivity.NAVIGATION_DELAY_INTERVAL, delay));
        if (autoload && !TextUtils.isEmpty(packageName)) {
            scheduleActivity(packageName, delay, NAVIGATION_REQUEST_CODE);
        }
    }

    /**
     * 音乐自启.
     */
    private void music() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean autoload = sp.getBoolean(SettingsActivity.MUSIC_AUTOLOAD_ON_BOOT, false);
        final String packageName = sp.getString(SettingsActivity.MUSIC_APP_INFO, null);
        final int delay = sp.getInt(SettingsActivity.MUSIC_DELAY_INTERVAL, 0);
        Log.d(TAG, String.format("%s = %b, %s = %s, %s = %d",
                SettingsActivity.MUSIC_AUTOLOAD_ON_BOOT, autoload,
                SettingsActivity.MUSIC_APP_INFO, packageName,
                SettingsActivity.MUSIC_DELAY_INTERVAL, delay));
        if (autoload && !TextUtils.isEmpty(packageName)) {
            scheduleActivity(packageName, delay, MUSIC_REQUEST_CODE);
        }
    }

    /**
     * 排期跳转应用.
     *
     * @param packageName 包名
     * @param delay       延时
     * @param requestCode 请求码
     * @throws ActivityNotFoundException 应用未找到时抛出异常
     */
    private void scheduleActivity(String packageName, int delay, int requestCode)
            throws ActivityNotFoundException {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            throw new ActivityNotFoundException("package = " + packageName);
        }

        // 无延时时直接启动
        if (delay <= 0) {
            startActivity(intent);
            return;
        }

        // 构建延时启动命令
        final int flags;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        }
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, flags);

        // 计算延时时长
        final long triggerAtMillis;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, delay);
            triggerAtMillis = c.getTimeInMillis();
        } else {
            triggerAtMillis = Instant.now().plusSeconds(delay).toEpochMilli();
        }

        // 注册延时动作命令
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}