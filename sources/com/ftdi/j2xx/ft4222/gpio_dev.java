package com.ftdi.j2xx.ft4222;

/* compiled from: FT_4222_Gpio.java */
/* loaded from: classes.dex */
public class gpio_dev {
    byte[] dat = new byte[1];
    byte dir;
    byte mask;
    dev_ctrl usb;

    public gpio_dev(char[] fwVer) {
        this.usb = new dev_ctrl(fwVer);
    }
}
