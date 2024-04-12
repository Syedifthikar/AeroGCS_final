package com.ftdi.j2xx;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_2232_Ctrl extends FT_EE_Ctrl {
    private static final short CHECKSUM_LOCATION = 63;
    private static final String DEFAULT_PID = "6010";
    private static final byte EEPROM_SIZE_LOCATION = 10;

    public FT_EE_2232_Ctrl(FT_Device usbC) throws D2xxManager.D2xxException {
        super(usbC);
        getEepromSize(EEPROM_SIZE_LOCATION);
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        boolean eeprom46;
        int offset;
        int[] data = new int[this.mEepromSize];
        if (ee.getClass() != FT_EEPROM_2232D.class) {
            return (short) 1;
        }
        FT_EEPROM_2232D eeprom = (FT_EEPROM_2232D) ee;
        try {
            data[0] = 0;
            if (eeprom.A_FIFO) {
                data[0] = data[0] | 1;
            } else if (eeprom.A_FIFOTarget) {
                data[0] = data[0] | 2;
            } else {
                data[0] = data[0] | 4;
            }
            if (eeprom.A_HighIO) {
                data[0] = data[0] | 16;
            }
            if (eeprom.A_LoadVCP) {
                data[0] = data[0] | 8;
            } else if (eeprom.B_FIFO) {
                data[0] = data[0] | 256;
            } else if (eeprom.B_FIFOTarget) {
                data[0] = data[0] | 512;
            } else {
                data[0] = data[0] | 1024;
            }
            if (eeprom.B_HighIO) {
                data[0] = data[0] | 4096;
            }
            if (eeprom.B_LoadVCP) {
                data[0] = data[0] | 2048;
            }
            data[1] = eeprom.VendorId;
            data[2] = eeprom.ProductId;
            data[3] = 1280;
            data[4] = setUSBConfig(ee);
            data[4] = setDeviceControl(ee);
            if (this.mEepromType != 70) {
                eeprom46 = false;
                offset = 75;
            } else {
                eeprom46 = true;
                offset = 11;
            }
            int offset2 = setStringDescriptor(eeprom.Product, data, setStringDescriptor(eeprom.Manufacturer, data, offset, 7, eeprom46), 8, eeprom46);
            if (eeprom.SerNumEnable) {
                setStringDescriptor(eeprom.SerialNumber, data, offset2, 9, eeprom46);
            }
            data[10] = this.mEepromType;
            if (data[1] == 0 || data[2] == 0) {
                return (short) 2;
            }
            boolean returnCode = programEeprom(data, this.mEepromSize - 1);
            return returnCode ? (short) 0 : (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM_2232D eeprom = new FT_EEPROM_2232D();
        int[] dataRead = new int[this.mEepromSize];
        for (int i = 0; i < this.mEepromSize; i++) {
            try {
                dataRead[i] = readWord((short) i);
            } catch (Exception e) {
                return null;
            }
        }
        short data02x00 = (short) (dataRead[0] & 7);
        if (data02x00 == 0) {
            eeprom.A_UART = true;
        } else if (data02x00 == 1) {
            eeprom.A_FIFO = true;
        } else if (data02x00 != 2) {
            if (data02x00 == 4) {
                eeprom.A_FastSerial = true;
            }
        } else {
            eeprom.A_FIFOTarget = true;
        }
        short data3x00 = (short) ((dataRead[0] & 8) >> 3);
        if (data3x00 == 1) {
            eeprom.A_LoadVCP = true;
        } else {
            eeprom.A_HighIO = true;
        }
        short data4x00 = (short) ((dataRead[0] & 16) >> 4);
        if (data4x00 == 1) {
            eeprom.A_HighIO = true;
        }
        short data810x00 = (short) ((dataRead[0] & 1792) >> 8);
        if (data810x00 == 0) {
            eeprom.B_UART = true;
        } else if (data810x00 == 1) {
            eeprom.B_FIFO = true;
        } else if (data810x00 != 2) {
            if (data810x00 == 4) {
                eeprom.B_FastSerial = true;
            }
        } else {
            eeprom.B_FIFOTarget = true;
        }
        short data11x00 = (short) ((dataRead[0] & 2048) >> 11);
        if (data11x00 == 1) {
            eeprom.B_LoadVCP = true;
        } else {
            eeprom.B_LoadD2XX = true;
        }
        short data12x00 = (short) ((dataRead[0] & 4096) >> 12);
        if (data12x00 == 1) {
            eeprom.B_HighIO = true;
        }
        eeprom.VendorId = (short) dataRead[1];
        eeprom.ProductId = (short) dataRead[2];
        getUSBConfig(eeprom, dataRead[4]);
        int addr = dataRead[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (this.mEepromType == 70) {
            eeprom.Manufacturer = getStringDescriptor((addr - 128) / 2, dataRead);
            eeprom.Product = getStringDescriptor(((dataRead[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, dataRead);
            eeprom.SerialNumber = getStringDescriptor(((dataRead[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, dataRead);
        } else {
            eeprom.Manufacturer = getStringDescriptor(addr / 2, dataRead);
            eeprom.Product = getStringDescriptor((dataRead[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, dataRead);
            eeprom.SerialNumber = getStringDescriptor((dataRead[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, dataRead);
        }
        return eeprom;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int getUserSize() {
        int data = readWord((short) 9);
        int ptr = data & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        int length = (65280 & data) >> 8;
        return (((this.mEepromSize - 1) - 1) - (ptr + (length / 2))) * 2;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int writeUserData(byte[] data) {
        int dataWrite;
        if (data.length > getUserSize()) {
            return 0;
        }
        int[] eeprom = new int[this.mEepromSize];
        for (short i = 0; i < this.mEepromSize; i = (short) (i + 1)) {
            eeprom[i] = readWord(i);
        }
        short offset = (short) (((this.mEepromSize - (getUserSize() / 2)) - 1) - 1);
        int i2 = 0;
        while (i2 < data.length) {
            if (i2 + 1 < data.length) {
                dataWrite = data[i2 + 1] & 255;
            } else {
                dataWrite = 0;
            }
            eeprom[offset] = (dataWrite << 8) | (data[i2] & 255);
            i2 += 2;
            offset = (short) (offset + 1);
        }
        int i3 = eeprom[1];
        if (i3 == 0 || eeprom[2] == 0) {
            return 0;
        }
        boolean returnCode = programEeprom(eeprom, this.mEepromSize - 1);
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
        short offset = (short) (((this.mEepromSize - (getUserSize() / 2)) - 1) - 1);
        int i = 0;
        while (i < length) {
            short offset2 = (short) (offset + 1);
            int dataRead = readWord(offset);
            if (i + 1 < data.length) {
                byte Hi = (byte) (dataRead & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
                data[i + 1] = Hi;
            }
            byte Lo = (byte) ((65280 & dataRead) >> 8);
            data[i] = Lo;
            i += 2;
            offset = offset2;
        }
        return data;
    }
}
