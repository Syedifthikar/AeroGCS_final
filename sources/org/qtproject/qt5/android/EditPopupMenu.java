package org.qtproject.qt5.android;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import org.qtproject.qt5.android.EditContextView;

/* loaded from: classes.dex */
public class EditPopupMenu implements ViewTreeObserver.OnPreDrawListener, View.OnLayoutChangeListener, EditContextView.OnClickListener {
    private int m_buttons;
    private CursorHandle m_cursorHandle;
    private View m_layout;
    private CursorHandle m_leftSelectionHandle;
    private PopupWindow m_popup = null;
    private int m_posX;
    private int m_posY;
    private CursorHandle m_rightSelectionHandle;
    private EditContextView m_view;

    public EditPopupMenu(Activity activity, View view) {
        this.m_layout = null;
        this.m_view = null;
        EditContextView editContextView = new EditContextView(activity, this);
        this.m_view = editContextView;
        editContextView.addOnLayoutChangeListener(this);
        this.m_layout = view;
    }

    private void initOverlay() {
        if (this.m_popup != null) {
            return;
        }
        PopupWindow popupWindow = new PopupWindow(this.m_layout.getContext(), (AttributeSet) null, 16843464);
        this.m_popup = popupWindow;
        popupWindow.setSplitTouchEnabled(true);
        this.m_popup.setClippingEnabled(false);
        this.m_popup.setContentView(this.m_view);
        this.m_popup.setWidth(-2);
        this.m_popup.setHeight(-2);
        this.m_layout.getViewTreeObserver().addOnPreDrawListener(this);
    }

    public void setPosition(int i, int i2, int i3, CursorHandle cursorHandle, CursorHandle cursorHandle2, CursorHandle cursorHandle3) {
        initOverlay();
        this.m_view.updateButtons(i3);
        int[] iArr = new int[2];
        this.m_layout.getLocationOnScreen(iArr);
        int width = (iArr[0] + i) - (this.m_view.getWidth() / 2);
        int height = (iArr[1] + i2) - this.m_view.getHeight();
        if (height < 0) {
            if (cursorHandle != null) {
                height = cursorHandle.bottom();
            } else if (cursorHandle2 != null && cursorHandle3 != null) {
                height = Math.max(cursorHandle2.bottom(), cursorHandle3.bottom());
            }
        }
        if (this.m_layout.getWidth() < (this.m_view.getWidth() / 2) + i) {
            width = this.m_layout.getWidth() - this.m_view.getWidth();
        }
        if (width < 0) {
            width = 0;
        }
        if (!this.m_popup.isShowing()) {
            this.m_popup.showAtLocation(this.m_layout, 0, width, height);
        } else {
            this.m_popup.update(width, height, -1, -1);
        }
        this.m_posX = i;
        this.m_posY = i2;
        this.m_buttons = i3;
        this.m_cursorHandle = cursorHandle;
        this.m_leftSelectionHandle = cursorHandle2;
        this.m_rightSelectionHandle = cursorHandle3;
    }

    public void hide() {
        PopupWindow popupWindow = this.m_popup;
        if (popupWindow != null) {
            popupWindow.dismiss();
            this.m_popup = null;
        }
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        PopupWindow popupWindow = this.m_popup;
        if (popupWindow != null && popupWindow.isShowing()) {
            setPosition(this.m_posX, this.m_posY, this.m_buttons, this.m_cursorHandle, this.m_leftSelectionHandle, this.m_rightSelectionHandle);
            return true;
        }
        return true;
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        PopupWindow popupWindow;
        if ((i3 - i != i7 - i5 || i4 - i2 != i8 - i6) && (popupWindow = this.m_popup) != null && popupWindow.isShowing()) {
            setPosition(this.m_posX, this.m_posY, this.m_buttons, this.m_cursorHandle, this.m_leftSelectionHandle, this.m_rightSelectionHandle);
        }
    }

    @Override // org.qtproject.qt5.android.EditContextView.OnClickListener
    public void contextButtonClicked(int i) {
        switch (i) {
            case 17039361:
                QtNativeInputConnection.copy();
                break;
            case 17039363:
                QtNativeInputConnection.cut();
                break;
            case 17039371:
                QtNativeInputConnection.paste();
                break;
            case 17039373:
                QtNativeInputConnection.selectAll();
                break;
        }
        hide();
    }
}
