package top.xmbun.auto.car;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiUtils {

    public static void tureOnWifi(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Toast.makeText(context, "当前wifi: " + wifiManager.isWifiEnabled(), Toast.LENGTH_SHORT).show();
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        Toast.makeText(context, "当前wifi: " + wifiManager.isWifiEnabled(), Toast.LENGTH_SHORT).show();
    }

    public static void tureOffWifi(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Toast.makeText(context, "当前wifi: " + wifiManager.isWifiEnabled(), Toast.LENGTH_SHORT).show();
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        Toast.makeText(context, "当前wifi: " + wifiManager.isWifiEnabled(), Toast.LENGTH_SHORT).show();
    }
}
