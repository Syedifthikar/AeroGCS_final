package org.qtproject.qt5.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class QtBluetoothLE {
    private static final String TAG = "QtBluetoothGatt";
    private final int DEFAULT_MTU;
    private int HANDLE_FOR_MTU_EXCHANGE;
    private int HANDLE_FOR_RESET;
    private final int MAX_MTU;
    private final int RUNNABLE_TIMEOUT;
    private final UUID clientCharacteristicUuid;
    private final ArrayList<GattEntry> entries;
    private final BluetoothGattCallback gattCallback;
    private AtomicInteger handleForTimeout;
    private boolean ioJobPending;
    private final ScanCallback leScanCallback21;
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mLeScanRunning;
    private String mRemoteGattAddress;
    private int mSupportedMtu;
    Context qtContext;
    long qtObject;
    private final LinkedList<ReadWriteJob> readWriteQueue;
    private final LinkedList<Integer> servicesToBeDiscovered;
    private final Handler timeoutHandler;
    private final Hashtable<UUID, List<Integer>> uuidToEntry;

    /* loaded from: classes.dex */
    public enum GattEntryType {
        Service,
        Characteristic,
        CharacteristicValue,
        Descriptor
    }

    /* loaded from: classes.dex */
    public enum IoJobType {
        Read,
        Write,
        Mtu
    }

    public native void leCharacteristicChanged(long j, int i, byte[] bArr);

    public native void leCharacteristicRead(long j, String str, int i, String str2, int i2, byte[] bArr);

    public native void leCharacteristicWritten(long j, int i, byte[] bArr, int i2);

    public native void leConnectionStateChange(long j, int i, int i2);

    public native void leDescriptorRead(long j, String str, String str2, int i, String str3, byte[] bArr);

    public native void leDescriptorWritten(long j, int i, byte[] bArr, int i2);

    public native void leScanResult(long j, BluetoothDevice bluetoothDevice, int i, byte[] bArr);

    public native void leServiceDetailDiscoveryFinished(long j, String str, int i, int i2);

    public native void leServiceError(long j, int i, int i2);

    public native void leServicesDiscovered(long j, int i, String str);

    /* loaded from: classes.dex */
    public class TimeoutRunnable implements Runnable {
        private int pendingJobHandle;

        public TimeoutRunnable(int i) {
            QtBluetoothLE.this = r1;
            this.pendingJobHandle = -1;
            this.pendingJobHandle = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (QtBluetoothLE.this.handleForTimeout.compareAndSet(this.pendingJobHandle, QtBluetoothLE.this.HANDLE_FOR_RESET)) {
                Log.w(QtBluetoothLE.TAG, "****** Timeout for request on handle " + (this.pendingJobHandle & 65535));
                Log.w(QtBluetoothLE.TAG, "****** Looks like the peripheral does NOT act in accordance to Bluetooth 4.x spec.");
                Log.w(QtBluetoothLE.TAG, "****** Please check server implementation. Continuing under reservation.");
                if (this.pendingJobHandle > QtBluetoothLE.this.HANDLE_FOR_RESET) {
                    QtBluetoothLE.this.interruptCurrentIO(this.pendingJobHandle & 65535);
                } else if (this.pendingJobHandle < QtBluetoothLE.this.HANDLE_FOR_RESET) {
                    QtBluetoothLE.this.interruptCurrentIO(this.pendingJobHandle);
                }
            }
        }
    }

    public QtBluetoothLE() {
        this.mLeScanRunning = false;
        this.mBluetoothGatt = null;
        this.clientCharacteristicUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        this.MAX_MTU = 512;
        this.DEFAULT_MTU = 23;
        this.mSupportedMtu = -1;
        this.HANDLE_FOR_RESET = -1;
        this.HANDLE_FOR_MTU_EXCHANGE = -2;
        this.handleForTimeout = new AtomicInteger(this.HANDLE_FOR_RESET);
        this.RUNNABLE_TIMEOUT = 3000;
        this.timeoutHandler = new Handler(Looper.getMainLooper());
        this.mBluetoothLeScanner = null;
        this.qtObject = 0L;
        this.qtContext = null;
        this.leScanCallback21 = new ScanCallback() { // from class: org.qtproject.qt5.android.bluetooth.QtBluetoothLE.1
            @Override // android.bluetooth.le.ScanCallback
            public void onScanResult(int i, ScanResult scanResult) {
                super.onScanResult(i, scanResult);
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                qtBluetoothLE.leScanResult(qtBluetoothLE.qtObject, scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onBatchScanResults(List<ScanResult> list) {
                super.onBatchScanResults(list);
                for (ScanResult scanResult : list) {
                    QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                    qtBluetoothLE.leScanResult(qtBluetoothLE.qtObject, scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int i) {
                super.onScanFailed(i);
                Log.d(QtBluetoothLE.TAG, "BTLE device scan failed with " + i);
            }
        };
        this.gattCallback = new BluetoothGattCallback() { // from class: org.qtproject.qt5.android.bluetooth.QtBluetoothLE.2
            @Override // android.bluetooth.BluetoothGattCallback
            public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
                if (QtBluetoothLE.this.qtObject == 0) {
                    return;
                }
                int i3 = 2;
                if (i2 == 0) {
                    QtBluetoothLE.this.resetData();
                    if (QtBluetoothLE.this.mBluetoothGatt != null) {
                        QtBluetoothLE.this.mBluetoothGatt.close();
                    }
                    QtBluetoothLE.this.mBluetoothGatt = null;
                    i3 = 0;
                } else if (i2 != 2) {
                    i3 = 0;
                }
                if (i != 0) {
                    if (i == 8) {
                        Log.w(QtBluetoothLE.TAG, "Connection Error: Try to delay connect() call after previous activity");
                        i = 5;
                    } else if (i == 257) {
                        i = 1;
                    } else {
                        switch (i) {
                            case FT_4222_Defines.FT4222_STATUS.FT4222_DEVICE_LIST_NOT_READY /* 19 */:
                            case 20:
                            case 21:
                                Log.w(QtBluetoothLE.TAG, "The remote host closed the connection");
                                i = 7;
                                break;
                            case 22:
                                i = 8;
                                break;
                            default:
                                Log.w(QtBluetoothLE.TAG, "Unhandled error code on connectionStateChanged: " + i + " " + i2);
                                break;
                        }
                    }
                } else {
                    i = 0;
                }
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                qtBluetoothLE.leConnectionStateChange(qtBluetoothLE.qtObject, i, i3);
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                StringBuilder sb = new StringBuilder();
                if (i == 0) {
                    i = 0;
                    for (BluetoothGattService bluetoothGattService : QtBluetoothLE.this.mBluetoothGatt.getServices()) {
                        sb.append(bluetoothGattService.getUuid().toString());
                        sb.append(" ");
                    }
                } else {
                    Log.w(QtBluetoothLE.TAG, "Unhandled error code on onServicesDiscovered: " + i);
                }
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                qtBluetoothLE.leServicesDiscovered(qtBluetoothLE.qtObject, i, sb.toString());
                QtBluetoothLE.this.scheduleMtuExchange();
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                synchronized (this) {
                    int handleForCharacteristic = QtBluetoothLE.this.handleForCharacteristic(bluetoothGattCharacteristic);
                    if (handleForCharacteristic != -1 && handleForCharacteristic < QtBluetoothLE.this.entries.size()) {
                        if (!(!QtBluetoothLE.this.handleForTimeout.compareAndSet(QtBluetoothLE.this.modifiedReadWriteHandle(handleForCharacteristic, IoJobType.Read), QtBluetoothLE.this.HANDLE_FOR_RESET))) {
                            GattEntry gattEntry = (GattEntry) QtBluetoothLE.this.entries.get(handleForCharacteristic);
                            boolean z = !gattEntry.valueKnown;
                            gattEntry.valueKnown = true;
                            if (i == 0) {
                                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                                qtBluetoothLE.leCharacteristicRead(qtBluetoothLE.qtObject, bluetoothGattCharacteristic.getService().getUuid().toString(), handleForCharacteristic + 1, bluetoothGattCharacteristic.getUuid().toString(), bluetoothGattCharacteristic.getProperties(), bluetoothGattCharacteristic.getValue());
                            } else if (z) {
                                Log.w(QtBluetoothLE.TAG, "onCharacteristicRead during discovery error: " + i);
                                Log.d(QtBluetoothLE.TAG, "Non-readable characteristic " + bluetoothGattCharacteristic.getUuid() + " for service " + bluetoothGattCharacteristic.getService().getUuid());
                                QtBluetoothLE qtBluetoothLE2 = QtBluetoothLE.this;
                                qtBluetoothLE2.leCharacteristicRead(qtBluetoothLE2.qtObject, bluetoothGattCharacteristic.getService().getUuid().toString(), handleForCharacteristic + 1, bluetoothGattCharacteristic.getUuid().toString(), bluetoothGattCharacteristic.getProperties(), bluetoothGattCharacteristic.getValue());
                            } else {
                                QtBluetoothLE qtBluetoothLE3 = QtBluetoothLE.this;
                                qtBluetoothLE3.leServiceError(qtBluetoothLE3.qtObject, handleForCharacteristic + 1, 5);
                            }
                            if (z && ((GattEntry) QtBluetoothLE.this.entries.get(gattEntry.associatedServiceHandle)).endHandle == handleForCharacteristic) {
                                QtBluetoothLE.this.finishCurrentServiceDiscovery(gattEntry.associatedServiceHandle);
                            }
                            synchronized (QtBluetoothLE.this.readWriteQueue) {
                                QtBluetoothLE.this.ioJobPending = false;
                            }
                            QtBluetoothLE.this.performNextIO();
                            return;
                        }
                        Log.w(QtBluetoothLE.TAG, "Late char read reply after timeout was hit for handle " + handleForCharacteristic);
                        return;
                    }
                    Log.w(QtBluetoothLE.TAG, "Cannot find characteristic read request for read notification - handle: " + handleForCharacteristic + " size: " + QtBluetoothLE.this.entries.size());
                    synchronized (QtBluetoothLE.this.readWriteQueue) {
                        QtBluetoothLE.this.ioJobPending = false;
                    }
                    QtBluetoothLE.this.performNextIO();
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if (i != 0) {
                    Log.w(QtBluetoothLE.TAG, "onCharacteristicWrite: error " + i);
                }
                int handleForCharacteristic = QtBluetoothLE.this.handleForCharacteristic(bluetoothGattCharacteristic);
                if (handleForCharacteristic == -1) {
                    Log.w(QtBluetoothLE.TAG, "onCharacteristicWrite: cannot find handle");
                } else if (!QtBluetoothLE.this.handleForTimeout.compareAndSet(QtBluetoothLE.this.modifiedReadWriteHandle(handleForCharacteristic, IoJobType.Write), QtBluetoothLE.this.HANDLE_FOR_RESET)) {
                    Log.w(QtBluetoothLE.TAG, "Late char write reply after timeout was hit for handle " + handleForCharacteristic);
                } else {
                    int i2 = i != 0 ? 2 : 0;
                    synchronized (QtBluetoothLE.this.readWriteQueue) {
                        QtBluetoothLE.this.ioJobPending = false;
                    }
                    QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                    qtBluetoothLE.leCharacteristicWritten(qtBluetoothLE.qtObject, handleForCharacteristic + 1, bluetoothGattCharacteristic.getValue(), i2);
                    QtBluetoothLE.this.performNextIO();
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                int handleForCharacteristic = QtBluetoothLE.this.handleForCharacteristic(bluetoothGattCharacteristic);
                if (handleForCharacteristic == -1) {
                    Log.w(QtBluetoothLE.TAG, "onCharacteristicChanged: cannot find handle");
                    return;
                }
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                qtBluetoothLE.leCharacteristicChanged(qtBluetoothLE.qtObject, handleForCharacteristic + 1, bluetoothGattCharacteristic.getValue());
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
                synchronized (this) {
                    int handleForDescriptor = QtBluetoothLE.this.handleForDescriptor(bluetoothGattDescriptor);
                    if (handleForDescriptor != -1 && handleForDescriptor < QtBluetoothLE.this.entries.size()) {
                        if (!(!QtBluetoothLE.this.handleForTimeout.compareAndSet(QtBluetoothLE.this.modifiedReadWriteHandle(handleForDescriptor, IoJobType.Read), QtBluetoothLE.this.HANDLE_FOR_RESET))) {
                            GattEntry gattEntry = (GattEntry) QtBluetoothLE.this.entries.get(handleForDescriptor);
                            boolean z = !gattEntry.valueKnown;
                            gattEntry.valueKnown = true;
                            if (i == 0) {
                                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                                qtBluetoothLE.leDescriptorRead(qtBluetoothLE.qtObject, bluetoothGattDescriptor.getCharacteristic().getService().getUuid().toString(), bluetoothGattDescriptor.getCharacteristic().getUuid().toString(), handleForDescriptor + 1, bluetoothGattDescriptor.getUuid().toString(), bluetoothGattDescriptor.getValue());
                            } else if (z) {
                                Log.w(QtBluetoothLE.TAG, "onDescriptorRead during discovery error: " + i);
                                Log.d(QtBluetoothLE.TAG, "Non-readable descriptor " + bluetoothGattDescriptor.getUuid() + " for characteristic " + bluetoothGattDescriptor.getCharacteristic().getUuid() + " for service " + bluetoothGattDescriptor.getCharacteristic().getService().getUuid());
                                QtBluetoothLE qtBluetoothLE2 = QtBluetoothLE.this;
                                qtBluetoothLE2.leDescriptorRead(qtBluetoothLE2.qtObject, bluetoothGattDescriptor.getCharacteristic().getService().getUuid().toString(), bluetoothGattDescriptor.getCharacteristic().getUuid().toString(), handleForDescriptor + 1, bluetoothGattDescriptor.getUuid().toString(), bluetoothGattDescriptor.getValue());
                            } else {
                                QtBluetoothLE qtBluetoothLE3 = QtBluetoothLE.this;
                                qtBluetoothLE3.leServiceError(qtBluetoothLE3.qtObject, handleForDescriptor + 1, 6);
                            }
                            if (z) {
                                if (((GattEntry) QtBluetoothLE.this.entries.get(gattEntry.associatedServiceHandle)).endHandle == handleForDescriptor) {
                                    QtBluetoothLE.this.finishCurrentServiceDiscovery(gattEntry.associatedServiceHandle);
                                }
                                if (bluetoothGattDescriptor.getUuid().compareTo(QtBluetoothLE.this.clientCharacteristicUuid) == 0) {
                                    byte[] value = bluetoothGattDescriptor.getValue();
                                    if ((((value == null || value.length <= 0) ? (byte) 0 : value[0]) & 3) > 0) {
                                        Log.d(QtBluetoothLE.TAG, "Found descriptor with automatic notifications.");
                                        QtBluetoothLE.this.mBluetoothGatt.setCharacteristicNotification(bluetoothGattDescriptor.getCharacteristic(), true);
                                    }
                                }
                            }
                            synchronized (QtBluetoothLE.this.readWriteQueue) {
                                QtBluetoothLE.this.ioJobPending = false;
                            }
                            QtBluetoothLE.this.performNextIO();
                            return;
                        }
                        Log.w(QtBluetoothLE.TAG, "Late descriptor read reply after timeout was hit for handle " + handleForDescriptor);
                        return;
                    }
                    Log.w(QtBluetoothLE.TAG, "Cannot find descriptor read request for read notification - handle: " + handleForDescriptor + " size: " + QtBluetoothLE.this.entries.size());
                    synchronized (QtBluetoothLE.this.readWriteQueue) {
                        QtBluetoothLE.this.ioJobPending = false;
                    }
                    QtBluetoothLE.this.performNextIO();
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
                if (i != 0) {
                    Log.w(QtBluetoothLE.TAG, "onDescriptorWrite: error " + i);
                }
                int handleForDescriptor = QtBluetoothLE.this.handleForDescriptor(bluetoothGattDescriptor);
                if (!QtBluetoothLE.this.handleForTimeout.compareAndSet(QtBluetoothLE.this.modifiedReadWriteHandle(handleForDescriptor, IoJobType.Write), QtBluetoothLE.this.HANDLE_FOR_RESET)) {
                    Log.w(QtBluetoothLE.TAG, "Late descriptor write reply after timeout was hit for handle " + handleForDescriptor);
                    return;
                }
                int i2 = i != 0 ? 3 : 0;
                synchronized (QtBluetoothLE.this.readWriteQueue) {
                    QtBluetoothLE.this.ioJobPending = false;
                }
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                qtBluetoothLE.leDescriptorWritten(qtBluetoothLE.qtObject, handleForDescriptor + 1, bluetoothGattDescriptor.getValue(), i2);
                QtBluetoothLE.this.performNextIO();
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
                if (i2 == 0) {
                    Log.w(QtBluetoothLE.TAG, "MTU changed to " + i);
                    QtBluetoothLE.this.mSupportedMtu = i;
                } else {
                    Log.w(QtBluetoothLE.TAG, "MTU change error " + i2 + ". New MTU " + i);
                    QtBluetoothLE.this.mSupportedMtu = 23;
                }
                AtomicInteger atomicInteger = QtBluetoothLE.this.handleForTimeout;
                QtBluetoothLE qtBluetoothLE = QtBluetoothLE.this;
                if (!(!atomicInteger.compareAndSet(qtBluetoothLE.modifiedReadWriteHandle(qtBluetoothLE.HANDLE_FOR_MTU_EXCHANGE, IoJobType.Mtu), QtBluetoothLE.this.HANDLE_FOR_RESET))) {
                    synchronized (QtBluetoothLE.this.readWriteQueue) {
                        QtBluetoothLE.this.ioJobPending = false;
                    }
                    QtBluetoothLE.this.performNextIO();
                    return;
                }
                Log.w(QtBluetoothLE.TAG, "Late mtu reply after timeout was hit");
            }
        };
        this.uuidToEntry = new Hashtable<>(100);
        this.entries = new ArrayList<>(100);
        this.servicesToBeDiscovered = new LinkedList<>();
        this.readWriteQueue = new LinkedList<>();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        this.mBluetoothLeScanner = defaultAdapter.getBluetoothLeScanner();
    }

    public QtBluetoothLE(String str, Context context) {
        this();
        this.qtContext = context;
        this.mRemoteGattAddress = str;
    }

    public boolean scanForLeDevice(boolean z) {
        if (z == this.mLeScanRunning) {
            return true;
        }
        if (z) {
            Log.d(TAG, "New BTLE scanning API");
            this.mBluetoothLeScanner.startScan(new ArrayList(2), new ScanSettings.Builder().setScanMode(1).build(), this.leScanCallback21);
            this.mLeScanRunning = true;
        } else {
            this.mBluetoothLeScanner.stopScan(this.leScanCallback21);
            this.mLeScanRunning = false;
        }
        return this.mLeScanRunning == z;
    }

    public boolean connect() {
        try {
            BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(this.mRemoteGattAddress);
            try {
                Method declaredMethod = remoteDevice.getClass().getDeclaredMethod("connectGatt", Context.class, Boolean.TYPE, BluetoothGattCallback.class, Integer.TYPE);
                if (declaredMethod != null) {
                    this.mBluetoothGatt = (BluetoothGatt) declaredMethod.invoke(remoteDevice, this.qtContext, false, this.gattCallback, 2);
                    Log.w(TAG, "Using Android v23 BluetoothDevice.connectGatt()");
                }
            } catch (Exception e) {
                this.mBluetoothGatt = remoteDevice.connectGatt(this.qtContext, false, this.gattCallback);
            }
            return this.mBluetoothGatt != null;
        } catch (IllegalArgumentException e2) {
            Log.w(TAG, "Remote address is not valid: " + this.mRemoteGattAddress);
            return false;
        }
    }

    public void disconnect() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
    }

    public boolean discoverServices() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        return bluetoothGatt != null && bluetoothGatt.discoverServices();
    }

    /* loaded from: classes.dex */
    public class GattEntry {
        public int associatedServiceHandle;
        public BluetoothGattCharacteristic characteristic;
        public BluetoothGattDescriptor descriptor;
        public int endHandle;
        public BluetoothGattService service;
        public GattEntryType type;
        public boolean valueKnown;

        private GattEntry() {
            QtBluetoothLE.this = r1;
            this.valueKnown = false;
            this.service = null;
            this.characteristic = null;
            this.descriptor = null;
            this.endHandle = -1;
        }
    }

    /* loaded from: classes.dex */
    public class ReadWriteJob {
        public GattEntry entry;
        public IoJobType jobType;
        public byte[] newValue;
        public int requestedWriteType;

        private ReadWriteJob() {
            QtBluetoothLE.this = r1;
        }
    }

    public int handleForCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        List<Integer> list;
        if (bluetoothGattCharacteristic == null || (list = this.uuidToEntry.get(bluetoothGattCharacteristic.getService().getUuid())) == null || list.isEmpty()) {
            return -1;
        }
        int intValue = list.get(0).intValue();
        while (true) {
            try {
                intValue++;
                if (intValue >= this.entries.size()) {
                    break;
                }
                GattEntry gattEntry = this.entries.get(intValue);
                if (gattEntry != null && AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[gattEntry.type.ordinal()] == 4 && gattEntry.characteristic == bluetoothGattCharacteristic) {
                    return intValue;
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return -1;
    }

    public int handleForDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        List<Integer> list;
        if (bluetoothGattDescriptor == null || (list = this.uuidToEntry.get(bluetoothGattDescriptor.getCharacteristic().getService().getUuid())) == null || list.isEmpty()) {
            return -1;
        }
        for (int intValue = list.get(0).intValue() + 1; intValue < this.entries.size(); intValue++) {
            try {
                GattEntry gattEntry = this.entries.get(intValue);
                if (gattEntry != null && AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[gattEntry.type.ordinal()] == 1 && gattEntry.descriptor == bluetoothGattDescriptor) {
                    return intValue;
                }
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return -1;
    }

    private void populateHandles() {
        for (BluetoothGattService bluetoothGattService : this.mBluetoothGatt.getServices()) {
            GattEntry gattEntry = new GattEntry();
            gattEntry.type = GattEntryType.Service;
            gattEntry.service = bluetoothGattService;
            this.entries.add(gattEntry);
            int size = this.entries.size() - 1;
            gattEntry.associatedServiceHandle = size;
            List<Integer> list = this.uuidToEntry.get(bluetoothGattService.getUuid());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(Integer.valueOf(this.entries.size() - 1));
            this.uuidToEntry.put(bluetoothGattService.getUuid(), list);
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                GattEntry gattEntry2 = new GattEntry();
                gattEntry2.type = GattEntryType.Characteristic;
                gattEntry2.characteristic = bluetoothGattCharacteristic;
                gattEntry2.associatedServiceHandle = size;
                this.entries.add(gattEntry2);
                GattEntry gattEntry3 = new GattEntry();
                gattEntry3.type = GattEntryType.CharacteristicValue;
                gattEntry3.associatedServiceHandle = size;
                gattEntry3.endHandle = this.entries.size();
                this.entries.add(gattEntry3);
                for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                    GattEntry gattEntry4 = new GattEntry();
                    gattEntry4.type = GattEntryType.Descriptor;
                    gattEntry4.descriptor = bluetoothGattDescriptor;
                    gattEntry4.associatedServiceHandle = size;
                    this.entries.add(gattEntry4);
                }
            }
            gattEntry.endHandle = this.entries.size() - 1;
        }
        this.entries.trimToSize();
    }

    public void resetData() {
        synchronized (this) {
            this.uuidToEntry.clear();
            this.entries.clear();
            this.servicesToBeDiscovered.clear();
        }
        this.timeoutHandler.removeCallbacksAndMessages(null);
        this.handleForTimeout.set(this.HANDLE_FOR_RESET);
        synchronized (this.readWriteQueue) {
            this.readWriteQueue.clear();
        }
    }

    public synchronized boolean discoverServiceDetails(String str) {
        try {
            if (this.mBluetoothGatt == null) {
                return false;
            }
            if (this.entries.isEmpty()) {
                populateHandles();
            }
            try {
                UUID fromString = UUID.fromString(str);
                List<Integer> list = this.uuidToEntry.get(fromString);
                if (list != null && !list.isEmpty()) {
                    int intValue = list.get(0).intValue();
                    GattEntry gattEntry = this.entries.get(intValue);
                    if (gattEntry == null) {
                        Log.w(TAG, "Service with UUID " + fromString.toString() + " not found");
                        return false;
                    } else if (gattEntry.type != GattEntryType.Service) {
                        Log.w(TAG, "Given UUID is not a service UUID: " + str);
                        return false;
                    } else {
                        if (!gattEntry.valueKnown && !this.servicesToBeDiscovered.contains(Integer.valueOf(intValue))) {
                            this.servicesToBeDiscovered.add(Integer.valueOf(intValue));
                            scheduleServiceDetailDiscovery(intValue);
                            performNextIO();
                            return true;
                        }
                        Log.w(TAG, "Service already known or to be discovered");
                        return true;
                    }
                }
                Log.w(TAG, "Unknown service uuid for current device: " + fromString.toString());
                return false;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Cannot parse given UUID");
                return false;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public String includedServices(String str) {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        try {
            BluetoothGattService service = this.mBluetoothGatt.getService(UUID.fromString(str));
            if (service == null) {
                return null;
            }
            List<BluetoothGattService> includedServices = service.getIncludedServices();
            if (includedServices.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (BluetoothGattService bluetoothGattService : includedServices) {
                sb.append(bluetoothGattService.getUuid().toString());
                sb.append(" ");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void finishCurrentServiceDiscovery(int i) {
        Log.w(TAG, "Finished current discovery for service handle " + i);
        GattEntry gattEntry = this.entries.get(i);
        gattEntry.valueKnown = true;
        synchronized (this) {
            try {
                this.servicesToBeDiscovered.removeFirst();
            } catch (NoSuchElementException e) {
                Log.w(TAG, "Expected queued service but didn't find any");
            }
        }
        leServiceDetailDiscoveryFinished(this.qtObject, gattEntry.service.getUuid().toString(), i + 1, gattEntry.endHandle + 1);
    }

    private boolean executeMtuExchange() {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Method declaredMethod = this.mBluetoothGatt.getClass().getDeclaredMethod("requestMtu", Integer.TYPE);
                if (declaredMethod != null) {
                    if (((Boolean) declaredMethod.invoke(this.mBluetoothGatt, 512)).booleanValue()) {
                        Log.w(TAG, "MTU change initiated");
                        return false;
                    }
                    Log.w(TAG, "MTU change request failed");
                }
            } catch (Exception e) {
            }
        }
        Log.w(TAG, "Assuming default MTU value of 23 bytes");
        this.mSupportedMtu = 23;
        return true;
    }

    public void scheduleMtuExchange() {
        ReadWriteJob readWriteJob = new ReadWriteJob();
        readWriteJob.jobType = IoJobType.Mtu;
        readWriteJob.entry = null;
        synchronized (this.readWriteQueue) {
            this.readWriteQueue.add(readWriteJob);
        }
        performNextIO();
    }

    private void scheduleServiceDetailDiscovery(int i) {
        GattEntry gattEntry = this.entries.get(i);
        int i2 = gattEntry.endHandle;
        if (i == i2) {
            Log.w(TAG, "scheduleServiceDetailDiscovery: service is empty; nothing to discover");
            finishCurrentServiceDiscovery(i);
            return;
        }
        synchronized (this.readWriteQueue) {
            while (true) {
                i++;
                if (i <= i2) {
                    GattEntry gattEntry2 = this.entries.get(i);
                    if (AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[gattEntry2.type.ordinal()] == 3) {
                        Log.w(TAG, "scheduleServiceDetailDiscovery: wrong endHandle");
                        return;
                    }
                    ReadWriteJob readWriteJob = new ReadWriteJob();
                    readWriteJob.entry = gattEntry2;
                    readWriteJob.jobType = IoJobType.Read;
                    if (!this.readWriteQueue.add(readWriteJob)) {
                        Log.w(TAG, "Cannot add service discovery job for " + gattEntry.service.getUuid() + " on item " + gattEntry2.type);
                    }
                } else {
                    return;
                }
            }
        }
    }

    public boolean writeCharacteristic(int i, byte[] bArr, int i2) {
        boolean add;
        if (this.mBluetoothGatt == null) {
            return false;
        }
        try {
            GattEntry gattEntry = this.entries.get(i - 1);
            ReadWriteJob readWriteJob = new ReadWriteJob();
            readWriteJob.newValue = bArr;
            readWriteJob.entry = gattEntry;
            readWriteJob.jobType = IoJobType.Write;
            if (i2 == 1) {
                readWriteJob.requestedWriteType = 1;
            } else if (i2 == 2) {
                readWriteJob.requestedWriteType = 4;
            } else {
                readWriteJob.requestedWriteType = 2;
            }
            synchronized (this.readWriteQueue) {
                add = this.readWriteQueue.add(readWriteJob);
            }
            if (!add) {
                Log.w(TAG, "Cannot add characteristic write request for " + i + " to queue");
                return false;
            }
            performNextIO();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeDescriptor(int i, byte[] bArr) {
        boolean add;
        if (this.mBluetoothGatt == null) {
            return false;
        }
        try {
            GattEntry gattEntry = this.entries.get(i - 1);
            ReadWriteJob readWriteJob = new ReadWriteJob();
            readWriteJob.newValue = bArr;
            readWriteJob.entry = gattEntry;
            readWriteJob.requestedWriteType = 2;
            readWriteJob.jobType = IoJobType.Write;
            synchronized (this.readWriteQueue) {
                add = this.readWriteQueue.add(readWriteJob);
            }
            if (!add) {
                Log.w(TAG, "Cannot add descriptor write request for " + i + " to queue");
                return false;
            }
            performNextIO();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean readCharacteristic(int i) {
        boolean add;
        if (this.mBluetoothGatt == null) {
            return false;
        }
        try {
            GattEntry gattEntry = this.entries.get(i - 1);
            ReadWriteJob readWriteJob = new ReadWriteJob();
            readWriteJob.entry = gattEntry;
            readWriteJob.jobType = IoJobType.Read;
            synchronized (this.readWriteQueue) {
                add = this.readWriteQueue.add(readWriteJob);
            }
            if (!add) {
                Log.w(TAG, "Cannot add characteristic read request for " + i + " to queue");
                return false;
            }
            performNextIO();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean readDescriptor(int i) {
        boolean add;
        if (this.mBluetoothGatt == null) {
            return false;
        }
        try {
            GattEntry gattEntry = this.entries.get(i - 1);
            ReadWriteJob readWriteJob = new ReadWriteJob();
            readWriteJob.entry = gattEntry;
            readWriteJob.jobType = IoJobType.Read;
            synchronized (this.readWriteQueue) {
                add = this.readWriteQueue.add(readWriteJob);
            }
            if (!add) {
                Log.w(TAG, "Cannot add descriptor read request for " + i + " to queue");
                return false;
            }
            performNextIO();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void interruptCurrentIO(int i) {
        synchronized (this.readWriteQueue) {
            this.ioJobPending = false;
        }
        performNextIO();
        if (i == this.HANDLE_FOR_MTU_EXCHANGE) {
            return;
        }
        try {
            synchronized (this) {
                GattEntry gattEntry = this.entries.get(i);
                if (gattEntry == null) {
                    return;
                }
                if (gattEntry.valueKnown) {
                    return;
                }
                gattEntry.valueKnown = true;
                GattEntry gattEntry2 = this.entries.get(gattEntry.associatedServiceHandle);
                if (gattEntry2 != null && gattEntry2.endHandle == i) {
                    finishCurrentServiceDiscovery(gattEntry.associatedServiceHandle);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "interruptCurrentIO(): Unknown gatt entry, index: " + i + " size: " + this.entries.size());
        }
    }

    public void performNextIO() {
        boolean executeReadJob;
        int i;
        if (this.mBluetoothGatt == null) {
            return;
        }
        int i2 = this.HANDLE_FOR_RESET;
        synchronized (this.readWriteQueue) {
            if (!this.readWriteQueue.isEmpty() && !this.ioJobPending) {
                ReadWriteJob remove = this.readWriteQueue.remove();
                if (remove.jobType == IoJobType.Mtu) {
                    i2 = this.HANDLE_FOR_MTU_EXCHANGE;
                } else {
                    int i3 = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[remove.entry.type.ordinal()];
                    if (i3 == 1) {
                        i2 = handleForDescriptor(remove.entry.descriptor);
                    } else if (i3 == 2) {
                        i2 = remove.entry.endHandle;
                    } else if (i3 == 4) {
                        i2 = handleForCharacteristic(remove.entry.characteristic);
                    }
                }
                this.timeoutHandler.removeCallbacksAndMessages(null);
                this.handleForTimeout.set(modifiedReadWriteHandle(i2, remove.jobType));
                int i4 = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType[remove.jobType.ordinal()];
                if (i4 == 1) {
                    executeReadJob = executeReadJob(remove);
                } else if (i4 == 2) {
                    executeReadJob = executeWriteJob(remove);
                } else {
                    executeReadJob = i4 != 3 ? false : executeMtuExchange();
                }
                if (executeReadJob) {
                    this.handleForTimeout.set(this.HANDLE_FOR_RESET);
                } else {
                    this.ioJobPending = true;
                    this.timeoutHandler.postDelayed(new TimeoutRunnable(modifiedReadWriteHandle(i2, remove.jobType)), 3000L);
                }
                if (remove.jobType != IoJobType.Mtu) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Performing queued job, handle: ");
                    sb.append(i2);
                    sb.append(" ");
                    sb.append(remove.jobType);
                    sb.append(" (");
                    sb.append(remove.requestedWriteType == 1);
                    sb.append(") ValueKnown: ");
                    sb.append(remove.entry.valueKnown);
                    sb.append(" Skipping: ");
                    sb.append(executeReadJob);
                    sb.append(" ");
                    sb.append(remove.entry.type);
                    Log.w(TAG, sb.toString());
                }
                GattEntry gattEntry = remove.entry;
                if (executeReadJob) {
                    if (i2 > this.HANDLE_FOR_RESET) {
                        if (!gattEntry.valueKnown) {
                            gattEntry.valueKnown = true;
                            int i5 = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[gattEntry.type.ordinal()];
                            if (i5 == 1) {
                                Log.d(TAG, "Non-readable descriptor " + gattEntry.descriptor.getUuid() + " for service/char" + gattEntry.descriptor.getCharacteristic().getService().getUuid() + "/" + gattEntry.descriptor.getCharacteristic().getUuid());
                                leDescriptorRead(this.qtObject, gattEntry.descriptor.getCharacteristic().getService().getUuid().toString(), gattEntry.descriptor.getCharacteristic().getUuid().toString(), i2 + 1, gattEntry.descriptor.getUuid().toString(), gattEntry.descriptor.getValue());
                            } else if (i5 == 3) {
                                Log.w(TAG, "Scheduling of Service Gatt entry for service discovery should never happen.");
                            } else if (i5 == 4) {
                                Log.d(TAG, "Non-readable characteristic " + gattEntry.characteristic.getUuid() + " for service " + gattEntry.characteristic.getService().getUuid());
                                leCharacteristicRead(this.qtObject, gattEntry.characteristic.getService().getUuid().toString(), i2 + 1, gattEntry.characteristic.getUuid().toString(), gattEntry.characteristic.getProperties(), gattEntry.characteristic.getValue());
                            }
                            synchronized (this) {
                                try {
                                    if (this.entries.get(gattEntry.associatedServiceHandle).endHandle == i2) {
                                        finishCurrentServiceDiscovery(gattEntry.associatedServiceHandle);
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    Log.w(TAG, "performNextIO(): Unknown service for entry, index: " + gattEntry.associatedServiceHandle + " size: " + this.entries.size());
                                }
                            }
                        } else {
                            if (remove.jobType == IoJobType.Read) {
                                i = gattEntry.type == GattEntryType.Characteristic ? 5 : 6;
                            } else {
                                i = gattEntry.type != GattEntryType.Characteristic ? 3 : 2;
                            }
                            leServiceError(this.qtObject, i2 + 1, i);
                        }
                    }
                    performNextIO();
                }
            }
        }
    }

    /* renamed from: org.qtproject.qt5.android.bluetooth.QtBluetoothLE$3 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType;
        static final /* synthetic */ int[] $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType;

        static {
            int[] iArr = new int[IoJobType.values().length];
            $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType = iArr;
            try {
                iArr[IoJobType.Read.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType[IoJobType.Write.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType[IoJobType.Mtu.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[GattEntryType.values().length];
            $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType = iArr2;
            try {
                iArr2[GattEntryType.Descriptor.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[GattEntryType.CharacteristicValue.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[GattEntryType.Service.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[GattEntryType.Characteristic.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private boolean executeWriteJob(ReadWriteJob readWriteJob) {
        int i = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[readWriteJob.entry.type.ordinal()];
        if (i == 1) {
            if (readWriteJob.entry.descriptor.getUuid().compareTo(this.clientCharacteristicUuid) == 0) {
                int i2 = readWriteJob.newValue[0] & 255;
                boolean z = (i2 & 1) == 1 || ((i2 >> 1) & 1) == 1;
                if (!this.mBluetoothGatt.setCharacteristicNotification(readWriteJob.entry.descriptor.getCharacteristic(), z)) {
                    Log.w(TAG, "Cannot set characteristic notification");
                }
                Log.d(TAG, "Enable notifications: " + z);
            }
            if (!readWriteJob.entry.descriptor.setValue(readWriteJob.newValue) || !this.mBluetoothGatt.writeDescriptor(readWriteJob.entry.descriptor)) {
                return true;
            }
        } else if (i == 2 || i == 3) {
            return true;
        } else {
            if (i == 4) {
                if (readWriteJob.entry.characteristic.getWriteType() != readWriteJob.requestedWriteType) {
                    readWriteJob.entry.characteristic.setWriteType(readWriteJob.requestedWriteType);
                }
                if (!readWriteJob.entry.characteristic.setValue(readWriteJob.newValue) || !this.mBluetoothGatt.writeCharacteristic(readWriteJob.entry.characteristic)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean executeReadJob(ReadWriteJob readWriteJob) {
        boolean z;
        boolean z2;
        int i = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$GattEntryType[readWriteJob.entry.type.ordinal()];
        if (i == 1) {
            try {
                z = this.mBluetoothGatt.readDescriptor(readWriteJob.entry.descriptor);
            } catch (SecurityException e) {
                e.printStackTrace();
                z = false;
            }
            if (!z) {
                return true;
            }
        } else if (i == 2 || i == 3) {
            return true;
        } else {
            if (i == 4) {
                try {
                    z2 = this.mBluetoothGatt.readCharacteristic(readWriteJob.entry.characteristic);
                } catch (SecurityException e2) {
                    e2.printStackTrace();
                    z2 = false;
                }
                if (!z2) {
                    return true;
                }
            }
        }
        return false;
    }

    public int modifiedReadWriteHandle(int i, IoJobType ioJobType) {
        if (i > 65535) {
            Log.w(TAG, "Invalid handle");
        }
        int i2 = i & 65535;
        int i3 = AnonymousClass3.$SwitchMap$org$qtproject$qt5$android$bluetooth$QtBluetoothLE$IoJobType[ioJobType.ordinal()];
        if (i3 != 1) {
            if (i3 != 2) {
                if (i3 == 3) {
                    return this.HANDLE_FOR_MTU_EXCHANGE;
                }
                return i2;
            }
            return i2 | 65536;
        }
        return i2 | 131072;
    }

    public boolean requestConnectionUpdatePriority(double d) {
        int i;
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return false;
        }
        try {
            Method declaredMethod = bluetoothGatt.getClass().getDeclaredMethod("requestConnectionPriority", Integer.TYPE);
            if (declaredMethod == null) {
                return false;
            }
            if (d < 30.0d) {
                i = 1;
            } else if (d <= 100.0d) {
                i = 0;
            } else {
                i = 2;
            }
            return ((Boolean) declaredMethod.invoke(this.mBluetoothGatt, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }
}
