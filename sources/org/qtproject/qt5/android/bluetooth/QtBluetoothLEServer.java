package org.qtproject.qt5.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class QtBluetoothLEServer {
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "QtBluetoothGattServer";
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mLeAdvertiser;
    private Context qtContext;
    long qtObject = 0;
    private BluetoothGattServer mGattServer = null;
    private String mRemoteName = BuildConfig.FLAVOR;
    private String mRemoteAddress = BuildConfig.FLAVOR;
    ClientCharacteristicManager clientCharacteristicManager = new ClientCharacteristicManager();
    private BluetoothGattServerCallback mGattServerListener = new BluetoothGattServerCallback() { // from class: org.qtproject.qt5.android.bluetooth.QtBluetoothLEServer.1
        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i2) {
            Log.w(QtBluetoothLEServer.TAG, "Our gatt server connection state changed, new state: " + i2 + " " + i);
            super.onConnectionStateChange(bluetoothDevice, i, i2);
            int i3 = 2;
            if (i2 != 0) {
                if (i2 != 2) {
                    i3 = 0;
                } else {
                    QtBluetoothLEServer.this.clientCharacteristicManager.markDeviceConnectivity(bluetoothDevice, true);
                }
            } else {
                QtBluetoothLEServer.this.clientCharacteristicManager.markDeviceConnectivity(bluetoothDevice, false);
                QtBluetoothLEServer.this.mGattServer.close();
                QtBluetoothLEServer.this.mGattServer = null;
                i3 = 0;
            }
            QtBluetoothLEServer.this.mRemoteName = bluetoothDevice.getName();
            QtBluetoothLEServer.this.mRemoteAddress = bluetoothDevice.getAddress();
            if (i == 0) {
                i = 0;
            } else {
                Log.w(QtBluetoothLEServer.TAG, "Unhandled error code on peripheral connectionStateChanged: " + i + " " + i2);
            }
            QtBluetoothLEServer qtBluetoothLEServer = QtBluetoothLEServer.this;
            qtBluetoothLEServer.leServerConnectionStateChange(qtBluetoothLEServer.qtObject, i, i3);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onServiceAdded(int i, BluetoothGattService bluetoothGattService) {
            super.onServiceAdded(i, bluetoothGattService);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onCharacteristicReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            try {
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, 0, i2, Arrays.copyOfRange(bluetoothGattCharacteristic.getValue(), i2, bluetoothGattCharacteristic.getValue().length));
            } catch (Exception e) {
                Log.w(QtBluetoothLEServer.TAG, "onCharacteristicReadRequest: " + i + " " + i2 + " " + bluetoothGattCharacteristic.getValue().length);
                e.printStackTrace();
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, 257, i2, null);
            }
            super.onCharacteristicReadRequest(bluetoothDevice, i, i2, bluetoothGattCharacteristic);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onCharacteristicWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z, boolean z2, int i2, byte[] bArr) {
            boolean z3;
            Log.w(QtBluetoothLEServer.TAG, "onCharacteristicWriteRequest");
            int i3 = 0;
            if (!z) {
                if (i2 == 0) {
                    bluetoothGattCharacteristic.setValue(bArr);
                    QtBluetoothLEServer qtBluetoothLEServer = QtBluetoothLEServer.this;
                    qtBluetoothLEServer.leServerCharacteristicChanged(qtBluetoothLEServer.qtObject, bluetoothGattCharacteristic, bArr);
                    z3 = true;
                } else {
                    Log.w(QtBluetoothLEServer.TAG, "onCharacteristicWriteRequest: !preparedWrite, offset " + i2 + ", Not supported");
                    i3 = 6;
                    z3 = false;
                }
            } else {
                Log.w(QtBluetoothLEServer.TAG, "onCharacteristicWriteRequest: preparedWrite, offset " + i2 + ", Not supported");
                i3 = 6;
                z3 = false;
            }
            if (z2) {
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, i3, i2, bArr);
            }
            if (z3) {
                QtBluetoothLEServer.this.sendNotificationsOrIndications(bluetoothGattCharacteristic);
            }
            super.onCharacteristicWriteRequest(bluetoothDevice, i, bluetoothGattCharacteristic, z, z2, i2, bArr);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onDescriptorReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattDescriptor bluetoothGattDescriptor) {
            byte[] value = bluetoothGattDescriptor.getValue();
            try {
                if (bluetoothGattDescriptor.getUuid().equals(QtBluetoothLEServer.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) && (value = QtBluetoothLEServer.this.clientCharacteristicManager.valueFor(bluetoothGattDescriptor.getCharacteristic(), bluetoothDevice)) == null) {
                    value = bluetoothGattDescriptor.getValue();
                }
                value = Arrays.copyOfRange(value, i2, value.length);
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, 0, i2, value);
            } catch (Exception e) {
                Log.w(QtBluetoothLEServer.TAG, "onDescriptorReadRequest: " + i + " " + i2 + " " + value.length);
                e.printStackTrace();
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, 257, i2, null);
            }
            super.onDescriptorReadRequest(bluetoothDevice, i, i2, bluetoothGattDescriptor);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onDescriptorWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattDescriptor bluetoothGattDescriptor, boolean z, boolean z2, int i2, byte[] bArr) {
            int i3;
            if (!z) {
                if (i2 == 0) {
                    bluetoothGattDescriptor.setValue(bArr);
                    if (bluetoothGattDescriptor.getUuid().equals(QtBluetoothLEServer.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID)) {
                        QtBluetoothLEServer.this.clientCharacteristicManager.insertOrUpdate(bluetoothGattDescriptor.getCharacteristic(), bluetoothDevice, bArr);
                    }
                    QtBluetoothLEServer qtBluetoothLEServer = QtBluetoothLEServer.this;
                    qtBluetoothLEServer.leServerDescriptorWritten(qtBluetoothLEServer.qtObject, bluetoothGattDescriptor, bArr);
                    i3 = 0;
                } else {
                    Log.w(QtBluetoothLEServer.TAG, "onDescriptorWriteRequest: !preparedWrite, offset " + i2 + ", Not supported");
                    i3 = 6;
                }
            } else {
                Log.w(QtBluetoothLEServer.TAG, "onDescriptorWriteRequest: preparedWrite, offset " + i2 + ", Not supported");
                i3 = 6;
            }
            if (z2) {
                QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, i3, i2, bArr);
            }
            super.onDescriptorWriteRequest(bluetoothDevice, i, bluetoothGattDescriptor, z, z2, i2, bArr);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onExecuteWrite(BluetoothDevice bluetoothDevice, int i, boolean z) {
            QtBluetoothLEServer.this.mGattServer.sendResponse(bluetoothDevice, i, 6, 0, null);
            super.onExecuteWrite(bluetoothDevice, i, z);
        }

        @Override // android.bluetooth.BluetoothGattServerCallback
        public void onNotificationSent(BluetoothDevice bluetoothDevice, int i) {
            super.onNotificationSent(bluetoothDevice, i);
            Log.w(QtBluetoothLEServer.TAG, "onNotificationSent" + bluetoothDevice + " " + i);
        }
    };
    private AdvertiseCallback mAdvertiseListener = new AdvertiseCallback() { // from class: org.qtproject.qt5.android.bluetooth.QtBluetoothLEServer.2
        @Override // android.bluetooth.le.AdvertiseCallback
        public void onStartSuccess(AdvertiseSettings advertiseSettings) {
            super.onStartSuccess(advertiseSettings);
        }

        @Override // android.bluetooth.le.AdvertiseCallback
        public void onStartFailure(int i) {
            Log.e(QtBluetoothLEServer.TAG, "Advertising failure: " + i);
            super.onStartFailure(i);
            int i2 = 3;
            if (i == 1) {
                i2 = 1;
            } else if (i != 2) {
                if (i == 3) {
                    return;
                }
                if (i == 5) {
                    i2 = 2;
                }
            } else {
                i2 = 4;
            }
            if (i2 > 0) {
                QtBluetoothLEServer qtBluetoothLEServer = QtBluetoothLEServer.this;
                qtBluetoothLEServer.leServerAdvertisementError(qtBluetoothLEServer.qtObject, i2);
            }
        }
    };

    public native void leServerAdvertisementError(long j, int i);

    public native void leServerCharacteristicChanged(long j, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr);

    public native void leServerConnectionStateChange(long j, int i, int i2);

    public native void leServerDescriptorWritten(long j, BluetoothGattDescriptor bluetoothGattDescriptor, byte[] bArr);

    public String remoteName() {
        return this.mRemoteName;
    }

    public String remoteAddress() {
        return this.mRemoteAddress;
    }

    /* loaded from: classes.dex */
    public class ClientCharacteristicManager {
        private final HashMap<BluetoothGattCharacteristic, List<Entry>> notificationStore;

        private ClientCharacteristicManager() {
            QtBluetoothLEServer.this = r1;
            this.notificationStore = new HashMap<>();
        }

        /* loaded from: classes.dex */
        public class Entry {
            BluetoothDevice device;
            boolean isConnected;
            byte[] value;

            private Entry() {
                ClientCharacteristicManager.this = r1;
                this.device = null;
                this.value = null;
                this.isConnected = false;
            }
        }

        public void insertOrUpdate(BluetoothGattCharacteristic bluetoothGattCharacteristic, BluetoothDevice bluetoothDevice, byte[] bArr) {
            if (this.notificationStore.containsKey(bluetoothGattCharacteristic)) {
                List<Entry> list = this.notificationStore.get(bluetoothGattCharacteristic);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).device.equals(bluetoothDevice)) {
                        Entry entry = list.get(i);
                        entry.value = bArr;
                        list.set(i, entry);
                        return;
                    }
                }
                Entry entry2 = new Entry();
                entry2.device = bluetoothDevice;
                entry2.value = bArr;
                entry2.isConnected = true;
                list.add(entry2);
                return;
            }
            Entry entry3 = new Entry();
            entry3.device = bluetoothDevice;
            entry3.value = bArr;
            entry3.isConnected = true;
            LinkedList linkedList = new LinkedList();
            linkedList.add(entry3);
            this.notificationStore.put(bluetoothGattCharacteristic, linkedList);
        }

        public void markDeviceConnectivity(BluetoothDevice bluetoothDevice, boolean z) {
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : this.notificationStore.keySet()) {
                List<Entry> list = this.notificationStore.get(bluetoothGattCharacteristic);
                if (list != null) {
                    ListIterator<Entry> listIterator = list.listIterator();
                    while (listIterator.hasNext()) {
                        Entry next = listIterator.next();
                        if (next.device.equals(bluetoothDevice)) {
                            next.isConnected = z;
                        }
                    }
                }
            }
        }

        List<BluetoothDevice> getToBeUpdatedDevices(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            ArrayList arrayList = new ArrayList();
            if (!this.notificationStore.containsKey(bluetoothGattCharacteristic)) {
                return arrayList;
            }
            ListIterator<Entry> listIterator = this.notificationStore.get(bluetoothGattCharacteristic).listIterator();
            while (listIterator.hasNext()) {
                arrayList.add(listIterator.next().device);
            }
            return arrayList;
        }

        byte[] valueFor(BluetoothGattCharacteristic bluetoothGattCharacteristic, BluetoothDevice bluetoothDevice) {
            if (this.notificationStore.containsKey(bluetoothGattCharacteristic)) {
                List<Entry> list = this.notificationStore.get(bluetoothGattCharacteristic);
                for (int i = 0; i < list.size(); i++) {
                    Entry entry = list.get(i);
                    if (entry.device.equals(bluetoothDevice) && entry.isConnected) {
                        return list.get(i).value;
                    }
                }
                return null;
            }
            return null;
        }
    }

    public QtBluetoothLEServer(Context context) {
        Context context2;
        this.qtContext = null;
        this.mLeAdvertiser = null;
        this.qtContext = context;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null || (context2 = this.qtContext) == null) {
            Log.w(TAG, "Missing Bluetooth adapter or Qt context. Peripheral role disabled.");
        } else if (((BluetoothManager) context2.getSystemService("bluetooth")) == null) {
            Log.w(TAG, "Bluetooth service not available.");
        } else {
            this.mLeAdvertiser = this.mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (!this.mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                Log.w(TAG, "Device does not support Bluetooth Low Energy advertisement.");
            } else {
                Log.w(TAG, "Let's do BTLE Peripheral.");
            }
        }
    }

    public boolean connectServer() {
        if (this.mGattServer != null) {
            return true;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) this.qtContext.getSystemService("bluetooth");
        if (bluetoothManager == null) {
            Log.w(TAG, "Bluetooth service not available.");
            return false;
        }
        BluetoothGattServer openGattServer = bluetoothManager.openGattServer(this.qtContext, this.mGattServerListener);
        this.mGattServer = openGattServer;
        return openGattServer != null;
    }

    public void disconnectServer() {
        BluetoothGattServer bluetoothGattServer = this.mGattServer;
        if (bluetoothGattServer == null) {
            return;
        }
        bluetoothGattServer.close();
        this.mGattServer = null;
        this.mRemoteAddress = BuildConfig.FLAVOR;
        this.mRemoteName = BuildConfig.FLAVOR;
        leServerConnectionStateChange(this.qtObject, 0, 0);
    }

    public boolean startAdvertising(AdvertiseData advertiseData, AdvertiseData advertiseData2, AdvertiseSettings advertiseSettings) {
        if (this.mLeAdvertiser == null) {
            return false;
        }
        if (!connectServer()) {
            Log.w(TAG, "Server::startAdvertising: Cannot open GATT server");
            return false;
        }
        Log.w(TAG, "Starting to advertise.");
        this.mLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseData2, this.mAdvertiseListener);
        return true;
    }

    public void stopAdvertising() {
        BluetoothLeAdvertiser bluetoothLeAdvertiser = this.mLeAdvertiser;
        if (bluetoothLeAdvertiser == null) {
            return;
        }
        bluetoothLeAdvertiser.stopAdvertising(this.mAdvertiseListener);
        Log.w(TAG, "Advertisement stopped.");
    }

    public void addService(BluetoothGattService bluetoothGattService) {
        if (!connectServer()) {
            Log.w(TAG, "Server::addService: Cannot open GATT server");
            return;
        }
        boolean addService = this.mGattServer.addService(bluetoothGattService);
        Log.w(TAG, "Services successfully added: " + addService);
    }

    public void sendNotificationsOrIndications(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        ListIterator<BluetoothDevice> listIterator = this.clientCharacteristicManager.getToBeUpdatedDevices(bluetoothGattCharacteristic).listIterator();
        while (listIterator.hasNext()) {
            BluetoothDevice next = listIterator.next();
            byte[] valueFor = this.clientCharacteristicManager.valueFor(bluetoothGattCharacteristic, next);
            if (valueFor != null) {
                if (Arrays.equals(valueFor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                    this.mGattServer.notifyCharacteristicChanged(next, bluetoothGattCharacteristic, false);
                } else if (Arrays.equals(valueFor, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                    this.mGattServer.notifyCharacteristicChanged(next, bluetoothGattCharacteristic, true);
                }
            }
        }
    }

    public boolean writeCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid, byte[] bArr) {
        Iterator<BluetoothGattCharacteristic> it = bluetoothGattService.getCharacteristics().iterator();
        BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BluetoothGattCharacteristic next = it.next();
            if (next.getUuid().equals(uuid) && bluetoothGattCharacteristic == null) {
                bluetoothGattCharacteristic = next;
            } else if (next.getUuid().equals(uuid)) {
                Log.w(TAG, "Found second char with same UUID. Wrong char may have been selected.");
                break;
            }
        }
        if (bluetoothGattCharacteristic == null) {
            Log.w(TAG, "writeCharacteristic: update for unknown characteristic failed");
            return false;
        }
        bluetoothGattCharacteristic.setValue(bArr);
        sendNotificationsOrIndications(bluetoothGattCharacteristic);
        return true;
    }

    public boolean writeDescriptor(BluetoothGattService bluetoothGattService, UUID uuid, UUID uuid2, byte[] bArr) {
        Iterator<BluetoothGattCharacteristic> it = bluetoothGattService.getCharacteristics().iterator();
        BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BluetoothGattCharacteristic next = it.next();
            if (next.getUuid().equals(uuid)) {
                if (bluetoothGattCharacteristic != null) {
                    Log.w(TAG, "Found second char with same UUID. Wrong char may have been selected.");
                    break;
                }
                bluetoothGattCharacteristic = next;
            }
        }
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic != null ? bluetoothGattCharacteristic.getDescriptor(uuid2) : null;
        if (bluetoothGattCharacteristic == null || descriptor == null) {
            Log.w(TAG, "writeDescriptor: update for unknown char or desc failed (" + bluetoothGattCharacteristic + ")");
            return false;
        }
        descriptor.setValue(bArr);
        return true;
    }
}
