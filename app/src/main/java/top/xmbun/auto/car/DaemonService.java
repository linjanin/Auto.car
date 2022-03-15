package top.xmbun.auto.car;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

/**
 * 守护服务.
 *
 * @author bun
 * @since 2022-03-15
 */
public class DaemonService extends Service {

    public DaemonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        final Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_brief))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(obtainPendingIntent(SettingsActivity.class))
                .build();
        startForeground(1329, notification);
        enforceComponentEnabled();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            stopForeground(true);
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        enforceComponentEnabled();
        super.onDestroy();
    }

    /**
     * 构建延时意图.
     *
     * @param component 目标组件
     * @return 延时意图
     */
    protected PendingIntent obtainPendingIntent(Class<?> component) {
        final Intent intent = new Intent(this, component);
        final int flags;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        }
        return PendingIntent.getActivity(this, 0, intent, flags);
    }

    /**
     * 强制启用组件.
     */
    private void enforceComponentEnabled() {
        final PackageManager pm = getPackageManager();
        final ComponentName receiver = new ComponentName(this, StartupBroadcastReceiver.class);
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);
    }
}