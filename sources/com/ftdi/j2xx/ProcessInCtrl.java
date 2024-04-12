package com.ftdi.j2xx;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.ftdi.j2xx.D2xxManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* compiled from: FT_Device.java */
/* loaded from: classes.dex */
public class ProcessInCtrl {
    private static final byte FT_MODEM_STATUS_SIZE = 2;
    private static final byte FT_PACKET_SIZE = 64;
    private static final int FT_PACKET_SIZE_HI = 512;
    private static final int MAX_PACKETS = 256;
    private int mBufInCounter;
    private ByteBuffer[] mBuffers;
    private Object mCounterLock;
    private FT_Device mDevice;
    private Condition mFullCon;
    private Lock mInFullLock;
    private InBuffer[] mInputBufs;
    private ByteBuffer mMainBuf;
    private Pipe mMainPipe;
    private Pipe.SinkChannel mMainSink;
    private Pipe.SourceChannel mMainSource;
    private int mMaxPacketSize;
    private int mNrBuf;
    private D2xxManager.DriverParameters mParams;
    private Condition mReadInCon;
    private Lock mReadInLock;
    private Semaphore[] mReadable;
    private boolean mSinkFull;
    private Object mSinkFullLock;
    private Semaphore[] mWritable;

    public ProcessInCtrl(FT_Device dev) {
        this.mDevice = dev;
        D2xxManager.DriverParameters driverParameters = dev.getDriverParameters();
        this.mParams = driverParameters;
        this.mNrBuf = driverParameters.getBufferNumber();
        int bufSize = this.mParams.getMaxBufferSize();
        this.mMaxPacketSize = this.mDevice.getMaxPacketSize();
        int i = this.mNrBuf;
        this.mWritable = new Semaphore[i];
        this.mReadable = new Semaphore[i];
        this.mInputBufs = new InBuffer[i];
        this.mBuffers = new ByteBuffer[MAX_PACKETS];
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mInFullLock = reentrantLock;
        this.mFullCon = reentrantLock.newCondition();
        this.mSinkFull = false;
        ReentrantLock reentrantLock2 = new ReentrantLock();
        this.mReadInLock = reentrantLock2;
        this.mReadInCon = reentrantLock2.newCondition();
        this.mCounterLock = new Object();
        this.mSinkFullLock = new Object();
        resetBufCount();
        this.mMainBuf = ByteBuffer.allocateDirect(bufSize);
        try {
            Pipe open = Pipe.open();
            this.mMainPipe = open;
            this.mMainSink = open.sink();
            this.mMainSource = this.mMainPipe.source();
        } catch (IOException ex) {
            Log.d("ProcessInCtrl", "Create mMainPipe failed!");
            ex.printStackTrace();
        }
        for (int i2 = 0; i2 < this.mNrBuf; i2++) {
            this.mInputBufs[i2] = new InBuffer(bufSize);
            this.mReadable[i2] = new Semaphore(1);
            this.mWritable[i2] = new Semaphore(1);
            try {
                acquireReadableBuffer(i2);
            } catch (Exception ex2) {
                Log.d("ProcessInCtrl", "Acquire read buffer " + i2 + " failed!");
                ex2.printStackTrace();
            }
        }
    }

    public boolean isSinkFull() {
        return this.mSinkFull;
    }

    public D2xxManager.DriverParameters getParams() {
        return this.mParams;
    }

    InBuffer getBuffer(int idx) {
        InBuffer buffer = null;
        synchronized (this.mInputBufs) {
            if (idx >= 0) {
                if (idx < this.mNrBuf) {
                    buffer = this.mInputBufs[idx];
                }
            }
        }
        return buffer;
    }

    public InBuffer acquireWritableBuffer(int idx) throws InterruptedException {
        this.mWritable[idx].acquire();
        InBuffer buffer = getBuffer(idx);
        if (buffer.acquire(idx) == null) {
            return null;
        }
        return buffer;
    }

    public InBuffer acquireReadableBuffer(int idx) throws InterruptedException {
        this.mReadable[idx].acquire();
        InBuffer buffer = getBuffer(idx);
        return buffer;
    }

    public void releaseWritableBuffer(int idx) throws InterruptedException {
        synchronized (this.mInputBufs) {
            this.mInputBufs[idx].release(idx);
        }
        this.mWritable[idx].release();
    }

    public void releaseReadableBuffer(int idx) throws InterruptedException {
        this.mReadable[idx].release();
    }

