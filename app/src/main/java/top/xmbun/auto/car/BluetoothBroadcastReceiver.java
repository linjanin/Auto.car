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
                Toast.makeText(context, "接受到了蓝牙设备连接的消息", Toast.LENGTH_LONG).show();
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String name = device.getName();
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit()
                        .putString("signature", name)
                        .apply();
                //WifiUtils.tureOnWifi(context);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                break;
        }
    }
}