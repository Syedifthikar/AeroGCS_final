package org.qtproject.qt5.android.extras;

import android.os.IBinder;
import android.os.Parcel;

/* loaded from: classes.dex */
public class QtNative {
    public static native void onServiceConnected(long j, String str, IBinder iBinder);

    public static native void onServiceDisconnected(long j, String str);

    public static native boolean onTransact(long j, int i, Parcel parcel, Parcel parcel2, int i2);
}