    public void processBulkInData(InBuffer inBuffer) throws D2xxManager.D2xxException {
        int freeS;
        int needS;
        try {
            int bufSize = inBuffer.getLength();
            if (bufSize < 2) {
                inBuffer.getInputBuffer().clear();
                return;
            }
            synchronized (this.mSinkFullLock) {
                freeS = getFreeSpace();
                needS = bufSize - 2;
                if (freeS < needS) {
                    Log.d("ProcessBulkIn::", " Buffer is full, waiting for read....");
                    processEventChars(false, (short) 0, (short) 0);
                    this.mInFullLock.lock();
                    this.mSinkFull = true;
                }
            }
            if (freeS < needS) {
                this.mFullCon.await();
                this.mInFullLock.unlock();
            }
            extractReadData(inBuffer);
        } catch (InterruptedException ex) {
            this.mInFullLock.unlock();
            Log.e("ProcessInCtrl", "Exception in Full await!");
            ex.printStackTrace();
        } catch (Exception ex2) {
            Log.e("ProcessInCtrl", "Exception in ProcessBulkIN");
            ex2.printStackTrace();
            throw new D2xxManager.D2xxException("Fatal error in BulkIn.");
        }
    }

    private void extractReadData(InBuffer inBuffer) throws InterruptedException {
        int pos;
        int lim;
        String str;
        String str2 = "extractReadData::";
        int totalData = 0;
        int pos2 = 0;
        int lim2 = 0;
        short signalEvents = 0;
        short signalLineEvents = 0;
        boolean signalRxChar = false;
        ByteBuffer buffer = inBuffer.getInputBuffer();
        int bufSize = inBuffer.getLength();
        if (bufSize > 0) {
            int i = this.mMaxPacketSize;
            int i2 = 0;
            int nrPackets = (bufSize / i) + (bufSize % i > 0 ? 1 : 0);
            int i3 = 0;
            while (i3 < nrPackets) {
                if (i3 == nrPackets - 1) {
                    buffer.limit(bufSize);
                    int pos3 = this.mMaxPacketSize * i3;
                    buffer.position(pos3);
                    byte b0 = buffer.get();
                    str = str2;
                    signalEvents = (short) (((short) (b0 & 240)) ^ this.mDevice.mDeviceInfoNode.modemStatus);
                    this.mDevice.mDeviceInfoNode.modemStatus = (short) (b0 & 240);
                    byte b1 = buffer.get();
                    int lim3 = b1 & 255;
                    this.mDevice.mDeviceInfoNode.lineStatus = (short) lim3;
                    pos2 = pos3 + 2;
                    if (buffer.hasRemaining()) {
                        signalLineEvents = (short) (this.mDevice.mDeviceInfoNode.lineStatus & 30);
                        lim2 = bufSize;
                    } else {
                        signalLineEvents = 0;
                        lim2 = bufSize;
                    }
                } else {
                    str = str2;
                    int lim4 = (i3 + 1) * this.mMaxPacketSize;
                    buffer.limit(lim4);
                    int pos4 = (this.mMaxPacketSize * i3) + 2;
                    buffer.position(pos4);
                    lim2 = lim4;
                    pos2 = pos4;
                }
                totalData += lim2 - pos2;
                this.mBuffers[i3] = buffer.slice();
                i3++;
                str2 = str;
                i2 = 0;
            }
            if (totalData != 0) {
                try {
                    long written = this.mMainSink.write(this.mBuffers, i2, nrPackets);
                    pos = pos2;
                    lim = lim2;
                    if (written != totalData) {
                        try {
                            Log.d(str2, "written != totalData, written= " + written + " totalData=" + totalData);
                        } catch (Exception e) {
                            ex = e;
                            Log.d(str2, "Write data to sink failed!!");
                            ex.printStackTrace();
                            signalRxChar = true;
                            buffer.clear();
                            processEventChars(signalRxChar, signalEvents, signalLineEvents);
                        }
                    }
                    incBufCount((int) written);
                    this.mReadInLock.lock();
                    this.mReadInCon.signalAll();
                    this.mReadInLock.unlock();
                    signalRxChar = true;
                } catch (Exception e2) {
                    ex = e2;
                    pos = pos2;
                    lim = lim2;
                }
            } else {
                pos = pos2;
                lim = lim2;
            }
            buffer.clear();
            processEventChars(signalRxChar, signalEvents, signalLineEvents);
        }
    }

