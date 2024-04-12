package org.qtproject.qt5.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

/* loaded from: classes.dex */
public class QtBluetoothSocketServer extends Thread {
    private static final int QT_ACCEPT_FAILED = 2;
    private static final int QT_LISTEN_FAILED = 1;
    private static final int QT_NO_BLUETOOTH_SUPPORTED = 0;
    private static final String TAG = "QtBluetooth";
    private String m_serviceName;
    private UUID m_uuid;
    long qtObject = 0;
    public boolean logEnabled = false;
    private boolean m_isSecure = false;
    private BluetoothServerSocket m_serverSocket = null;

    public static native void errorOccurred(long j, int i);

    public static native void newSocket(long j, BluetoothSocket bluetoothSocket);

    public QtBluetoothSocketServer() {
        setName("QtSocketServerThread");
    }

    public void setServiceDetails(String str, String str2, boolean z) {
        this.m_uuid = UUID.fromString(str);
        this.m_serviceName = str2;
        this.m_isSecure = z;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            errorOccurred(this.qtObject, 0);
            return;
        }
        try {
            if (this.m_isSecure) {
                this.m_serverSocket = defaultAdapter.listenUsingRfcommWithServiceRecord(this.m_serviceName, this.m_uuid);
                if (this.logEnabled) {
                    Log.d(TAG, "Using secure socket listener");
                }
            } else {
                this.m_serverSocket = defaultAdapter.listenUsingInsecureRfcommWithServiceRecord(this.m_serviceName, this.m_uuid);
                if (this.logEnabled) {
                    Log.d(TAG, "Using insecure socket listener");
                }
            }
            if (this.m_serverSocket != null) {
                while (!isInterrupted()) {
                    try {
                        if (this.logEnabled) {
                            Log.d(TAG, "Waiting for new incoming socket");
                        }
                        BluetoothSocket accept = this.m_serverSocket.accept();
                        if (this.logEnabled) {
                            Log.d(TAG, "New socket accepted");
                        }
                        newSocket(this.qtObject, accept);
                    } catch (IOException e) {
                        if (this.logEnabled) {
                            Log.d(TAG, "Server socket accept() failed:" + e.toString());
                        }
                        e.printStackTrace();
                        errorOccurred(this.qtObject, 2);
                    }
                }
            }
            Log.d(TAG, "Leaving server socket thread.");
        } catch (IOException e2) {
            if (this.logEnabled) {
                Log.d(TAG, "Server socket listen() failed:" + e2.toString());
            }
            e2.printStackTrace();
            errorOccurred(this.qtObject, 1);
        }
    }

    public void close() {
        if (!isAlive()) {
            return;
        }
        try {
            interrupt();
            if (this.m_serverSocket != null) {
                this.m_serverSocket.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "Closing server socket close() failed:" + e.toString());
            e.printStackTrace();
        }
    }
}
