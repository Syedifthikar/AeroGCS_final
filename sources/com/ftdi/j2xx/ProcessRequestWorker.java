package com.ftdi.j2xx;

import android.util.Log;

/* compiled from: FT_Device.java */
/* loaded from: classes.dex */
public class ProcessRequestWorker implements Runnable {
    int mNrBuf;
    private ProcessInCtrl mProInCtrl;

    public ProcessRequestWorker(ProcessInCtrl inCtrl) {
        this.mProInCtrl = inCtrl;
        this.mNrBuf = inCtrl.getParams().getBufferNumber();
    }

    @Override // java.lang.Runnable
    public void run() {
        int bufferIndex = 0;
        while (true) {
            try {
                InBuffer inBuf = this.mProInCtrl.acquireReadableBuffer(bufferIndex);
                if (inBuf.getLength() > 0) {
                    this.mProInCtrl.processBulkInData(inBuf);
                    inBuf.purge();
                }
                this.mProInCtrl.releaseWritableBuffer(bufferIndex);
                bufferIndex = (bufferIndex + 1) % this.mNrBuf;
                if (Thread.interrupted()) {
                    break;
                }
            } catch (InterruptedException ex) {
                Log.d("ProcessRequestThread::", "Device has been closed.");
                ex.printStackTrace();
                return;
            } catch (Exception ex2) {
                Log.e("ProcessRequestThread::", "Fatal error!");
                ex2.printStackTrace();
                return;
            }
        }
        throw new InterruptedException();
    }
}
