package com.ftdi.j2xx;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

/* compiled from: FT_Device.java */
/* loaded from: classes.dex */
public class BulkInWorker implements Runnable {
    UsbEndpoint mBulkInEndpoint;
    UsbDeviceConnection mConnection;
    FT_Device mDevice;
    int mNrBuf;
    Semaphore mPauseLock = new Semaphore(1);
    boolean mPaused = false;
    ProcessInCtrl mProInCtrl;
    int mReadTimeout;
    int mTransSize;

    public BulkInWorker(FT_Device dev, ProcessInCtrl inCtrl, UsbDeviceConnection connection, UsbEndpoint endpoint) {
        this.mDevice = dev;
        this.mBulkInEndpoint = endpoint;
        this.mConnection = connection;
        this.mProInCtrl = inCtrl;
        this.mNrBuf = inCtrl.getParams().getBufferNumber();
        this.mTransSize = this.mProInCtrl.getParams().getMaxTransferSize();
        this.mReadTimeout = this.mDevice.getDriverParameters().getReadTimeout();
    }

    public void pause() throws InterruptedException {
        this.mPauseLock.acquire();
        this.mPaused = true;
    }

    public void restart() {
        this.mPaused = false;
        this.mPauseLock.release();
    }

    public boolean paused() {
        return this.mPaused;
    }

    @Override // java.lang.Runnable
    public void run() {
        int bufferIndex = 0;
        while (true) {
            try {
                if (this.mPaused) {
                    this.mPauseLock.acquire();
                    this.mPauseLock.release();
                }
                InBuffer inBuf = this.mProInCtrl.acquireWritableBuffer(bufferIndex);
                if (inBuf.getLength() == 0) {
                    ByteBuffer buffer = inBuf.getInputBuffer();
                    buffer.clear();
                    inBuf.setBufferId(bufferIndex);
                    byte[] readBuf = buffer.array();
                    int totalBytesRead = this.mConnection.bulkTransfer(this.mBulkInEndpoint, readBuf, this.mTransSize, this.mReadTimeout);
                    if (totalBytesRead > 0) {
                        buffer.position(totalBytesRead);
                        buffer.flip();
                        inBuf.setLength(totalBytesRead);
                        this.mProInCtrl.releaseReadableBuffer(bufferIndex);
                    }
                }
                bufferIndex = (bufferIndex + 1) % this.mNrBuf;
                if (Thread.interrupted()) {
                    break;
                }
            } catch (InterruptedException e) {
                try {
                    this.mProInCtrl.releaseWritableBuffers();
                    this.mProInCtrl.purgeINData();
                    return;
                } catch (Exception e2) {
                    Log.d("BulkIn::", "Stop BulkIn thread");
                    e2.printStackTrace();
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("BulkIn::", "Fatal error in BulkIn thread");
                return;
            }
        }
        throw new InterruptedException();
    }
}
