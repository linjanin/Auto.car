package top.xmbun.auto.car;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Toast.makeText(context, "接收到了蓝牙设备连接的消息", Toast.LENGTH_LONG).show();
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