package org.qtproject.qt5.android.bearer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/* loaded from: classes.dex */
public class QtNetworkReceiver {
    private static final String LOG_TAG = "QtNetworkReceiver";
    private static BroadcastReceiverPrivate m_broadcastReceiver = null;
    private static final Object m_lock = new Object();

    public static native void activeNetworkInfoChanged();

    /* loaded from: classes.dex */
    private static class BroadcastReceiverPrivate extends BroadcastReceiver {
        private BroadcastReceiverPrivate() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            QtNetworkReceiver.activeNetworkInfoChanged();
        }
    }

    private QtNetworkReceiver() {
    }

    public static void registerReceiver(Context context) {
        synchronized (m_lock) {
            if (m_broadcastReceiver == null) {
                m_broadcastReceiver = new BroadcastReceiverPrivate();
                context.registerReceiver(m_broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
        }
    }

    public static void unregisterReceiver(Context context) {
        synchronized (m_lock) {
            if (m_broadcastReceiver == null) {
                return;
            }
            context.unregisterReceiver(m_broadcastReceiver);
        }
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService("connectivity");
    }
}
