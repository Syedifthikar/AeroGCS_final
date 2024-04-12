package org.pdrl.aerogcs;

import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: classes2.dex */
public class UsbIoManager implements Runnable {
    private static final int BUFSIZ = 4096;
    private static final int READ_WAIT_MILLIS = 100;
    private static final String TAG = "AEROGCS_UsbIoManager";
    private final UsbSerialDriver mDriver;
    private Listener mListener;
    private final ByteBuffer mReadBuffer;
    private State mState;
    private long mUserData;
    private final ByteBuffer mWriteBuffer;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onNewData(byte[] bArr, long j);

        void onRunError(Exception exc, long j);
    }

    /* loaded from: classes2.dex */
    public enum State {
        STOPPED,
        RUNNING,
        STOPPING
    }

    public UsbIoManager(UsbSerialDriver driver) {
        this(driver, null, 0L);
        Log.i(TAG, "Instance created");
    }

    public UsbIoManager(UsbSerialDriver driver, Listener listener, long userData) {
        this.mReadBuffer = ByteBuffer.allocate(BUFSIZ);
        this.mWriteBuffer = ByteBuffer.allocate(BUFSIZ);
        this.mState = State.STOPPED;
        this.mDriver = driver;
        this.mListener = listener;
        this.mUserData = userData;
    }

    public synchronized void setListener(Listener listener) {
        this.mListener = listener;
    }

    public synchronized Listener getListener() {
        return this.mListener;
    }

    public void writeAsync(byte[] data) {
        synchronized (this.mWriteBuffer) {
            this.mWriteBuffer.put(data);
        }
    }

    public synchronized void stop() {
        if (getState() == State.RUNNING) {
            this.mState = State.STOPPING;
            this.mUserData = 0L;
        }
    }

    private synchronized State getState() {
        return this.mState;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this) {
            if (this.mState != State.STOPPED) {
                throw new IllegalStateException("Already running.");
            }
            this.mState = State.RUNNING;
        }
        while (this.mState == State.RUNNING) {
            try {
                try {
                    step();
                } catch (Exception e) {
                    Listener listener = getListener();
                    if (listener != null) {
                        listener.onRunError(e, this.mUserData);
                    }
                    synchronized (this) {
                        this.mState = State.STOPPED;
                        return;
                    }
                }
            } catch (Throwable th) {
                synchronized (this) {
                    this.mState = State.STOPPED;
                    throw th;
                }
            }
        }
        synchronized (this) {
            this.mState = State.STOPPED;
        }
    }

    private void step() throws IOException {
        int len = this.mDriver.read(this.mReadBuffer.array(), READ_WAIT_MILLIS);
        if (len > 0) {
            Listener listener = getListener();
            if (listener != null) {
                byte[] data = new byte[len];
                this.mReadBuffer.get(data, 0, len);
                listener.onNewData(data, this.mUserData);
            }
            this.mReadBuffer.clear();
        }
    }
}
