package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_245R_Ctrl extends FT_EE_Ctrl {
    private static final short EEPROM_SIZE = 80;
    private static final short EE_MAX_SIZE = 1024;
    private static final short ENDOFUSERLOCATION = 63;
    private static final int EXTERNAL_OSCILLATOR = 2;
    private static final int HIGH_CURRENT_IO = 4;
    private static final int INVERT_CTS = 2048;
    private static final int INVERT_DCD = 16384;
    private static final int INVERT_DSR = 8192;
    private static final int INVERT_DTR = 4096;
    private static final int INVERT_RI = 32768;
    private static final int INVERT_RTS = 1024;
    private static final int INVERT_RXD = 512;
    private static final int INVERT_TXD = 256;
    private static final int LOAD_D2XX_DRIVER = 8;
    private static FT_Device ft_device;

    public FT_EE_245R_Ctrl(FT_Device usbC) {
        super(usbC);
        ft_device = usbC;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public boolean writeWord(short offset, short value) {
        int wValue = value & 65535;
        int wIndex = 65535 & offset;
        if (offset >= INVERT_RTS) {
            return false;
        }
        byte latency = ft_device.getLatencyTimer();
        ft_device.setLatencyTimer((byte) 119);
        int status = ft_device.getConnection().controlTransfer(64, 145, wValue, wIndex, null, 0, 0);
        boolean rc = status == 0;
        ft_device.setLatencyTimer(latency);
        return rc;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        int wordx00;
        int wordx05;
        int[] data = new int[80];
        if (ee.getClass() != FT_EEPROM_245R.class) {
            return (short) 1;
        }
        FT_EEPROM_245R eeprom = (FT_EEPROM_245R) ee;
        for (short i = 0; i < 80; i = (short) (i + 1)) {
            try {
                data[i] = readWord(i);
            } catch (Exception e) {
                e.printStackTrace();
                return (short) 0;
            }
        }
        int wordx002 = 0 | (data[0] & 65280);
        if (eeprom.HighIO) {
            wordx002 |= 4;
        }
        if (eeprom.LoadVCP) {
            wordx002 |= 8;
        }
        if (eeprom.ExternalOscillator) {
            wordx00 = wordx002 | 2;
        } else {
            wordx00 = wordx002 & 65533;
        }
        data[0] = wordx00;
        data[1] = eeprom.VendorId;
        data[2] = eeprom.ProductId;
        data[3] = 1536;
        data[4] = setUSBConfig(ee);
        int wordx052 = setDeviceControl(ee);
        if (eeprom.InvertTXD) {
            wordx052 |= INVERT_TXD;
        }
        if (eeprom.InvertRXD) {
            wordx052 |= INVERT_RXD;
        }
        if (eeprom.InvertRTS) {
            wordx052 |= INVERT_RTS;
        }
        if (eeprom.InvertCTS) {
            wordx052 |= INVERT_CTS;
        }
        if (eeprom.InvertDTR) {
            wordx052 |= INVERT_DTR;
        }
        if (eeprom.InvertDSR) {
            wordx052 |= INVERT_DSR;
        }
        if (eeprom.InvertDCD) {
            wordx052 |= 16384;
        }
        if (!eeprom.InvertRI) {
            wordx05 = wordx052;
        } else {
            wordx05 = wordx052 | INVERT_RI;
        }
        data[5] = wordx05;
        int c0 = eeprom.CBus0;
        int c1 = eeprom.CBus1;
        int c2 = eeprom.CBus2;
        int c22 = c2 << 8;
        int c23 = eeprom.CBus3;
        int c3 = c23 << 12;
        int c32 = c0 | (c1 << 4);
        int wordx0A = c32 | c22 | c3;
        data[10] = wordx0A;
        int c4 = eeprom.CBus4;
        data[11] = c4;
        int saddr = setStringDescriptor(eeprom.Manufacturer, data, 12, 7, true);
        int saddr2 = setStringDescriptor(eeprom.Product, data, saddr, 8, true);
        if (eeprom.SerNumEnable) {
            setStringDescriptor(eeprom.SerialNumber, data, saddr2, 9, true);
        }
        if (data[1] == 0 || data[2] == 0) {
            return (short) 2;
        }
        byte latency = ft_device.getLatencyTimer();
        ft_device.setLatencyTimer((byte) 119);
        boolean returnCode = programEeprom(data, 80);
        ft_device.setLatencyTimer(latency);
        return returnCode ? (short) 0 : (short) 1;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM_245R eeprom = new FT_EEPROM_245R();
        int[] data = new int[80];
        for (int i = 0; i < 80; i++) {
            try {
                data[i] = readWord((short) i);
            } catch (Exception e) {
                return null;
            }
        }
        if ((data[0] & 4) == 4) {
            eeprom.HighIO = true;
        } else {
            eeprom.HighIO = false;
        }
        if ((data[0] & 8) == 8) {
            eeprom.LoadVCP = true;
        } else {
            eeprom.LoadVCP = false;
        }
        if ((data[0] & 2) == 2) {
            eeprom.ExternalOscillator = true;
        } else {
            eeprom.ExternalOscillator = false;
        }
        eeprom.VendorId = (short) data[1];
        eeprom.ProductId = (short) data[2];
        getUSBConfig(eeprom, data[4]);
        getDeviceControl(eeprom, data[5]);
        if ((data[5] & INVERT_TXD) == INVERT_TXD) {
            eeprom.InvertTXD = true;
        } else {
            eeprom.InvertTXD = false;
        }
        if ((data[5] & INVERT_RXD) == INVERT_RXD) {
            eeprom.InvertRXD = true;
        } else {
            eeprom.InvertRXD = false;
        }
        if ((data[5] & INVERT_RTS) == INVERT_RTS) {
            eeprom.InvertRTS = true;
        } else {
            eeprom.InvertRTS = false;
        }
        if ((data[5] & INVERT_CTS) == INVERT_CTS) {
            eeprom.InvertCTS = true;
        } else {
            eeprom.InvertCTS = false;
        }
        if ((data[5] & INVERT_DTR) == INVERT_DTR) {
            eeprom.InvertDTR = true;
        } else {
            eeprom.InvertDTR = false;
        }
        if ((data[5] & INVERT_DSR) == INVERT_DSR) {
            eeprom.InvertDSR = true;
        } else {
            eeprom.InvertDSR = false;
        }
        if ((data[5] & 16384) == 16384) {
            eeprom.InvertDCD = true;
        } else {
            eeprom.InvertDCD = false;
        }
        if ((data[5] & INVERT_RI) == INVERT_RI) {
            eeprom.InvertRI = true;
        } else {
            eeprom.InvertRI = false;
        }
        int temp = data[10];
        int cbus0 = temp & 15;
        eeprom.CBus0 = (byte) cbus0;
        int cbus1 = temp & 240;
        eeprom.CBus1 = (byte) (cbus1 >> 4);
        int cbus2 = temp & 3840;
        eeprom.CBus2 = (byte) (cbus2 >> 8);
        int cbus3 = 61440 & temp;
        eeprom.CBus3 = (byte) (cbus3 >> 12);
        int cbus4 = data[11] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.CBus4 = (byte) cbus4;
        int addr = data[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Manufacturer = getStringDescriptor((addr - 128) / 2, data);
        int addr2 = data[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Product = getStringDescriptor((addr2 - 128) / 2, data);
        int addr3 = data[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.SerialNumber = getStringDescriptor((addr3 - 128) / 2, data);
        return eeprom;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int getUserSize() {
        int data = readWord((short) 7);
        int ptr07 = (data & 65280) >> 8;
        int data2 = readWord((short) 8);
        int ptr08 = (data2 & 65280) >> 8;
        int ptr = (ptr07 / 2) + 12 + (ptr08 / 2) + 1;
        int data3 = readWord((short) 9);
        int length = (65280 & data3) >> 8;
        return (((63 - ptr) - (length / 2)) - 1) * 2;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int writeUserData(byte[] data) {
        int dataWrite;
        if (data.length > getUserSize()) {
            return 0;
        }
        int[] eeprom = new int[80];
        for (short i = 0; i < 80; i = (short) (i + 1)) {
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
        if (eeprom[1] == 0 || eeprom[2] == 0) {
            return 0;
        }
        byte latency = ft_device.getLatencyTimer();
        ft_device.setLatencyTimer((byte) 119);
        boolean returnCode = programEeprom(eeprom, 63);
        ft_device.setLatencyTimer(latency);
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
