package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class ProlificSerialDriver extends CommonUsbSerialDriver {
    private static final int CONTROL_DTR = 1;
    private static final int CONTROL_RTS = 2;
    private static final int DEVICE_TYPE_0 = 1;
    private static final int DEVICE_TYPE_1 = 2;
    private static final int DEVICE_TYPE_HX = 0;
    private static final int FLUSH_RX_REQUEST = 8;
    private static final int FLUSH_TX_REQUEST = 9;
    private static final int INTERRUPT_ENDPOINT = 129;
    private static final int PROLIFIC_CTRL_OUT_REQTYPE = 33;
    private static final int PROLIFIC_VENDOR_IN_REQTYPE = 192;
    private static final int PROLIFIC_VENDOR_OUT_REQTYPE = 64;
    private static final int PROLIFIC_VENDOR_READ_REQUEST = 1;
    private static final int PROLIFIC_VENDOR_WRITE_REQUEST = 1;
    private static final int READ_ENDPOINT = 131;
    private static final int SET_CONTROL_REQUEST = 34;
    private static final int SET_LINE_REQUEST = 32;
    private static final int STATUS_BUFFER_SIZE = 10;
    private static final int STATUS_BYTE_IDX = 8;
    private static final int STATUS_FLAG_CD = 1;
    private static final int STATUS_FLAG_CTS = 128;
    private static final int STATUS_FLAG_DSR = 2;
    private static final int STATUS_FLAG_RI = 8;
    private static final int USB_READ_TIMEOUT_MILLIS = 1000;
    private static final int USB_RECIP_INTERFACE = 1;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
    private static final int WRITE_ENDPOINT = 2;
    private final String TAG;
    private int mBaudRate;
    private int mControlLinesValue;
    private int mDataBits;
    private int mDeviceType;
    private UsbEndpoint mInterruptEndpoint;
    private int mParity;
    private UsbEndpoint mReadEndpoint;
    private IOException mReadStatusException;
    private volatile Thread mReadStatusThread;
    private final Object mReadStatusThreadLock;
    private int mStatus;
    private int mStopBits;
    boolean mStopReadStatusThread;
    private UsbEndpoint mWriteEndpoint;

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ int permissionStatus() {
        return super.permissionStatus();
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ void setConnection(UsbDeviceConnection usbDeviceConnection) {
        super.setConnection(usbDeviceConnection);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ void setPermissionStatus(int i) {
        super.setPermissionStatus(i);
    }

    private final byte[] inControlTransfer(int requestType, int request, int value, int index, int length) throws IOException {
        byte[] buffer = new byte[length];
        int result = this.mConnection.controlTransfer(requestType, request, value, index, buffer, length, 1000);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", Integer.valueOf(value), Integer.valueOf(result)));
        }
        return buffer;
    }

    private final void outControlTransfer(int requestType, int request, int value, int index, byte[] data) throws IOException {
        int length = data == null ? 0 : data.length;
        int result = this.mConnection.controlTransfer(requestType, request, value, index, data, length, 5000);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", Integer.valueOf(value), Integer.valueOf(result)));
        }
    }

    private final byte[] vendorIn(int value, int index, int length) throws IOException {
        return inControlTransfer(192, 1, value, index, length);
    }

    private final void vendorOut(int value, int index, byte[] data) throws IOException {
        outControlTransfer(64, 1, value, index, data);
    }

    private final void ctrlOut(int request, int value, int index, byte[] data) throws IOException {
        outControlTransfer(PROLIFIC_CTRL_OUT_REQTYPE, request, value, index, data);
    }

    private void doBlackMagic() throws IOException {
        vendorIn(33924, 0, 1);
        vendorOut(1028, 0, null);
        vendorIn(33924, 0, 1);
        vendorIn(33667, 0, 1);
        vendorIn(33924, 0, 1);
        vendorOut(1028, 1, null);
        vendorIn(33924, 0, 1);
        vendorIn(33667, 0, 1);
        vendorOut(0, 1, null);
        vendorOut(1, 0, null);
        vendorOut(2, this.mDeviceType == 0 ? 68 : 36, null);
    }

    private void resetDevice() throws IOException {
        purgeHwBuffers(true, true);
    }

    private void setControlLines(int newControlLinesValue) throws IOException {
        ctrlOut(SET_CONTROL_REQUEST, newControlLinesValue, 0, null);
        this.mControlLinesValue = newControlLinesValue;
    }

    public final void readStatusThreadFunction() {
        while (!this.mStopReadStatusThread) {
            try {
                byte[] buffer = new byte[10];
                int readBytesCount = this.mConnection.bulkTransfer(this.mInterruptEndpoint, buffer, 10, 500);
                if (readBytesCount > 0) {
                    if (readBytesCount == 10) {
                        this.mStatus = buffer[8] & 255;
                    } else {
                        throw new IOException(String.format("Invalid CTS / DSR / CD / RI status buffer received, expected %d bytes, but received %d", 10, Integer.valueOf(readBytesCount)));
                    }
                }
            } catch (IOException e) {
                this.mReadStatusException = e;
                return;
            }
        }
    }

    private final int getStatus() throws IOException {
        if (this.mReadStatusThread == null && this.mReadStatusException == null) {
            synchronized (this.mReadStatusThreadLock) {
                if (this.mReadStatusThread == null) {
                    byte[] buffer = new byte[10];
                    int readBytes = this.mConnection.bulkTransfer(this.mInterruptEndpoint, buffer, 10, 100);
                    if (readBytes != 10) {
                        Log.w(this.TAG, "Could not read initial CTS / DSR / CD / RI status");
                    } else {
                        this.mStatus = buffer[8] & 255;
                    }
                    this.mReadStatusThread = new Thread(new Runnable() { // from class: com.hoho.android.usbserial.driver.ProlificSerialDriver.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ProlificSerialDriver.this.readStatusThreadFunction();
                        }
                    });
                    this.mReadStatusThread.setDaemon(true);
                    this.mReadStatusThread.start();
                }
            }
        }
        IOException readStatusException = this.mReadStatusException;
        if (this.mReadStatusException != null) {
            this.mReadStatusException = null;
            throw readStatusException;
        }
        return this.mStatus;
    }

    private final boolean testStatusFlag(int flag) throws IOException {
        return (getStatus() & flag) == flag;
    }

    public ProlificSerialDriver(UsbDevice device) {
        super(device);
        this.mDeviceType = 0;
        this.mControlLinesValue = 0;
        this.mBaudRate = -1;
        this.mDataBits = -1;
        this.mStopBits = -1;
        this.mParity = -1;
        this.mStatus = 0;
        this.mReadStatusThread = null;
        this.mReadStatusThreadLock = new Object();
        this.mStopReadStatusThread = false;
        this.mReadStatusException = null;
        this.TAG = ProlificSerialDriver.class.getSimpleName();
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void open() throws IOException {
        UsbInterface usbInterface = this.mDevice.getInterface(0);
        if (!this.mConnection.claimInterface(usbInterface, true)) {
            throw new IOException("Error claiming Prolific interface 0");
        }
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            try {
                UsbEndpoint currentEndpoint = usbInterface.getEndpoint(i);
                int address = currentEndpoint.getAddress();
                if (address == 2) {
                    this.mWriteEndpoint = currentEndpoint;
                } else if (address == 129) {
                    this.mInterruptEndpoint = currentEndpoint;
                } else if (address == 131) {
                    this.mReadEndpoint = currentEndpoint;
                }
            } catch (Throwable th) {
                if (0 == 0) {
                    try {
                        this.mConnection.releaseInterface(usbInterface);
                    } catch (Exception e) {
                    }
                }
                throw th;
            }
        }
        if (this.mDevice.getDeviceClass() == 2) {
            this.mDeviceType = 1;
        } else {
            try {
                try {
                    Method getRawDescriptorsMethod = this.mConnection.getClass().getMethod("getRawDescriptors", new Class[0]);
                    byte[] rawDescriptors = (byte[]) getRawDescriptorsMethod.invoke(this.mConnection, new Object[0]);
                    byte maxPacketSize0 = rawDescriptors[7];
                    if (maxPacketSize0 == 64) {
                        this.mDeviceType = 0;
                    } else {
                        if (this.mDevice.getDeviceClass() != 0 && this.mDevice.getDeviceClass() != 255) {
                            Log.w(this.TAG, "Could not detect PL2303 subtype, Assuming that it is a HX device");
                            this.mDeviceType = 0;
                        }
                        this.mDeviceType = 2;
                    }
                } catch (Exception e2) {
                    Log.e(this.TAG, "An unexpected exception occurred while trying to detect PL2303 subtype", e2);
                }
            } catch (NoSuchMethodException e3) {
                Log.w(this.TAG, "Method UsbDeviceConnection.getRawDescriptors, required for PL2303 subtype detection, not available! Assuming that it is a HX device");
                this.mDeviceType = 0;
            }
        }
        setControlLines(this.mControlLinesValue);
        resetDevice();
        doBlackMagic();
        if (1 == 0) {
            try {
                this.mConnection.releaseInterface(usbInterface);
            } catch (Exception e4) {
            }
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void close() throws IOException {
        try {
            this.mStopReadStatusThread = true;
            synchronized (this.mReadStatusThreadLock) {
                if (this.mReadStatusThread != null) {
                    try {
                        this.mReadStatusThread.join();
                    } catch (Exception e) {
                        Log.w(this.TAG, "An error occurred while waiting for status read thread", e);
                    }
                }
            }
            resetDevice();
        } finally {
            this.mConnection.releaseInterface(this.mDevice.getInterface(0));
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public int read(byte[] dest, int timeoutMillis) throws IOException {
        synchronized (this.mReadBufferLock) {
            int readAmt = Math.min(dest.length, this.mReadBuffer.length);
            int numBytesRead = this.mConnection.bulkTransfer(this.mReadEndpoint, this.mReadBuffer, readAmt, timeoutMillis);
            if (numBytesRead < 0) {
                return 0;
            }
            System.arraycopy(this.mReadBuffer, 0, dest, 0, numBytesRead);
            return numBytesRead;
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public int write(byte[] src, int timeoutMillis) throws IOException {
        int writeLength;
        byte[] writeBuffer;
        int amtWritten;
        int offset = 0;
        while (offset < src.length) {
            synchronized (this.mWriteBufferLock) {
                writeLength = Math.min(src.length - offset, this.mWriteBuffer.length);
                if (offset == 0) {
                    writeBuffer = src;
                } else {
                    byte[] writeBuffer2 = this.mWriteBuffer;
                    System.arraycopy(src, offset, writeBuffer2, 0, writeLength);
                    writeBuffer = this.mWriteBuffer;
                }
                amtWritten = this.mConnection.bulkTransfer(this.mWriteEndpoint, writeBuffer, writeLength, timeoutMillis);
            }
            if (amtWritten <= 0) {
                throw new IOException("Error writing " + writeLength + " bytes at offset " + offset + " length=" + src.length);
            }
            offset += amtWritten;
        }
        return offset;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        if (this.mBaudRate == baudRate && this.mDataBits == dataBits && this.mStopBits == stopBits && this.mParity == parity) {
            return;
        }
        byte[] lineRequestData = new byte[7];
        lineRequestData[0] = (byte) (baudRate & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        lineRequestData[1] = (byte) ((baudRate >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        lineRequestData[2] = (byte) ((baudRate >> 16) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        lineRequestData[3] = (byte) ((baudRate >> 24) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        if (stopBits == 1) {
            lineRequestData[4] = 0;
        } else if (stopBits == 2) {
            lineRequestData[4] = 2;
        } else if (stopBits == 3) {
            lineRequestData[4] = 1;
        } else {
            throw new IllegalArgumentException("Unknown stopBits value: " + stopBits);
        }
        if (parity == 0) {
            lineRequestData[5] = 0;
        } else if (parity == 1) {
            lineRequestData[5] = 1;
        } else if (parity == 2) {
            lineRequestData[5] = 2;
        } else if (parity != 3) {
            if (parity == 4) {
                lineRequestData[5] = 4;
            } else {
                throw new IllegalArgumentException("Unknown parity value: " + parity);
            }
        } else {
            lineRequestData[5] = 3;
        }
        lineRequestData[6] = (byte) dataBits;
        ctrlOut(SET_LINE_REQUEST, 0, 0, lineRequestData);
        resetDevice();
        this.mBaudRate = baudRate;
        this.mDataBits = dataBits;
        this.mStopBits = stopBits;
        this.mParity = parity;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getCD() throws IOException {
        return testStatusFlag(1);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getCTS() throws IOException {
        return testStatusFlag(128);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getDSR() throws IOException {
        return testStatusFlag(2);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getDTR() throws IOException {
        return (this.mControlLinesValue & 1) == 1;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setDTR(boolean value) throws IOException {
        int newControlLinesValue;
        if (value) {
            newControlLinesValue = this.mControlLinesValue | 1;
        } else {
            int newControlLinesValue2 = this.mControlLinesValue;
            newControlLinesValue = newControlLinesValue2 & (-2);
        }
        setControlLines(newControlLinesValue);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRI() throws IOException {
        return testStatusFlag(8);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRTS() throws IOException {
        return (this.mControlLinesValue & 2) == 2;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setRTS(boolean value) throws IOException {
        int newControlLinesValue;
        if (value) {
            newControlLinesValue = this.mControlLinesValue | 2;
        } else {
            int newControlLinesValue2 = this.mControlLinesValue;
            newControlLinesValue = newControlLinesValue2 & (-3);
        }
        setControlLines(newControlLinesValue);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) throws IOException {
        if (purgeReadBuffers) {
            vendorOut(8, 0, null);
        }
        if (purgeWriteBuffers) {
            vendorOut(9, 0, null);
            return true;
        }
        return true;
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_PROLIFIC), new int[]{UsbId.PROLIFIC_PL2303});
        return supportedDevices;
    }
}
