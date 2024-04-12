package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.interfaces.Gpio;
import com.ftdi.j2xx.interfaces.I2cMaster;
import com.ftdi.j2xx.interfaces.I2cSlave;
import com.ftdi.j2xx.interfaces.SpiMaster;
import com.ftdi.j2xx.interfaces.SpiSlave;

/* loaded from: classes.dex */
public class FT_4222_Device {
    protected FT_Device mFtDev;
    protected String TAG = "FT4222";
    protected chiptop_mgr mChipStatus = new chiptop_mgr();
    protected SPI_MasterCfg mSpiMasterCfg = new SPI_MasterCfg();
    protected gpio_mgr mGpio = new gpio_mgr();

    public FT_4222_Device(FT_Device ftDev) {
        this.mFtDev = ftDev;
    }

    public int init() {
        byte[] buf = new byte[13];
        int ftStatus = this.mFtDev.VendorCmdGet(32, 1, buf, 13);
        if (ftStatus != 13) {
            return 18;
        }
        this.mChipStatus.formByteArray(buf);
        return 0;
    }

    public int setClock(byte clk) {
        if (clk == this.mChipStatus.clk_ctl) {
            return 0;
        }
        int ftStatus = this.mFtDev.VendorCmdSet(33, (clk << 8) | 4);
        if (ftStatus == 0) {
            this.mChipStatus.clk_ctl = clk;
        }
        return ftStatus;
    }

    public int getClock(byte[] clk) {
        if (this.mFtDev.VendorCmdGet(32, 4, clk, 1) >= 0) {
            this.mChipStatus.clk_ctl = clk[0];
            return 0;
        }
        return 18;
    }

    public boolean cleanRxData() {
        int ret = this.mFtDev.getQueueStatus();
        if (ret > 0) {
            byte[] rd_tmp_buf = new byte[ret];
            if (this.mFtDev.read(rd_tmp_buf, ret) != rd_tmp_buf.length) {
                return false;
            }
            return true;
        }
        return true;
    }

    public int getMaxBuckSize() {
        if (this.mChipStatus.fs_only != 0) {
            return 64;
        }
        byte b = this.mChipStatus.chip_mode;
        if (b != 1 && b != 2) {
            return 512;
        }
        return 256;
    }

    public boolean isFT4222Device() {
        FT_Device fT_Device = this.mFtDev;
        if (fT_Device != null) {
            int i = fT_Device.getDeviceInfo().bcdDevice & 65280;
            if (i == 5888) {
                this.mFtDev.getDeviceInfo().type = 12;
                return true;
            } else if (i == 6144) {
                this.mFtDev.getDeviceInfo().type = 10;
                return true;
            } else if (i == 6400) {
                this.mFtDev.getDeviceInfo().type = 11;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public I2cMaster getI2cMasterDevice() {
        if (!isFT4222Device()) {
            return null;
        }
        return new FT_4222_I2c_Master(this);
    }

    public I2cSlave getI2cSlaveDevice() {
        if (!isFT4222Device()) {
            return null;
        }
        return new FT_4222_I2c_Slave(this);
    }

    public SpiMaster getSpiMasterDevice() {
        if (!isFT4222Device()) {
            return null;
        }
        return new FT_4222_Spi_Master(this);
    }

    public SpiSlave getSpiSlaveDevice() {
        if (!isFT4222Device()) {
            return null;
        }
        return new FT_4222_Spi_Slave(this);
    }

    public Gpio getGpioDevice() {
        if (!isFT4222Device()) {
            return null;
        }
        return new FT_4222_Gpio(this);
    }
}
