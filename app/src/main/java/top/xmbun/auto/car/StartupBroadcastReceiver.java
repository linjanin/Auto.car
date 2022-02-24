package top.xmbun.auto.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "接收到了系统启动消息");
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean autoload = sp.getBoolean("autoload_on_boot", false);
        if (autoload || true) {
            WifiUtils.tureOnWifi(context);
        }
    }
}