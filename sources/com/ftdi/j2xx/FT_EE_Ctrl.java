package com.ftdi.j2xx;

import android.util.Log;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class FT_EE_Ctrl {
    private static final int BUS_POWERED = 128;
    private static final short EE_MAX_SIZE = 1024;
    private static final int ENABLE_SERIAL_NUMBER = 8;
    private static final int PULL_DOWN_IN_USB_SUSPEND = 4;
    private static final int SELF_POWERED = 64;
    private static final int USB_REMOTE_WAKEUP = 32;
    private FT_Device mDevice;
    boolean mEepromBlank;
    int mEepromSize;
    short mEepromType;

    /* loaded from: classes.dex */
    static final class EepromType {
        static final short INVALID = 255;
        static final short TYPE_46 = 70;
        static final short TYPE_52 = 82;
        static final short TYPE_56 = 86;
        static final short TYPE_66 = 102;
        static final short TYPE_MTP = 1;

        EepromType() {
        }
    }

    public FT_EE_Ctrl(FT_Device dev) {
        this.mDevice = dev;
    }

    public int readWord(short offset) {
        byte[] dataRead = new byte[2];
        if (offset >= 1024) {
            return -1;
        }
        this.mDevice.getConnection().controlTransfer(-64, 144, 0, offset, dataRead, 2, 0);
        int value = dataRead[1] & 255;
        return (value << 8) | (dataRead[0] & 255);
    }

    public boolean writeWord(short offset, short value) {
        int wValue = value & 65535;
        int wIndex = 65535 & offset;
        if (offset >= 1024) {
            return false;
        }
        int status = this.mDevice.getConnection().controlTransfer(64, 145, wValue, wIndex, null, 0, 0);
        return status == 0;
    }

    public int eraseEeprom() {
        int status = this.mDevice.getConnection().controlTransfer(64, 146, 0, 0, null, 0, 0);
        return status;
    }

    public short programEeprom(FT_EEPROM eeprom) {
        return (short) 1;
    }

    public boolean programEeprom(int[] dataToWrite, int ee_size) {
        int Checksum = 43690;
        int addressCounter = 0;
        while (addressCounter < ee_size) {
            writeWord((short) addressCounter, (short) dataToWrite[addressCounter]);
            int TempChecksum = (dataToWrite[addressCounter] ^ Checksum) & 65535;
            int a = (short) ((TempChecksum << 1) & 65535);
            int b = (short) ((TempChecksum >> 15) & 65535);
            int Checksum2 = a | b;
            Checksum = Checksum2 & 65535;
            addressCounter++;
            Log.d("FT_EE_Ctrl", "Entered WriteWord Checksum : " + Checksum);
        }
        writeWord((short) ee_size, (short) Checksum);
        return true;
    }

    public FT_EEPROM readEeprom() {
        return null;
    }

    public int setUSBConfig(Object ee) {
        FT_EEPROM ft = (FT_EEPROM) ee;
        int lowerbits = 0 | 128;
        if (ft.RemoteWakeup) {
            lowerbits |= USB_REMOTE_WAKEUP;
        }
        if (ft.SelfPowered) {
            lowerbits |= 64;
        }
        int upperbits = ft.MaxPower;
        int word0x04 = ((upperbits / 2) << 8) | lowerbits;
        return word0x04;
    }

    public void getUSBConfig(FT_EEPROM ee, int dataRead) {
        byte mP = (byte) (dataRead >> 8);
        ee.MaxPower = (short) (mP * 2);
        byte P = (byte) dataRead;
        if ((P & 64) == 64 && (P & D2xxManager.FT_DCD) == 128) {
            ee.SelfPowered = true;
        } else {
            ee.SelfPowered = false;
        }
        if ((P & 32) == USB_REMOTE_WAKEUP) {
            ee.RemoteWakeup = true;
        } else {
            ee.RemoteWakeup = false;
        }
    }

    public int setDeviceControl(Object ee) {
        int data;
        FT_EEPROM ft = (FT_EEPROM) ee;
        if (ft.PullDownEnable) {
            data = 0 | 4;
        } else {
            data = 0 & 251;
        }
        if (ft.SerNumEnable) {
            return data | 8;
        }
        return data & 247;
    }

    public void getDeviceControl(Object ee, int dataRead) {
        FT_EEPROM ft = (FT_EEPROM) ee;
        if ((dataRead & 4) > 0) {
            ft.PullDownEnable = true;
        } else {
            ft.PullDownEnable = false;
        }
        if ((dataRead & 8) > 0) {
            ft.SerNumEnable = true;
        } else {
            ft.SerNumEnable = false;
        }
    }

    public int setStringDescriptor(String s, int[] data, int addrs, int pointer, boolean rdevice) {
        int i = 0;
        int strLength = (s.length() * 2) + 2;
        data[pointer] = (strLength << 8) | (addrs * 2);
        if (rdevice) {
            data[pointer] = data[pointer] + 128;
        }
        char[] strchar = s.toCharArray();
        int addrs2 = addrs + 1;
        data[addrs] = strLength | 768;
        int strLength2 = (strLength - 2) / 2;
        while (true) {
            int addrs3 = addrs2 + 1;
            data[addrs2] = strchar[i];
            i++;
            if (i < strLength2) {
                addrs2 = addrs3;
            } else {
                return addrs3;
            }
        }
    }

    public String getStringDescriptor(int addr, int[] dataRead) {
        String descriptor = BuildConfig.FLAVOR;
        int len = dataRead[addr] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        int addr2 = addr + 1;
        int endaddr = addr2 + ((len / 2) - 1);
        for (int i = addr2; i < endaddr; i++) {
            descriptor = String.valueOf(descriptor) + ((char) dataRead[i]);
        }
        return descriptor;
    }

    void clearUserDataArea(int saddr, int eeprom_size, int[] data) {
        while (saddr < eeprom_size) {
            data[saddr] = 0;
            saddr++;
        }
    }

    public int getEepromSize(byte location) throws D2xxManager.D2xxException {
        short address = (short) (location & (-1));
        int[] dataRead = new int[3];
        int eeData = (short) readWord(address);
        if (eeData == 65535) {
            boolean rc = writeWord((short) 192, (short) 192);
            dataRead[0] = readWord((short) 192);
            dataRead[1] = readWord((short) 64);
            dataRead[2] = readWord((short) 0);
            if (!rc) {
                this.mEepromType = (short) 255;
                this.mEepromSize = 0;
                return 0;
            }
            this.mEepromBlank = true;
            int wordRead = readWord((short) 0);
            if ((wordRead & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) == 192) {
                eraseEeprom();
                this.mEepromType = (short) 70;
                this.mEepromSize = 64;
                return 64;
            }
            int wordRead2 = readWord((short) 64);
            if ((wordRead2 & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) == 192) {
                eraseEeprom();
                this.mEepromType = (short) 86;
                this.mEepromSize = 128;
                return 128;
            }
            int wordRead3 = readWord((short) 192);
            if ((wordRead3 & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) == 192) {
                eraseEeprom();
                this.mEepromType = (short) 102;
                this.mEepromSize = 128;
                return 256;
            }
            eraseEeprom();
            return 0;
        } else if (eeData == 70) {
            this.mEepromType = (short) 70;
            this.mEepromSize = 64;
            this.mEepromBlank = false;
            return 64;
        } else if (eeData == 82) {
            this.mEepromType = (short) 82;
            this.mEepromSize = 1024;
            this.mEepromBlank = false;
            return 1024;
        } else if (eeData == 86) {
            this.mEepromType = (short) 86;
            this.mEepromSize = 128;
            this.mEepromBlank = false;
            return 128;
        } else if (eeData != 102) {
            return 0;
        } else {
            this.mEepromType = (short) 102;
            this.mEepromSize = 128;
            this.mEepromBlank = false;
            return 256;
        }
    }

    public int writeUserData(byte[] data) {
        return 0;
    }

    public byte[] readUserData(int length) {
        return null;
    }

    public int getUserSize() {
        return 0;
    }
}
