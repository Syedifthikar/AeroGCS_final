package org.pdrl.aerogcs;

import android.os.ParcelFileDescriptor;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class TSync {
    private static final int HEADER_SIZE = 28;
    private static final byte PROTOCOL_CHANNEL = 2;
    private static final byte PROTOCOL_DATA = 3;
    private static final byte PROTOCOL_REQUEST_CONNECTION = 0;
    private static final byte PROTOCOL_VERSION = 1;
    private static final int TAISYNC_SETTINGS_PORT = 8200;
    private static final int TAISYNC_TELEMETRY_PORT = 8400;
    private static final int TAISYNC_VIDEO_PORT = 8000;
    private static final int VIDEO_PORT = 5600;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private ParcelFileDescriptor mParcelFileDescriptor;
    private boolean running = false;
    private DatagramSocket udpSocket = null;
    private Socket tcpSettingsSocket = null;
    private InputStream settingsInStream = null;
    private OutputStream settingsOutStream = null;
    private Socket tcpTelemetrySocket = null;
    private InputStream telemetryInStream = null;
    private OutputStream telemetryOutStream = null;
    private byte[] mBytes = new byte[32768];
    private byte vMaj = 0;
    private Object runLock = new Object();
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(3);

    public boolean isRunning() {
        boolean z;
        synchronized (this.runLock) {
            z = this.running;
        }
        return z;
    }

    public void open(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        synchronized (this.runLock) {
            if (this.running) {
                return;
            }
            this.running = true;
            this.mParcelFileDescriptor = parcelFileDescriptor;
            if (parcelFileDescriptor == null) {
                throw new IOException("parcelFileDescriptor is null");
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            this.mFileInputStream = new FileInputStream(fileDescriptor);
            this.mFileOutputStream = new FileOutputStream(fileDescriptor);
            this.udpSocket = new DatagramSocket();
            final InetAddress address = InetAddress.getByName("localhost");
            Socket socket = new Socket(address, (int) TAISYNC_TELEMETRY_PORT);
            this.tcpTelemetrySocket = socket;
            this.telemetryInStream = socket.getInputStream();
            this.telemetryOutStream = this.tcpTelemetrySocket.getOutputStream();
            Socket socket2 = new Socket(address, (int) TAISYNC_SETTINGS_PORT);
            this.tcpSettingsSocket = socket2;
            this.settingsInStream = socket2.getInputStream();
            this.settingsOutStream = this.tcpSettingsSocket.getOutputStream();
            sendTaiSyncMessage((byte) 0, 0, null, 0);
            this.mThreadPool.execute(new Runnable() { // from class: org.pdrl.aerogcs.TSync.1
                /* JADX WARN: Code restructure failed: missing block: B:221:0x0016, code lost:
                    android.util.Log.d("TSync", "Bytes Read : 0");
                 */
                /* JADX WARN: Code restructure failed: missing block: B:222:0x002c, code lost:
                    if (0 <= 0) goto L48;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:223:0x002e, code lost:
                    android.util.Log.d("TSync", "Bytes Read : 0");
                 */
                /* JADX WARN: Code restructure failed: missing block: B:224:0x0050, code lost:
                    if (org.pdrl.aerogcs.TSync.this.mBytes[3] != 1) goto L15;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:225:0x0052, code lost:
                    org.pdrl.aerogcs.TSync.this.vMaj = org.pdrl.aerogcs.TSync.this.mBytes[19];
                    org.pdrl.aerogcs.TSync.this.sendTaiSyncMessage((byte) 1, 0, null, 0);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:227:0x007c, code lost:
                    if (org.pdrl.aerogcs.TSync.this.mBytes[3] != 2) goto L18;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:228:0x007e, code lost:
                    r1 = ((((org.pdrl.aerogcs.TSync.this.mBytes[4] & 255) << 24) | ((org.pdrl.aerogcs.TSync.this.mBytes[5] & 255) << 16)) | ((org.pdrl.aerogcs.TSync.this.mBytes[6] & 255) << 8)) | (org.pdrl.aerogcs.TSync.this.mBytes[7] & 255);
                    r2 = ((((org.pdrl.aerogcs.TSync.this.mBytes[8] & 255) << 24) | ((org.pdrl.aerogcs.TSync.this.mBytes[9] & 255) << 16)) | ((org.pdrl.aerogcs.TSync.this.mBytes[10] & 255) << 8)) | (org.pdrl.aerogcs.TSync.this.mBytes[11] & 255);
                    org.pdrl.aerogcs.TSync.this.sendTaiSyncMessage((byte) 2, r1, null, 0);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:230:0x00ed, code lost:
                    if (org.pdrl.aerogcs.TSync.this.mBytes[3] != 3) goto L41;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:231:0x00ef, code lost:
                    r1 = ((((org.pdrl.aerogcs.TSync.this.mBytes[4] & 255) << 24) | ((org.pdrl.aerogcs.TSync.this.mBytes[5] & 255) << 16)) | ((org.pdrl.aerogcs.TSync.this.mBytes[6] & 255) << 8)) | (org.pdrl.aerogcs.TSync.this.mBytes[7] & 255);
                    r2 = ((((org.pdrl.aerogcs.TSync.this.mBytes[8] & 255) << 24) | ((org.pdrl.aerogcs.TSync.this.mBytes[9] & 255) << 16)) | ((org.pdrl.aerogcs.TSync.this.mBytes[10] & 255) << 8)) | (org.pdrl.aerogcs.TSync.this.mBytes[11] & 255);
                    r4 = 0 - org.pdrl.aerogcs.TSync.HEADER_SIZE;
                    r6 = new byte[r4];
                    java.lang.System.arraycopy(org.pdrl.aerogcs.TSync.this.mBytes, org.pdrl.aerogcs.TSync.HEADER_SIZE, r6, 0, r4);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:232:0x0160, code lost:
                    if (r1 != org.pdrl.aerogcs.TSync.TAISYNC_VIDEO_PORT) goto L24;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:233:0x0162, code lost:
                    r5 = new java.net.DatagramPacket(r6, r6.length, r2, (int) org.pdrl.aerogcs.TSync.VIDEO_PORT);
                    org.pdrl.aerogcs.TSync.this.udpSocket.send(r5);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:235:0x0178, code lost:
                    if (r1 != org.pdrl.aerogcs.TSync.TAISYNC_SETTINGS_PORT) goto L27;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:236:0x017a, code lost:
                    org.pdrl.aerogcs.TSync.this.settingsOutStream.write(r6);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:238:0x0186, code lost:
                    if (r1 != org.pdrl.aerogcs.TSync.TAISYNC_TELEMETRY_PORT) goto L34;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:239:0x0188, code lost:
                    org.pdrl.aerogcs.TSync.this.telemetryOutStream.write(r6);
                 */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public void run() {
                    /*
                        Method dump skipped, instructions count: 448
                        To view this dump change 'Code comments level' option to 'DEBUG'
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.pdrl.aerogcs.TSync.AnonymousClass1.run():void");
                }
            });
            this.mThreadPool.execute(new Runnable() { // from class: org.pdrl.aerogcs.TSync.2
                /* JADX WARN: Code restructure failed: missing block: B:112:0x001b, code lost:
                    r1 = org.pdrl.aerogcs.TSync.this.telemetryInStream.read(r0, 0, r0.length);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:113:0x0027, code lost:
                    if (r1 <= 0) goto L15;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:114:0x0029, code lost:
                    android.util.Log.d("TSync", "telemetry Input Stream Bytes Read: " + r1);
                    org.pdrl.aerogcs.TSync.this.sendTaiSyncMessage((byte) 3, org.pdrl.aerogcs.TSync.TAISYNC_TELEMETRY_PORT, r0, r1);
                 */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public void run() {
                    /*
                        r5 = this;
                        r0 = 256(0x100, float:3.59E-43)
                        byte[] r0 = new byte[r0]
                    L4:
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        java.lang.Object r1 = org.pdrl.aerogcs.TSync.access$000(r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        monitor-enter(r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L48
                        boolean r2 = org.pdrl.aerogcs.TSync.access$100(r2)     // Catch: java.lang.Throwable -> L48
                        if (r2 != 0) goto L1a
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L48
                    L14:
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this
                        r1.close()
                        goto L68
                    L1a:
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L48
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        java.io.InputStream r1 = org.pdrl.aerogcs.TSync.access$800(r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        r2 = 0
                        int r3 = r0.length     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        int r1 = r1.read(r0, r2, r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        if (r1 <= 0) goto L47
                        java.lang.String r2 = "TSync"
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        r3.<init>()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        java.lang.String r4 = "telemetry Input Stream Bytes Read: "
                        r3.append(r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        r3.append(r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        android.util.Log.d(r2, r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                        r3 = 3
                        r4 = 8400(0x20d0, float:1.1771E-41)
                        org.pdrl.aerogcs.TSync.access$400(r2, r3, r4, r0, r1)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                    L47:
                        goto L4
                    L48:
                        r2 = move-exception
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L48
                        throw r2     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L4d
                    L4b:
                        r1 = move-exception
                        goto L69
                    L4d:
                        r1 = move-exception
                        java.lang.String r2 = "TSync"
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4b
                        r3.<init>()     // Catch: java.lang.Throwable -> L4b
                        java.lang.String r4 = "Exception Occurred: "
                        r3.append(r4)     // Catch: java.lang.Throwable -> L4b
                        r3.append(r1)     // Catch: java.lang.Throwable -> L4b
                        java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4b
                        android.util.Log.e(r2, r3)     // Catch: java.lang.Throwable -> L4b
                        r1.printStackTrace()     // Catch: java.lang.Throwable -> L4b
                        goto L14
                    L68:
                        return
                    L69:
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this
                        r2.close()
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.pdrl.aerogcs.TSync.AnonymousClass2.run():void");
                }
            });
            this.mThreadPool.execute(new Runnable() { // from class: org.pdrl.aerogcs.TSync.3
                /* JADX WARN: Code restructure failed: missing block: B:115:0x001b, code lost:
                    r1 = org.pdrl.aerogcs.TSync.this.settingsInStream.read(r0, 0, r0.length);
                 */
                /* JADX WARN: Code restructure failed: missing block: B:116:0x0027, code lost:
                    if (r1 <= 0) goto L15;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:117:0x0029, code lost:
                    org.pdrl.aerogcs.TSync.this.sendTaiSyncMessage((byte) 3, org.pdrl.aerogcs.TSync.TAISYNC_SETTINGS_PORT, r0, r1);
                 */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public void run() {
                    /*
                        r5 = this;
                        r0 = 1024(0x400, float:1.435E-42)
                        byte[] r0 = new byte[r0]
                    L4:
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        java.lang.Object r1 = org.pdrl.aerogcs.TSync.access$000(r1)     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        monitor-enter(r1)     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L32
                        boolean r2 = org.pdrl.aerogcs.TSync.access$100(r2)     // Catch: java.lang.Throwable -> L32
                        if (r2 != 0) goto L1a
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L32
                    L14:
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this
                        r1.close()
                        goto L52
                    L1a:
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L32
                        org.pdrl.aerogcs.TSync r1 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        java.io.InputStream r1 = org.pdrl.aerogcs.TSync.access$900(r1)     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        r2 = 0
                        int r3 = r0.length     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        int r1 = r1.read(r0, r2, r3)     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        if (r1 <= 0) goto L31
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                        r3 = 3
                        r4 = 8200(0x2008, float:1.149E-41)
                        org.pdrl.aerogcs.TSync.access$400(r2, r3, r4, r0, r1)     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                    L31:
                        goto L4
                    L32:
                        r2 = move-exception
                        monitor-exit(r1)     // Catch: java.lang.Throwable -> L32
                        throw r2     // Catch: java.lang.Throwable -> L35 java.io.IOException -> L37
                    L35:
                        r1 = move-exception
                        goto L53
                    L37:
                        r1 = move-exception
                        java.lang.String r2 = "TSync"
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L35
                        r3.<init>()     // Catch: java.lang.Throwable -> L35
                        java.lang.String r4 = "Exception Occurred: "
                        r3.append(r4)     // Catch: java.lang.Throwable -> L35
                        r3.append(r1)     // Catch: java.lang.Throwable -> L35
                        java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L35
                        android.util.Log.e(r2, r3)     // Catch: java.lang.Throwable -> L35
                        r1.printStackTrace()     // Catch: java.lang.Throwable -> L35
                        goto L14
                    L52:
                        return
                    L53:
                        org.pdrl.aerogcs.TSync r2 = org.pdrl.aerogcs.TSync.this
                        r2.close()
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.pdrl.aerogcs.TSync.AnonymousClass3.run():void");
                }
            });
        }
    }

    public void sendTaiSyncMessage(byte protocol, int dataPort, byte[] data, int dataLen) throws IOException {
        byte portMSB = (byte) ((dataPort >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        byte portLSB = (byte) (dataPort & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        byte[] lA = new byte[4];
        int len = dataLen + HEADER_SIZE;
        byte[] buffer = new byte[len];
        for (int i = 3; i >= 0; i--) {
            lA[i] = (byte) (len & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
            len >>= 8;
        }
        byte[] header = {0, 0, 0, protocol, 0, 0, portMSB, portLSB, lA[0], lA[1], lA[2], lA[3], 0, 0, 0, 0, 0, 0, 0, this.vMaj, 0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(header, 0, buffer, 0, header.length);
        if (data != null && dataLen > 0) {
            System.arraycopy(data, 0, buffer, header.length, dataLen);
        }
        synchronized (this.runLock) {
            this.mFileOutputStream.write(buffer);
        }
    }

    public void close() {
        synchronized (this.runLock) {
            this.running = false;
        }
        try {
            if (this.udpSocket != null) {
                this.udpSocket.close();
            }
        } catch (Exception e) {
        }
        try {
            if (this.tcpTelemetrySocket != null) {
                this.tcpTelemetrySocket.close();
            }
        } catch (Exception e2) {
        }
        try {
            if (this.tcpSettingsSocket != null) {
                this.tcpSettingsSocket.close();
            }
        } catch (Exception e3) {
        }
        try {
            if (this.mParcelFileDescriptor != null) {
                this.mParcelFileDescriptor.close();
            }
        } catch (Exception e4) {
        }
        this.udpSocket = null;
        this.tcpSettingsSocket = null;
        this.tcpTelemetrySocket = null;
        this.settingsInStream = null;
        this.settingsOutStream = null;
        this.mParcelFileDescriptor = null;
    }
}
