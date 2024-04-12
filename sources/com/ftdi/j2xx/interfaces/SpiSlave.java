package com.ftdi.j2xx.interfaces;

/* loaded from: classes.dex */
public interface SpiSlave {
    int getRxStatus(int[] iArr);

    int init();

    int read(byte[] bArr, int i, int[] iArr);

    int reset();

    int write(byte[] bArr, int i, int[] iArr);
}
