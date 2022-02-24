package top.xmbun.auto.car;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils {

    private static final String TAG = "WifiUtils";

    public static void tureOnWifi(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
    }

    public static void tureOffWifi(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
    }
}
