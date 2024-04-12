package com.ftdi.j2xx;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbId;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class D2xxManager {
    protected static final String ACTION_USB_PERMISSION = "com.ftdi.j2xx";
    public static final int FTDI_BREAK_OFF = 0;
    public static final int FTDI_BREAK_ON = 16384;
    public static final byte FT_BI = 16;
    public static final byte FT_BITMODE_ASYNC_BITBANG = 1;
    public static final byte FT_BITMODE_CBUS_BITBANG = 32;
    public static final byte FT_BITMODE_FAST_SERIAL = 16;
    public static final byte FT_BITMODE_MCU_HOST = 8;
    public static final byte FT_BITMODE_MPSSE = 2;
    public static final byte FT_BITMODE_RESET = 0;
    public static final byte FT_BITMODE_SYNC_BITBANG = 4;
    public static final byte FT_BITMODE_SYNC_FIFO = 64;
    public static final byte FT_CTS = 16;
    public static final byte FT_DATA_BITS_7 = 7;
    public static final byte FT_DATA_BITS_8 = 8;
    public static final byte FT_DCD = Byte.MIN_VALUE;
    public static final int FT_DEVICE_2232 = 4;
    public static final int FT_DEVICE_2232H = 6;
    public static final int FT_DEVICE_232B = 0;
    public static final int FT_DEVICE_232H = 8;
    public static final int FT_DEVICE_232R = 5;
    public static final int FT_DEVICE_245R = 5;
    public static final int FT_DEVICE_4222_0 = 10;
    public static final int FT_DEVICE_4222_1_2 = 11;
    public static final int FT_DEVICE_4222_3 = 12;
    public static final int FT_DEVICE_4232H = 7;
    public static final int FT_DEVICE_8U232AM = 1;
    public static final int FT_DEVICE_UNKNOWN = 3;
    public static final int FT_DEVICE_X_SERIES = 9;
    public static final byte FT_DSR = 32;
    public static final byte FT_EVENT_LINE_STATUS = 4;
    public static final byte FT_EVENT_MODEM_STATUS = 2;
    public static final byte FT_EVENT_REMOVED = 8;
    public static final byte FT_EVENT_RXCHAR = 1;
    public static final byte FT_FE = 8;
    public static final byte FT_FLAGS_HI_SPEED = 2;
    public static final byte FT_FLAGS_OPENED = 1;
    public static final short FT_FLOW_DTR_DSR = 512;
    public static final short FT_FLOW_NONE = 0;
    public static final short FT_FLOW_RTS_CTS = 256;
    public static final short FT_FLOW_XON_XOFF = 1024;
    public static final byte FT_OE = 2;
    public static final byte FT_PARITY_EVEN = 2;
    public static final byte FT_PARITY_MARK = 3;
    public static final byte FT_PARITY_NONE = 0;
    public static final byte FT_PARITY_ODD = 1;
    public static final byte FT_PARITY_SPACE = 4;
    public static final byte FT_PE = 4;
    public static final byte FT_PURGE_RX = 1;
    public static final byte FT_PURGE_TX = 2;
    public static final byte FT_RI = 64;
    public static final byte FT_STOP_BITS_1 = 0;
    public static final byte FT_STOP_BITS_2 = 2;
    private static final String TAG = "D2xx::";
    private static UsbManager mUsbManager;
    private ArrayList<FT_Device> mFtdiDevices;
    private BroadcastReceiver mUsbPlugEvents = new BroadcastReceiver() { // from class: com.ftdi.j2xx.D2xxManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                UsbDevice dev = (UsbDevice) intent.getParcelableExtra("device");
                FT_Device ftDev = D2xxManager.this.findDevice(dev);
                FT_Device ftDev2 = ftDev;
                while (ftDev2 != null) {
                    ftDev2.close();
                    synchronized (D2xxManager.this.mFtdiDevices) {
                        D2xxManager.this.mFtdiDevices.remove(ftDev2);
                    }
                    ftDev2 = D2xxManager.this.findDevice(dev);
                }
            } else if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                D2xxManager.this.addUsbDevice((UsbDevice) intent.getParcelableExtra("device"));
            }
        }
    };
    private static D2xxManager mInstance = null;
    private static Context mContext = null;
    private static PendingIntent mPendingIntent = null;
    private static IntentFilter mPermissionFilter = null;
    private static List<FtVidPid> mSupportedDevices = new ArrayList(Arrays.asList(new FtVidPid(UsbId.VENDOR_FTDI, UsbId.FTDI_FT231X), new FtVidPid(UsbId.VENDOR_FTDI, 24596), new FtVidPid(UsbId.VENDOR_FTDI, 24593), new FtVidPid(UsbId.VENDOR_FTDI, 24592), new FtVidPid(UsbId.VENDOR_FTDI, UsbId.FTDI_FT232R), new FtVidPid(UsbId.VENDOR_FTDI, 24582), new FtVidPid(UsbId.VENDOR_FTDI, 24604), new FtVidPid(UsbId.VENDOR_FTDI, 64193), new FtVidPid(UsbId.VENDOR_FTDI, 64194), new FtVidPid(UsbId.VENDOR_FTDI, 64195), new FtVidPid(UsbId.VENDOR_FTDI, 64196), new FtVidPid(UsbId.VENDOR_FTDI, 64197), new FtVidPid(UsbId.VENDOR_FTDI, 64198), new FtVidPid(UsbId.VENDOR_FTDI, 24594), new FtVidPid(2220, 4133), new FtVidPid(5590, 1), new FtVidPid(UsbId.VENDOR_FTDI, 24599)));
    private static BroadcastReceiver mUsbDevicePermissions = new BroadcastReceiver() { // from class: com.ftdi.j2xx.D2xxManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (D2xxManager.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false)) {
                        Log.d(D2xxManager.TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    /* loaded from: classes.dex */
    public static class FtDeviceInfoListNode {
        public short bcdDevice;
        public int breakOnParam;
        public String description;
        public int flags;
        public int handle;
        public byte iSerialNumber;
        public int id;
        public short lineStatus;
        public int location;
        public short modemStatus;
        public String serialNumber;
        public int type;
    }

    /* loaded from: classes.dex */
    public static class DriverParameters {
        private int mBufferSize = 16384;
        private int mMaxTransferSize = 16384;
        private int mNrBuffers = 16;
        private int mRxTimeout = 5000;

        public boolean setMaxBufferSize(int size) {
            if (size >= 64 && size <= 262144) {
                this.mBufferSize = size;
                return true;
            }
            Log.e(D2xxManager.TAG, "***bufferSize Out of correct range***");
            return false;
        }

        public int getMaxBufferSize() {
            return this.mBufferSize;
        }

        public boolean setMaxTransferSize(int size) {
            if (size >= 64 && size <= 262144) {
                this.mMaxTransferSize = size;
                return true;
            }
            Log.e(D2xxManager.TAG, "***maxTransferSize Out of correct range***");
            return false;
        }

        public int getMaxTransferSize() {
            return this.mMaxTransferSize;
        }

        public boolean setBufferNumber(int number) {
            if (number >= 2 && number <= 16) {
                this.mNrBuffers = number;
                return true;
            }
            Log.e(D2xxManager.TAG, "***nrBuffers Out of correct range***");
            return false;
        }

        public int getBufferNumber() {
            return this.mNrBuffers;
        }

        public boolean setReadTimeout(int timeout) {
            this.mRxTimeout = timeout;
            return true;
        }

        public int getReadTimeout() {
            return this.mRxTimeout;
        }
    }

    /* loaded from: classes.dex */
    public static class D2xxException extends IOException {
        private static final long serialVersionUID = 1;

        public D2xxException() {
        }

        public D2xxException(String ftStatusMsg) {
            super(ftStatusMsg);
        }
    }

    public FT_Device findDevice(UsbDevice usbDev) {
        FT_Device rtDev = null;
        synchronized (this.mFtdiDevices) {
            int nr_dev = this.mFtdiDevices.size();
            int i = 0;
            while (true) {
                if (i >= nr_dev) {
                    break;
                }
                FT_Device ftDevice = this.mFtdiDevices.get(i);
                UsbDevice dev = ftDevice.getUsbDevice();
                if (!dev.equals(usbDev)) {
                    i++;
                } else {
                    rtDev = ftDevice;
                    break;
                }
            }
        }
        return rtDev;
    }

    public boolean isFtDevice(UsbDevice dev) {
        if (mContext == null) {
            return false;
        }
        FtVidPid vidPid = new FtVidPid(dev.getVendorId(), dev.getProductId());
        boolean rc = mSupportedDevices.contains(vidPid);
        Log.v(TAG, vidPid.toString());
        return rc;
    }

    private static synchronized boolean updateContext(Context context) {
        synchronized (D2xxManager.class) {
            if (context == null) {
                return false;
            }
            if (mContext != context) {
                mContext = context;
                mPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 134217728);
                mPermissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
                mContext.getApplicationContext().registerReceiver(mUsbDevicePermissions, mPermissionFilter);
            }
            return true;
        }
    }

    private boolean isPermitted(UsbDevice dev) {
        if (!mUsbManager.hasPermission(dev)) {
            mUsbManager.requestPermission(dev, mPendingIntent);
        }
        if (!mUsbManager.hasPermission(dev)) {
            return false;
        }
        return true;
    }

    private D2xxManager(Context parentContext) throws D2xxException {
        Log.v(TAG, "Start constructor");
        if (parentContext == null) {
            throw new D2xxException("D2xx init failed: Can not find parentContext!");
        }
        updateContext(parentContext);
        if (!findUsbManger()) {
            throw new D2xxException("D2xx init failed: Can not find UsbManager!");
        }
        this.mFtdiDevices = new ArrayList<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        parentContext.getApplicationContext().registerReceiver(this.mUsbPlugEvents, filter);
        Log.v(TAG, "End constructor");
    }

    public static synchronized D2xxManager getInstance(Context parentContext) throws D2xxException {
        D2xxManager d2xxManager;
        synchronized (D2xxManager.class) {
            if (mInstance == null) {
                mInstance = new D2xxManager(parentContext);
            }
            if (parentContext != null) {
                updateContext(parentContext);
            }
            d2xxManager = mInstance;
        }
        return d2xxManager;
    }

    private static boolean findUsbManger() {
        Context context;
        if (mUsbManager == null && (context = mContext) != null) {
            mUsbManager = (UsbManager) context.getApplicationContext().getSystemService("usb");
        }
        return mUsbManager != null;
    }

    public boolean setVIDPID(int vendorId, int productId) {
        if (vendorId == 0 || productId == 0) {
            Log.d(TAG, "Invalid parameter to setVIDPID");
            return false;
        }
        FtVidPid vidpid = new FtVidPid(vendorId, productId);
        if (mSupportedDevices.contains(vidpid)) {
            Log.i(TAG, "Existing vid:" + vendorId + "  pid:" + productId);
            return true;
        } else if (!mSupportedDevices.add(vidpid)) {
            Log.d(TAG, "Failed to add VID/PID combination to list.");
            return false;
        } else {
            return true;
        }
    }

    public int[][] getVIDPID() {
        int listSize = mSupportedDevices.size();
        int[][] arrayVIDPID = (int[][]) Array.newInstance(int.class, 2, listSize);
        for (int i = 0; i < listSize; i++) {
            FtVidPid vidpid = mSupportedDevices.get(i);
            arrayVIDPID[0][i] = vidpid.getVid();
            arrayVIDPID[1][i] = vidpid.getPid();
        }
        return arrayVIDPID;
    }

    private void clearDevices() {
        synchronized (this.mFtdiDevices) {
            int nr_dev = this.mFtdiDevices.size();
            for (int i = 0; i < nr_dev; i++) {
                this.mFtdiDevices.remove(0);
            }
        }
    }

    public int createDeviceInfoList(Context parentContext) {
        int rc;
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        ArrayList<FT_Device> devices = new ArrayList<>();
        if (parentContext == null) {
            return 0;
        }
        updateContext(parentContext);
        for (UsbDevice usbDevice : deviceList.values()) {
            if (isFtDevice(usbDevice)) {
                int numInterfaces = usbDevice.getInterfaceCount();
                for (int i = 0; i < numInterfaces; i++) {
                    if (isPermitted(usbDevice)) {
                        synchronized (this.mFtdiDevices) {
                            FT_Device ftDev = findDevice(usbDevice);
                            if (ftDev == null) {
                                ftDev = new FT_Device(parentContext, mUsbManager, usbDevice, usbDevice.getInterface(i));
                            } else {
                                this.mFtdiDevices.remove(ftDev);
                                ftDev.setContext(parentContext);
                            }
                            devices.add(ftDev);
                        }
                    }
                }
                continue;
            }
        }
        synchronized (this.mFtdiDevices) {
            clearDevices();
            this.mFtdiDevices = devices;
            rc = devices.size();
        }
        return rc;
    }

    public synchronized int getDeviceInfoList(int numDevs, FtDeviceInfoListNode[] deviceList) {
        for (int i = 0; i < numDevs; i++) {
            deviceList[i] = this.mFtdiDevices.get(i).mDeviceInfoNode;
        }
        return this.mFtdiDevices.size();
    }

    public synchronized FtDeviceInfoListNode getDeviceInfoListDetail(int index) {
        if (index <= this.mFtdiDevices.size() && index >= 0) {
            return this.mFtdiDevices.get(index).mDeviceInfoNode;
        }
        return null;
    }

    public static int getLibraryVersion() {
        return 541065216;
    }

    private boolean tryOpen(Context parentContext, FT_Device ftDev, DriverParameters params) {
        if (ftDev == null || parentContext == null) {
            return false;
        }
        ftDev.setContext(parentContext);
        if (params != null) {
            ftDev.setDriverParameters(params);
        }
        if (!ftDev.openDevice(mUsbManager) || !ftDev.isOpen()) {
            return false;
        }
        return true;
    }

    public synchronized FT_Device openByUsbDevice(Context parentContext, UsbDevice dev, DriverParameters params) {
        FT_Device ftDev;
        ftDev = null;
        if (isFtDevice(dev)) {
            ftDev = findDevice(dev);
            if (!tryOpen(parentContext, ftDev, params)) {
                ftDev = null;
            }
        }
        return ftDev;
    }

    public synchronized FT_Device openByUsbDevice(Context parentContext, UsbDevice dev) {
        return openByUsbDevice(parentContext, dev, null);
    }

    public synchronized FT_Device openByIndex(Context parentContext, int index, DriverParameters params) {
        if (index < 0) {
            return null;
        }
        if (parentContext == null) {
            return null;
        }
        updateContext(parentContext);
        FT_Device ftDev = this.mFtdiDevices.get(index);
        if (!tryOpen(parentContext, ftDev, params)) {
            ftDev = null;
        }
        return ftDev;
    }

    public synchronized FT_Device openByIndex(Context parentContext, int index) {
        return openByIndex(parentContext, index, null);
    }

    public synchronized FT_Device openBySerialNumber(Context parentContext, String serialNumber, DriverParameters params) {
        FT_Device ftDev = null;
        if (parentContext == null) {
            return null;
        }
        updateContext(parentContext);
        int i = 0;
        while (true) {
            if (i >= this.mFtdiDevices.size()) {
                break;
            }
            FT_Device tmpDev = this.mFtdiDevices.get(i);
            if (tmpDev != null) {
                FtDeviceInfoListNode devInfo = tmpDev.mDeviceInfoNode;
                if (devInfo == null) {
                    Log.d(TAG, "***devInfo cannot be null***");
                } else if (devInfo.serialNumber.equals(serialNumber)) {
                    ftDev = tmpDev;
                    break;
                }
            }
            i++;
        }
        if (!tryOpen(parentContext, ftDev, params)) {
            ftDev = null;
        }
        return ftDev;
    }

    public synchronized FT_Device openBySerialNumber(Context parentContext, String serialNumber) {
        return openBySerialNumber(parentContext, serialNumber, null);
    }

    public synchronized FT_Device openByDescription(Context parentContext, String description, DriverParameters params) {
        FT_Device ftDev = null;
        if (parentContext == null) {
            return null;
        }
        updateContext(parentContext);
        int i = 0;
        while (true) {
            if (i >= this.mFtdiDevices.size()) {
                break;
            }
            FT_Device tmpDev = this.mFtdiDevices.get(i);
            if (tmpDev != null) {
                FtDeviceInfoListNode devInfo = tmpDev.mDeviceInfoNode;
                if (devInfo == null) {
                    Log.d(TAG, "***devInfo cannot be null***");
                } else if (devInfo.description.equals(description)) {
                    ftDev = tmpDev;
                    break;
                }
            }
            i++;
        }
        if (!tryOpen(parentContext, ftDev, params)) {
            ftDev = null;
        }
        return ftDev;
    }

    public synchronized FT_Device openByDescription(Context parentContext, String description) {
        return openByDescription(parentContext, description, null);
    }

    public synchronized FT_Device openByLocation(Context parentContext, int location, DriverParameters params) {
        FT_Device ftDev = null;
        if (parentContext == null) {
            return null;
        }
        updateContext(parentContext);
        int i = 0;
        while (true) {
            if (i >= this.mFtdiDevices.size()) {
                break;
            }
            FT_Device tmpDev = this.mFtdiDevices.get(i);
            if (tmpDev != null) {
                FtDeviceInfoListNode devInfo = tmpDev.mDeviceInfoNode;
                if (devInfo == null) {
                    Log.d(TAG, "***devInfo cannot be null***");
                } else if (devInfo.location == location) {
                    ftDev = tmpDev;
                    break;
                }
            }
            i++;
        }
        if (!tryOpen(parentContext, ftDev, params)) {
            ftDev = null;
        }
        return ftDev;
    }

    public synchronized FT_Device openByLocation(Context parentContext, int location) {
        return openByLocation(parentContext, location, null);
    }

    public int addUsbDevice(UsbDevice dev) {
        int rc = 0;
        if (isFtDevice(dev)) {
            int numInterfaces = dev.getInterfaceCount();
            for (int i = 0; i < numInterfaces; i++) {
                if (isPermitted(dev)) {
                    synchronized (this.mFtdiDevices) {
                        FT_Device ftDev = findDevice(dev);
                        if (ftDev == null) {
                            ftDev = new FT_Device(mContext, mUsbManager, dev, dev.getInterface(i));
                        } else {
                            ftDev.setContext(mContext);
                            this.mFtdiDevices.remove(ftDev);
                        }
                        this.mFtdiDevices.add(ftDev);
                        rc++;
                    }
                }
            }
        }
        return rc;
    }
}
