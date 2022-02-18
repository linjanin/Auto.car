package top.xmbun.auto.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartupBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "接受到了系统启动消息", Toast.LENGTH_LONG).show();
        WifiUtils.tureOnWifi(context);
    }
}