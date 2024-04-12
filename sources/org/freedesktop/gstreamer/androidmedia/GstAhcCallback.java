package org.freedesktop.gstreamer.androidmedia;

import android.hardware.Camera;

/* loaded from: classes2.dex */
public class GstAhcCallback implements Camera.PreviewCallback, Camera.ErrorCallback, Camera.AutoFocusCallback {
    public long mCallback;
    public long mUserData;

    public static native void gst_ah_camera_on_auto_focus(boolean z, Camera camera, long j, long j2);

    public static native void gst_ah_camera_on_error(int i, Camera camera, long j, long j2);

    public static native void gst_ah_camera_on_preview_frame(byte[] bArr, Camera camera, long j, long j2);

    public GstAhcCallback(long callback, long user_data) {
        this.mCallback = callback;
        this.mUserData = user_data;
    }

    @Override // android.hardware.Camera.PreviewCallback
    public void onPreviewFrame(byte[] data, Camera camera) {
        gst_ah_camera_on_preview_frame(data, camera, this.mCallback, this.mUserData);
    }

    @Override // android.hardware.Camera.ErrorCallback
    public void onError(int error, Camera camera) {
        gst_ah_camera_on_error(error, camera, this.mCallback, this.mUserData);
    }

    @Override // android.hardware.Camera.AutoFocusCallback
    public void onAutoFocus(boolean success, Camera camera) {
        gst_ah_camera_on_auto_focus(success, camera, this.mCallback, this.mUserData);
    }
}
