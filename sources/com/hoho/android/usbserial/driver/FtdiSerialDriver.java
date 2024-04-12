package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.pdrl.aerogcs.AeroGCSActivity;

/* loaded from: classes2.dex */
public class FtdiSerialDriver extends CommonUsbSerialDriver {
    private static final boolean ENABLE_ASYNC_READS = false;
    public static final int FTDI_DEVICE_IN_REQTYPE = 192;
    public static final int FTDI_DEVICE_OUT_REQTYPE = 64;
    private static final int MODEM_STATUS_HEADER_LENGTH = 2;
    private static final int SIO_MODEM_CTRL_REQUEST = 1;
    private static final int SIO_RESET_PURGE_RX = 1;
    private static final int SIO_RESET_PURGE_TX = 2;
    private static final int SIO_RESET_REQUEST = 0;
    private static final int SIO_RESET_SIO = 0;
    private static final int SIO_SET_BAUD_RATE_REQUEST = 3;
    private static final int SIO_SET_DATA_REQUEST = 4;
    private static final int SIO_SET_FLOW_CTRL_REQUEST = 2;
    public static final int USB_ENDPOINT_IN = 128;
    public static final int USB_ENDPOINT_OUT = 0;
    public static final int USB_READ_TIMEOUT_MILLIS = 5000;
    public static final int USB_RECIP_DEVICE = 0;
    public static final int USB_RECIP_ENDPOINT = 2;
    public static final int USB_RECIP_INTERFACE = 1;
    public static final int USB_RECIP_OTHER = 3;
    public static final int USB_TYPE_CLASS = 0;
    public static final int USB_TYPE_RESERVED = 0;
    public static final int USB_TYPE_STANDARD = 0;
    public static final int USB_TYPE_VENDOR = 0;
    public static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
    private final String TAG;
    private int mInterface;
    private int mMaxPacketSize;
    private DeviceType mType;
    FT_Device m_ftDev;

    /* loaded from: classes2.dex */
    public enum DeviceType {
        TYPE_BM,
        TYPE_AM,
        TYPE_2232C,
        TYPE_R,
        TYPE_2232H,
        TYPE_4232H
    }

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

    private final int filterStatusBytes(byte[] src, byte[] dest, int totalBytesRead, int maxPacketSize) {
        int packetsCount = (totalBytesRead / maxPacketSize) + 1;
        int packetIdx = 0;
        while (packetIdx < packetsCount) {
            int count = packetIdx == packetsCount + (-1) ? (totalBytesRead % maxPacketSize) - 2 : maxPacketSize - 2;
            if (count > 0) {
                System.arraycopy(src, (packetIdx * maxPacketSize) + 2, dest, (maxPacketSize - 2) * packetIdx, count);
            }
            packetIdx++;
        }
        int packetIdx2 = packetsCount * 2;
        return totalBytesRead - packetIdx2;
    }

    public FtdiSerialDriver(UsbDevice usbDevice) {
        super(usbDevice);
        this.TAG = FtdiSerialDriver.class.getSimpleName();
        this.mInterface = 0;
        this.mMaxPacketSize = 64;
        this.mType = null;
    }

