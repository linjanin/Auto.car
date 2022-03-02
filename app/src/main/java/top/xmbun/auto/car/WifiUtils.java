package top.xmbun.auto.car;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 网络工具.
 *
 * @author bun
 * @since 2022-02-18
 */
public class WifiUtils {

    private static final String TAG = "WifiUtils";

    /**
     * 打开网络.
     *
     * @param context 上下文
     */
    public static void tureOn(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
    }

    /**
     * 关闭网络.
     *
     * @param context 上下文
     */
    public static void tureOff(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        Log.d(TAG, "当前wifi: " + wifiManager.isWifiEnabled());
    }
}
