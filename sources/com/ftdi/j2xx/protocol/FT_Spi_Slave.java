package com.ftdi.j2xx.protocol;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.interfaces.SpiSlave;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FT_Spi_Slave extends SpiSlaveThread {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$ftdi$j2xx$protocol$FT_Spi_Slave$DECODE_STATE = null;
    private static final int FT4222_SPI_SLAVE_SYNC_WORD = 90;
    private static final int SPI_ACK = 132;
    private static final int SPI_MASTER_TRANSFER = 128;
    private static final int SPI_QUERY_VER = 136;
    private static final int SPI_SHART_SLAVE_TRANSFER = 131;
    private static final int SPI_SHORT_MASTER_TRANSFER = 130;
    private static final int SPI_SLAVE_TRANSFER = 129;
    private byte[] mBuffer;
    private int mBufferSize;
    private int mCheckSum;
    private int mCmd;
    private int mCurrentBufferSize;
    private DECODE_STATE mDecodeState = DECODE_STATE.STATE_SYNC;
    private boolean mIsOpened;
    private int mSn;
    private SpiSlave mSpiSlave;
    private SpiSlaveListener mSpiSlaveListener;
    private int mSync;
    private int mWrSn;

    /* loaded from: classes.dex */
    public enum DECODE_STATE {
        STATE_SYNC,
        STATE_CMD,
        STATE_SN,
        STATE_SIZE_HIGH,
        STATE_SIZE_LOW,
        STATE_COLLECT_DATA,
        STATE_CHECKSUM_HIGH,
        STATE_CHECKSUM_LOW;

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static DECODE_STATE[] valuesCustom() {
            DECODE_STATE[] valuesCustom = values();
            int length = valuesCustom.length;
            DECODE_STATE[] decode_stateArr = new DECODE_STATE[length];
            System.arraycopy(valuesCustom, 0, decode_stateArr, 0, length);
            return decode_stateArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$ftdi$j2xx$protocol$FT_Spi_Slave$DECODE_STATE() {
        int[] iArr = $SWITCH_TABLE$com$ftdi$j2xx$protocol$FT_Spi_Slave$DECODE_STATE;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[DECODE_STATE.valuesCustom().length];
        try {
            iArr2[DECODE_STATE.STATE_CHECKSUM_HIGH.ordinal()] = 7;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr2[DECODE_STATE.STATE_CHECKSUM_LOW.ordinal()] = 8;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr2[DECODE_STATE.STATE_CMD.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr2[DECODE_STATE.STATE_COLLECT_DATA.ordinal()] = 6;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr2[DECODE_STATE.STATE_SIZE_HIGH.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr2[DECODE_STATE.STATE_SIZE_LOW.ordinal()] = 5;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr2[DECODE_STATE.STATE_SN.ordinal()] = 3;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr2[DECODE_STATE.STATE_SYNC.ordinal()] = 1;
        } catch (NoSuchFieldError e8) {
        }
        $SWITCH_TABLE$com$ftdi$j2xx$protocol$FT_Spi_Slave$DECODE_STATE = iArr2;
        return iArr2;
    }

    public FT_Spi_Slave(SpiSlave pSlaveInterface) {
        this.mSpiSlave = pSlaveInterface;
    }

    public void registerSpiSlaveListener(SpiSlaveListener pListener) {
        this.mSpiSlaveListener = pListener;
    }

    public int open() {
        if (this.mIsOpened) {
            return 1;
        }
        this.mIsOpened = true;
        this.mSpiSlave.init();
        start();
        return 0;
    }

    public int close() {
        if (!this.mIsOpened) {
            return 3;
        }
        SpiSlaveRequestEvent event = new SpiSlaveRequestEvent(-1, true, null, null, null);
        sendMessage(event);
        this.mIsOpened = false;
        return 0;
    }

    public int write(byte[] wrBuf) {
        if (!this.mIsOpened) {
            return 3;
        }
        if (wrBuf.length > 65536) {
            return FT_4222_Defines.FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        int[] sizeTransferred = new int[1];
        int wrSize = wrBuf.length;
        int checksum = getCheckSum(wrBuf, 90, 129, this.mWrSn, wrSize);
        byte[] buffer = new byte[wrBuf.length + 8];
        int idx = 0 + 1;
        buffer[0] = 0;
        int idx2 = idx + 1;
        buffer[idx] = 90;
        int idx3 = idx2 + 1;
        buffer[idx2] = -127;
        int idx4 = idx3 + 1;
        buffer[idx3] = (byte) this.mWrSn;
        int idx5 = idx4 + 1;
        buffer[idx4] = (byte) ((wrSize & 65280) >> 8);
        int idx6 = idx5 + 1;
        buffer[idx5] = (byte) (wrSize & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        int i = 0;
        while (i < wrBuf.length) {
            buffer[idx6] = wrBuf[i];
            i++;
            idx6++;
        }
        int i2 = idx6 + 1;
        buffer[idx6] = (byte) ((65280 & checksum) >> 8);
        int i3 = i2 + 1;
        buffer[i2] = (byte) (checksum & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        this.mSpiSlave.write(buffer, buffer.length, sizeTransferred);
        if (sizeTransferred[0] == buffer.length) {
            int i4 = this.mWrSn + 1;
            this.mWrSn = i4;
            if (i4 >= 256) {
                this.mWrSn = 0;
            }
            return 0;
        }
        return 4;
    }

    private boolean check_valid_spi_cmd(int cmd) {
        if (cmd == 128 || cmd == 130 || cmd == SPI_QUERY_VER) {
            return true;
        }
        return false;
    }

    private int getCheckSum(byte[] sendBuf, int sync, int cmd, int sn, int bufsize) {
        int sum = 0;
        if (sendBuf != null) {
            for (int i : sendBuf) {
                sum += i & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
            }
        }
        return sum + sync + cmd + sn + ((65280 & bufsize) >> 8) + (bufsize & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
    }

    private void spi_push_req_ack_queue() {
        byte[] buffer = new byte[8];
        int idx = 0 + 1;
        buffer[0] = 0;
        int idx2 = idx + 1;
        buffer[idx] = 90;
        int idx3 = idx2 + 1;
        buffer[idx2] = -124;
        int idx4 = idx3 + 1;
        int i = this.mSn;
        buffer[idx3] = (byte) i;
        int idx5 = idx4 + 1;
        buffer[idx4] = 0;
        int idx6 = idx5 + 1;
        buffer[idx5] = 0;
        int checksum = getCheckSum(null, 90, 132, i, 0);
        int idx7 = idx6 + 1;
        buffer[idx6] = (byte) ((65280 & checksum) >> 8);
        int i2 = idx7 + 1;
        buffer[idx7] = (byte) (checksum & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        int[] sizeTransferred = new int[1];
        this.mSpiSlave.write(buffer, buffer.length, sizeTransferred);
    }

    private void sp_slave_parse_and_push_queue(byte[] rdBuf) {
        boolean reset = false;
        boolean dataCorrupted = false;
        for (int i = 0; i < rdBuf.length; i++) {
            int val = rdBuf[i] & 255;
            switch ($SWITCH_TABLE$com$ftdi$j2xx$protocol$FT_Spi_Slave$DECODE_STATE()[this.mDecodeState.ordinal()]) {
                case 1:
                    if (val != 90) {
                        reset = true;
                        break;
                    } else {
                        this.mDecodeState = DECODE_STATE.STATE_CMD;
                        this.mSync = val;
                        break;
                    }
                case 2:
                    if (!check_valid_spi_cmd(val)) {
                        reset = true;
                        dataCorrupted = true;
                    } else {
                        this.mCmd = val;
                    }
                    this.mDecodeState = DECODE_STATE.STATE_SN;
                    break;
                case 3:
                    this.mSn = val;
                    this.mDecodeState = DECODE_STATE.STATE_SIZE_HIGH;
                    break;
                case 4:
                    this.mBufferSize = val * 256;
                    this.mDecodeState = DECODE_STATE.STATE_SIZE_LOW;
                    break;
                case 5:
                    int i2 = this.mBufferSize + val;
                    this.mBufferSize = i2;
                    this.mCurrentBufferSize = 0;
                    this.mBuffer = new byte[i2];
                    this.mDecodeState = DECODE_STATE.STATE_COLLECT_DATA;
                    break;
                case 6:
                    byte[] bArr = this.mBuffer;
                    int i3 = this.mCurrentBufferSize;
                    bArr[i3] = rdBuf[i];
                    int i4 = i3 + 1;
                    this.mCurrentBufferSize = i4;
                    if (i4 == this.mBufferSize) {
                        this.mDecodeState = DECODE_STATE.STATE_CHECKSUM_HIGH;
                        break;
                    }
                    break;
                case 7:
                    int dataCheckSum = val * 256;
                    this.mCheckSum = dataCheckSum;
                    this.mDecodeState = DECODE_STATE.STATE_CHECKSUM_LOW;
                    break;
                case 8:
                    this.mCheckSum += val;
                    int dataCheckSum2 = getCheckSum(this.mBuffer, this.mSync, this.mCmd, this.mSn, this.mBufferSize);
                    if (this.mCheckSum == dataCheckSum2) {
                        if (this.mCmd == 128) {
                            spi_push_req_ack_queue();
                            if (this.mSpiSlaveListener != null) {
                                SpiSlaveResponseEvent pEvent = new SpiSlaveResponseEvent(3, 0, this.mBuffer, null, null);
                                this.mSpiSlaveListener.OnDataReceived(pEvent);
                            }
                        }
                    } else {
                        dataCorrupted = true;
                    }
                    reset = true;
                    break;
            }
            if (dataCorrupted && this.mSpiSlaveListener != null) {
                SpiSlaveResponseEvent pEvent2 = new SpiSlaveResponseEvent(3, 1, null, null, null);
                this.mSpiSlaveListener.OnDataReceived(pEvent2);
            }
            if (reset) {
                this.mDecodeState = DECODE_STATE.STATE_SYNC;
                this.mSync = 0;
                this.mCmd = 0;
                this.mSn = 0;
                this.mBufferSize = 0;
                this.mCurrentBufferSize = 0;
                this.mCheckSum = 0;
                this.mBuffer = null;
                reset = false;
                dataCorrupted = false;
            }
        }
    }

    @Override // com.ftdi.j2xx.protocol.SpiSlaveThread
    protected boolean pollData() {
        int[] rxSize = new int[1];
        int status = this.mSpiSlave.getRxStatus(rxSize);
        if (rxSize[0] > 0 && status == 0) {
            byte[] rdBuf = new byte[rxSize[0]];
            status = this.mSpiSlave.read(rdBuf, rdBuf.length, rxSize);
            if (status == 0) {
                sp_slave_parse_and_push_queue(rdBuf);
            }
        }
        if (status == 4 && this.mSpiSlaveListener != null) {
            SpiSlaveResponseEvent pEvent = new SpiSlaveResponseEvent(3, 2, this.mBuffer, null, null);
            this.mSpiSlaveListener.OnDataReceived(pEvent);
        }
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
        }
        return true;
    }

    @Override // com.ftdi.j2xx.protocol.SpiSlaveThread
    protected void requestEvent(SpiSlaveEvent pEvent) {
        if (pEvent instanceof SpiSlaveRequestEvent) {
            pEvent.getEventType();
            return;
        }
        Assert.assertTrue("processEvent wrong type" + pEvent.getEventType(), false);
    }

    @Override // com.ftdi.j2xx.protocol.SpiSlaveThread
    protected boolean isTerminateEvent(SpiSlaveEvent pEvent) {
        if (Thread.interrupted()) {
            if (pEvent instanceof SpiSlaveRequestEvent) {
                if (pEvent.getEventType() == -1) {
                    return true;
                }
            } else {
                Assert.assertTrue("processEvent wrong type" + pEvent.getEventType(), false);
            }
            return false;
        }
        return true;
    }
}
