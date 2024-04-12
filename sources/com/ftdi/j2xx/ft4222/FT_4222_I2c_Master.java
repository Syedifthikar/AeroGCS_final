package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.I2cMaster;

/* loaded from: classes.dex */
public class FT_4222_I2c_Master implements I2cMaster {
    FT_4222_Device mFt4222Dev;
    FT_Device mFtDev;
    int mI2cMasterKbps;

    public FT_4222_I2c_Master(FT_4222_Device ft4222Device) {
        this.mFt4222Dev = ft4222Device;
        this.mFtDev = ft4222Device.mFtDev;
    }

    int cmdSet(int wValue1, int wValue2) {
        return this.mFtDev.VendorCmdSet(33, (wValue2 << 8) | wValue1);
    }

    int cmdSet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.mFtDev.VendorCmdSet(33, (wValue2 << 8) | wValue1, buf, datalen);
    }

    int cmdGet(int wValue1, int wValue2, byte[] buf, int datalen) {
        return this.mFtDev.VendorCmdGet(32, (wValue2 << 8) | wValue1, buf, datalen);
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int init(int kbps) {
        byte[] clk = new byte[1];
        int ftStatus = this.mFt4222Dev.init();
        if (ftStatus != 0) {
            return ftStatus;
        }
        if (!I2C_Mode_Check()) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_I2C_NOT_SUPPORTED_IN_THIS_MODE;
        }
        cmdSet(81, 0);
        int ftStatus2 = this.mFt4222Dev.getClock(clk);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        int i2cMP = i2c_master_setup_timer_period(clk[0], kbps);
        int ftStatus3 = cmdSet(5, 1);
        if (ftStatus3 >= 0) {
            this.mFt4222Dev.mChipStatus.function = (byte) 1;
            int ftStatus4 = cmdSet(82, i2cMP);
            if (ftStatus4 < 0) {
                return ftStatus4;
            }
            this.mI2cMasterKbps = kbps;
            return 0;
        }
        return ftStatus3;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int reset() {
        int ftStatus = I2C_Check(true);
        if (ftStatus == 0) {
            return cmdSet(81, 1);
        }
        return ftStatus;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int read(int deviceAddress, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        return readEx(deviceAddress, 6, buffer, sizeToTransfer, sizeTransferred);
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int readEx(int deviceAddress, int flag, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        short slave_addr = (short) (deviceAddress & 65535);
        short shortSizeToTransfer = (short) sizeToTransfer;
        int[] maxSize = new int[1];
        byte[] headBuf = new byte[4];
        long startTime = System.currentTimeMillis();
        int iTimeout = this.mFtDev.getReadTimeout();
        int ftStatus = I2C_Version_Check(flag);
        if (ftStatus != 0) {
            return ftStatus;
        }
        int ftStatus2 = I2C_Address_Check(deviceAddress);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        if (sizeToTransfer < 1) {
            return 6;
        }
        int ftStatus3 = I2C_Check(true);
        if (ftStatus3 != 0) {
            return ftStatus3;
        }
        int ftStatus4 = getMaxTransferSize(maxSize);
        if (ftStatus4 != 0) {
            return ftStatus4;
        }
        if (sizeToTransfer > maxSize[0]) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        headBuf[0] = (byte) ((slave_addr << 1) + 1);
        headBuf[1] = (byte) flag;
        headBuf[2] = (byte) ((shortSizeToTransfer >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        headBuf[3] = (byte) (shortSizeToTransfer & 255);
        if (4 != this.mFtDev.write(headBuf, 4)) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
        }
        int dataSize = this.mFtDev.getQueueStatus();
        while (dataSize < sizeToTransfer && System.currentTimeMillis() - startTime < iTimeout) {
            dataSize = this.mFtDev.getQueueStatus();
        }
        if (dataSize > sizeToTransfer) {
            dataSize = sizeToTransfer;
        }
        int ftStatus5 = this.mFtDev.read(buffer, dataSize);
        sizeTransferred[0] = ftStatus5;
        if (ftStatus5 >= 0) {
            return 0;
        }
        return FT_4222_Defines.FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int write(int deviceAddress, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        return writeEx(deviceAddress, 6, buffer, sizeToTransfer, sizeTransferred);
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int writeEx(int deviceAddress, int flag, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        short slave_addr = (short) deviceAddress;
        short shortSizeToTransfer = (short) sizeToTransfer;
        byte[] transferBuf = new byte[sizeToTransfer + 4];
        int[] maxSize = new int[1];
        int ftStatus = I2C_Version_Check(flag);
        if (ftStatus != 0) {
            return ftStatus;
        }
        int ftStatus2 = I2C_Address_Check(deviceAddress);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        if (sizeToTransfer < 1) {
            return 6;
        }
        int ftStatus3 = I2C_Check(true);
        if (ftStatus3 != 0) {
            return ftStatus3;
        }
        int ftStatus4 = getMaxTransferSize(maxSize);
        if (ftStatus4 != 0) {
            return ftStatus4;
        }
        if (sizeToTransfer > maxSize[0]) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        transferBuf[0] = (byte) (slave_addr << 1);
        transferBuf[1] = (byte) flag;
        transferBuf[2] = (byte) ((shortSizeToTransfer >> 8) & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        transferBuf[3] = (byte) (shortSizeToTransfer & 255);
        for (int i = 0; i < sizeToTransfer; i++) {
            transferBuf[i + 4] = buffer[i];
        }
        sizeTransferred[0] = this.mFtDev.write(transferBuf, sizeToTransfer + 4) - 4;
        if (sizeToTransfer == sizeTransferred[0]) {
            return 0;
        }
        return 10;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cMaster
    public int getStatus(int deviceAddress, byte[] controllerStatus) {
        int ftStatus = I2C_Check(true);
        if (ftStatus != 0) {
            return ftStatus;
        }
        if (this.mFtDev.VendorCmdGet(34, 62900, controllerStatus, 1) < 0) {
            return 18;
        }
        return 0;
    }

    boolean I2C_Mode_Check() {
        if (this.mFt4222Dev.mChipStatus.chip_mode == 0 || this.mFt4222Dev.mChipStatus.chip_mode == 3) {
            return true;
        }
        return false;
    }

    int I2C_Check(boolean isMaster) {
        if (isMaster) {
            if (this.mFt4222Dev.mChipStatus.function != 1) {
                return FT_4222_Defines.FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
            }
            return 0;
        } else if (this.mFt4222Dev.mChipStatus.function != 2) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
        } else {
            return 0;
        }
    }

    int I2C_Version_Check(int flag) {
        FT_Device fT_Device = this.mFtDev;
        if (fT_Device == null || !fT_Device.isOpen()) {
            return 3;
        }
        if (flag != 6) {
            char[] fwVer = new char[1];
            getFWVersion(fwVer);
            if (fwVer[0] < 'B') {
                return FT_4222_Defines.FT4222_STATUS.FT4222_FUN_NOT_SUPPORT;
            }
        }
        return 0;
    }

    int I2C_Address_Check(int deviceAddress) {
        if ((64512 & deviceAddress) > 0) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_WRONG_I2C_ADDR;
        }
        return 0;
    }

    private int i2c_master_setup_timer_period(int CLK_CTRL, int kbps) {
        double CLK_PRD;
        if (CLK_CTRL != 1) {
            if (CLK_CTRL != 2) {
                if (CLK_CTRL != 3) {
                    CLK_PRD = 16.666666666666668d;
                } else {
                    CLK_PRD = 12.5d;
                }
            } else {
                CLK_PRD = 20.833333333333332d;
            }
        } else {
            CLK_PRD = 41.666666666666664d;
        }
        if (60 <= kbps && kbps <= 100) {
            double SCL_PERIOD = 1000000.0d / kbps;
            int TIMER_PRD = (int) (((SCL_PERIOD / (8.0d * CLK_PRD)) - 1.0d) + 0.5d);
            if (TIMER_PRD > 127) {
                TIMER_PRD = 127;
            }
            int I2CMTP = TIMER_PRD;
            return I2CMTP;
        } else if (100 >= kbps || kbps > 400) {
            if (400 < kbps && kbps <= 1000) {
                double SCL_PERIOD2 = 1000000.0d / kbps;
                int TIMER_PRD2 = (int) (((SCL_PERIOD2 / (6.0d * CLK_PRD)) - 1.0d) + 0.5d);
                int I2CMTP2 = TIMER_PRD2 | 192;
                return I2CMTP2;
            } else if (1000 < kbps && kbps <= 3400) {
                double SCL_PERIOD3 = 1000000.0d / kbps;
                int TIMER_PRD3 = (int) (((SCL_PERIOD3 / (6.0d * CLK_PRD)) - 1.0d) + 0.5d);
                int I2CMTP3 = TIMER_PRD3 | 128;
                return I2CMTP3 & (-65);
            } else {
                return 74;
            }
        } else {
            double SCL_PERIOD4 = 1000000.0d / kbps;
            int TIMER_PRD4 = (int) (((SCL_PERIOD4 / (6.0d * CLK_PRD)) - 1.0d) + 0.5d);
            int I2CMTP4 = TIMER_PRD4 | 192;
            return I2CMTP4;
        }
    }

    int getMaxTransferSize(int[] pMaxSize) {
        pMaxSize[0] = 0;
        int maxBuckSize = this.mFt4222Dev.getMaxBuckSize();
        if (this.mFt4222Dev.mChipStatus.function == 1) {
            pMaxSize[0] = maxBuckSize - 4;
            return 0;
        }
        return 17;
    }

    int getFWVersion(char[] ver) {
        byte[] bVer = new byte[12];
        int ftStatus = this.mFtDev.VendorCmdGet(32, 0, bVer, 12);
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
