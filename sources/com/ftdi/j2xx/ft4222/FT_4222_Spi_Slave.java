package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.SpiSlave;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class FT_4222_Spi_Slave implements SpiSlave {
    private static final String TAG = "FTDI_Device::";
    private FT_4222_Device mFT4222Device;
    private FT_Device mFTDevice;
    private Lock m_pDevLock = new ReentrantLock();

    public FT_4222_Spi_Slave(FT_4222_Device pDevice) {
        this.mFT4222Device = pDevice;
        this.mFTDevice = pDevice.mFtDev;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiSlave
    public int init() {
        int status = 0;
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        SPI_MasterCfg config = this.mFT4222Device.mSpiMasterCfg;
        config.ioLine = 1;
        config.clock = 2;
        config.cpol = 0;
        config.cpha = 0;
        config.ssoMap = (byte) 1;
        this.m_pDevLock.lock();
        this.mFT4222Device.cleanRxData();
        if (this.mFTDevice.VendorCmdSet(33, (config.ioLine << 8) | 66) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (config.clock << 8) | 68) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (config.cpol << 8) | 69) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (config.cpha << 8) | 70) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (0 << 8) | 67) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (config.ssoMap << 8) | 72) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (4 << 8) | 5) < 0) {
            status = 4;
        }
        this.m_pDevLock.unlock();
        chipStatus.function = (byte) 4;
        return status;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiSlave
    public int getRxStatus(int[] pRxSize) {
        if (pRxSize == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        int status = check();
        if (status != 0) {
            return status;
        }
        this.m_pDevLock.lock();
        int ret = this.mFTDevice.getQueueStatus();
        this.m_pDevLock.unlock();
        if (ret < 0) {
            pRxSize[0] = -1;
            return 4;
        }
        pRxSize[0] = ret;
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiSlave
    public int read(byte[] buffer, int bufferSize, int[] sizeOfRead) {
        this.m_pDevLock.lock();
        FT_Device fT_Device = this.mFTDevice;
        if (fT_Device == null || !fT_Device.isOpen()) {
            this.m_pDevLock.unlock();
            return 3;
        }
        int ret = this.mFTDevice.read(buffer, bufferSize);
        this.m_pDevLock.unlock();
        sizeOfRead[0] = ret;
        if (ret >= 0) {
            return 0;
        }
        return 4;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiSlave
    public int write(byte[] buffer, int bufferSize, int[] sizeTransferred) {
        if (sizeTransferred == null || buffer == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        int status = check();
        if (status != 0) {
            return status;
        }
        if (bufferSize > 512) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        this.m_pDevLock.lock();
        sizeTransferred[0] = this.mFTDevice.write(buffer, bufferSize);
        this.m_pDevLock.unlock();
        if (sizeTransferred[0] != bufferSize) {
            Log.e(TAG, "Error write =" + bufferSize + " tx=" + sizeTransferred[0]);
            return 4;
        }
        return status;
    }

    private int check() {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        if (chipStatus.function != 4) {
            return 1003;
        }
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiSlave
    public int reset() {
        int status = 0;
        this.m_pDevLock.lock();
        if (this.mFTDevice.VendorCmdSet(33, (0 << 8) | 74) < 0) {
            status = 4;
        }
        this.m_pDevLock.unlock();
        return status;
    }

    public int setDrivingStrength(int clkStrength, int ioStrength, int ssoStregth) {
        int verderFun;
        int status = 0;
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        if (chipStatus.function != 3 && chipStatus.function != 4) {
            return 1003;
        }
        int actual_strength = clkStrength << 4;
        int actual_strength2 = actual_strength | (ioStrength << 2) | ssoStregth;
        if (chipStatus.function == 3) {
            verderFun = 3;
        } else {
            verderFun = 4;
        }
        this.m_pDevLock.lock();
        if (this.mFTDevice.VendorCmdSet(33, (actual_strength2 << 8) | FT_4222_Defines.CHIPTOP_CMD.CHIPTOP_SET_DS_CTL0_REG) < 0) {
            status = 4;
        }
        if (this.mFTDevice.VendorCmdSet(33, (verderFun << 8) | 5) < 0) {
            status = 4;
        }
        this.m_pDevLock.unlock();
        return status;
    }
}
