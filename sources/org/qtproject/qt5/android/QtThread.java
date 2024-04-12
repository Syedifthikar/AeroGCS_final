package org.qtproject.qt5.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

/* loaded from: classes.dex */
public class QtThread {
    private Thread m_qtThread;
    private ArrayList<Runnable> m_pendingRunnables = new ArrayList<>();
    private boolean m_exit = false;

    public QtThread() {
        Thread thread = new Thread(new Runnable() { // from class: org.qtproject.qt5.android.QtThread.1
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList;
                while (!QtThread.this.m_exit) {
                    try {
                        synchronized (QtThread.this.m_qtThread) {
                            if (QtThread.this.m_pendingRunnables.size() == 0) {
                                QtThread.this.m_qtThread.wait();
                            }
                            arrayList = new ArrayList(QtThread.this.m_pendingRunnables);
                            QtThread.this.m_pendingRunnables.clear();
                        }
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            ((Runnable) it.next()).run();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.m_qtThread = thread;
        thread.setName("qtMainLoopThread");
        this.m_qtThread.start();
    }

    public void post(Runnable runnable) {
        synchronized (this.m_qtThread) {
            this.m_pendingRunnables.add(runnable);
            this.m_qtThread.notify();
        }
    }

    public void run(final Runnable runnable) {
        final Semaphore semaphore = new Semaphore(0);
        synchronized (this.m_qtThread) {
            this.m_pendingRunnables.add(new Runnable() { // from class: org.qtproject.qt5.android.QtThread.2
                @Override // java.lang.Runnable
                public void run() {
                    runnable.run();
                    semaphore.release();
                }
            });
            this.m_qtThread.notify();
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        this.m_exit = true;
        synchronized (this.m_qtThread) {
            this.m_qtThread.notify();
        }
        try {
            this.m_qtThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
