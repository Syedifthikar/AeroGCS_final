package com.ftdi.j2xx;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class FT_Device {
    private static final String TAG = "FTDI_Device::";
    private BulkInWorker mBulkIn;
    UsbEndpoint mBulkInEndpoint;
    private Thread mBulkInThread;
    UsbEndpoint mBulkOutEndpoint;
    Context mContext;
    D2xxManager.FtDeviceInfoListNode mDeviceInfoNode;
    private D2xxManager.DriverParameters mDriverParams;
    private FT_EE_Ctrl mEEPROM;
    long mEventMask;
    TFtEventNotify mEventNotification;
    private int mInterfaceID;
    Boolean mIsOpen;
    private byte mLatencyTimer;
    private int mMaxPacketSize;
    private ProcessInCtrl mProcessInCtrl;
    private Thread mProcessRequestThread;
    TFtSpecialChars mTftSpecialChars;
    private UsbDeviceConnection mUsbConnection;
    UsbDevice mUsbDevice;
    UsbInterface mUsbInterface;
    private UsbRequest mUsbRequest;

    public FT_Device(Context parentContext, UsbManager usbManager, UsbDevice dev, UsbInterface itf) {
        int i;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode2;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode3;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode4;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode5;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode6;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode7;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode8;
        this.mInterfaceID = 0;
        byte[] buffer = new byte[FT_4222_Defines.CHIPTOP_DEBUG_REQUEST];
        this.mContext = parentContext;
        this.mDriverParams = new D2xxManager.DriverParameters();
        try {
            this.mUsbDevice = dev;
            try {
                this.mUsbInterface = itf;
                this.mBulkOutEndpoint = null;
                this.mBulkInEndpoint = null;
                this.mMaxPacketSize = 0;
                this.mTftSpecialChars = new TFtSpecialChars();
                this.mEventNotification = new TFtEventNotify();
                this.mDeviceInfoNode = new D2xxManager.FtDeviceInfoListNode();
                this.mUsbRequest = new UsbRequest();
                setConnection(usbManager.openDevice(this.mUsbDevice));
                if (getConnection() != null) {
                    getConnection().claimInterface(this.mUsbInterface, false);
                    byte[] rawDescriptors = getConnection().getRawDescriptors();
                    int devID = this.mUsbDevice.getDeviceId();
                    int id = this.mUsbInterface.getId() + 1;
                    this.mInterfaceID = id;
                    this.mDeviceInfoNode.location = (id & 15) | (devID << 4);
                    ByteBuffer bcdDevice = ByteBuffer.allocate(2);
                    bcdDevice.order(ByteOrder.LITTLE_ENDIAN);
                    bcdDevice.put(rawDescriptors[12]);
                    bcdDevice.put(rawDescriptors[13]);
                    this.mDeviceInfoNode.bcdDevice = bcdDevice.getShort(0);
                    this.mDeviceInfoNode.iSerialNumber = rawDescriptors[16];
                    this.mDeviceInfoNode.serialNumber = getConnection().getSerial();
                    this.mDeviceInfoNode.id = this.mUsbDevice.getProductId() | (this.mUsbDevice.getVendorId() << 16);
                    this.mDeviceInfoNode.breakOnParam = 8;
                    getConnection().controlTransfer(-128, 6, rawDescriptors[15] | 768, 0, buffer, FT_4222_Defines.CHIPTOP_DEBUG_REQUEST, 0);
                    this.mDeviceInfoNode.description = stringFromUtf16le(buffer);
                    switch (this.mDeviceInfoNode.bcdDevice & 65280) {
                        case 512:
                            i = 8;
                            if (this.mDeviceInfoNode.iSerialNumber == 0) {
                                this.mEEPROM = new FT_EE_232B_Ctrl(this);
                                this.mDeviceInfoNode.type = 0;
                                break;
                            } else {
                                this.mDeviceInfoNode.type = 1;
                                this.mEEPROM = new FT_EE_232A_Ctrl(this);
                                break;
                            }
                        case 1024:
                            i = 8;
                            this.mEEPROM = new FT_EE_232B_Ctrl(this);
                            this.mDeviceInfoNode.type = 0;
                            break;
                        case 1280:
                            i = 8;
                            this.mEEPROM = new FT_EE_2232_Ctrl(this);
                            this.mDeviceInfoNode.type = 4;
                            dualQuadChannelDevice();
                            break;
                        case 1536:
                            i = 8;
                            FT_EE_Ctrl fT_EE_Ctrl = new FT_EE_Ctrl(this);
                            this.mEEPROM = fT_EE_Ctrl;
                            short word00x00 = (short) (fT_EE_Ctrl.readWord((short) 0) & 1);
                            this.mEEPROM = null;
                            if (word00x00 == 0) {
                                this.mDeviceInfoNode.type = 5;
                                this.mEEPROM = new FT_EE_232R_Ctrl(this);
                                break;
                            } else {
                                this.mDeviceInfoNode.type = 5;
                                this.mEEPROM = new FT_EE_245R_Ctrl(this);
                                break;
                            }
                        case 1792:
                            i = 8;
                            this.mDeviceInfoNode.type = 6;
                            this.mDeviceInfoNode.flags = 2;
                            dualQuadChannelDevice();
                            this.mEEPROM = new FT_EE_2232H_Ctrl(this);
                            break;
                        case 2048:
                            i = 8;
                            this.mDeviceInfoNode.type = 7;
                            this.mDeviceInfoNode.flags = 2;
                            dualQuadChannelDevice();
                            this.mEEPROM = new FT_EE_4232H_Ctrl(this);
                            break;
                        case 2304:
                            i = 8;
                            this.mDeviceInfoNode.type = 8;
                            this.mDeviceInfoNode.flags = 2;
                            this.mEEPROM = new FT_EE_232H_Ctrl(this);
                            break;
                        case 4096:
                            i = 8;
                            this.mDeviceInfoNode.type = 9;
                            this.mEEPROM = new FT_EE_X_Ctrl(this);
                            break;
                        case 5888:
                            i = 8;
                            this.mDeviceInfoNode.type = 12;
                            this.mDeviceInfoNode.flags = 2;
                            break;
                        case 6144:
                            i = 8;
                            this.mDeviceInfoNode.type = 10;
                            if (this.mInterfaceID == 1) {
                                this.mDeviceInfoNode.flags = 2;
                                break;
                            } else {
                                this.mDeviceInfoNode.flags = 0;
                                break;
                            }
                        case 6400:
                            this.mDeviceInfoNode.type = 11;
                            if (this.mInterfaceID == 4) {
                                int iMaxPacketSize = this.mUsbDevice.getInterface(this.mInterfaceID - 1).getEndpoint(0).getMaxPacketSize();
                                Log.e("dev", "mInterfaceID : " + this.mInterfaceID + "   iMaxPacketSize : " + iMaxPacketSize);
                                i = 8;
                                if (iMaxPacketSize == 8) {
                                    this.mDeviceInfoNode.flags = 0;
                                    break;
                                } else {
                                    this.mDeviceInfoNode.flags = 2;
                                    break;
                                }
                            } else {
                                i = 8;
                                this.mDeviceInfoNode.flags = 2;
                                break;
                            }
                        default:
                            i = 8;
                            this.mDeviceInfoNode.type = 3;
                            this.mEEPROM = new FT_EE_Ctrl(this);
                            break;
                    }
                    int i2 = this.mDeviceInfoNode.bcdDevice & 65280;
                    if ((i2 == 5888 || i2 == 6144 || i2 == 6400) && this.mDeviceInfoNode.serialNumber == null) {
                        byte[] dataRead = new byte[16];
                        getConnection().controlTransfer(-64, 144, 0, 27, dataRead, 16, 0);
                        String tmpStr = BuildConfig.FLAVOR;
                        int m = 0;
                        while (m < i) {
                            tmpStr = String.valueOf(tmpStr) + ((char) dataRead[m * 2]);
                            m++;
                            i = 8;
                        }
                        this.mDeviceInfoNode.serialNumber = new String(tmpStr);
                    }
                    int i3 = this.mDeviceInfoNode.bcdDevice & 65280;
                    if (i3 == 6144 || i3 == 6400) {
                        if (this.mInterfaceID != 1) {
                            if (this.mInterfaceID != 2) {
                                if (this.mInterfaceID == 3) {
                                    this.mDeviceInfoNode.description = String.valueOf(ftDeviceInfoListNode3.description) + " C";
                                    this.mDeviceInfoNode.serialNumber = String.valueOf(ftDeviceInfoListNode4.serialNumber) + "C";
                                } else if (this.mInterfaceID == 4) {
                                    this.mDeviceInfoNode.description = String.valueOf(ftDeviceInfoListNode.description) + " D";
                                    this.mDeviceInfoNode.serialNumber = String.valueOf(ftDeviceInfoListNode2.serialNumber) + "D";
                                }
                            } else {
                                this.mDeviceInfoNode.description = String.valueOf(ftDeviceInfoListNode5.description) + " B";
                                this.mDeviceInfoNode.serialNumber = String.valueOf(ftDeviceInfoListNode6.serialNumber) + "B";
                            }
                        } else {
                            this.mDeviceInfoNode.description = String.valueOf(ftDeviceInfoListNode7.description) + " A";
                            this.mDeviceInfoNode.serialNumber = String.valueOf(ftDeviceInfoListNode8.serialNumber) + "A";
                        }
                    }
                    getConnection().releaseInterface(this.mUsbInterface);
                    getConnection().close();
                    setConnection(null);
                    setClosed();
                    return;
                }
                Log.e(TAG, "Failed to open the device!");
                throw new D2xxManager.D2xxException("Failed to open the device!");
            } catch (Exception e) {
                e = e;
                if (e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    private final boolean isHiSpeed() {
        return isFt232h() || isFt2232h() || isFt4232h();
    }

    private final boolean isBmDevice() {
        return isFt232b() || isFt2232() || isFt232r() || isFt2232h() || isFt4232h() || isFt232h() || isFt232ex();
    }

    final boolean isMultiIfDevice() {
        return isFt2232() || isFt2232h() || isFt4232h();
    }

    private final boolean isFt232ex() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 4096;
    }

    private final boolean isFt232h() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 2304;
    }

    final boolean isFt4232h() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 2048;
    }

    private final boolean isFt2232h() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 1792;
    }

    private final boolean isFt232r() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 1536;
    }

    private final boolean isFt2232() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 1280;
    }

    private final boolean isFt232b() {
        if ((this.mDeviceInfoNode.bcdDevice & 65280) != 1024) {
            return (this.mDeviceInfoNode.bcdDevice & 65280) == 512 && this.mDeviceInfoNode.iSerialNumber == 0;
        }
        return true;
    }

    private final boolean ifFt8u232am() {
        return (this.mDeviceInfoNode.bcdDevice & 65280) == 512 && this.mDeviceInfoNode.iSerialNumber != 0;
    }

    private final String stringFromUtf16le(byte[] data) throws UnsupportedEncodingException {
        return new String(data, 2, data[0] - 2, "UTF-16LE");
    }

    public UsbDeviceConnection getConnection() {
        return this.mUsbConnection;
    }

    void setConnection(UsbDeviceConnection mUsbConnection) {
        this.mUsbConnection = mUsbConnection;
    }

    public synchronized boolean setContext(Context parentContext) {
        boolean rc;
        rc = false;
        if (parentContext != null) {
            this.mContext = parentContext;
            rc = true;
        }
        return rc;
    }

    public void setDriverParameters(D2xxManager.DriverParameters params) {
        this.mDriverParams.setMaxBufferSize(params.getMaxBufferSize());
        this.mDriverParams.setMaxTransferSize(params.getMaxTransferSize());
        this.mDriverParams.setBufferNumber(params.getBufferNumber());
        this.mDriverParams.setReadTimeout(params.getReadTimeout());
    }

    public D2xxManager.DriverParameters getDriverParameters() {
        return this.mDriverParams;
    }

    public int getReadTimeout() {
        return this.mDriverParams.getReadTimeout();
    }

    private void dualQuadChannelDevice() {
        int i = this.mInterfaceID;
        if (i == 1) {
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode = this.mDeviceInfoNode;
            ftDeviceInfoListNode.serialNumber = String.valueOf(ftDeviceInfoListNode.serialNumber) + "A";
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode2 = this.mDeviceInfoNode;
            ftDeviceInfoListNode2.description = String.valueOf(ftDeviceInfoListNode2.description) + " A";
        } else if (i == 2) {
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode3 = this.mDeviceInfoNode;
            ftDeviceInfoListNode3.serialNumber = String.valueOf(ftDeviceInfoListNode3.serialNumber) + "B";
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode4 = this.mDeviceInfoNode;
            ftDeviceInfoListNode4.description = String.valueOf(ftDeviceInfoListNode4.description) + " B";
        } else if (i == 3) {
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode5 = this.mDeviceInfoNode;
            ftDeviceInfoListNode5.serialNumber = String.valueOf(ftDeviceInfoListNode5.serialNumber) + "C";
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode6 = this.mDeviceInfoNode;
            ftDeviceInfoListNode6.description = String.valueOf(ftDeviceInfoListNode6.description) + " C";
        } else if (i == 4) {
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode7 = this.mDeviceInfoNode;
            ftDeviceInfoListNode7.serialNumber = String.valueOf(ftDeviceInfoListNode7.serialNumber) + "D";
            D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode8 = this.mDeviceInfoNode;
            ftDeviceInfoListNode8.description = String.valueOf(ftDeviceInfoListNode8.description) + " D";
        }
    }

    public synchronized boolean openDevice(UsbManager usbManager) {
        if (isOpen()) {
            return false;
        }
        if (usbManager == null) {
            Log.e(TAG, "UsbManager cannot be null.");
            return false;
        } else if (getConnection() != null) {
            Log.e(TAG, "There should not have an UsbConnection.");
            return false;
        } else {
            setConnection(usbManager.openDevice(this.mUsbDevice));
            if (getConnection() == null) {
                Log.e(TAG, "UsbConnection cannot be null.");
                return false;
            } else if (!getConnection().claimInterface(this.mUsbInterface, true)) {
                Log.e(TAG, "ClaimInteface returned false.");
                return false;
            } else {
                Log.d(TAG, "open SUCCESS");
                if (!findDeviceEndpoints()) {
                    Log.e(TAG, "Failed to find endpoints.");
                    return false;
                }
                this.mUsbRequest.initialize(this.mUsbConnection, this.mBulkOutEndpoint);
                Log.d("D2XX::", "**********************Device Opened**********************");
                this.mProcessInCtrl = new ProcessInCtrl(this);
                this.mBulkIn = new BulkInWorker(this, this.mProcessInCtrl, getConnection(), this.mBulkInEndpoint);
                Thread thread = new Thread(this.mBulkIn);
                this.mBulkInThread = thread;
                thread.setName("bulkInThread");
                Thread thread2 = new Thread(new ProcessRequestWorker(this.mProcessInCtrl));
                this.mProcessRequestThread = thread2;
                thread2.setName("processRequestThread");
                purgeRxTx(true, true);
                this.mBulkInThread.start();
                this.mProcessRequestThread.start();
                setOpen();
                return true;
            }
        }
    }

    public synchronized boolean isOpen() {
        return this.mIsOpen.booleanValue();
    }

    private synchronized void setOpen() {
        this.mIsOpen = true;
        D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode = this.mDeviceInfoNode;
        ftDeviceInfoListNode.flags = 1 | ftDeviceInfoListNode.flags;
    }

    private synchronized void setClosed() {
        this.mIsOpen = false;
        this.mDeviceInfoNode.flags &= 2;
    }

    public synchronized void close() {
        if (this.mProcessRequestThread != null) {
            this.mProcessRequestThread.interrupt();
        }
        if (this.mBulkInThread != null) {
            this.mBulkInThread.interrupt();
        }
        if (this.mUsbConnection != null) {
            this.mUsbConnection.releaseInterface(this.mUsbInterface);
            this.mUsbConnection.close();
            this.mUsbConnection = null;
        }
        if (this.mProcessInCtrl != null) {
            this.mProcessInCtrl.close();
        }
        this.mProcessRequestThread = null;
        this.mBulkInThread = null;
        this.mBulkIn = null;
        this.mProcessInCtrl = null;
        setClosed();
    }

    public UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    public D2xxManager.FtDeviceInfoListNode getDeviceInfo() {
        return this.mDeviceInfoNode;
    }

    public int read(byte[] data, int length, long wait_ms) {
        if (!isOpen()) {
            return -1;
        }
        if (length <= 0) {
            return -2;
        }
        ProcessInCtrl processInCtrl = this.mProcessInCtrl;
        if (processInCtrl == null) {
            return -3;
        }
        int rc = processInCtrl.readBulkInData(data, length, wait_ms);
        return rc;
    }

    public int read(byte[] data, int length) {
        return read(data, length, this.mDriverParams.getReadTimeout());
    }

    public int read(byte[] data) {
        return read(data, data.length, this.mDriverParams.getReadTimeout());
    }

    public int write(byte[] data, int length) {
        return write(data, length, true);
    }

    public int write(byte[] data, int length, boolean wait) {
        Object obj;
        int rc = -1;
        if (!isOpen() || length < 0) {
            return -1;
        }
        UsbRequest request = this.mUsbRequest;
        if (wait) {
            request.setClientData(this);
        }
        if (length == 0) {
            byte[] tmpData = new byte[1];
            if (request.queue(ByteBuffer.wrap(tmpData), length)) {
                rc = length;
            }
        } else if (request.queue(ByteBuffer.wrap(data), length)) {
            rc = length;
        }
        if (wait) {
            do {
                UsbRequest request2 = this.mUsbConnection.requestWait();
                if (request2 != null) {
                    obj = request2.getClientData();
                } else {
                    Log.e(TAG, "UsbConnection.requestWait() == null");
                    return -99;
                }
            } while (obj != this);
            return rc;
        }
        return rc;
    }

    public int write(byte[] data) {
        return write(data, data.length, true);
    }

    public short getModemStatus() {
        if (!isOpen()) {
            return (short) -1;
        }
        if (this.mProcessInCtrl == null) {
            return (short) -2;
        }
        this.mEventMask &= -3;
        return (short) (this.mDeviceInfoNode.modemStatus & 255);
    }

    public short getLineStatus() {
        if (!isOpen()) {
            return (short) -1;
        }
        if (this.mProcessInCtrl == null) {
            return (short) -2;
        }
        return this.mDeviceInfoNode.lineStatus;
    }

    public int getQueueStatus() {
        if (!isOpen()) {
            return -1;
        }
        ProcessInCtrl processInCtrl = this.mProcessInCtrl;
        if (processInCtrl == null) {
            return -2;
        }
        return processInCtrl.getBytesAvailable();
    }

    public boolean readBufferFull() {
        return this.mProcessInCtrl.isSinkFull();
    }

    public long getEventStatus() {
        if (!isOpen()) {
            return -1L;
        }
        if (this.mProcessInCtrl == null) {
            return -2L;
        }
        long temp = this.mEventMask;
        this.mEventMask = 0L;
        return temp;
    }

    public boolean setBaudRate(int baudRate) {
        int result = 1;
        int[] divisors = new int[2];
        if (!isOpen()) {
            return false;
        }
        switch (baudRate) {
            case 300:
                divisors[0] = 10000;
                break;
            case 600:
                divisors[0] = 5000;
                break;
            case 1200:
                divisors[0] = 2500;
                break;
            case 2400:
                divisors[0] = 1250;
                break;
            case 4800:
                divisors[0] = 625;
                break;
            case 9600:
                divisors[0] = 16696;
                break;
            case 19200:
                divisors[0] = 32924;
                break;
            case 38400:
                divisors[0] = 49230;
                break;
            case 57600:
                divisors[0] = 52;
                break;
            case 115200:
                divisors[0] = 26;
                break;
            case 230400:
                divisors[0] = 13;
                break;
            case 460800:
                divisors[0] = 16390;
                break;
            case 921600:
                divisors[0] = 32771;
                break;
            default:
                if (isHiSpeed() && baudRate >= 1200) {
                    result = FT_BaudRate.FT_GetDivisorHi(baudRate, divisors);
                } else {
                    result = FT_BaudRate.FT_GetDivisor(baudRate, divisors, isBmDevice());
                }
                break;
        }
        if (isMultiIfDevice() || isFt232h() || isFt232ex()) {
            divisors[1] = divisors[1] << 8;
            divisors[1] = divisors[1] & 65280;
            divisors[1] = divisors[1] | this.mInterfaceID;
        }
        if (result != 1) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 3, divisors[0], divisors[1], null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean setDataCharacteristics(byte dataBits, byte stopBits, byte parity) {
        if (!isOpen()) {
            return false;
        }
        short wValue = (short) ((stopBits << 11) | ((short) ((parity << 8) | dataBits)));
        this.mDeviceInfoNode.breakOnParam = wValue;
        int status = getConnection().controlTransfer(64, 4, wValue, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean setBreakOn() {
        return setBreak(16384);
    }

    public boolean setBreakOff() {
        return setBreak(0);
    }

    private boolean setBreak(int OnOrOff) {
        int wValue = this.mDeviceInfoNode.breakOnParam;
        int wValue2 = wValue | OnOrOff;
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 4, wValue2, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean setFlowControl(short flowControl, byte xon, byte xoff) {
        short wValue = 0;
        if (!isOpen()) {
            return false;
        }
        if (flowControl == 1024) {
            short wValue2 = (short) (xoff << 8);
            wValue = (short) ((xon & 255) | wValue2);
        }
        int status = getConnection().controlTransfer(64, 2, wValue, this.mInterfaceID | flowControl, null, 0, 0);
        if (status != 0) {
            return false;
        }
        if (flowControl == 256) {
            boolean rc = setRts();
            return rc;
        } else if (flowControl != 512) {
            return true;
        } else {
            boolean rc2 = setDtr();
            return rc2;
        }
    }

    public boolean setRts() {
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 1, 514, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean clrRts() {
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 1, 512, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean setDtr() {
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 1, 257, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean clrDtr() {
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 1, 256, this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public boolean setChars(byte eventChar, byte eventCharEnable, byte errorChar, byte errorCharEnable) {
        TFtSpecialChars SpecialChars = new TFtSpecialChars();
        SpecialChars.EventChar = eventChar;
        SpecialChars.EventCharEnabled = eventCharEnable;
        SpecialChars.ErrorChar = errorChar;
        SpecialChars.ErrorCharEnabled = errorCharEnable;
        if (isOpen()) {
            int wValue = eventChar & 255;
            if (eventCharEnable != 0) {
                wValue |= 256;
            }
            int status = getConnection().controlTransfer(64, 6, wValue, this.mInterfaceID, null, 0, 0);
            if (status != 0) {
                return false;
            }
            int wValue2 = errorChar & 255;
            if (errorCharEnable > 0) {
                wValue2 |= 256;
            }
            int status2 = getConnection().controlTransfer(64, 7, wValue2, this.mInterfaceID, null, 0, 0);
            if (status2 == 0) {
                this.mTftSpecialChars = SpecialChars;
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean setBitMode(byte mask, byte bitMode) {
        int devType = this.mDeviceInfoNode.type;
        if (!isOpen()) {
            return false;
        }
        if (devType == 1) {
            return false;
        }
        if (devType == 0 && bitMode != 0) {
            if ((bitMode & 1) == 0) {
                return false;
            }
        } else if (devType == 4 && bitMode != 0) {
            if ((bitMode & 31) == 0) {
                return false;
            }
            if ((this.mUsbInterface.getId() != 0) & (bitMode == 2)) {
                return false;
            }
        } else if (devType == 5 && bitMode != 0) {
            if ((bitMode & 37) == 0) {
                return false;
            }
        } else if (devType == 6 && bitMode != 0) {
            if ((bitMode & 95) == 0) {
                return false;
            }
            if ((this.mUsbInterface.getId() != 0) & ((bitMode & 72) > 0)) {
                return false;
            }
        } else if (devType == 7 && bitMode != 0) {
            if ((bitMode & 7) == 0) {
                return false;
            }
            if ((this.mUsbInterface.getId() != 1) & (bitMode == 2) & (this.mUsbInterface.getId() != 0)) {
                return false;
            }
        } else if (devType == 8 && bitMode != 0 && bitMode > 64) {
            return false;
        }
        int wValue = bitMode << 8;
        int status = getConnection().controlTransfer(64, 11, wValue | (mask & 255), this.mInterfaceID, null, 0, 0);
        if (status != 0) {
            return false;
        }
        return true;
    }

    public byte getBitMode() {
        byte[] buf = new byte[1];
        if (!isOpen()) {
            return (byte) -1;
        }
        if (!isBmDevice()) {
            return (byte) -2;
        }
        int status = getConnection().controlTransfer(-64, 12, 0, this.mInterfaceID, buf, buf.length, 0);
        if (status == buf.length) {
            return buf[0];
        }
        return (byte) -3;
    }

    public boolean resetDevice() {
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 0, 0, 0, null, 0, 0);
        return status == 0;
    }

    public int VendorCmdSet(int request, int wValue) {
        if (!isOpen()) {
            return -1;
        }
        int status = getConnection().controlTransfer(64, request, wValue, this.mInterfaceID, null, 0, 0);
        return status;
    }

    public int VendorCmdSet(int request, int wValue, byte[] buf, int datalen) {
        if (!isOpen()) {
            Log.e(TAG, "VendorCmdSet: Device not open");
            return -1;
        } else if (datalen < 0) {
            Log.e(TAG, "VendorCmdSet: Invalid data length");
            return -1;
        } else {
            if (buf == null) {
                if (datalen > 0) {
                    Log.e(TAG, "VendorCmdSet: buf is null!");
                    return -1;
                }
            } else if (buf.length < datalen) {
                Log.e(TAG, "VendorCmdSet: length of buffer is smaller than data length to set");
                return -1;
            }
            int status = getConnection().controlTransfer(64, request, wValue, this.mInterfaceID, buf, datalen, 0);
            return status;
        }
    }

    public int VendorCmdGet(int request, int wValue, byte[] buf, int datalen) {
        if (!isOpen()) {
            Log.e(TAG, "VendorCmdGet: Device not open");
            return -1;
        } else if (datalen < 0) {
            Log.e(TAG, "VendorCmdGet: Invalid data length");
            return -1;
        } else if (buf == null) {
            Log.e(TAG, "VendorCmdGet: buf is null");
            return -1;
        } else if (buf.length < datalen) {
            Log.e(TAG, "VendorCmdGet: length of buffer is smaller than data length to get");
            return -1;
        } else {
            int status = getConnection().controlTransfer(-64, request, wValue, this.mInterfaceID, buf, datalen, 0);
            return status;
        }
    }

    public void stopInTask() {
        try {
            if (!this.mBulkIn.paused()) {
                this.mBulkIn.pause();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "stopInTask called!");
            e.printStackTrace();
        }
    }

    public void restartInTask() {
        this.mBulkIn.restart();
    }

    public boolean stoppedInTask() {
        return this.mBulkIn.paused();
    }

    public boolean purge(byte flags) {
        boolean RXBuffer = false;
        if ((flags & 1) == 1) {
            RXBuffer = true;
        }
        boolean TXBuffer = (flags & 2) == 2;
        return purgeRxTx(RXBuffer, TXBuffer);
    }

    private boolean purgeRxTx(boolean RXBuffer, boolean TXBuffer) {
        int status = 0;
        if (!isOpen()) {
            return false;
        }
        if (RXBuffer) {
            for (int i = 0; i < 6; i++) {
                status = getConnection().controlTransfer(64, 0, 1, this.mInterfaceID, null, 0, 0);
            }
            if (status > 0) {
                return false;
            }
            this.mProcessInCtrl.purgeINData();
        }
        if (!TXBuffer) {
            return false;
        }
        int status2 = getConnection().controlTransfer(64, 0, 2, this.mInterfaceID, null, 0, 0);
        return status2 == 0;
    }

    public boolean setLatencyTimer(byte latency) {
        int wValue = latency & 255;
        if (!isOpen()) {
            return false;
        }
        int status = getConnection().controlTransfer(64, 9, wValue, this.mInterfaceID, null, 0, 0);
        if (status == 0) {
            this.mLatencyTimer = latency;
            return true;
        }
        return false;
    }

    public byte getLatencyTimer() {
        byte[] latency = new byte[1];
        if (!isOpen()) {
            return (byte) -1;
        }
        int status = getConnection().controlTransfer(-64, 10, 0, this.mInterfaceID, latency, latency.length, 0);
        if (status == latency.length) {
            return latency[0];
        }
        return (byte) 0;
    }

    public boolean setEventNotification(long Mask) {
        if (!isOpen() || Mask == 0) {
            return false;
        }
        this.mEventMask = 0L;
        this.mEventNotification.Mask = Mask;
        return true;
    }

    private boolean findDeviceEndpoints() {
        for (int i = 0; i < this.mUsbInterface.getEndpointCount(); i++) {
            Log.i(TAG, "EP: " + String.format("0x%02X", Integer.valueOf(this.mUsbInterface.getEndpoint(i).getAddress())));
            if (this.mUsbInterface.getEndpoint(i).getType() == 2) {
                if (this.mUsbInterface.getEndpoint(i).getDirection() == 128) {
                    UsbEndpoint endpoint = this.mUsbInterface.getEndpoint(i);
                    this.mBulkInEndpoint = endpoint;
                    this.mMaxPacketSize = endpoint.getMaxPacketSize();
                } else {
                    this.mBulkOutEndpoint = this.mUsbInterface.getEndpoint(i);
                }
            } else {
                Log.i(TAG, "Not Bulk Endpoint");
            }
        }
        return (this.mBulkOutEndpoint == null || this.mBulkInEndpoint == null) ? false : true;
    }

    public FT_EEPROM eepromRead() {
        if (!isOpen()) {
            return null;
        }
        return this.mEEPROM.readEeprom();
    }

    public short eepromWrite(FT_EEPROM eeData) {
        if (!isOpen()) {
            return (short) -1;
        }
        return this.mEEPROM.programEeprom(eeData);
    }

    public boolean eepromErase() {
        return isOpen() && this.mEEPROM.eraseEeprom() == 0;
    }

    public int eepromWriteUserArea(byte[] data) {
        if (!isOpen()) {
            return 0;
        }
        return this.mEEPROM.writeUserData(data);
    }

    public byte[] eepromReadUserArea(int length) {
        if (!isOpen()) {
            return null;
        }
        return this.mEEPROM.readUserData(length);
    }

    public int eepromGetUserAreaSize() {
        if (!isOpen()) {
            return -1;
        }
        return this.mEEPROM.getUserSize();
    }

    public int eepromReadWord(short offset) {
        if (!isOpen()) {
            return -1;
        }
        int rc = this.mEEPROM.readWord(offset);
        return rc;
    }

    public boolean eepromWriteWord(short address, short data) {
        if (!isOpen()) {
            return false;
        }
        boolean rc = this.mEEPROM.writeWord(address, data);
        return rc;
    }

    public int getMaxPacketSize() {
        return this.mMaxPacketSize;
    }
}
