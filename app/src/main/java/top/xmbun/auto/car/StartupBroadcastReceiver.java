package top.xmbun.auto.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "接收到了系统启动消息", Toast.LENGTH_LONG).show();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean autoload = sp.getBoolean("autoload_on_boot", false);
        if (autoload) {
            WifiUtils.tureOnWifi(context);
        }
    }
}