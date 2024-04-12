package org.qtproject.qt5.android;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/* compiled from: QtInputConnection.java */
/* loaded from: classes.dex */
public class HideKeyboardRunnable implements Runnable {
    private long m_hideTimeStamp = System.nanoTime();

    @Override // java.lang.Runnable
    public void run() {
        Activity activity = QtNative.activity();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (displayMetrics.heightPixels - rect.bottom < 100) {
            QtNative.activityDelegate().setKeyboardVisibility(false, this.m_hideTimeStamp);
        }
    }
}
