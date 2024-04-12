package org.qtproject.qt5.android.extras;

import android.os.Binder;
import android.os.Parcel;

/* loaded from: classes.dex */
public class QtAndroidBinder extends Binder {
    private long m_id;

    public QtAndroidBinder(long j) {
        this.m_id = j;
    }

    public void setId(long j) {
        synchronized (this) {
            this.m_id = j;
        }
    }

    @Override // android.os.Binder
    protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        boolean onTransact;
        synchronized (this) {
            onTransact = QtNative.onTransact(this.m_id, i, parcel, parcel2, i2);
        }
        return onTransact;
    }
}
