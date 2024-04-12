package com.ftdi.j2xx;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_232H_Ctrl extends FT_EE_Ctrl {
    private static final int AL_DRIVE_CURRENT = 3;
    private static final int AL_FAST_SLEW = 4;
    private static final int AL_SCHMITT_INPUT = 8;
    private static final int BL_DRIVE_CURRENT = 768;
    private static final int BL_FAST_SLEW = 1024;
    private static final int BL_SCHMITT_INPUT = 2048;
    private static final String DEFAULT_PID = "6014";
    private static final byte EEPROM_SIZE_LOCATION = 15;
    private static FT_Device ft_device;

    public FT_EE_232H_Ctrl(FT_Device usbc) throws D2xxManager.D2xxException {
        super(usbc);
        getEepromSize(EEPROM_SIZE_LOCATION);
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        int offset;
        int[] dataToWrite = new int[this.mEepromSize];
        if (ee.getClass() != FT_EEPROM_232H.class) {
            return (short) 1;
        }
        FT_EEPROM_232H eeprom = (FT_EEPROM_232H) ee;
        try {
            if (eeprom.FIFO) {
                dataToWrite[0] = dataToWrite[0] | 1;
            } else if (eeprom.FIFOTarget) {
                dataToWrite[0] = dataToWrite[0] | 2;
            } else if (eeprom.FastSerial) {
                dataToWrite[0] = dataToWrite[0] | 4;
            }
            if (eeprom.FT1248) {
                dataToWrite[0] = dataToWrite[0] | 8;
            }
            if (eeprom.LoadVCP) {
                dataToWrite[0] = dataToWrite[0] | 16;
            }
            if (eeprom.FT1248ClockPolarity) {
                dataToWrite[0] = dataToWrite[0] | 256;
            }
            if (eeprom.FT1248LSB) {
                dataToWrite[0] = dataToWrite[0] | 512;
            }
            if (eeprom.FT1248FlowControl) {
                dataToWrite[0] = dataToWrite[0] | BL_FAST_SLEW;
            }
            if (eeprom.PowerSaveEnable) {
                dataToWrite[0] = dataToWrite[0] | 32768;
            }
            dataToWrite[1] = eeprom.VendorId;
            dataToWrite[2] = eeprom.ProductId;
            dataToWrite[3] = 2304;
            dataToWrite[4] = setUSBConfig(ee);
            dataToWrite[5] = setDeviceControl(ee);
            byte b = eeprom.AL_DriveCurrent;
            if (b == -1) {
                b = 0;
            }
            dataToWrite[6] = dataToWrite[6] | b;
            if (eeprom.AL_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | 4;
            }
            if (eeprom.AL_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 8;
            }
            short driveC = eeprom.BL_DriveCurrent;
            if (driveC == -1) {
                driveC = 0;
            }
            dataToWrite[6] = dataToWrite[6] | ((short) (driveC << 8));
            if (eeprom.BL_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | BL_FAST_SLEW;
            }
            if (eeprom.BL_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | BL_SCHMITT_INPUT;
            }
            int offset2 = setStringDescriptor(eeprom.Product, dataToWrite, setStringDescriptor(eeprom.Manufacturer, dataToWrite, 80, 7, false), 8, false);
            if (!eeprom.SerNumEnable) {
                offset = offset2;
            } else {
                offset = setStringDescriptor(eeprom.SerialNumber, dataToWrite, offset2, 9, false);
            }
            dataToWrite[10] = 0;
            dataToWrite[11] = 0;
            dataToWrite[12] = 0;
            int c0 = eeprom.CBus0;
            int c1 = eeprom.CBus1;
            int c2 = eeprom.CBus2;
            int c3 = eeprom.CBus3;
            dataToWrite[12] = c0 | (c1 << 4) | (c2 << 8) | (c3 << 12);
            dataToWrite[13] = 0;
            int c4 = eeprom.CBus4;
            int c5 = eeprom.CBus5;
            int c6 = eeprom.CBus6;
            int c7 = eeprom.CBus7;
            int c72 = c7 << 12;
            int c73 = c4 | (c5 << 4);
            dataToWrite[13] = c73 | (c6 << 8) | c72;
            dataToWrite[14] = 0;
            int c8 = eeprom.CBus8;
            int c9 = eeprom.CBus9;
            dataToWrite[14] = c8 | (c9 << 4);
            short driveA = this.mEepromType;
            dataToWrite[15] = driveA;
            dataToWrite[69] = 72;
            if (this.mEepromType == 70) {
                return (short) 1;
            }
            if (dataToWrite[1] != 0 && dataToWrite[2] != 0) {
                boolean returnCode = programEeprom(dataToWrite, this.mEepromSize - 1);
                return returnCode ? (short) 0 : (short) 1;
            }
            return (short) 2;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM_232H eeprom = new FT_EEPROM_232H();
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
        eeprom.UART = false;
        int i2 = data[0] & 15;
        if (i2 == 0) {
            eeprom.UART = true;
        } else if (i2 == 1) {
            eeprom.FIFO = true;
        } else if (i2 == 2) {
            eeprom.FIFOTarget = true;
        } else if (i2 == 4) {
            eeprom.FastSerial = true;
        } else if (i2 == 8) {
            eeprom.FT1248 = true;
        } else {
            eeprom.UART = true;
        }
        if ((data[0] & 16) > 0) {
            eeprom.LoadVCP = true;
            eeprom.LoadD2XX = false;
        } else {
            eeprom.LoadVCP = false;
            eeprom.LoadD2XX = true;
        }
        if ((data[0] & 256) > 0) {
            eeprom.FT1248ClockPolarity = true;
        } else {
            eeprom.FT1248ClockPolarity = false;
        }
        if ((data[0] & 512) > 0) {
            eeprom.FT1248LSB = true;
        } else {
            eeprom.FT1248LSB = false;
        }
        if ((data[0] & BL_FAST_SLEW) > 0) {
            eeprom.FT1248FlowControl = true;
        } else {
            eeprom.FT1248FlowControl = false;
        }
        if ((data[0] & 32768) > 0) {
            eeprom.PowerSaveEnable = true;
        }
        eeprom.VendorId = (short) data[1];
        eeprom.ProductId = (short) data[2];
        getUSBConfig(eeprom, data[4]);
        getDeviceControl(eeprom, data[5]);
        int data01x06 = data[6] & 3;
        if (data01x06 == 0) {
            eeprom.AL_DriveCurrent = (byte) 0;
        } else if (data01x06 == 1) {
            eeprom.AL_DriveCurrent = (byte) 1;
        } else if (data01x06 == 2) {
            eeprom.AL_DriveCurrent = (byte) 2;
        } else if (data01x06 == 3) {
            eeprom.AL_DriveCurrent = (byte) 3;
        }
        if ((data[6] & 4) > 0) {
            eeprom.AL_SlowSlew = true;
        } else {
            eeprom.AL_SlowSlew = false;
        }
        if ((data[6] & 8) > 0) {
            eeprom.AL_SchmittInput = true;
        } else {
            eeprom.AL_SchmittInput = false;
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
        if ((data[6] & BL_FAST_SLEW) > 0) {
            eeprom.BL_SlowSlew = true;
        } else {
            eeprom.BL_SlowSlew = false;
        }
        if ((data[6] & BL_SCHMITT_INPUT) > 0) {
            eeprom.BL_SchmittInput = true;
        } else {
            eeprom.BL_SchmittInput = false;
        }
        short cbus0 = (short) ((data[12] >> 0) & 15);
        eeprom.CBus0 = (byte) cbus0;
        short cbus1 = (short) ((data[12] >> 4) & 15);
        eeprom.CBus1 = (byte) cbus1;
        short cbus2 = (short) ((data[12] >> 8) & 15);
        eeprom.CBus2 = (byte) cbus2;
        short cbus3 = (short) ((data[12] >> 12) & 15);
        eeprom.CBus3 = (byte) cbus3;
        short cbus4 = (short) ((data[13] >> 0) & 15);
        eeprom.CBus4 = (byte) cbus4;
        short cbus5 = (short) ((data[13] >> 4) & 15);
        eeprom.CBus5 = (byte) cbus5;
        short cbus6 = (short) ((data[13] >> 8) & 15);
        eeprom.CBus6 = (byte) cbus6;
        short cbus7 = (short) ((data[13] >> 12) & 15);
        eeprom.CBus7 = (byte) cbus7;
        short cbus8 = (short) ((data[14] >> 0) & 15);
        eeprom.CBus8 = (byte) cbus8;
        short cbus9 = (short) ((data[14] >> 4) & 15);
        eeprom.CBus9 = (byte) cbus9;
        int addr = data[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Manufacturer = getStringDescriptor(addr / 2, data);
        int addr2 = data[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Product = getStringDescriptor(addr2 / 2, data);
        int addr3 = data[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.SerialNumber = getStringDescriptor(addr3 / 2, data);
        return eeprom;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public int getUserSize() {
        int data = readWord((short) 9);
        int ptr = data & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        int length = (65280 & data) >> 8;
        return (((this.mEepromSize - ((ptr / 2) + 1)) - 1) - ((length / 2) + 1)) * 2;
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
