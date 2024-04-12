package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.SpiMaster;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FT_4222_Spi_Master implements SpiMaster {
    private static final String TAG = "FTDI_Device::";
    private FT_4222_Device mFT4222Device;
    private FT_Device mFTDevice;

    public FT_4222_Spi_Master(FT_4222_Device pDevice) {
        this.mFT4222Device = pDevice;
        this.mFTDevice = pDevice.mFtDev;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int init(int ioLine, int clock, int cpol, int cpha, byte ssoMap) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        int venderSSOMap = 0;
        SPI_MasterCfg config = this.mFT4222Device.mSpiMasterCfg;
        config.ioLine = ioLine;
        config.clock = clock;
        config.cpol = cpol;
        config.cpha = cpha;
        config.ssoMap = ssoMap;
        if (config.ioLine == 1 || config.ioLine == 2 || config.ioLine == 4) {
            this.mFT4222Device.cleanRxData();
            byte b = chipStatus.chip_mode;
            if (b == 0) {
                venderSSOMap = 1;
            } else if (b == 1) {
                venderSSOMap = 7;
            } else if (b == 2) {
                venderSSOMap = 15;
            } else if (b == 3) {
                venderSSOMap = 1;
            }
            if ((config.ssoMap & venderSSOMap) == 0) {
                return 6;
            }
            config.ssoMap = (byte) (config.ssoMap & venderSSOMap);
            if (this.mFTDevice.VendorCmdSet(33, (config.ioLine << 8) | 66) >= 0 && this.mFTDevice.VendorCmdSet(33, (config.clock << 8) | 68) >= 0 && this.mFTDevice.VendorCmdSet(33, (config.cpol << 8) | 69) >= 0 && this.mFTDevice.VendorCmdSet(33, (config.cpha << 8) | 70) >= 0 && this.mFTDevice.VendorCmdSet(33, (0 << 8) | 67) >= 0 && this.mFTDevice.VendorCmdSet(33, (config.ssoMap << 8) | 72) >= 0 && this.mFTDevice.VendorCmdSet(33, (3 << 8) | 5) >= 0) {
                chipStatus.function = (byte) 3;
                return 0;
            }
            return 4;
        }
        return 6;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int setLines(int spiMode) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        if (chipStatus.function != 3) {
            return 1003;
        }
        if (spiMode == 0) {
            return 17;
        }
        if (this.mFTDevice.VendorCmdSet(33, (spiMode << 8) | 66) >= 0 && this.mFTDevice.VendorCmdSet(33, (1 << 8) | 74) >= 0) {
            SPI_MasterCfg spiCfg = this.mFT4222Device.mSpiMasterCfg;
            spiCfg.ioLine = spiMode;
            return 0;
        }
        return 4;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int singleWrite(byte[] writeBuffer, int sizeToTransfer, int[] sizeTransferred, boolean isEndTransaction) {
        byte[] readBuffer = new byte[writeBuffer.length];
        return singleReadWrite(readBuffer, writeBuffer, sizeToTransfer, sizeTransferred, isEndTransaction);
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int singleRead(byte[] readBuffer, int sizeToTransfer, int[] sizeOfRead, boolean isEndTransaction) {
        byte[] writeBuffer = new byte[readBuffer.length];
        return singleReadWrite(readBuffer, writeBuffer, sizeToTransfer, sizeOfRead, isEndTransaction);
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int singleReadWrite(byte[] readBuffer, byte[] writeBuffer, int sizeToTransfer, int[] sizeTransferred, boolean isEndTransaction) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        SPI_MasterCfg spiCfg = this.mFT4222Device.mSpiMasterCfg;
        if (writeBuffer == null || readBuffer == null || sizeTransferred == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        sizeTransferred[0] = 0;
        if (chipStatus.function != 3 || spiCfg.ioLine != 1) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_IS_NOT_SPI_SINGLE_MODE;
        }
        if (sizeToTransfer == 0) {
            return 6;
        }
        if (sizeToTransfer > writeBuffer.length || sizeToTransfer > readBuffer.length) {
            Assert.assertTrue("sizeToTransfer > writeBuffer.length || sizeToTransfer > readBuffer.length", false);
        }
        if (writeBuffer.length != readBuffer.length || writeBuffer.length == 0) {
            Assert.assertTrue("writeBuffer.length != readBuffer.length || writeBuffer.length == 0", false);
        }
        sizeTransferred[0] = sendReadWriteBuffer(this.mFTDevice, writeBuffer, readBuffer, sizeToTransfer);
        if (isEndTransaction) {
            this.mFTDevice.write(null, 0);
        }
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int multiReadWrite(byte[] readBuffer, byte[] writeBuffer, int singleWriteBytes, int multiWriteBytes, int multiReadBytes, int[] sizeOfRead) {
        chiptop_mgr chipStatus = this.mFT4222Device.mChipStatus;
        SPI_MasterCfg spiCfg = this.mFT4222Device.mSpiMasterCfg;
        if (multiReadBytes > 0 && readBuffer == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (singleWriteBytes + multiWriteBytes > 0 && writeBuffer == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (multiReadBytes > 0 && sizeOfRead == null) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (chipStatus.function != 3 || spiCfg.ioLine == 1) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_IS_NOT_SPI_MULTI_MODE;
        }
        if (singleWriteBytes > 15) {
            Log.e(TAG, "The maxium single write bytes are 15 bytes");
            return 6;
        }
        int sendDataSize = singleWriteBytes + 5 + multiWriteBytes;
        byte[] sendData = new byte[sendDataSize];
        sendData[0] = (byte) ((singleWriteBytes & 15) | 128);
        sendData[1] = (byte) ((multiWriteBytes & 65280) >> 8);
        sendData[2] = (byte) (multiWriteBytes & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        sendData[3] = (byte) ((multiReadBytes & 65280) >> 8);
        sendData[4] = (byte) (multiReadBytes & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        for (int i = 0; i < singleWriteBytes + multiWriteBytes; i++) {
            sendData[i + 5] = writeBuffer[i];
        }
        sizeOfRead[0] = setMultiReadWritePackage(this.mFTDevice, sendData, readBuffer);
        return 0;
    }

    @Override // com.ftdi.j2xx.interfaces.SpiMaster
    public int reset() {
        if (this.mFTDevice.VendorCmdSet(33, (0 << 8) | 74) < 0) {
            return 4;
        }
        return 0;
    }

    public int setDrivingStrength(int clkStrength, int ioStrength, int ssoStregth) {
        int verderFun;
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
        return (this.mFTDevice.VendorCmdSet(33, (actual_strength2 << 8) | FT_4222_Defines.CHIPTOP_CMD.CHIPTOP_SET_DS_CTL0_REG) >= 0 && this.mFTDevice.VendorCmdSet(33, (verderFun << 8) | 5) >= 0) ? 0 : 4;
    }

    private int setMultiReadWritePackage(FT_Device ftSPIDevice, byte[] wr_buffer, byte[] rd_buffer) {
        int sleepCnt = 0;
        int bytesRead = 0;
        if (ftSPIDevice == null || !ftSPIDevice.isOpen()) {
            return -1;
        }
        ftSPIDevice.write(wr_buffer, wr_buffer.length);
        while (bytesRead < rd_buffer.length && sleepCnt < 30000) {
            int ret = ftSPIDevice.getQueueStatus();
            if (ret > 0) {
                byte[] rd_tmp_buf = new byte[ret];
                int ret2 = ftSPIDevice.read(rd_tmp_buf, ret);
                Assert.assertEquals(rd_tmp_buf.length == ret2, true);
                for (int i = 0; i < rd_tmp_buf.length; i++) {
                    if (bytesRead + i < rd_buffer.length) {
                        rd_buffer[bytesRead + i] = rd_tmp_buf[i];
                    }
                }
                bytesRead += ret2;
                sleepCnt = 0;
            }
            try {
                Thread.sleep(10);
                sleepCnt += 10;
            } catch (InterruptedException e) {
                sleepCnt = 30000;
            }
        }
        if (rd_buffer.length != bytesRead || sleepCnt > 30000) {
            Log.e(TAG, "MultiReadWritePackage timeout!!!!");
            return -1;
        }
        return bytesRead;
    }

    private int sendReadWriteBuffer(FT_Device ftDevice, byte[] wr_buffer, byte[] rd_buffer, int sizeToTransfer) {
        byte[] wrPackBuf = new byte[16384];
        byte[] rdPackBuf = new byte[wrPackBuf.length];
        int packCount = sizeToTransfer / wrPackBuf.length;
        int restCount = sizeToTransfer % wrPackBuf.length;
        int readIdx = 0;
        int writeIdx = 0;
        for (int i = 0; i < packCount; i++) {
            for (int j = 0; j < wrPackBuf.length; j++) {
                wrPackBuf[j] = wr_buffer[writeIdx];
                writeIdx++;
            }
            int valRet = setReadWritePackage(ftDevice, wrPackBuf, rdPackBuf);
            if (valRet <= 0) {
                return -1;
            }
            for (byte b : rdPackBuf) {
                rd_buffer[readIdx] = b;
                readIdx++;
            }
        }
        if (restCount > 0) {
            byte[] wrPackBuf2 = new byte[restCount];
            byte[] rdPackBuf2 = new byte[wrPackBuf2.length];
            for (int j2 = 0; j2 < wrPackBuf2.length; j2++) {
                wrPackBuf2[j2] = wr_buffer[writeIdx];
                writeIdx++;
            }
            int valRet2 = setReadWritePackage(ftDevice, wrPackBuf2, rdPackBuf2);
            if (valRet2 <= 0) {
                return -1;
            }
            for (byte b2 : rdPackBuf2) {
                rd_buffer[readIdx] = b2;
                readIdx++;
            }
        }
        return readIdx;
    }

    private int setReadWritePackage(FT_Device ftSPIDevice, byte[] wr_buffer, byte[] rd_buffer) {
        int ret;
        int sleepCnt = 0;
        int bytesRead = 0;
        if (ftSPIDevice == null || !ftSPIDevice.isOpen()) {
            return -1;
        }
        boolean z = true;
        Assert.assertEquals(wr_buffer.length == rd_buffer.length, true);
        if (wr_buffer.length != ftSPIDevice.write(wr_buffer, wr_buffer.length)) {
            Log.e(TAG, "setReadWritePackage Incomplete Write Error!!!");
            return -1;
        }
        while (bytesRead < rd_buffer.length && sleepCnt < 30000) {
            int ret2 = ftSPIDevice.getQueueStatus();
            if (ret2 <= 0) {
                ret = ret2;
            } else {
                byte[] rd_tmp_buf = new byte[ret2];
                ret = ftSPIDevice.read(rd_tmp_buf, ret2);
                Assert.assertEquals(rd_tmp_buf.length == ret, z);
                for (int i = 0; i < rd_tmp_buf.length; i++) {
                    if (bytesRead + i < rd_buffer.length) {
                        rd_buffer[bytesRead + i] = rd_tmp_buf[i];
                    }
                }
                bytesRead += ret;
                sleepCnt = 0;
            }
            try {
                Thread.sleep(10);
                sleepCnt += 10;
                z = true;
            } catch (InterruptedException e) {
                sleepCnt = 30000;
                z = true;
            }
        }
        if (rd_buffer.length != bytesRead || sleepCnt > 30000) {
            Log.e(TAG, "SingleReadWritePackage timeout!!!!");
            return -1;
        }
        return bytesRead;
    }
}
