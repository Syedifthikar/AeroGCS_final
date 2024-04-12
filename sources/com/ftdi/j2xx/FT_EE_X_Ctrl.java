package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* loaded from: classes.dex */
public class FT_EE_X_Ctrl extends FT_EE_Ctrl {
    private static final int BCD_ENABLE = 1;
    private static final int CBUS_DRIVE = 48;
    private static final int CBUS_SCHMITT = 128;
    private static final int CBUS_SLEW = 64;
    private static final int DBUS_DRIVE = 3;
    private static final int DBUS_SCHMITT = 8;
    private static final int DBUS_SLEW = 4;
    private static final int DEACTIVATE_SLEEP = 4;
    private static final String DEFAULT_PID = "6015";
    private static final int DEVICE_TYPE_EE_LOC = 73;
    private static final short EE_MAX_SIZE = 1024;
    private static final byte FIFO = 1;
    private static final int FORCE_POWER_ENABLE = 2;
    private static final byte FT1248 = 2;
    private static final int FT1248_BIT_ORDER = 32;
    private static final int FT1248_CLK_POLARITY = 16;
    private static final int FT1248_FLOW_CTRL = 64;
    private static final byte I2C = 3;
    private static final int I2C_DISABLE_SCHMITT = 128;
    private static final int INVERT_CTS = 2048;
    private static final int INVERT_DCD = 16384;
    private static final int INVERT_DSR = 8192;
    private static final int INVERT_DTR = 4096;
    private static final int INVERT_RI = 32768;
    private static final int INVERT_RTS = 1024;
    private static final int INVERT_RXD = 512;
    private static final int INVERT_TXD = 256;
    private static final int LOAD_DRIVER = 128;
    private static final int RS485_ECHO = 8;
    private static final byte UART = 0;
    private static final int VBUS_SUSPEND = 64;
    private static FT_Device ft_device;

