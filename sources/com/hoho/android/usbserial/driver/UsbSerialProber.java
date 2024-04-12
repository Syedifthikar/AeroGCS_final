package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public enum UsbSerialProber {
    FTDI_SERIAL { // from class: com.hoho.android.usbserial.driver.UsbSerialProber.1
        @Override // com.hoho.android.usbserial.driver.UsbSerialProber
        public List<UsbSerialDriver> probe(UsbManager manager, UsbDevice usbDevice) {
            if (!UsbSerialProber.testIfSupported(usbDevice, FtdiSerialDriver.getSupportedDevices())) {
                return Collections.emptyList();
            }
            UsbSerialDriver driver = new FtdiSerialDriver(usbDevice);
            return Collections.singletonList(driver);
        }
    },
    CDC_ACM_SERIAL { // from class: com.hoho.android.usbserial.driver.UsbSerialProber.2
        @Override // com.hoho.android.usbserial.driver.UsbSerialProber
        public List<UsbSerialDriver> probe(UsbManager manager, UsbDevice usbDevice) {
            if (!UsbSerialProber.testIfSupported(usbDevice, CdcAcmSerialDriver.getSupportedDevices())) {
                return Collections.emptyList();
            }
            UsbSerialDriver driver = new CdcAcmSerialDriver(usbDevice);
            return Collections.singletonList(driver);
        }
    },
    SILAB_SERIAL { // from class: com.hoho.android.usbserial.driver.UsbSerialProber.3
        @Override // com.hoho.android.usbserial.driver.UsbSerialProber
        public List<UsbSerialDriver> probe(UsbManager manager, UsbDevice usbDevice) {
            if (!UsbSerialProber.testIfSupported(usbDevice, Cp2102SerialDriver.getSupportedDevices())) {
                return Collections.emptyList();
            }
            UsbSerialDriver driver = new Cp2102SerialDriver(usbDevice);
            return Collections.singletonList(driver);
        }
    },
    PROLIFIC_SERIAL { // from class: com.hoho.android.usbserial.driver.UsbSerialProber.4
        @Override // com.hoho.android.usbserial.driver.UsbSerialProber
        public List<UsbSerialDriver> probe(UsbManager manager, UsbDevice usbDevice) {
            if (!UsbSerialProber.testIfSupported(usbDevice, ProlificSerialDriver.getSupportedDevices())) {
                return Collections.emptyList();
            }
            UsbSerialDriver driver = new ProlificSerialDriver(usbDevice);
            return Collections.singletonList(driver);
        }
    };

    protected abstract List<UsbSerialDriver> probe(UsbManager usbManager, UsbDevice usbDevice);

    public static UsbSerialDriver findFirstDevice(UsbManager usbManager) {
        UsbSerialProber[] values;
        for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            for (UsbSerialProber prober : values()) {
                List<UsbSerialDriver> probedDevices = prober.probe(usbManager, usbDevice);
                if (!probedDevices.isEmpty()) {
                    return probedDevices.get(0);
                }
            }
        }
        return null;
    }

    public static List<UsbSerialDriver> findAllDevices(UsbManager usbManager) {
        List<UsbSerialDriver> result = new ArrayList<>();
        for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            result.addAll(probeSingleDevice(usbManager, usbDevice));
        }
        return result;
    }

    public static List<UsbSerialDriver> probeSingleDevice(UsbManager usbManager, UsbDevice usbDevice) {
        UsbSerialProber[] values;
        List<UsbSerialDriver> result = new ArrayList<>();
        for (UsbSerialProber prober : values()) {
            List<UsbSerialDriver> probedDevices = prober.probe(usbManager, usbDevice);
            result.addAll(probedDevices);
        }
        return result;
    }

    @Deprecated
    public static UsbSerialDriver acquire(UsbManager usbManager) {
        return findFirstDevice(usbManager);
    }

    @Deprecated
    public static UsbSerialDriver acquire(UsbManager usbManager, UsbDevice usbDevice) {
        List<UsbSerialDriver> probedDevices = probeSingleDevice(usbManager, usbDevice);
        if (!probedDevices.isEmpty()) {
            return probedDevices.get(0);
        }
        return null;
    }

    public static boolean testIfSupported(UsbDevice usbDevice, Map<Integer, int[]> supportedDevices) {
        return supportedDevices.containsKey(Integer.valueOf(usbDevice.getVendorId()));
    }
}
