package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class CdcAcmSerialDriver extends CommonUsbSerialDriver {
    private static final int GET_LINE_CODING = 33;
    private static final int SEND_BREAK = 35;
    private static final int SET_CONTROL_LINE_STATE = 34;
    private static final int SET_LINE_CODING = 32;
    private static final int USB_RECIP_INTERFACE = 1;
    private static final int USB_RT_ACM = 33;
    private final String TAG;
    private UsbEndpoint mControlEndpoint;
    private UsbInterface mControlInterface;
    private UsbInterface mDataInterface;
    private boolean mDtr;
    private UsbEndpoint mReadEndpoint;
    private boolean mRts;
    private UsbEndpoint mWriteEndpoint;

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ int permissionStatus() {
        return super.permissionStatus();
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ boolean purgeHwBuffers(boolean z, boolean z2) throws IOException {
        return super.purgeHwBuffers(z, z2);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ void setConnection(UsbDeviceConnection usbDeviceConnection) {
        super.setConnection(usbDeviceConnection);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public /* bridge */ /* synthetic */ void setPermissionStatus(int i) {
        super.setPermissionStatus(i);
    }

    public CdcAcmSerialDriver(UsbDevice device) {
        super(device);
        this.TAG = CdcAcmSerialDriver.class.getSimpleName();
        this.mRts = false;
        this.mDtr = false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void open() throws IOException {
        String str = this.TAG;
        Log.d(str, "device " + this.mDevice);
        this.mControlInterface = null;
        this.mDataInterface = null;
        this.mWriteEndpoint = null;
        this.mReadEndpoint = null;
        for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
            UsbInterface iface = this.mDevice.getInterface(i);
            int interfaceClass = iface.getInterfaceClass();
            if (interfaceClass == 2) {
                this.mControlInterface = iface;
                String str2 = this.TAG;
                Log.d(str2, "control iface=" + iface);
            } else if (interfaceClass == 10) {
                this.mDataInterface = iface;
                String str3 = this.TAG;
                Log.d(str3, "data iface=" + iface);
            } else {
                String str4 = this.TAG;
                Log.d(str4, "skipping iface=" + iface);
            }
        }
        if (this.mControlInterface == null) {
            this.mControlInterface = this.mDevice.getInterface(0);
            String str5 = this.TAG;
            Log.d(str5, "Failback: Control iface=" + this.mControlInterface);
        }
        if (!this.mConnection.claimInterface(this.mControlInterface, true)) {
            throw new IOException("Could not claim control interface.");
        }
        this.mControlEndpoint = this.mControlInterface.getEndpoint(0);
        String str6 = this.TAG;
        Log.d(str6, "Control endpoint: " + this.mControlEndpoint);
        if (this.mDataInterface == null) {
            this.mDataInterface = this.mDevice.getInterface(1);
            String str7 = this.TAG;
            Log.d(str7, "Failback: data iface=" + this.mDataInterface);
        }
        if (!this.mConnection.claimInterface(this.mDataInterface, true)) {
            throw new IOException("Could not claim data interface.");
        }
        for (int i2 = 0; i2 < this.mDataInterface.getEndpointCount(); i2++) {
            UsbEndpoint endpoint = this.mDataInterface.getEndpoint(i2);
            int direction = endpoint.getDirection();
            if (direction == 0) {
                this.mWriteEndpoint = endpoint;
                String str8 = this.TAG;
                Log.d(str8, "Write endpoint: " + this.mWriteEndpoint);
            } else if (direction == 128) {
                this.mReadEndpoint = endpoint;
                String str9 = this.TAG;
                Log.d(str9, "Read endpoint: " + this.mReadEndpoint);
            }
        }
        if (this.mReadEndpoint == null || this.mWriteEndpoint == null) {
            this.mReadEndpoint = this.mDataInterface.getEndpoint(0);
            String str10 = this.TAG;
            Log.d(str10, "Read endpoint direction: " + this.mReadEndpoint.getDirection());
            this.mWriteEndpoint = this.mDataInterface.getEndpoint(1);
            String str11 = this.TAG;
            Log.d(str11, "Write endpoint direction: " + this.mWriteEndpoint.getDirection());
        }
    }

    private int sendAcmControlMessage(int request, int value, byte[] buf) {
        return this.mConnection.controlTransfer(33, request, value, 0, buf, buf != null ? buf.length : 0, 5000);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void close() throws IOException {
        this.mConnection.close();
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
    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) {
        byte stopBitsByte;
        byte parityBitesByte;
        if (stopBits == 1) {
            stopBitsByte = 0;
        } else if (stopBits != 2) {
            if (stopBits == 3) {
                stopBitsByte = 1;
            } else {
                throw new IllegalArgumentException("Bad value for stopBits: " + stopBits);
            }
        } else {
            stopBitsByte = 2;
        }
        if (parity == 0) {
            parityBitesByte = 0;
        } else if (parity == 1) {
            parityBitesByte = 1;
        } else if (parity != 2) {
            if (parity != 3) {
                if (parity == 4) {
                    parityBitesByte = 4;
                } else {
                    throw new IllegalArgumentException("Bad value for parity: " + parity);
                }
            } else {
                parityBitesByte = 3;
            }
        } else {
            parityBitesByte = 2;
        }
        byte[] msg = {(byte) (baudRate & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 16) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 24) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), stopBitsByte, parityBitesByte, (byte) dataBits};
        sendAcmControlMessage(SET_LINE_CODING, 0, msg);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getCD() throws IOException {
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getCTS() throws IOException {
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getDSR() throws IOException {
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getDTR() throws IOException {
        return this.mDtr;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setDTR(boolean value) throws IOException {
        this.mDtr = value;
        setDtrRts();
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRI() throws IOException {
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRTS() throws IOException {
        return this.mRts;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setRTS(boolean value) throws IOException {
        this.mRts = value;
        setDtrRts();
    }

    private void setDtrRts() {
        int value = (this.mRts ? 2 : 0) | (this.mDtr ? 1 : 0);
        sendAcmControlMessage(SET_CONTROL_LINE_STATE, value, null);
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_ARDUINO), new int[]{1, 67, 16, 66, 59, 68, 63, 68, UsbId.ARDUINO_LEONARDO});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_VAN_OOIJEN_TECH), new int[]{1155});
        supportedDevices.put(1003, new int[]{UsbId.ATMEL_LUFA_CDC_DEMO_APP});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_LEAFLABS), new int[]{4});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_PX4), new int[]{17});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_UBLOX), new int[]{UsbId.DEVICE_UBLOX_5, UsbId.DEVICE_UBLOX_6, UsbId.DEVICE_UBLOX_7, UsbId.DEVICE_UBLOX_8});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_OPENPILOT), new int[]{UsbId.DEVICE_CC3D, UsbId.DEVICE_REVOLUTION, UsbId.DEVICE_SPARKY2, UsbId.DEVICE_OPLINK});
        supportedDevices.put(1155, new int[]{UsbId.DEVICE_ARDUPILOT_CHIBIOS});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_ARDUPILOT_CHIBIOS2), new int[]{UsbId.DEVICE_ARDUPILOT_CHIBIOS});
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_DRAGONLINK), new int[]{131});
        return supportedDevices;
    }
}
