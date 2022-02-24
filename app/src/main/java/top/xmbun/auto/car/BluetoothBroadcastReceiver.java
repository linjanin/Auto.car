package top.xmbun.auto.car;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.d(TAG, "接收到了蓝牙设备连接的消息");
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                final boolean follow = sp.getBoolean("follow_bluetooth", false);
                if (follow) {
                    WifiUtils.tureOnWifi(context);
                }
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                break;
        }
    }
}