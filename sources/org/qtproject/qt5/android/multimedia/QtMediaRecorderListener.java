package org.qtproject.qt5.android.multimedia;

import android.media.MediaRecorder;

/* loaded from: classes.dex */
public class QtMediaRecorderListener implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
    private long m_id;

    private static native void notifyError(long j, int i, int i2);

    private static native void notifyInfo(long j, int i, int i2);

    public QtMediaRecorderListener(long j) {
        this.m_id = -1L;
        this.m_id = j;
    }

    @Override // android.media.MediaRecorder.OnErrorListener
    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
        notifyError(this.m_id, i, i2);
    }

    @Override // android.media.MediaRecorder.OnInfoListener
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        notifyInfo(this.m_id, i, i2);
    }
}
