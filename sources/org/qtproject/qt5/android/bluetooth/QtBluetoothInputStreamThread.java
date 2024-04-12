package org.qtproject.qt5.android.bluetooth;

import android.util.Log;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class QtBluetoothInputStreamThread extends Thread {
    public static final int QT_MISSING_INPUT_STREAM = 0;
    public static final int QT_READ_FAILED = 1;
    public static final int QT_THREAD_INTERRUPTED = 2;
    private static final String TAG = "QtBluetooth";
    long qtObject = 0;
    public boolean logEnabled = false;
    private InputStream m_inputStream = null;

    public static native void errorOccurred(long j, int i);

    public static native void readyData(long j, byte[] bArr, int i);

    public QtBluetoothInputStreamThread() {
        setName("QtBtInputStreamThread");
    }

    public void setInputStream(InputStream inputStream) {
        this.m_inputStream = inputStream;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (this.m_inputStream == null) {
            errorOccurred(this.qtObject, 0);
            return;
        }
        byte[] bArr = new byte[FT_4222_Defines.FT4222_STATUS.FT4222_DEVICE_NOT_SUPPORTED];
        while (!isInterrupted()) {
            try {
                readyData(this.qtObject, bArr, this.m_inputStream.read(bArr));
            } catch (IOException e) {
                if (this.logEnabled) {
                    Log.d(TAG, "InputStream.read() failed:" + e.toString());
                }
                e.printStackTrace();
                errorOccurred(this.qtObject, 1);
            }
        }
        errorOccurred(this.qtObject, 2);
        if (this.logEnabled) {
            Log.d(TAG, "Leaving input stream thread");
        }
    }
}
