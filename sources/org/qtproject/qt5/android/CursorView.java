package org.qtproject.qt5.android;

import android.content.Context;
import android.widget.ImageView;

/* compiled from: CursorHandle.java */
/* loaded from: classes.dex */
public class CursorView extends ImageView {
    private CursorHandle mHandle;
    private float m_offsetX;
    private float m_offsetY;
    private boolean m_pressed;

    public CursorView(Context context, CursorHandle cursorHandle) {
        super(context);
        this.m_pressed = false;
        this.mHandle = cursorHandle;
    }

    public void adjusted(int i, int i2) {
        this.m_offsetX += i;
        this.m_offsetY += i2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x000e, code lost:
        if (r0 != 3) goto L8;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getActionMasked()
            r1 = 2
            r2 = 1
            if (r0 == 0) goto L35
            r3 = 0
            if (r0 == r2) goto L32
            if (r0 == r1) goto L11
            r5 = 3
            if (r0 == r5) goto L32
            goto L4b
        L11:
            boolean r0 = r4.m_pressed
            if (r0 != 0) goto L16
            return r3
        L16:
            org.qtproject.qt5.android.CursorHandle r0 = r4.mHandle
            float r1 = r5.getRawX()
            float r3 = r4.m_offsetX
            float r1 = r1 - r3
            int r1 = java.lang.Math.round(r1)
            float r5 = r5.getRawY()
            float r3 = r4.m_offsetY
            float r5 = r5 - r3
            int r5 = java.lang.Math.round(r5)
            r0.updatePosition(r1, r5)
            goto L4b
        L32:
            r4.m_pressed = r3
            goto L4b
        L35:
            float r0 = r5.getRawX()
            r4.m_offsetX = r0
            float r5 = r5.getRawY()
            int r0 = r4.getHeight()
            int r0 = r0 / r1
            float r0 = (float) r0
            float r5 = r5 + r0
            r4.m_offsetY = r5
            r4.m_pressed = r2
        L4b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.qtproject.qt5.android.CursorView.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
