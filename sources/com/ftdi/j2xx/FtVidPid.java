package com.ftdi.j2xx;

/* compiled from: D2xxManager.java */
/* loaded from: classes.dex */
public class FtVidPid {
    private int mProductId;
    private int mVendorId;

    public FtVidPid(int vendor, int product) {
        this.mVendorId = vendor;
        this.mProductId = product;
    }

    FtVidPid() {
        this.mVendorId = 0;
        this.mProductId = 0;
    }

    public void setVid(int vid) {
        this.mVendorId = vid;
    }

    public void setPid(int pid) {
        this.mProductId = pid;
    }

    public int getVid() {
        return this.mVendorId;
    }

    public int getPid() {
        return this.mProductId;
    }

    public String toString() {
        return "Vendor: " + String.format("%04x", Integer.valueOf(this.mVendorId)) + ", Product: " + String.format("%04x", Integer.valueOf(this.mProductId));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof FtVidPid) {
            FtVidPid testObj = (FtVidPid) o;
            return this.mVendorId == testObj.mVendorId && this.mProductId == testObj.mProductId;
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
