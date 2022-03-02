package top.xmbun.auto.car;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Collections;
import java.util.Set;

/**
 * 蓝牙广播接收器.
 *
 * @author bun
 * @since 2022-02-18
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.d(TAG, "接收到了蓝牙设备连接的消息");
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    final Set<String> devices = sp.getStringSet(SettingsActivity.WLAN_FOLLOW_BLUETOOTH, Collections.emptySet());
                    if (devices.contains(device.getName())) {
                        WifiUtils.tureOn(context);
                    }
                }
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                break;
        }
    }
}