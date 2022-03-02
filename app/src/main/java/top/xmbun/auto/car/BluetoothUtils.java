package top.xmbun.auto.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Collections;
import java.util.Set;

/**
 * 蓝牙工具.
 *
 * @author bun
 * @since 2022-03-02
 */
public class BluetoothUtils {

    private static final String TAG = "BluetoothUtils";

    /**
     * 获取蓝牙适配器.
     *
     * @param context 上下文
     * @return 蓝牙适配器，设备不支持蓝牙时返回 {@code null}
     */
    public static BluetoothAdapter getAdapter(Context context) {
        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null) {
                adapter = manager.getAdapter();
            }
        }
        return adapter;
    }

    /**
     * 打开蓝牙.
     *
     * @param context 上下文
     */
    public static void tureOn(Context context) {
        final BluetoothAdapter adapter = getAdapter(context);
        if (adapter == null) {
            Log.w(TAG, "当前设备不支持蓝牙");
            return;
        }

        Log.d(TAG, "当前蓝牙: " + adapter.isEnabled());
        if (!adapter.isEnabled()) {
            Log.d(TAG, "开启蓝牙");
            adapter.enable();
        }
    }

    /**
     * 关闭蓝牙.
     *
     * @param context 上下文
     */
    public static void tureOff(Context context) {
        final BluetoothAdapter adapter = getAdapter(context);
        if (adapter == null) {
            Log.w(TAG, "当前设备不支持蓝牙");
            return;
        }

        Log.d(TAG, "当前蓝牙: " + adapter.isEnabled());
        if (adapter.isEnabled()) {
            Log.d(TAG, "关闭蓝牙");
            adapter.disable();
        }
    }

    /**
     * 获取已配对蓝牙设备.
     *
     * @param context 上下文
     * @return 已配对蓝牙设备
     */
    public static Set<BluetoothDevice> getBondedDevices(Context context) {
        final BluetoothAdapter adapter = getAdapter(context);
        if (adapter == null) {
            Log.w(TAG, "当前设备不支持蓝牙");
            return Collections.emptySet();
        }

        final Set<BluetoothDevice> devices = adapter.getBondedDevices();
        return devices != null ? devices : Collections.emptySet();
    }
}
