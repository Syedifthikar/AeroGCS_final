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
public class Cp2102SerialDriver extends CommonUsbSerialDriver {
    private static final int BAUD_RATE_GEN_FREQ = 3686400;
    private static final int CONTROL_WRITE_DTR = 256;
    private static final int CONTROL_WRITE_RTS = 512;
    private static final int DEFAULT_BAUD_RATE = 9600;
    private static final int FLUSH_READ_CODE = 10;
    private static final int FLUSH_WRITE_CODE = 5;
    private static final int MCR_ALL = 3;
    private static final int MCR_DTR = 1;
    private static final int MCR_RTS = 2;
    private static final int REQTYPE_HOST_TO_DEVICE = 65;
    private static final int SILABSER_FLUSH_REQUEST_CODE = 18;
    private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0;
    private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 1;
    private static final int SILABSER_SET_BAUDRATE = 30;
    private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 3;
    private static final int SILABSER_SET_MHS_REQUEST_CODE = 7;
    private static final String TAG = Cp2102SerialDriver.class.getSimpleName();
    private static final int UART_DISABLE = 0;
    private static final int UART_ENABLE = 1;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
    private UsbEndpoint mReadEndpoint;
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

    public Cp2102SerialDriver(UsbDevice device) {
        super(device);
    }

    private int setConfigSingle(int request, int value) {
        return this.mConnection.controlTransfer(65, request, value, 0, null, 0, 5000);
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void open() throws IOException {
        boolean opened = false;
        for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
            try {
                UsbInterface usbIface = this.mDevice.getInterface(i);
                if (this.mConnection.claimInterface(usbIface, true)) {
                    String str = TAG;
                    Log.d(str, "claimInterface " + i + " SUCCESS");
                } else {
                    String str2 = TAG;
                    Log.d(str2, "claimInterface " + i + " FAIL");
                }
            } finally {
                if (!opened) {
                    close();
                }
            }
        }
        UsbInterface dataIface = this.mDevice.getInterface(this.mDevice.getInterfaceCount() - 1);
        for (int i2 = 0; i2 < dataIface.getEndpointCount(); i2++) {
            UsbEndpoint ep = dataIface.getEndpoint(i2);
            if (ep.getType() == 2) {
                if (ep.getDirection() == 128) {
                    this.mReadEndpoint = ep;
                } else {
                    this.mWriteEndpoint = ep;
                }
            }
        }
        setConfigSingle(0, 1);
        setConfigSingle(7, 771);
        setConfigSingle(1, 384);
        opened = true;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void close() throws IOException {
        setConfigSingle(0, 0);
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

    private void setBaudRate(int baudRate) throws IOException {
        byte[] data = {(byte) (baudRate & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 16) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), (byte) ((baudRate >> 24) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST)};
        int ret = this.mConnection.controlTransfer(65, SILABSER_SET_BAUDRATE, 0, 0, data, 4, 5000);
        if (ret < 0) {
            throw new IOException("Error setting baud rate.");
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        int configDataBits;
        setBaudRate(baudRate);
        if (dataBits == 5) {
            configDataBits = 0 | 1280;
        } else if (dataBits == 6) {
            configDataBits = 0 | 1536;
        } else if (dataBits == 7) {
            configDataBits = 0 | 1792;
        } else if (dataBits == 8) {
            configDataBits = 0 | 2048;
        } else {
            configDataBits = 0 | 2048;
        }
        setConfigSingle(3, configDataBits);
        int configParityBits = 0;
        if (parity == 1) {
            configParityBits = 0 | 16;
        } else if (parity == 2) {
            configParityBits = 0 | 32;
        }
        setConfigSingle(3, configParityBits);
        int configStopBits = 0;
        if (stopBits == 1) {
            configStopBits = 0 | 0;
        } else if (stopBits == 2) {
            configStopBits = 0 | 2;
        }
        setConfigSingle(3, configStopBits);
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
        return true;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setDTR(boolean value) throws IOException {
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRI() throws IOException {
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean getRTS() throws IOException {
        return true;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) throws IOException {
        int value = (purgeWriteBuffers ? 5 : 0) | (purgeReadBuffers ? 10 : 0);
        if (value != 0) {
            setConfigSingle(18, value);
            return true;
        }
        return true;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setRTS(boolean value) throws IOException {
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_SILAB), new int[]{UsbId.SILAB_CP2102});
        return supportedDevices;
    }
}
