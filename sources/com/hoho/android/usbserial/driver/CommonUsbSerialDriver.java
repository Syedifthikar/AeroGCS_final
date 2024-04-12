package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;

/* loaded from: classes2.dex */
public abstract class CommonUsbSerialDriver implements UsbSerialDriver {
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    protected final UsbDevice mDevice;
    protected final Object mReadBufferLock = new Object();
    protected final Object mWriteBufferLock = new Object();
    protected UsbDeviceConnection mConnection = null;
    private int _permissionStatus = 3;
    protected byte[] mReadBuffer = new byte[16384];
    protected byte[] mWriteBuffer = new byte[16384];

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract void close() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getCD() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getCTS() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getDSR() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getDTR() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getRI() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract boolean getRTS() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract void open() throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract int read(byte[] bArr, int i) throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract void setDTR(boolean z) throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract void setParameters(int i, int i2, int i3, int i4) throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract void setRTS(boolean z) throws IOException;

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public abstract int write(byte[] bArr, int i) throws IOException;

    public CommonUsbSerialDriver(UsbDevice device) {
        this.mDevice = device;
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setConnection(UsbDeviceConnection connection) {
        this.mConnection = connection;
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public int permissionStatus() {
        return this._permissionStatus;
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setPermissionStatus(int permissionStatus) {
        this._permissionStatus = permissionStatus;
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public final UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public final UsbDeviceConnection getDeviceConnection() {
        return this.mConnection;
    }

    public final void setReadBufferSize(int bufferSize) {
        synchronized (this.mReadBufferLock) {
            if (bufferSize == this.mReadBuffer.length) {
                return;
            }
            this.mReadBuffer = new byte[bufferSize];
        }
    }

    public final void setWriteBufferSize(int bufferSize) {
        synchronized (this.mWriteBufferLock) {
            if (bufferSize == this.mWriteBuffer.length) {
                return;
            }
            this.mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override // com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean purgeHwBuffers(boolean flushReadBuffers, boolean flushWriteBuffers) throws IOException {
        return (flushReadBuffers || flushWriteBuffers) ? false : true;
    }
}
