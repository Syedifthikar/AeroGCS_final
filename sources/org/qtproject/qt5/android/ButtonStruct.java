package org.qtproject.qt5.android;

import android.text.Html;
import android.text.Spanned;
import android.view.View;

/* compiled from: QtMessageDialogHelper.java */
/* loaded from: classes.dex */
public class ButtonStruct implements View.OnClickListener {
    QtMessageDialogHelper m_dialog;
    private int m_id;
    Spanned m_text;

    public ButtonStruct(QtMessageDialogHelper qtMessageDialogHelper, int i, String str) {
        this.m_dialog = qtMessageDialogHelper;
        this.m_id = i;
        this.m_text = Html.fromHtml(str);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        QtNativeDialogHelper.dialogResult(this.m_dialog.handler(), this.m_id);
    }
}
