package top.xmbun.auto.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 开机广播接收器.
 *
 * @author bun
 * @since 2022-02-18
 */
public class StartupBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    /**
     * 开机时间.
     */
    public static final String BOOT_AT = "boot_at";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "接收到了系统启动消息: " + intent.getAction());

        // 判断是否开机初次启动: 记录的开机时间和当前计算出来的开机时间相差超过 5 秒时认为机器重启过
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final long lastBootAt = sp.getLong(BOOT_AT, 0);
        final long bootAt = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        final boolean reboot = Math.abs(bootAt - lastBootAt) > 5000;
        Log.d(TAG, String.format("lastBootAt = %d, bootAt = %d, reboot = %b", lastBootAt, bootAt, reboot));
        if (reboot) {
            // 更新开机时间
            sp.edit().putLong(BOOT_AT, bootAt).apply();

            // 启动开机服务
            final Intent serviceIntent = new Intent(context, StartupService.class);
            context.startService(serviceIntent);
        }
    }
}