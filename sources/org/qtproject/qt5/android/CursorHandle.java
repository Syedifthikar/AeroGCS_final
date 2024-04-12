package org.qtproject.qt5.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

/* loaded from: classes.dex */
public class CursorHandle implements ViewTreeObserver.OnPreDrawListener {
    private Activity m_activity;
    private int m_attr;
    private int m_id;
    private int m_lastX;
    private int m_lastY;
    private View m_layout;
    private boolean m_rtl;
    int m_yShift;
    int tolerance;
    private CursorView m_cursorView = null;
    private PopupWindow m_popup = null;
    private int m_posX = 0;
    private int m_posY = 0;

    public CursorHandle(Activity activity, View view, int i, int i2, boolean z) {
        this.m_layout = null;
        this.m_activity = activity;
        this.m_id = i;
        this.m_attr = i2;
        this.m_layout = view;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int applyDimension = (int) TypedValue.applyDimension(5, 1.0f, displayMetrics);
        this.m_yShift = applyDimension;
        int min = Math.min(1, (int) (applyDimension / 2.0f));
        this.tolerance = min;
        int i3 = (-1) - min;
        this.m_lastY = i3;
        this.m_lastX = i3;
        this.m_rtl = z;
    }

    private boolean initOverlay() {
        if (this.m_popup == null) {
            Context context = this.m_layout.getContext();
            Drawable drawable = context.getTheme().obtainStyledAttributes(new int[]{this.m_attr}).getDrawable(0);
            CursorView cursorView = new CursorView(context, this);
            this.m_cursorView = cursorView;
            cursorView.setImageDrawable(drawable);
            PopupWindow popupWindow = new PopupWindow(context, (AttributeSet) null, 16843464);
            this.m_popup = popupWindow;
            popupWindow.setSplitTouchEnabled(true);
            this.m_popup.setClippingEnabled(false);
            this.m_popup.setContentView(this.m_cursorView);
            this.m_popup.setWidth(drawable.getIntrinsicWidth());
            this.m_popup.setHeight(drawable.getIntrinsicHeight());
            this.m_layout.getViewTreeObserver().addOnPreDrawListener(this);
        }
        return true;
    }

    public void setPosition(int i, int i2) {
        int width;
        initOverlay();
        int[] iArr = new int[2];
        this.m_layout.getLocationOnScreen(iArr);
        int i3 = iArr[0] + i;
        int i4 = iArr[1] + i2 + this.m_yShift;
        int i5 = this.m_id;
        if (i5 == 1) {
            width = i3 - (this.m_popup.getWidth() / 2);
        } else if ((i5 == 2 && !this.m_rtl) || (this.m_id == 3 && this.m_rtl)) {
            width = i3 - ((this.m_popup.getWidth() * 3) / 4);
        } else {
            width = i3 - (this.m_popup.getWidth() / 4);
        }
        if (!this.m_popup.isShowing()) {
            this.m_popup.showAtLocation(this.m_layout, 0, width, i4);
        } else {
            this.m_popup.update(width, i4, -1, -1);
            this.m_cursorView.adjusted(i - this.m_posX, i2 - this.m_posY);
        }
        this.m_posX = i;
        this.m_posY = i2;
    }

    public int bottom() {
        initOverlay();
        int[] iArr = new int[2];
        this.m_cursorView.getLocationOnScreen(iArr);
        return iArr[1] + this.m_cursorView.getHeight();
    }

    public void hide() {
        PopupWindow popupWindow = this.m_popup;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void updatePosition(int i, int i2) {
        int i3 = i2 - this.m_yShift;
        if (Math.abs(this.m_lastX - i) > this.tolerance || Math.abs(this.m_lastY - i3) > this.tolerance) {
            QtNative.handleLocationChanged(this.m_id, this.m_posX + i, this.m_posY + i3);
            this.m_lastX = i;
            this.m_lastY = i3;
        }
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        PopupWindow popupWindow = this.m_popup;
        if (popupWindow != null && popupWindow.isShowing()) {
            setPosition(this.m_posX, this.m_posY);
            return true;
        }
        return true;
    }
}
