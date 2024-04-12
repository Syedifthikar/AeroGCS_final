package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.I2cSlave;

/* loaded from: classes.dex */
public class FT_4222_I2c_Slave implements I2cSlave {
    FT_4222_Device mFt4222Dev;
    FT_Device mFtDev;

    public FT_4222_I2c_Slave(FT_4222_Device ft4222Device) {
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

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int init() {
        int ftStatus = this.mFt4222Dev.init();
        if (ftStatus != 0) {
            return ftStatus;
        }
        if (!I2C_ModeCheck()) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_I2C_NOT_SUPPORTED_IN_THIS_MODE;
        }
        int ftStatus2 = cmdSet(5, 2);
        if (ftStatus2 < 0) {
            return ftStatus2;
        }
        this.mFt4222Dev.mChipStatus.function = (byte) 2;
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int reset() {
        int ftStatus = I2C_Check(false);
        if (ftStatus == 0) {
            return cmdSet(91, 1);
        }
        return ftStatus;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int getAddress(int[] addr) {
        byte[] bAddr = new byte[1];
        int ftStatus = I2C_Check(false);
        if (ftStatus != 0) {
            return ftStatus;
        }
        if (this.mFtDev.VendorCmdGet(33, 92, bAddr, 1) >= 0) {
            addr[0] = bAddr[0];
            return 0;
        }
        return 18;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int setAddress(int addr) {
        byte[] bAddr = {(byte) (addr & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST)};
        int ftStatus = I2C_Check(false);
        if (ftStatus != 0) {
            return ftStatus;
        }
        return cmdSet(92, bAddr[0]) < 0 ? 18 : 0;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int read(byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        int[] maxSize = new int[1];
        long startTime = System.currentTimeMillis();
        int iTimeout = this.mFtDev.getReadTimeout();
        if (sizeToTransfer < 1) {
            return 6;
        }
        int ftStatus = I2C_Check(false);
        if (ftStatus != 0) {
            return ftStatus;
        }
        int ftStatus2 = getMaxTransferSize(maxSize);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        if (sizeToTransfer > maxSize[0]) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        int dataSize = this.mFtDev.getQueueStatus();
        while (dataSize < sizeToTransfer && System.currentTimeMillis() - startTime < iTimeout) {
            dataSize = this.mFtDev.getQueueStatus();
        }
        if (dataSize > sizeToTransfer) {
            dataSize = sizeToTransfer;
        }
        int ftStatus3 = this.mFtDev.read(buffer, dataSize);
        if (ftStatus3 < 0) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
        }
        sizeTransferred[0] = ftStatus3;
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.I2cSlave
    public int write(byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        int[] maxSize = new int[1];
        if (sizeToTransfer < 1) {
            return 6;
        }
        int ftStatus = I2C_Check(false);
        if (ftStatus != 0) {
            return ftStatus;
        }
        int ftStatus2 = getMaxTransferSize(maxSize);
        if (ftStatus2 != 0) {
            return ftStatus2;
        }
        if (sizeToTransfer > maxSize[0]) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        int ftStatus3 = this.mFtDev.write(buffer, sizeToTransfer);
        sizeTransferred[0] = ftStatus3;
        if (sizeToTransfer == ftStatus3) {
            return 0;
        }
        return 10;
    }

    boolean I2C_ModeCheck() {
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

    int getMaxTransferSize(int[] pMaxSize) {
        pMaxSize[0] = 0;
        int maxBuckSize = this.mFt4222Dev.getMaxBuckSize();
        if (this.mFt4222Dev.mChipStatus.function == 2) {
            pMaxSize[0] = maxBuckSize - 4;
            return 0;
        }
        return 17;
    }
}
