package org.pdrl.aerogcs;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.Cp2102SerialDriver;
import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.ProlificSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pdrl.aerogcs.UsbIoManager;
import org.qtproject.qt5.android.bindings.QtActivity;

/* loaded from: classes2.dex */
public class AeroGCSActivity extends QtActivity {
    private static final String ACTION_USB_PERMISSION = "org.mavlink.qgroundcontrol.action.USB_PERMISSION";
    private static final String TAG = "T_AeroGCSActivity";
    private static List<UsbSerialDriver> _drivers;
    private static HashMap<Integer, Long> _userDataHashByDeviceId;
    private static PowerManager.WakeLock _wakeLock;
    public static Context m_context;
    private static HashMap<Integer, UsbIoManager> m_ioManager;
    public static int BAD_DEVICE_ID = 0;
    private static AeroGCSActivity _instance = null;
    private static UsbManager _usbManager = null;
    private static PendingIntent _usbPermissionIntent = null;
    private static final ExecutorService m_Executor = Executors.newSingleThreadExecutor();
    private static final UsbIoManager.Listener m_Listener = new UsbIoManager.Listener() { // from class: org.pdrl.aerogcs.AeroGCSActivity.1
        @Override // org.pdrl.aerogcs.UsbIoManager.Listener
        public void onRunError(Exception eA, long userData) {
            Log.e(AeroGCSActivity.TAG, "onRunError Exception");
            AeroGCSActivity.nativeDeviceException(userData, eA.getMessage());
        }

        @Override // org.pdrl.aerogcs.UsbIoManager.Listener
        public void onNewData(byte[] dataA, long userData) {
            AeroGCSActivity.nativeDeviceNewData(userData, dataA);
        }
    };
    private static final BroadcastReceiver _usbReceiver = new BroadcastReceiver() { // from class: org.pdrl.aerogcs.AeroGCSActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UsbDevice device;
            String action = intent.getAction();
            Log.i(AeroGCSActivity.TAG, "BroadcastReceiver USB action " + action);
            if (AeroGCSActivity.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (AeroGCSActivity._instance) {
                    UsbDevice device2 = (UsbDevice) intent.getParcelableExtra("device");
                    if (device2 != null) {
                        UsbSerialDriver driver = AeroGCSActivity._findDriverByDeviceId(device2.getDeviceId());
                        if (intent.getBooleanExtra("permission", false)) {
                            AeroGCSActivity.aerogcsLogDebug("Permission granted to " + device2.getDeviceName());
                            driver.setPermissionStatus(0);
                        } else {
                            AeroGCSActivity.aerogcsLogDebug("Permission denied for " + device2.getDeviceName());
                            driver.setPermissionStatus(1);
                        }
                    }
                }
            } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action) && (device = (UsbDevice) intent.getParcelableExtra("device")) != null && AeroGCSActivity._userDataHashByDeviceId.containsKey(Integer.valueOf(device.getDeviceId()))) {
                AeroGCSActivity.nativeDeviceHasDisconnected(((Long) AeroGCSActivity._userDataHashByDeviceId.get(Integer.valueOf(device.getDeviceId()))).longValue());
            }
        }
    };
    private TSync taiSync = null;
    private Timer probeAccessoriesTimer = null;
    private final BroadcastReceiver mOpenAccessoryReceiver = new BroadcastReceiver() { // from class: org.pdrl.aerogcs.AeroGCSActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UsbAccessory accessory;
            String action = intent.getAction();
            if (AeroGCSActivity.ACTION_USB_PERMISSION.equals(action)) {
                UsbAccessory accessory2 = (UsbAccessory) intent.getParcelableExtra("accessory");
                if (accessory2 != null && intent.getBooleanExtra("permission", false)) {
                    AeroGCSActivity.this.openAccessory(accessory2);
                }
            } else if ("android.hardware.usb.action.USB_ACCESSORY_DETACHED".equals(action) && (accessory = (UsbAccessory) intent.getParcelableExtra("accessory")) != null) {
                AeroGCSActivity.this.closeAccessory(accessory);
            }
        }
    };
    UsbAccessory openUsbAccessory = null;
    Object openAccessoryLock = new Object();
    Object probeAccessoriesLock = new Object();

    public static native void nativeDeviceException(long j, String str);

    public static native void nativeDeviceHasDisconnected(long j);

    public static native void nativeDeviceNewData(long j, byte[] bArr);

    private static native void nativeUpdateAvailableJoysticks();

    public native void nativeInit();

    public static UsbSerialDriver _findDriverByDeviceId(int deviceId) {
        for (UsbSerialDriver driver : _drivers) {
            if (driver.getDevice().getDeviceId() == deviceId) {
                return driver;
            }
        }
        return null;
    }

    private static UsbSerialDriver _findDriverByDeviceName(String deviceName) {
        for (UsbSerialDriver driver : _drivers) {
            if (driver.getDevice().getDeviceName().equals(deviceName)) {
                return driver;
            }
        }
        return null;
    }

    public static void aerogcsLogDebug(String message) {
    }

    public static void aerogcsLogWarning(String message) {
    }

    public AeroGCSActivity() {
        _instance = this;
        _drivers = new ArrayList();
        _userDataHashByDeviceId = new HashMap<>();
        m_ioManager = new HashMap<>();
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) _instance.getSystemService("power");
        PowerManager.WakeLock newWakeLock = pm.newWakeLock(10, "QGroundControl");
        _wakeLock = newWakeLock;
        if (newWakeLock == null) {
            Log.i(TAG, "SCREEN_BRIGHT_WAKE_LOCK not acquired!!!");
        } else {
            newWakeLock.acquire();
        }
        _instance.getWindow().addFlags(128);
        _usbManager = (UsbManager) _instance.getSystemService("usb");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        _instance.registerReceiver(_usbReceiver, filter);
        _usbPermissionIntent = PendingIntent.getBroadcast(_instance, 0, new Intent(ACTION_USB_PERMISSION), 0);
        try {
            this.taiSync = new TSync();
            IntentFilter accessoryFilter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
            registerReceiver(this.mOpenAccessoryReceiver, accessoryFilter);
            Timer timer = new Timer();
            this.probeAccessoriesTimer = timer;
            timer.schedule(new TimerTask() { // from class: org.pdrl.aerogcs.AeroGCSActivity.4
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    AeroGCSActivity.this.probeAccessories();
                }
            }, 0L, 3000L);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        probeAccessories();
    }

    @Override // org.qtproject.qt5.android.bindings.QtActivity, android.app.Activity
    public void onDestroy() {
        Timer timer = this.probeAccessoriesTimer;
        if (timer != null) {
            timer.cancel();
        }
        unregisterReceiver(this.mOpenAccessoryReceiver);
        try {
            if (_wakeLock != null) {
                _wakeLock.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception onDestroy()");
        }
        super.onDestroy();
    }

    public void onInit(int status) {
    }

    private static void updateCurrentDrivers() {
        List<UsbSerialDriver> currentDrivers = UsbSerialProber.findAllDevices(_usbManager);
        for (int i = _drivers.size() - 1; i >= 0; i--) {
            boolean found = false;
            Iterator<UsbSerialDriver> it = currentDrivers.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                UsbSerialDriver currentDriver = it.next();
                if (_drivers.get(i).getDevice().getDeviceId() == currentDriver.getDevice().getDeviceId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                aerogcsLogDebug("Remove stale driver " + _drivers.get(i).getDevice().getDeviceName());
                _drivers.remove(i);
            }
        }
        for (int i2 = 0; i2 < currentDrivers.size(); i2++) {
            boolean found2 = false;
            int j = 0;
            while (true) {
                if (j < _drivers.size()) {
                    if (currentDrivers.get(i2).getDevice().getDeviceId() != _drivers.get(j).getDevice().getDeviceId()) {
                        j++;
                    } else {
                        found2 = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!found2) {
                UsbSerialDriver newDriver = currentDrivers.get(i2);
                UsbDevice device = newDriver.getDevice();
                String deviceName = device.getDeviceName();
                _drivers.add(newDriver);
                aerogcsLogDebug("Adding new driver " + deviceName);
                if (_usbManager.hasPermission(device)) {
                    aerogcsLogDebug("Already have permission to use device " + deviceName);
                    newDriver.setPermissionStatus(0);
                } else {
                    aerogcsLogDebug("Requesting permission to use device " + deviceName);
                    newDriver.setPermissionStatus(2);
                    _usbManager.requestPermission(device, _usbPermissionIntent);
                }
            }
        }
    }

    public static String[] availableDevicesInfo() {
        String deviceInfo;
        updateCurrentDrivers();
        if (_drivers.size() <= 0) {
            return null;
        }
        List<String> deviceInfoList = new ArrayList<>();
        for (int i = 0; i < _drivers.size(); i++) {
            UsbSerialDriver driver = _drivers.get(i);
            if (driver.permissionStatus() == 0) {
                UsbDevice device = driver.getDevice();
                String deviceInfo2 = device.getDeviceName() + ":";
                if (driver instanceof FtdiSerialDriver) {
                    deviceInfo = deviceInfo2 + "FTDI:";
                } else if (driver instanceof CdcAcmSerialDriver) {
                    deviceInfo = deviceInfo2 + "Cdc Acm:";
                } else if (driver instanceof Cp2102SerialDriver) {
                    deviceInfo = deviceInfo2 + "Cp2102:";
                } else if (driver instanceof ProlificSerialDriver) {
                    deviceInfo = deviceInfo2 + "Prolific:";
                } else {
                    deviceInfo = deviceInfo2 + "Unknown:";
                }
                deviceInfoList.add((deviceInfo + Integer.toString(device.getProductId()) + ":") + Integer.toString(device.getVendorId()) + ":");
            }
        }
        int i2 = deviceInfoList.size();
        String[] rgDeviceInfo = new String[i2];
        for (int i3 = 0; i3 < deviceInfoList.size(); i3++) {
            rgDeviceInfo[i3] = deviceInfoList.get(i3);
        }
        return rgDeviceInfo;
    }

    public static int open(Context parentContext, String deviceName, long userData) {
        int i = BAD_DEVICE_ID;
        m_context = parentContext;
        UsbSerialDriver driver = _findDriverByDeviceName(deviceName);
        if (driver == null) {
            aerogcsLogWarning("Attempt to open unknown device " + deviceName);
            return BAD_DEVICE_ID;
        } else if (driver.permissionStatus() != 0) {
            aerogcsLogWarning("Attempt to open device with incorrect permission status " + deviceName + " " + driver.permissionStatus());
            return BAD_DEVICE_ID;
        } else {
            UsbDevice device = driver.getDevice();
            int deviceId = device.getDeviceId();
            try {
                driver.setConnection(_usbManager.openDevice(device));
                driver.open();
                driver.setPermissionStatus(4);
                _userDataHashByDeviceId.put(Integer.valueOf(deviceId), Long.valueOf(userData));
                UsbIoManager ioManager = new UsbIoManager(driver, m_Listener, userData);
                m_ioManager.put(Integer.valueOf(deviceId), ioManager);
                m_Executor.submit(ioManager);
                aerogcsLogDebug("Port open successful");
                return deviceId;
            } catch (IOException exA) {
                driver.setPermissionStatus(3);
                _userDataHashByDeviceId.remove(Integer.valueOf(deviceId));
                if (m_ioManager.get(Integer.valueOf(deviceId)) != null) {
                    m_ioManager.get(Integer.valueOf(deviceId)).stop();
                    m_ioManager.remove(Integer.valueOf(deviceId));
                }
                aerogcsLogWarning("Port open exception: " + exA.getMessage());
                return BAD_DEVICE_ID;
            }
        }
    }

    public static void startIoManager(int idA) {
        UsbSerialDriver driverL;
        if (m_ioManager.get(Integer.valueOf(idA)) != null || (driverL = _findDriverByDeviceId(idA)) == null) {
            return;
        }
        UsbIoManager managerL = new UsbIoManager(driverL, m_Listener, _userDataHashByDeviceId.get(Integer.valueOf(idA)).longValue());
        m_ioManager.put(Integer.valueOf(idA), managerL);
        m_Executor.submit(managerL);
    }

    public static void stopIoManager(int idA) {
        if (m_ioManager.get(Integer.valueOf(idA)) == null) {
            return;
        }
        m_ioManager.get(Integer.valueOf(idA)).stop();
        m_ioManager.remove(Integer.valueOf(idA));
    }

    public static boolean setParameters(int idA, int baudRateA, int dataBitsA, int stopBitsA, int parityA) {
        UsbSerialDriver driverL = _findDriverByDeviceId(idA);
        if (driverL == null) {
            return false;
        }
        try {
            driverL.setParameters(baudRateA, dataBitsA, stopBitsA, parityA);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean close(int idA) {
        UsbSerialDriver driverL = _findDriverByDeviceId(idA);
        if (driverL == null) {
            return false;
        }
        try {
            stopIoManager(idA);
            _userDataHashByDeviceId.remove(Integer.valueOf(idA));
            driverL.setPermissionStatus(3);
            driverL.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static int write(int idA, byte[] sourceA, int timeoutMSecA) {
        UsbSerialDriver driverL = _findDriverByDeviceId(idA);
        if (driverL == null) {
            return 0;
        }
        try {
            return driverL.write(sourceA, timeoutMSecA);
        } catch (IOException e) {
            return 0;
        }
    }

    public static boolean isDeviceNameValid(String nameA) {
        for (UsbSerialDriver driver : _drivers) {
            if (driver.getDevice().getDeviceName() == nameA) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDeviceNameOpen(String nameA) {
        for (UsbSerialDriver driverL : _drivers) {
            if (nameA.equals(driverL.getDevice().getDeviceName()) && driverL.permissionStatus() == 4) {
                return true;
            }
        }
        return false;
    }

    public static boolean setDataTerminalReady(int idA, boolean onA) {
        try {
            UsbSerialDriver driverL = _findDriverByDeviceId(idA);
            if (driverL == null) {
                return false;
            }
            driverL.setDTR(onA);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean setRequestToSend(int idA, boolean onA) {
        try {
            UsbSerialDriver driverL = _findDriverByDeviceId(idA);
            if (driverL == null) {
                return false;
            }
            driverL.setRTS(onA);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean purgeBuffers(int idA, boolean inputA, boolean outputA) {
        try {
            UsbSerialDriver driverL = _findDriverByDeviceId(idA);
            if (driverL == null) {
                return false;
            }
            return driverL.purgeHwBuffers(inputA, outputA);
        } catch (IOException e) {
            return false;
        }
    }

    public static int getDeviceHandle(int idA) {
        UsbDeviceConnection connectL;
        UsbSerialDriver driverL = _findDriverByDeviceId(idA);
        if (driverL == null || (connectL = driverL.getDeviceConnection()) == null) {
            return -1;
        }
        return connectL.getFileDescriptor();
    }

    public void openAccessory(UsbAccessory usbAccessory) {
        Log.d(TAG, "openAccessory: " + usbAccessory.getSerial());
        try {
            synchronized (this.openAccessoryLock) {
                if ((this.openUsbAccessory != null && !this.taiSync.isRunning()) || this.openUsbAccessory == null) {
                    this.openUsbAccessory = usbAccessory;
                    Log.d(TAG, "TAISYNC OPEN!");
                    this.taiSync.open(_usbManager.openAccessory(usbAccessory));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "openAccessory exception: " + e);
            this.taiSync.close();
            closeAccessory(this.openUsbAccessory);
        }
    }

    public void closeAccessory(UsbAccessory usbAccessory) {
        Log.i(TAG, "closeAccessory");
        synchronized (this.openAccessoryLock) {
            if (this.openUsbAccessory != null && usbAccessory == this.openUsbAccessory && this.taiSync.isRunning()) {
                this.taiSync.close();
                this.openUsbAccessory = null;
            }
        }
    }

    public void probeAccessories() {
        PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        new Thread(new Runnable() { // from class: org.pdrl.aerogcs.AeroGCSActivity.5
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AeroGCSActivity.this.openAccessoryLock) {
                    UsbAccessory[] accessories = AeroGCSActivity._usbManager.getAccessoryList();
                    if (accessories != null) {
                        for (UsbAccessory usbAccessory : accessories) {
                            if (usbAccessory != null) {
                                if (AeroGCSActivity._usbManager.hasPermission(usbAccessory)) {
                                    Log.d(AeroGCSActivity.TAG, "gonna openAccessory");
                                    AeroGCSActivity.this.openAccessory(usbAccessory);
                                } else {
                                    Log.d(AeroGCSActivity.TAG, "no permission");
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
