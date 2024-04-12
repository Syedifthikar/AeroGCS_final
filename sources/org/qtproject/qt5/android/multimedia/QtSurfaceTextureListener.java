package org.qtproject.qt5.android.multimedia;

import android.graphics.SurfaceTexture;

/* loaded from: classes.dex */
public class QtSurfaceTextureListener implements SurfaceTexture.OnFrameAvailableListener {
    private final long m_id;

    private static native void notifyFrameAvailable(long j);

    public QtSurfaceTextureListener(long j) {
        this.m_id = j;
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        notifyFrameAvailable(this.m_id);
    }
}
