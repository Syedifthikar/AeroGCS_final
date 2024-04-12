package org.qtproject.qt5.android.multimedia;

import android.view.SurfaceHolder;

/* loaded from: classes.dex */
public class QtSurfaceHolderCallback implements SurfaceHolder.Callback {
    private long m_id;

    private static native void notifySurfaceCreated(long j);

    private static native void notifySurfaceDestroyed(long j);

    public QtSurfaceHolderCallback(long j) {
        this.m_id = -1L;
        this.m_id = j;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        notifySurfaceCreated(this.m_id);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        notifySurfaceDestroyed(this.m_id);
    }
}
