package org.qtproject.qt5.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

/* loaded from: classes.dex */
public class QtBluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "QtBluetoothBroadcastReceiver";
    private static final int TURN_BT_DISCOVERABLE = 3331;
    private static final int TURN_BT_ON = 3330;
    static Context qtContext = null;
    long qtObject = 0;

    public native void jniOnReceive(long j, Context context, Intent intent);

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        synchronized (qtContext) {
            if (this.qtObject == 0) {
                return;
            }
            jniOnReceive(this.qtObject, context, intent);
        }
    }

    public void unregisterReceiver() {
        synchronized (qtContext) {
            this.qtObject = 0L;
            qtContext.unregisterReceiver(this);
        }
    }

    public static void setContext(Context context) {
        qtContext = context;
    }

    public static void setDiscoverable() {
        if (!(qtContext instanceof Activity)) {
            Log.w(TAG, "Discovery mode cannot be enabled from a service.");
            return;
        }
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
        intent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
        try {
            ((Activity) qtContext).startActivityForResult(intent, TURN_BT_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setConnectable() {
        if (!(qtContext instanceof Activity)) {
            Log.w(TAG, "Connectable mode cannot be enabled from a service.");
            return;
        }
        try {
            ((Activity) qtContext).startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), TURN_BT_DISCOVERABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean setPairingMode(String str, boolean z) {
        try {
            BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(str);
            String str2 = "createBond";
            if (!z) {
                str2 = "removeBond";
            }
            remoteDevice.getClass().getMethod(str2, null).invoke(remoteDevice, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[] getConnectedDevices() {
        try {
            Class<?> cls = Class.forName("android.bluetooth.BluetoothProfile");
            int i = cls.getField("GATT").getInt(null);
            int i2 = cls.getField("GATT_SERVER").getInt(null);
            Object systemService = qtContext.getSystemService((String) Context.class.getField("BLUETOOTH_SERVICE").get(qtContext));
            Method method = systemService.getClass().getMethod("getConnectedDevices", Integer.TYPE);
            Object[] objArr = {Integer.valueOf(i)};
            List<Object> list = (List) method.invoke(systemService, Integer.valueOf(i2));
            HashSet hashSet = new HashSet();
            for (Object obj : (List) method.invoke(systemService, objArr)) {
                hashSet.add(obj.toString());
            }
            for (Object obj2 : list) {
                hashSet.add(obj2.toString());
            }
            return (String[]) hashSet.toArray(new String[hashSet.size()]);
        } catch (Exception e) {
            return new String[0];
        }
    }
}
