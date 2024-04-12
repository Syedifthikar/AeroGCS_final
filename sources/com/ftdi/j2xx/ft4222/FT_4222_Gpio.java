package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.Gpio;

/* loaded from: classes.dex */
public class FT_4222_Gpio implements Gpio {
    static final int GET_DIRECTION = 33;
    static final int GET_OPEN_DRAIN = 35;
    static final int GET_PULL_DOWN = 36;
    static final int GET_PULL_UP = 34;
    static final int GET_STATUS = 32;
    static final int SET_DIRECTION = 33;
    static final int SET_OPEN_DRAIN = 35;
    static final int SET_PULL_DOWN = 36;
    static final int SET_PULL_UP = 34;
    private static final int TOTAL_GPIOS = 4;
    private FT_4222_Device mFT4222Device;
    private FT_Device mFtDev;

    public FT_4222_Gpio(FT_4222_Device ft4222Device) {
        this.mFT4222Device = ft4222Device;
        this.mFtDev = ft4222Device.mFtDev;
    }

    int cmdSet(int wValue1, int wValue2) {
        return this.mFtDev.VendorCmdSet(33, (wValue2 << 8) | wValue1);
    }

    int cmdSet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.mFtDev.VendorCmdSet(33, (wValue2 << 8) | wValue1, buf, datalen);
    }

    int cmdGet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.mFtDev.VendorCmdGet(GET_STATUS, (wValue2 << 8) | wValue1, buf, datalen);
    }

    @Override // com.ftdi.j2xx.interfaces.Gpio
    public int init(int[] gpio) {
        int i;
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        gpio_dev gpioStatus = new gpio_dev(cFwVer);
        byte[] data = new byte[1];
        gpio_mgr gpioMgr = new gpio_mgr();
        cmdSet(7, 0);
        cmdSet(6, 0);
        int ftStatus = this.mFT4222Device.init();
        if (ftStatus != 0) {
            Log.e("GPIO_M", "FT4222_GPIO init - 1 NG ftStatus:" + ftStatus);
            return ftStatus;
        } else if (chipStatus.chip_mode == 2 || chipStatus.chip_mode == 3) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        } else {
            getStatus(gpioStatus);
            byte dir = gpioStatus.dir;
            data[0] = gpioStatus.dat[0];
            for (int idx = 0; idx < 4; idx++) {
                if (gpio[idx] == 1) {
                    i = (1 << idx) | dir;
                } else {
                    i = (~(1 << idx)) & dir;
                }
                dir = (byte) (i & 15);
            }
            gpioMgr.lastGpioData = data[0];
            cmdSet(33, dir);
            return 0;
        }
    }

    @Override // com.ftdi.j2xx.interfaces.Gpio
    public int read(int portNum, boolean[] bValue) {
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        gpio_dev gpioStatus = new gpio_dev(cFwVer);
        int ftStatus = check(portNum);
        if (ftStatus != 0) {
            return ftStatus;
        }
        int ftStatus2 = getStatus(gpioStatus);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        getGpioPinLevel(portNum, gpioStatus.dat[0], bValue);
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.Gpio
    public int write(int portNum, boolean bValue) {
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        gpio_dev gpioStatus = new gpio_dev(cFwVer);
        int ftStatus = check(portNum);
        if (ftStatus != 0) {
            return ftStatus;
        }
        if (!is_GPIOPort_Valid_Output(portNum)) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_GPIO_WRITE_NOT_SUPPORTED;
        }
        getStatus(gpioStatus);
        if (bValue) {
            byte[] bArr = gpioStatus.dat;
            bArr[0] = (byte) (bArr[0] | (1 << portNum));
        } else {
            byte[] bArr2 = gpioStatus.dat;
            bArr2[0] = (byte) (bArr2[0] & (~(1 << portNum)) & 15);
        }
        int status = this.mFtDev.write(gpioStatus.dat, 1);
        return status;
    }

    int check(int portNum) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        if (chipStatus.chip_mode == 2 || chipStatus.chip_mode == 3) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_GPIO_NOT_SUPPORTED_IN_THIS_MODE;
        }
        if (portNum >= 4) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_GPIO_EXCEEDED_MAX_PORTNUM;
        }
        return 0;
    }

    int getStatus(gpio_dev gpioStatus) {
        byte[] buf;
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        if (cFwVer[0] < 'B') {
            buf = new byte[8];
        } else {
            buf = new byte[6];
        }
        int ftStatus = cmdGet(GET_STATUS, 0, buf, buf.length);
        gpioStatus.usb.ep_in = buf[0];
        gpioStatus.usb.ep_out = buf[1];
        gpioStatus.mask = buf[buf.length - 3];
        gpioStatus.dir = buf[buf.length - 2];
        gpioStatus.dat[0] = buf[buf.length - 1];
        if (ftStatus == buf.length) {
            return 0;
        }
        return ftStatus;
    }

    void getGpioPinLevel(int portNum, byte data, boolean[] value) {
        value[0] = IntToBool(1 & (((1 << portNum) & data) >> portNum));
    }

    boolean is_GPIOPort(int portNum) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        boolean ret = true;
        byte b = chipStatus.chip_mode;
        if (b == 0) {
            if ((portNum == 0 || portNum == 1) && (chipStatus.function == 1 || chipStatus.function == 2)) {
                ret = false;
            }
            if (IntToBool(chipStatus.enable_suspend_out) && portNum == 2) {
                ret = false;
            }
            if (IntToBool(chipStatus.enable_wakeup_int) && portNum == 3) {
                return false;
            }
            return ret;
        } else if (b != 1) {
            if (b != 2 && b != 3) {
                return true;
            }
            return false;
        } else {
            ret = (portNum == 0 || portNum == 1) ? false : false;
            if (IntToBool(chipStatus.enable_suspend_out) && portNum == 2) {
                ret = false;
            }
            if (IntToBool(chipStatus.enable_wakeup_int) && portNum == 3) {
                return false;
            }
            return ret;
        }
    }

    boolean is_GPIOPort_Valid_Output(int portNum) {
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        gpio_dev gpioStatus = new gpio_dev(cFwVer);
        boolean ret = is_GPIOPort(portNum);
        getStatus(gpioStatus);
        if (ret && ((gpioStatus.dir >> portNum) & 1) != 1) {
            return false;
        }
        return ret;
    }

    boolean is_GPIOPort_Valid_Input(int portNum) {
        char[] cFwVer = new char[1];
        getFWVersion(cFwVer);
        gpio_dev gpioStatus = new gpio_dev(cFwVer);
        boolean ret = is_GPIOPort(portNum);
        getStatus(gpioStatus);
        if (ret && (1 & (gpioStatus.dir >> portNum)) != 0) {
            return false;
        }
        return ret;
    }

    boolean update_GPIO_Status(int portNum, int gpioStatus) {
        gpio_mgr gpio = new gpio_mgr();
        if (gpio.gpioStatus[portNum] != gpioStatus) {
            char pullup = 0;
            char pulldown = 0;
            char opendrain = 0;
            gpio.gpioStatus[portNum] = gpioStatus;
            for (int idx = 0; idx < 4; idx++) {
                int i = gpio.gpioStatus[idx];
                if (i == 1) {
                    pullup = (char) ((1 << idx) + pullup);
                } else if (i == 2) {
                    pulldown = (char) ((1 << idx) + pulldown);
                } else if (i == 3) {
                    opendrain = (char) ((1 << idx) + opendrain);
                }
            }
            int ftStatus = cmdSet(34, pullup) | cmdSet(36, pulldown) | cmdSet(35, opendrain);
            if (ftStatus == 0) {
                gpio.gpioStatus[portNum] = gpioStatus;
            }
            return ftStatus == 0;
        }
        return true;
    }

    boolean IntToBool(int i) {
        return i != 0;
    }

    int getFWVersion(char[] ver) {
        byte[] bVer = new byte[12];
        int ftStatus = this.mFtDev.VendorCmdGet(GET_STATUS, 0, bVer, 12);
        if (ftStatus < 0) {
            return 18;
        }
        if (bVer[2] != 1) {
            if (bVer[2] == 2) {
                ver[0] = 'B';
            }
        } else {
            ver[0] = 'A';
        }
        return 0;
    }
}
