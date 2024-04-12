package org.qtproject.qt5.android;

/* compiled from: QtInputConnection.java */
/* loaded from: classes.dex */
class QtNativeInputConnection {
    public static native boolean beginBatchEdit();

    public static native boolean commitCompletion(String str, int i);

    public static native boolean commitText(String str, int i);

    public static native boolean copy();

    public static native boolean copyURL();

    public static native boolean cut();

    public static native boolean deleteSurroundingText(int i, int i2);

    public static native boolean endBatchEdit();

    public static native boolean finishComposingText();

    public static native int getCursorCapsMode(int i);

    public static native QtExtractedText getExtractedText(int i, int i2, int i3);

    public static native String getSelectedText(int i);

    public static native String getTextAfterCursor(int i, int i2);

    public static native String getTextBeforeCursor(int i, int i2);

    public static native boolean paste();

    public static native boolean selectAll();

    public static native boolean setComposingRegion(int i, int i2);

    public static native boolean setComposingText(String str, int i);

    public static native boolean setSelection(int i, int i2);

    public static native boolean updateCursorPosition();

    QtNativeInputConnection() {
    }
}