    public FT_EE_X_Ctrl(FT_Device usbC) {
        super(usbC);
        ft_device = usbC;
        this.mEepromSize = 128;
        this.mEepromType = (short) 1;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public short programEeprom(FT_EEPROM ee) {
        int[] dataToWrite = new int[this.mEepromSize];
        short counter = 0;
        if (ee.getClass() != FT_EEPROM_X_Series.class) {
            return (short) 1;
        }
        FT_EEPROM_X_Series eeprom = (FT_EEPROM_X_Series) ee;
        while (true) {
            dataToWrite[counter] = readWord(counter);
            short counter2 = (short) (counter + 1);
            if (counter2 >= this.mEepromSize) {
                break;
            }
            counter = counter2;
        }
        try {
            dataToWrite[0] = 0;
            if (eeprom.BCDEnable) {
                dataToWrite[0] = dataToWrite[0] | 1;
            }
            if (eeprom.BCDForceCBusPWREN) {
                dataToWrite[0] = dataToWrite[0] | 2;
            }
            if (eeprom.BCDDisableSleep) {
                dataToWrite[0] = dataToWrite[0] | 4;
            }
            if (eeprom.RS485EchoSuppress) {
                dataToWrite[0] = dataToWrite[0] | 8;
            }
            if (eeprom.A_LoadVCP) {
                dataToWrite[0] = dataToWrite[0] | 128;
            }
            if (eeprom.PowerSaveEnable) {
                boolean found = false;
                if (eeprom.CBus0 == 17) {
                    found = true;
                }
                if (eeprom.CBus1 == 17) {
                    found = true;
                }
                if (eeprom.CBus2 == 17) {
                    found = true;
                }
                if (eeprom.CBus3 == 17) {
                    found = true;
                }
                if (eeprom.CBus4 == 17) {
                    found = true;
                }
                if (eeprom.CBus5 == 17) {
                    found = true;
                }
                if (eeprom.CBus6 == 17) {
                    found = true;
                }
                if (!found) {
                    return (short) 1;
                }
                dataToWrite[0] = dataToWrite[0] | 64;
            }
            dataToWrite[1] = eeprom.VendorId;
            dataToWrite[2] = eeprom.ProductId;
            dataToWrite[3] = INVERT_DTR;
            dataToWrite[4] = setUSBConfig(ee);
            dataToWrite[5] = setDeviceControl(ee);
            if (eeprom.FT1248ClockPolarity) {
                dataToWrite[5] = dataToWrite[5] | 16;
            }
            if (eeprom.FT1248LSB) {
                dataToWrite[5] = dataToWrite[5] | FT1248_BIT_ORDER;
            }
            if (eeprom.FT1248FlowControl) {
                dataToWrite[5] = dataToWrite[5] | 64;
            }
            if (eeprom.I2CDisableSchmitt) {
                dataToWrite[5] = dataToWrite[5] | 128;
            }
            if (eeprom.InvertTXD) {
                dataToWrite[5] = dataToWrite[5] | INVERT_TXD;
            }
            if (eeprom.InvertRXD) {
                dataToWrite[5] = dataToWrite[5] | INVERT_RXD;
            }
            if (eeprom.InvertRTS) {
                dataToWrite[5] = dataToWrite[5] | INVERT_RTS;
            }
            if (eeprom.InvertCTS) {
                dataToWrite[5] = dataToWrite[5] | INVERT_CTS;
            }
            if (eeprom.InvertDTR) {
                dataToWrite[5] = dataToWrite[5] | INVERT_DTR;
            }
            if (eeprom.InvertDSR) {
                dataToWrite[5] = dataToWrite[5] | INVERT_DSR;
            }
            if (eeprom.InvertDCD) {
                dataToWrite[5] = dataToWrite[5] | 16384;
            }
            if (eeprom.InvertRI) {
                dataToWrite[5] = dataToWrite[5] | INVERT_RI;
            }
            dataToWrite[6] = 0;
            short driveA = eeprom.AD_DriveCurrent;
            if (driveA == -1) {
                driveA = 0;
            }
            dataToWrite[6] = dataToWrite[6] | driveA;
            if (eeprom.AD_SlowSlew) {
                dataToWrite[6] = 4 | dataToWrite[6];
            }
            if (eeprom.AD_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 8;
            }
            short driveC = eeprom.AC_DriveCurrent;
            if (driveC == -1) {
                driveC = 0;
            }
            dataToWrite[6] = dataToWrite[6] | ((short) (driveC << 4));
            if (eeprom.AC_SlowSlew) {
                dataToWrite[6] = dataToWrite[6] | 64;
            }
            if (eeprom.AC_SchmittInput) {
                dataToWrite[6] = dataToWrite[6] | 128;
            }
            int offset = setStringDescriptor(eeprom.Manufacturer, dataToWrite, 80, 7, false);
            int offset2 = setStringDescriptor(eeprom.Product, dataToWrite, offset, 8, false);
            if (eeprom.SerNumEnable) {
                setStringDescriptor(eeprom.SerialNumber, dataToWrite, offset2, 9, false);
            }
            dataToWrite[10] = eeprom.I2CSlaveAddress;
            dataToWrite[11] = eeprom.I2CDeviceID & 65535;
            dataToWrite[12] = eeprom.I2CDeviceID >> 16;
            int c0 = eeprom.CBus0;
            if (c0 == -1) {
                c0 = 0;
            }
            int c1 = eeprom.CBus1;
            if (c1 == -1) {
                c1 = 0;
            }
            dataToWrite[13] = (short) (c0 | (c1 << 8));
            int c2 = eeprom.CBus2;
            if (c2 == -1) {
                c2 = 0;
            }
            int c3 = eeprom.CBus3;
            if (c3 == -1) {
                c3 = 0;
            }
            dataToWrite[14] = (short) (c2 | (c3 << 8));
            int c4 = eeprom.CBus4;
            if (c4 == -1) {
                c4 = 0;
            }
            int c5 = eeprom.CBus5;
            if (c5 == -1) {
                c5 = 0;
            }
            dataToWrite[15] = (short) (c4 | (c5 << 8));
            int c6 = eeprom.CBus6;
            if (c6 == -1) {
                c6 = 0;
            }
            dataToWrite[16] = (short) c6;
            if (dataToWrite[1] == 0 || dataToWrite[2] == 0) {
                return (short) 2;
            }
            boolean returnCode = programXeeprom(dataToWrite, this.mEepromSize - 1);
            return returnCode ? (short) 0 : (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    boolean programXeeprom(int[] dataToWrite, int ee_size) {
        int b;
        int Checksum = 43690;
        int addressCounter = 0;
        do {
            int data = dataToWrite[addressCounter] & 65535;
            writeWord((short) addressCounter, (short) data);
            int TempChecksum = (data ^ Checksum) & 65535;
            int a = TempChecksum << 1;
            int a2 = a & 65535;
            if ((INVERT_RI & TempChecksum) > 0) {
                b = 1;
            } else {
                b = 0;
            }
            int Checksum2 = a2 | b;
            Checksum = Checksum2 & 65535;
            addressCounter++;
            if (addressCounter == 18) {
                addressCounter = 64;
                continue;
            }
        } while (addressCounter != ee_size);
        writeWord((short) ee_size, (short) Checksum);
        return true;
    }

    @Override // com.ftdi.j2xx.FT_EE_Ctrl
    public FT_EEPROM readEeprom() {
        FT_EEPROM_X_Series eeprom = new FT_EEPROM_X_Series();
        int[] dataRead = new int[this.mEepromSize];
        for (short i = 0; i < this.mEepromSize; i = (short) (i + 1)) {
            try {
                dataRead[i] = readWord(i);
            } catch (Exception e) {
                return null;
            }
        }
        if ((dataRead[0] & 1) > 0) {
            eeprom.BCDEnable = true;
        } else {
            eeprom.BCDEnable = false;
        }
        if ((dataRead[0] & 2) > 0) {
            eeprom.BCDForceCBusPWREN = true;
        } else {
            eeprom.BCDForceCBusPWREN = false;
        }
        if ((dataRead[0] & 4) > 0) {
            eeprom.BCDDisableSleep = true;
        } else {
            eeprom.BCDDisableSleep = false;
        }
        if ((dataRead[0] & 8) > 0) {
            eeprom.RS485EchoSuppress = true;
        } else {
            eeprom.RS485EchoSuppress = false;
        }
        if ((dataRead[0] & 64) > 0) {
            eeprom.PowerSaveEnable = true;
        } else {
            eeprom.PowerSaveEnable = false;
        }
        if ((dataRead[0] & 128) > 0) {
            eeprom.A_LoadVCP = true;
            eeprom.A_LoadD2XX = false;
        } else {
            eeprom.A_LoadVCP = false;
            eeprom.A_LoadD2XX = true;
        }
        eeprom.VendorId = (short) dataRead[1];
        eeprom.ProductId = (short) dataRead[2];
        getUSBConfig(eeprom, dataRead[4]);
        getDeviceControl(eeprom, dataRead[5]);
        if ((dataRead[5] & 16) > 0) {
            eeprom.FT1248ClockPolarity = true;
        } else {
            eeprom.FT1248ClockPolarity = false;
        }
        if ((dataRead[5] & FT1248_BIT_ORDER) > 0) {
            eeprom.FT1248LSB = true;
        } else {
            eeprom.FT1248LSB = false;
        }
        if ((dataRead[5] & 64) > 0) {
            eeprom.FT1248FlowControl = true;
        } else {
            eeprom.FT1248FlowControl = false;
        }
        if ((dataRead[5] & 128) > 0) {
            eeprom.I2CDisableSchmitt = true;
        } else {
            eeprom.I2CDisableSchmitt = false;
        }
        if ((dataRead[5] & INVERT_TXD) == INVERT_TXD) {
            eeprom.InvertTXD = true;
        } else {
            eeprom.InvertTXD = false;
        }
        if ((dataRead[5] & INVERT_RXD) == INVERT_RXD) {
            eeprom.InvertRXD = true;
        } else {
            eeprom.InvertRXD = false;
        }
        if ((dataRead[5] & INVERT_RTS) == INVERT_RTS) {
            eeprom.InvertRTS = true;
        } else {
            eeprom.InvertRTS = false;
        }
        if ((dataRead[5] & INVERT_CTS) == INVERT_CTS) {
            eeprom.InvertCTS = true;
        } else {
            eeprom.InvertCTS = false;
        }
        if ((dataRead[5] & INVERT_DTR) == INVERT_DTR) {
            eeprom.InvertDTR = true;
        } else {
            eeprom.InvertDTR = false;
        }
        if ((dataRead[5] & INVERT_DSR) == INVERT_DSR) {
            eeprom.InvertDSR = true;
        } else {
            eeprom.InvertDSR = false;
        }
        if ((dataRead[5] & 16384) == 16384) {
            eeprom.InvertDCD = true;
        } else {
            eeprom.InvertDCD = false;
        }
        if ((dataRead[5] & INVERT_RI) == INVERT_RI) {
            eeprom.InvertRI = true;
        } else {
            eeprom.InvertRI = false;
        }
        short data01x06 = (short) (dataRead[6] & 3);
        if (data01x06 == 0) {
            eeprom.AD_DriveCurrent = (byte) 0;
        } else if (data01x06 == 1) {
            eeprom.AD_DriveCurrent = (byte) 1;
        } else if (data01x06 == 2) {
            eeprom.AD_DriveCurrent = (byte) 2;
        } else if (data01x06 == 3) {
            eeprom.AD_DriveCurrent = (byte) 3;
        }
        short data2x06 = (short) (dataRead[6] & 4);
        if (data2x06 == 4) {
            eeprom.AD_SlowSlew = true;
        } else {
            eeprom.AD_SlowSlew = false;
        }
        short data3x06 = (short) (dataRead[6] & 8);
        if (data3x06 == 8) {
            eeprom.AD_SchmittInput = true;
        } else {
            eeprom.AD_SchmittInput = false;
        }
        short data45x06 = (short) ((dataRead[6] & CBUS_DRIVE) >> 4);
        if (data45x06 == 0) {
            eeprom.AC_DriveCurrent = (byte) 0;
        } else if (data45x06 == 1) {
            eeprom.AC_DriveCurrent = (byte) 1;
        } else if (data45x06 == 2) {
            eeprom.AC_DriveCurrent = (byte) 2;
        } else if (data45x06 == 3) {
            eeprom.AC_DriveCurrent = (byte) 3;
        }
        short data6x06 = (short) (dataRead[6] & 64);
        if (data6x06 == 64) {
            eeprom.AC_SlowSlew = true;
        } else {
            eeprom.AC_SlowSlew = false;
        }
        short data7x06 = (short) (dataRead[6] & 128);
        if (data7x06 == 128) {
            eeprom.AC_SchmittInput = true;
        } else {
            eeprom.AC_SchmittInput = false;
        }
        eeprom.I2CSlaveAddress = dataRead[10];
        eeprom.I2CDeviceID = dataRead[11];
        eeprom.I2CDeviceID |= (dataRead[12] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) << 16;
        eeprom.CBus0 = (byte) (dataRead[13] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus1 = (byte) ((dataRead[13] >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus2 = (byte) (dataRead[14] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus3 = (byte) ((dataRead[14] >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus4 = (byte) (dataRead[15] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus5 = (byte) ((dataRead[15] >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        eeprom.CBus6 = (byte) (dataRead[16] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        this.mEepromType = (short) (dataRead[73] >> 8);
        int addr = dataRead[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Manufacturer = getStringDescriptor(addr / 2, dataRead);
        int addr2 = dataRead[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.Product = getStringDescriptor(addr2 / 2, dataRead);
        int addr3 = dataRead[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        eeprom.SerialNumber = getStringDescriptor(addr3 / 2, dataRead);
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
        boolean returnCode = programXeeprom(eeprom, this.mEepromSize - 1);
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