    public void reset() throws IOException {
        int result = this.mConnection.controlTransfer(64, 0, 0, 0, null, 0, 5000);
        if (result != 0) {
            throw new IOException("Reset failed: result=" + result);
        }
        this.mType = DeviceType.TYPE_R;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void open() throws IOException {
        D2xxManager ftD2xx = null;
        try {
            ftD2xx = D2xxManager.getInstance(AeroGCSActivity.m_context);
        } catch (D2xxManager.D2xxException ex) {
            AeroGCSActivity.aerogcsLogDebug("D2xxManager.getInstance threw exception: " + ex.getMessage());
        }
        if (ftD2xx == null) {
            AeroGCSActivity.aerogcsLogWarning("Unable to retrieve D2xxManager instance.");
            throw new IOException("Unable to retrieve D2xxManager instance.");
        }
        AeroGCSActivity.aerogcsLogDebug("Opened D2xxManager");
        int DevCount = ftD2xx.createDeviceInfoList(AeroGCSActivity.m_context);
        AeroGCSActivity.aerogcsLogDebug("Found " + DevCount + " ftdi devices.");
        if (DevCount < 1) {
            throw new IOException("No FTDI Devices found");
        }
        this.m_ftDev = null;
        try {
            try {
                FT_Device openByIndex = ftD2xx.openByIndex(AeroGCSActivity.m_context, 0);
                this.m_ftDev = openByIndex;
                if (openByIndex == null) {
                    throw new IOException("No FTDI Devices found");
                }
            } catch (NullPointerException e) {
                AeroGCSActivity.aerogcsLogDebug("ftD2xx.openByIndex exception: " + e.getMessage());
                if (this.m_ftDev == null) {
                    throw new IOException("No FTDI Devices found");
                }
            }
            AeroGCSActivity.aerogcsLogDebug("Opened FTDI device.");
        } catch (Throwable th) {
            if (this.m_ftDev != null) {
                throw th;
            }
            throw new IOException("No FTDI Devices found");
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void close() {
        FT_Device fT_Device = this.m_ftDev;
        if (fT_Device != null) {
            try {
                fT_Device.close();
            } catch (Exception e) {
                AeroGCSActivity.aerogcsLogWarning("close exception: " + e.getMessage());
            }
            this.m_ftDev = null;
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public int read(byte[] dest, int timeoutMillis) throws IOException {
        int bytesAvailable = this.m_ftDev.getQueueStatus();
        if (bytesAvailable <= 0) {
            return 0;
        }
        try {
            int totalBytesRead = this.m_ftDev.read(dest, Math.min(4096, bytesAvailable), timeoutMillis);
            return totalBytesRead;
        } catch (NullPointerException e) {
            String errorMsg = "Error reading: " + e.getMessage();
            AeroGCSActivity.aerogcsLogWarning(errorMsg);
            throw new IOException(errorMsg, e);
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public int write(byte[] src, int timeoutMillis) throws IOException {
        try {
            this.m_ftDev.write(src);
            return src.length;
        } catch (Exception e) {
            AeroGCSActivity.aerogcsLogWarning("Error writing: " + e.getMessage());
            return 0;
        }
    }

    private int setBaudRate(int baudRate) throws IOException {
        try {
            this.m_ftDev.setBaudRate(baudRate);
            return baudRate;
        } catch (Exception e) {
            AeroGCSActivity.aerogcsLogWarning("Error setting baud rate: " + e.getMessage());
            return 0;
        }
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        int dataBits2;
        int stopBits2;
        int parity2;
        setBaudRate(baudRate);
        if (dataBits == 7) {
            dataBits2 = 7;
        } else {
            dataBits2 = 8;
        }
        if (stopBits != 1) {
            stopBits2 = 0;
        } else {
            stopBits2 = 2;
        }
        if (parity == 1) {
            parity2 = 1;
        } else if (parity == 2) {
            parity2 = 2;
        } else if (parity == 3) {
            parity2 = 3;
        } else if (parity != 4) {
            parity2 = 0;
        } else {
            parity2 = 4;
        }
        try {
            this.m_ftDev.setDataCharacteristics((byte) dataBits2, (byte) stopBits2, (byte) parity2);
        } catch (Exception e) {
            AeroGCSActivity.aerogcsLogWarning("Error setDataCharacteristics: " + e.getMessage());
        }
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
        return false;
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
        return false;
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public void setRTS(boolean value) throws IOException {
    }

    @Override // com.hoho.android.usbserial.driver.CommonUsbSerialDriver, com.hoho.android.usbserial.driver.UsbSerialDriver
    public boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) throws IOException {
        if (purgeReadBuffers) {
            try {
                this.m_ftDev.purge((byte) 1);
            } catch (Exception e) {
                String errMsg = "Error purgeHwBuffers(RX): " + e.getMessage();
                AeroGCSActivity.aerogcsLogWarning(errMsg);
                throw new IOException(errMsg);
            }
        }
        if (purgeWriteBuffers) {
            try {
                this.m_ftDev.purge((byte) 2);
            } catch (Exception e2) {
                String errMsg2 = "Error purgeHwBuffers(TX): " + e2.getMessage();
                AeroGCSActivity.aerogcsLogWarning(errMsg2);
                throw new IOException(errMsg2);
            }
        }
        return true;
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf((int) UsbId.VENDOR_FTDI), new int[]{UsbId.FTDI_FT232R, UsbId.FTDI_FT231X});
        return supportedDevices;
    }
}
