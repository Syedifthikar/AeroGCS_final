package com.ftdi.j2xx;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_2232H_Ctrl extends FT_EE_Ctrl {
    private static final int AH_DRIVE_CURRENT = 48;
    private static final int AH_FAST_SLEW = 64;
    private static final int AH_SCHMITT_INPUT = 128;
    private static final int AL_DRIVE_CURRENT = 3;
    private static final int AL_FAST_SLEW = 4;
    private static final int AL_SCHMITT_INPUT = 8;
    private static final int A_245_FIFO = 1;
    private static final int A_245_FIFO_TARGET = 2;
    private static final int A_FAST_SERIAL = 4;
    private static final int A_LOAD_VCP_DRIVER = 8;
    private static final int A_UART_RS232 = 0;
    private static final int BH_DRIVE_CURRENT = 12288;
    private static final int BH_FAST_SLEW = 16384;
    private static final int BH_SCHMITT_INPUT = 32768;
    private static final int BL_DRIVE_CURRENT = 768;
    private static final int BL_FAST_SLEW = 1024;
    private static final int BL_SCHMITT_INPUT = 2048;
    private static final String DEFAULT_PID = "6010";
    private static final byte EEPROM_SIZE_LOCATION = 12;
    private static final int INVERT_CTS = 2048;
    private static final int INVERT_DCD = 16384;
    private static final int INVERT_DSR = 8192;
    private static final int INVERT_DTR = 4096;
    private static final int INVERT_RI = 32768;
    private static final int INVERT_RTS = 1024;
    private static final int INVERT_RXD = 512;
    private static final int INVERT_TXD = 256;
    private static final int TPRDRV = 24;

    public FT_EE_2232H_Ctrl(FT_Device usbC) throws D2xxManager.D2xxException {
        super(usbC);
        getEepromSize(EEPROM_SIZE_LOCATION);
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        int offset;
        int[] dataToWrite = new int[this.mEepromSize];
        if (ee.getClass() != FT_EEPROM_2232H.class) {
            return (short) 1;
        }
        FT_EEPROM_2232H eeprom = (FT_EEPROM_2232H) ee;
        try {
            if (!eeprom.A_UART) {
                if (eeprom.A_FIFO) {
                    dataToWrite[0] = dataToWrite[0] | 1;
                } else if (eeprom.A_FIFOTarget) {
                    dataToWrite[0] = dataToWrite[0] | 2;
                } else {
                    dataToWrite[0] = dataToWrite[0] | 4;
                }
            }
            if (eeprom.A_LoadVCP) {
                dataToWrite[0] = dataToWrite[0] | 8;
            }
            if (!eeprom.B_UART) {
                if (eeprom.B_FIFO) {
                    dataToWrite[0] = dataToWrite[0] | INVERT_TXD;
                } else if (eeprom.B_FIFOTarget) {
                    dataToWrite[0] = dataToWrite[0] | INVERT_RXD;
                } else {
                    dataToWrite[0] = dataToWrite[0] | 1024;
                }
            }
            if (eeprom.B_LoadVCP) {
                dataToWrite[0] = dataToWrite[0] | 2048;
            }
            if (eeprom.PowerSaveEnable) {
                dataToWrite[0] = dataToWrite[0] | 32768;
            }
            dataToWrite[1] = eeprom.VendorId;
            dataToWrite[2] = eeprom.ProductId;
            dataToWrite[3] = 1792;
            dataToWrite[4] = setUSBConfig(ee);
            dataToWrite[5] = setDeviceControl(ee);
            dataToWrite[6] = 0;
            short driveA = eeprom.AL_DriveCurrent;
            if (driveA == -1) {
                driveA = 0;
            }
            dataToWrite[6] = dataToWrite[6] | driveA;
            if (eeprom.AL_SlowSlew) {
                dataToWrite[6] = 4 | dataToWrite[6];
            }
            if (eeprom.AL_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 8;
            }
            short driveB = eeprom.AH_DriveCurrent;
            if (driveB == -1) {
                driveB = 0;
            }
            dataToWrite[6] = dataToWrite[6] | ((short) (driveB << 4));
            if (eeprom.AH_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | 64;
            }
            if (eeprom.AH_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 128;
            }
            short driveC = eeprom.BL_DriveCurrent;
            if (driveC == -1) {
                driveC = 0;
            }
            dataToWrite[6] = dataToWrite[6] | ((short) (driveC << 8));
            if (eeprom.BL_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | 1024;
            }
            if (eeprom.BL_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 2048;
            }
            short driveD = eeprom.BH_DriveCurrent;
            dataToWrite[6] = dataToWrite[6] | ((short) (driveD << 12));
            if (eeprom.BH_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | 16384;
            }
            if (eeprom.BH_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 32768;
            }
            boolean eeprom46 = false;
            if (this.mEepromType != 70) {
                offset = 77;
            } else {
                eeprom46 = true;
                offset = 13;
            }
            int offset2 = setStringDescriptor(eeprom.Product, dataToWrite, setStringDescriptor(eeprom.Manufacturer, dataToWrite, offset, 7, eeprom46), 8, eeprom46);
            if (eeprom.SerNumEnable) {
                setStringDescriptor(eeprom.SerialNumber, dataToWrite, offset2, 9, eeprom46);
            }
            int i = eeprom.TPRDRV;
            if (i == 0) {
                dataToWrite[11] = 0;
            } else if (i == 1) {
                dataToWrite[11] = 8;
            } else if (i == 2) {
                dataToWrite[11] = 16;
            } else if (i == 3) {
                dataToWrite[11] = TPRDRV;
            } else {
                dataToWrite[11] = 0;
            }
            dataToWrite[12] = this.mEepromType;
            if (dataToWrite[1] == 0 || dataToWrite[2] == 0) {
                return (short) 2;
            }
            boolean returnCode = programEeprom(dataToWrite, this.mEepromSize - 1);
            return returnCode ? (short) 0 : (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM_2232H eeprom = new FT_EEPROM_2232H();
        int[] data = new int[this.mEepromSize];
        if (this.mEepromBlank) {
            return eeprom;
        }
        for (short i = 0; i < this.mEepromSize; i = (short) (i + 1)) {
            try {
                data[i] = readWord(i);
            } catch (Exception e) {
                return null;
            }
        }
        int wordx00 = data[0];
        short data02x00 = (short) (wordx00 & 7);
        if (data02x00 == 0) {
            eeprom.A_UART = true;
        } else if (data02x00 == 1) {
            eeprom.A_FIFO = true;
        } else if (data02x00 != 2) {
            if (data02x00 == 4) {
                eeprom.A_FastSerial = true;
            } else {
                eeprom.A_UART = true;
            }
        } else {
            eeprom.A_FIFOTarget = true;
        }
        short data3x00 = (short) ((wordx00 & 8) >> 3);
        if (data3x00 == 1) {
            eeprom.A_LoadVCP = true;
            eeprom.A_LoadD2XX = false;
        } else {
            eeprom.A_LoadVCP = false;
            eeprom.A_LoadD2XX = true;
        }
        short data810x00 = (short) ((wordx00 & 1792) >> 8);
        if (data810x00 == 0) {
            eeprom.B_UART = true;
        } else if (data810x00 == 1) {
            eeprom.B_FIFO = true;
        } else if (data810x00 != 2) {
            if (data810x00 == 4) {
                eeprom.B_FastSerial = true;
            } else {
                eeprom.B_UART = true;
            }
        } else {
            eeprom.B_FIFOTarget = true;
        }
        short data11x00 = (short) ((wordx00 & 2048) >> 11);
        if (data11x00 == 1) {
            eeprom.B_LoadVCP = true;
            eeprom.B_LoadD2XX = false;
        } else {
            eeprom.B_LoadVCP = false;
            eeprom.B_LoadD2XX = true;
        }
        short data15x00 = (short) ((wordx00 & 32768) >> 15);
        if (data15x00 == 1) {
            eeprom.PowerSaveEnable = true;
        } else {
            eeprom.PowerSaveEnable = false;
        }
        eeprom.VendorId = (short) data[1];
        eeprom.ProductId = (short) data[2];
        getUSBConfig(eeprom, data[4]);
        getDeviceControl(eeprom, data[5]);
        short data01x06 = (short) (data[6] & 3);
        if (data01x06 == 0) {
            eeprom.AL_DriveCurrent = (byte) 0;
        } else if (data01x06 == 1) {
            eeprom.AL_DriveCurrent = (byte) 1;
        } else if (data01x06 == 2) {
            eeprom.AL_DriveCurrent = (byte) 2;
        } else if (data01x06 == 3) {
            eeprom.AL_DriveCurrent = (byte) 3;
        }
        short data2x06 = (short) (data[6] & 4);
        if (data2x06 == 4) {
            eeprom.AL_SlowSlew = true;
        } else {
            eeprom.AL_SlowSlew = false;
        }
        short data3x06 = (short) (data[6] & 8);
        if (data3x06 == 8) {
            eeprom.AL_SchmittInput = true;
        } else {
            eeprom.AL_SchmittInput = false;
        }
        short data45x06 = (short) ((data[6] & AH_DRIVE_CURRENT) >> 4);
        if (data45x06 == 0) {
            eeprom.AH_DriveCurrent = (byte) 0;
        } else if (data45x06 == 1) {
            eeprom.AH_DriveCurrent = (byte) 1;
        } else if (data45x06 == 2) {
            eeprom.AH_DriveCurrent = (byte) 2;
        } else if (data45x06 == 3) {
            eeprom.AH_DriveCurrent = (byte) 3;
        }
        short data6x06 = (short) (data[6] & 64);
        if (data6x06 == 64) {
            eeprom.AH_SlowSlew = true;
        } else {
            eeprom.AH_SlowSlew = false;
        }
        short data7x06 = (short) (data[6] & 128);
        if (data7x06 == 128) {
            eeprom.AH_SchmittInput = true;
        } else {
            eeprom.AH_SchmittInput = false;
        }
        short data89X06 = (short) ((data[6] & BL_DRIVE_CURRENT) >> 8);
        if (data89X06 == 0) {
            eeprom.BL_DriveCurrent = (byte) 0;
        } else if (data89X06 == 1) {
            eeprom.BL_DriveCurrent = (byte) 1;
        } else if (data89X06 == 2) {
            eeprom.BL_DriveCurrent = (byte) 2;
        } else if (data89X06 == 3) {
            eeprom.BL_DriveCurrent = (byte) 3;
        }
        short data10x06 = (short) (data[6] & 1024);
        if (data10x06 == 1024) {
            eeprom.BL_SlowSlew = true;
        } else {
            eeprom.BL_SlowSlew = false;
        }
        short s = (short) (data[6] & 2048);
        if (data7x06 == 2048) {
            eeprom.BL_SchmittInput = true;
        } else {
            eeprom.BL_SchmittInput = false;
        }
        short data1213X06 = (short) ((data[6] & BH_DRIVE_CURRENT) >> 12);
        if (data1213X06 == 0) {
            eeprom.BH_DriveCurrent = (byte) 0;
        } else if (data1213X06 == 1) {
            eeprom.BH_DriveCurrent = (byte) 1;
        } else if (data1213X06 == 2) {
            eeprom.BH_DriveCurrent = (byte) 2;
        } else if (data1213X06 == 3) {
            eeprom.BH_DriveCurrent = (byte) 3;
        }
        short data14x06 = (short) (data[6] & 16384);
        if (data14x06 == 16384) {
            eeprom.BH_SlowSlew = true;
        } else {
            eeprom.BH_SlowSlew = false;
        }
        short data15x06 = (short) (data[6] & 32768);
        if (data15x06 == 32768) {
            eeprom.BH_SchmittInput = true;
        } else {
            eeprom.BH_SchmittInput = false;
        }
        short datax0B = (short) ((data[11] & TPRDRV) >> 3);
        if (datax0B < 4) {
            eeprom.TPRDRV = datax0B;
        } else {
            eeprom.TPRDRV = 0;
        }
        int addr = data[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (this.mEepromType == 70) {
            eeprom.Manufacturer = getStringDescriptor((addr - 128) / 2, data);
            eeprom.Product = getStringDescriptor(((data[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, data);
            eeprom.SerialNumber = getStringDescriptor(((data[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, data);
        } else {
            eeprom.Manufacturer = getStringDescriptor(addr / 2, data);
            eeprom.Product = getStringDescriptor((data[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, data);
            eeprom.SerialNumber = getStringDescriptor((data[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) / 2, data);
        }
        return eeprom;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int getUserSize() {
        int data = readWord((short) 9);
        int ptr = data & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        int length = (65280 & data) >> 8;
        return (((this.mEepromSize - 1) - 1) - (((ptr / 2) + (length / 2)) + 1)) * 2;
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
