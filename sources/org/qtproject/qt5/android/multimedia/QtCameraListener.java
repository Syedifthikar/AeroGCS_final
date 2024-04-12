package org.qtproject.qt5.android.multimedia;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import java.lang.reflect.Array;

/* loaded from: classes.dex */
public class QtCameraListener implements Camera.ShutterCallback, Camera.PictureCallback, Camera.AutoFocusCallback, Camera.PreviewCallback {
    private static final int BUFFER_POOL_SIZE = 2;
    private static final String TAG = "Qt Camera";
    private int m_cameraId;
    private boolean m_notifyNewFrames = false;
    private boolean m_notifyWhenFrameAvailable = false;
    private byte[][] m_previewBuffers = null;
    private byte[] m_lastPreviewBuffer = null;
    private Camera.Size m_previewSize = null;
    private int m_previewFormat = 17;
    private int m_previewBytesPerLine = -1;

    private static native void notifyAutoFocusComplete(int i, boolean z);

    private static native void notifyFrameAvailable(int i);

    private static native void notifyNewPreviewFrame(int i, byte[] bArr, int i2, int i3, int i4, int i5);

    private static native void notifyPictureCaptured(int i, byte[] bArr);

    private static native void notifyPictureExposed(int i);

    private QtCameraListener(int i) {
        this.m_cameraId = -1;
        this.m_cameraId = i;
    }

    public void notifyNewFrames(boolean z) {
        this.m_notifyNewFrames = z;
    }

    public void notifyWhenFrameAvailable(boolean z) {
        this.m_notifyWhenFrameAvailable = z;
    }

    public byte[] lastPreviewBuffer() {
        return this.m_lastPreviewBuffer;
    }

    public int previewWidth() {
        Camera.Size size = this.m_previewSize;
        if (size == null) {
            return -1;
        }
        return size.width;
    }

    public int previewHeight() {
        Camera.Size size = this.m_previewSize;
        if (size == null) {
            return -1;
        }
        return size.height;
    }

    public int previewFormat() {
        return this.m_previewFormat;
    }

    public int previewBytesPerLine() {
        return this.m_previewBytesPerLine;
    }

    public void clearPreviewCallback(Camera camera) {
        camera.setPreviewCallbackWithBuffer(null);
    }

    public void setupPreviewCallback(Camera camera) {
        int ceil;
        clearPreviewCallback(camera);
        this.m_lastPreviewBuffer = null;
        Camera.Parameters parameters = camera.getParameters();
        this.m_previewSize = parameters.getPreviewSize();
        int previewFormat = parameters.getPreviewFormat();
        this.m_previewFormat = previewFormat;
        if (previewFormat == 842094169) {
            int ceil2 = ((int) Math.ceil(this.m_previewSize.width / 16.0d)) * 16;
            ceil = (this.m_previewSize.height * ceil2) + ((((((int) Math.ceil((ceil2 / 2) / 16.0d)) * 16) * this.m_previewSize.height) / 2) * 2);
            this.m_previewBytesPerLine = ceil2;
        } else {
            ceil = (int) Math.ceil((ImageFormat.getBitsPerPixel(previewFormat) / 8.0d) * this.m_previewSize.width * this.m_previewSize.height);
            int i = this.m_previewFormat;
            if (i != 4) {
                if (i == 17) {
                    this.m_previewBytesPerLine = this.m_previewSize.width;
                } else if (i != 20) {
                    this.m_previewBytesPerLine = -1;
                }
            }
            this.m_previewBytesPerLine = this.m_previewSize.width * 2;
        }
        byte[][] bArr = this.m_previewBuffers;
        if (bArr == null || bArr[0].length != ceil) {
            this.m_previewBuffers = (byte[][]) Array.newInstance(byte.class, 2, ceil);
        }
        camera.setPreviewCallbackWithBuffer(this);
        for (byte[] bArr2 : this.m_previewBuffers) {
            camera.addCallbackBuffer(bArr2);
        }
    }

    @Override // android.hardware.Camera.PreviewCallback
    public void onPreviewFrame(byte[] bArr, Camera camera) {
        byte[] bArr2 = this.m_lastPreviewBuffer;
        if (bArr2 != null) {
            camera.addCallbackBuffer(bArr2);
        }
        this.m_lastPreviewBuffer = bArr;
        if (bArr != null) {
            if (this.m_notifyWhenFrameAvailable) {
                this.m_notifyWhenFrameAvailable = false;
                notifyFrameAvailable(this.m_cameraId);
            }
            if (this.m_notifyNewFrames) {
                notifyNewPreviewFrame(this.m_cameraId, bArr, this.m_previewSize.width, this.m_previewSize.height, this.m_previewFormat, this.m_previewBytesPerLine);
            }
        }
    }

    @Override // android.hardware.Camera.ShutterCallback
    public void onShutter() {
        notifyPictureExposed(this.m_cameraId);
    }

    @Override // android.hardware.Camera.PictureCallback
    public void onPictureTaken(byte[] bArr, Camera camera) {
        notifyPictureCaptured(this.m_cameraId, bArr);
    }

    @Override // android.hardware.Camera.AutoFocusCallback
    public void onAutoFocus(boolean z, Camera camera) {
        notifyAutoFocusComplete(this.m_cameraId, z);
    }
}
