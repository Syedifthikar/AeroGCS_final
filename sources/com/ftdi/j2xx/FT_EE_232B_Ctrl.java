package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_232B_Ctrl extends FT_EE_Ctrl {
    private static final short CHECKSUM_LOCATION = 63;
    private static final short EEPROM_SIZE = 64;
    private static FT_Device ft_device;

    public FT_EE_232B_Ctrl(FT_Device usbC) {
        super(usbC);
        ft_device = usbC;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        int[] data = new int[64];
        if (ee.getClass() != FT_EEPROM.class) {
            return (short) 1;
        }
        for (short i = 0; i < 64; i = (short) (i + 1)) {
            try {
                data[i] = readWord(i);
            } catch (Exception e) {
                e.printStackTrace();
                return (short) 0;
            }
        }
        data[1] = ee.VendorId;
        data[2] = ee.ProductId;
        data[3] = ft_device.mDeviceInfoNode.bcdDevice;
        data[4] = setUSBConfig(ee);
        int saddr = setStringDescriptor(ee.Manufacturer, data, 10, 7, true);
        int saddr2 = setStringDescriptor(ee.Product, data, saddr, 8, true);
        if (ee.SerNumEnable) {
            setStringDescriptor(ee.SerialNumber, data, saddr2, 9, true);
        }
        if (data[1] == 0 || data[2] == 0) {
            return (short) 2;
        }
        boolean returnCode = programEeprom(data, 63);
        return returnCode ? (short) 0 : (short) 1;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM eeprom = new FT_EEPROM();
        int[] data = new int[64];
        for (int i = 0; i < 64; i++) {
            try {
                data[i] = readWord((short) i);
            } catch (Exception e) {
                return null;
            }
        }
        eeprom.VendorId = (short) data[1];
        eeprom.ProductId = (short) data[2];
        getUSBConfig(eeprom, data[4]);
        eeprom.Manufacturer = getStringDescriptor(10, data);
        int addr = 10 + eeprom.Manufacturer.length() + 1;
        eeprom.Product = getStringDescriptor(addr, data);
        eeprom.SerialNumber = getStringDescriptor(addr + eeprom.Product.length() + 1, data);
        return eeprom;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int getUserSize() {
        int data = readWord((short) 7);
        int ptr07 = (data & 65280) >> 8;
        int data2 = readWord((short) 8);
        int ptr08 = (data2 & 65280) >> 8;
        int ptr = (ptr07 / 2) + 10 + (ptr08 / 2) + 1;
        int data3 = readWord((short) 9);
        int length = (65280 & data3) >> 8;
        return (((63 - ptr) - 1) - (length / 2)) * 2;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int writeUserData(byte[] data) {
        int dataWrite;
        if (data.length > getUserSize()) {
            return 0;
        }
        int[] eeprom = new int[64];
        for (short i = 0; i < 64; i = (short) (i + 1)) {
            eeprom[i] = readWord(i);
        }
        short offset = (short) ((63 - (getUserSize() / 2)) - 1);
        short offset2 = (short) (65535 & offset);
        int i2 = 0;
        while (i2 < data.length) {
            if (i2 + 1 < data.length) {
                dataWrite = data[i2 + 1] & 255;
            } else {
                dataWrite = 0;
            }
            eeprom[offset2] = (dataWrite << 8) | (data[i2] & 255);
            i2 += 2;
            offset2 = (short) (offset2 + 1);
        }
        int i3 = eeprom[1];
        if (i3 == 0 || eeprom[2] == 0) {
            return 0;
        }
        boolean returnCode = programEeprom(eeprom, 63);
        if (returnCode) {
            return data.length;
        }
        return 0;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public byte[] readUserData(int length) {
        byte[] data = new byte[length];
        if (length == 0 || length > getUserSize()) {
            return null;
        }
        short offset = (short) ((63 - (getUserSize() / 2)) - 1);
        short offset2 = (short) (65535 & offset);
        int i = 0;
        while (i < length) {
            short offset3 = (short) (offset2 + 1);
            int dataRead = readWord(offset2);
            if (i + 1 < data.length) {
                byte Hi = (byte) (dataRead & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
                data[i + 1] = Hi;
            }
            byte Lo = (byte) ((65280 & dataRead) >> 8);
            data[i] = Lo;
            i += 2;
            offset2 = offset3;
        }
        return data;
    }
}
