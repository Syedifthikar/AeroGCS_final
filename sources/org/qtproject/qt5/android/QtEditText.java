package org.qtproject.qt5.android;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* loaded from: classes.dex */
public class QtEditText extends View {
    QtActivityDelegate m_activityDelegate;
    int m_imeOptions;
    int m_initialCapsMode;
    int m_inputType;
    boolean m_optionsChanged;

    public void setImeOptions(int i) {
        if (i == this.m_imeOptions) {
            return;
        }
        this.m_imeOptions = i;
        this.m_optionsChanged = true;
    }

    public void setInitialCapsMode(int i) {
        if (i == this.m_initialCapsMode) {
            return;
        }
        this.m_initialCapsMode = i;
        this.m_optionsChanged = true;
    }

    public void setInputType(int i) {
        if (i == this.m_inputType) {
            return;
        }
        this.m_inputType = i;
        this.m_optionsChanged = true;
    }

    public QtEditText(Context context, QtActivityDelegate qtActivityDelegate) {
        super(context);
        this.m_initialCapsMode = 0;
        this.m_imeOptions = 0;
        this.m_inputType = 1;
        this.m_optionsChanged = false;
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.m_activityDelegate = qtActivityDelegate;
    }

    public QtActivityDelegate getActivityDelegate() {
        return this.m_activityDelegate;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        editorInfo.inputType = this.m_inputType;
        editorInfo.imeOptions = this.m_imeOptions;
        editorInfo.initialCapsMode = this.m_initialCapsMode;
        editorInfo.imeOptions |= 268435456;
        return new QtInputConnection(this);
    }
}