    public int readBulkInData(byte[] data, int length, long timeout_ms) {
        this.mParams.getMaxBufferSize();
        long startTime = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        if (timeout_ms == 0) {
            timeout_ms = this.mParams.getReadTimeout();
        }
        while (this.mDevice.isOpen()) {
            if (getBytesAvailable() >= length) {
                synchronized (this.mMainSource) {
                    try {
                        this.mMainSource.read(buffer);
                        decBufCount(length);
                    } catch (Exception ex) {
                        Log.d("readBulkInData::", "Cannot read data from Source!!");
                        ex.printStackTrace();
                    }
                }
                synchronized (this.mSinkFullLock) {
                    if (this.mSinkFull) {
                        Log.i("FTDI debug::", "buffer is full , and also re start buffer");
                        this.mInFullLock.lock();
                        this.mFullCon.signalAll();
                        this.mSinkFull = false;
                        this.mInFullLock.unlock();
                    }
                }
                return length;
            }
            try {
                this.mReadInLock.lock();
                this.mReadInCon.await(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                this.mReadInLock.unlock();
            } catch (InterruptedException ex2) {
                Log.d("readBulkInData::", "Cannot wait to read data!!");
                ex2.printStackTrace();
                this.mReadInLock.unlock();
            }
            if (System.currentTimeMillis() - startTime >= timeout_ms) {
                return 0;
            }
        }
        return 0;
    }

    private int incBufCount(int size) {
        int rc;
        synchronized (this.mCounterLock) {
            rc = this.mBufInCounter + size;
            this.mBufInCounter = rc;
        }
        return rc;
    }

    private int decBufCount(int size) {
        int rc;
        synchronized (this.mCounterLock) {
            rc = this.mBufInCounter - size;
            this.mBufInCounter = rc;
        }
        return rc;
    }

    private void resetBufCount() {
        synchronized (this.mCounterLock) {
            this.mBufInCounter = 0;
        }
    }

    public int getBytesAvailable() {
        int rc;
        synchronized (this.mCounterLock) {
            rc = this.mBufInCounter;
        }
        return rc;
    }

    public int getFreeSpace() {
        return (this.mParams.getMaxBufferSize() - getBytesAvailable()) - 1;
    }

    public int purgeINData() {
        int read;
        int nrBuf = this.mParams.getBufferNumber();
        synchronized (this.mMainBuf) {
            do {
                try {
                    this.mMainSource.configureBlocking(false);
                    read = this.mMainSource.read(this.mMainBuf);
                    this.mMainBuf.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (read != 0);
            resetBufCount();
            for (int i = 0; i < nrBuf; i++) {
                InBuffer inBuf = getBuffer(i);
                if (inBuf.acquired() && inBuf.getLength() > 2) {
                    inBuf.purge();
                }
            }
        }
        return 0;
    }

    public int processEventChars(boolean fRxChar, short sEvents, short slEvents) throws InterruptedException {
        TFtEventNotify Events = new TFtEventNotify();
        Events.Mask = this.mDevice.mEventNotification.Mask;
        if (fRxChar && (Events.Mask & 1) != 0 && (this.mDevice.mEventMask ^ 1) == 1) {
            this.mDevice.mEventMask |= 1;
            Intent intent = new Intent("FT_EVENT_RXCHAR");
            intent.putExtra("message", "FT_EVENT_RXCHAR");
            LocalBroadcastManager.getInstance(this.mDevice.mContext).sendBroadcast(intent);
        }
        if (sEvents != 0 && (Events.Mask & 2) != 0 && (this.mDevice.mEventMask ^ 2) == 2) {
            this.mDevice.mEventMask |= 2;
            Intent intent2 = new Intent("FT_EVENT_MODEM_STATUS");
            intent2.putExtra("message", "FT_EVENT_MODEM_STATUS");
            LocalBroadcastManager.getInstance(this.mDevice.mContext).sendBroadcast(intent2);
        }
        if (slEvents != 0 && (Events.Mask & 4) != 0 && (this.mDevice.mEventMask ^ 4) == 4) {
            this.mDevice.mEventMask |= 4;
            Intent intent3 = new Intent("FT_EVENT_LINE_STATUS");
            intent3.putExtra("message", "FT_EVENT_LINE_STATUS");
            LocalBroadcastManager.getInstance(this.mDevice.mContext).sendBroadcast(intent3);
            return 0;
        }
        return 0;
    }

    public void releaseWritableBuffers() throws InterruptedException {
        int nrBuf = this.mParams.getBufferNumber();
        for (int i = 0; i < nrBuf; i++) {
            if (getBuffer(i).acquired()) {
                releaseWritableBuffer(i);
            }
        }
    }

    public void close() {
        for (int i = 0; i < this.mNrBuf; i++) {
            try {
                releaseReadableBuffer(i);
            } catch (Exception ex) {
                Log.d("ProcessInCtrl", "Acquire read buffer " + i + " failed!");
                ex.printStackTrace();
            }
            this.mInputBufs[i] = null;
            this.mReadable[i] = null;
            this.mWritable[i] = null;
        }
        for (int i2 = 0; i2 < MAX_PACKETS; i2++) {
            this.mBuffers[i2] = null;
        }
        this.mWritable = null;
        this.mReadable = null;
        this.mInputBufs = null;
        this.mBuffers = null;
        this.mMainBuf = null;
        if (this.mSinkFull) {
            this.mInFullLock.lock();
            this.mFullCon.signalAll();
            this.mInFullLock.unlock();
        }
        this.mReadInLock.lock();
        this.mReadInCon.signalAll();
        this.mReadInLock.unlock();
        this.mInFullLock = null;
        this.mFullCon = null;
        this.mCounterLock = null;
        this.mReadInLock = null;
        this.mReadInCon = null;
        try {
            this.mMainSink.close();
            this.mMainSink = null;
            this.mMainSource.close();
            this.mMainSource = null;
            this.mMainPipe = null;
        } catch (IOException ex2) {
            Log.d("ProcessInCtrl", "Close mMainPipe failed!");
            ex2.printStackTrace();
        }
        this.mDevice = null;
        this.mParams = null;
    }
}
