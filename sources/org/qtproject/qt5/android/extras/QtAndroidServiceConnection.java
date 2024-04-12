package org.qtproject.qt5.android.extras;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/* loaded from: classes.dex */
public class QtAndroidServiceConnection implements ServiceConnection {
    private long m_id;

    public QtAndroidServiceConnection(long j) {
        this.m_id = j;
    }

    public void setId(long j) {
        synchronized (this) {
            this.m_id = j;
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this) {
            QtNative.onServiceConnected(this.m_id, componentName.flattenToString(), iBinder);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        synchronized (this) {
            QtNative.onServiceDisconnected(this.m_id, componentName.flattenToString());
        }
    }
}
