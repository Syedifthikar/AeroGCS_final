package org.qtproject.qt5.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class QtMessageDialogHelper {
    private Activity m_activity;
    private ArrayList<ButtonStruct> m_buttonsList;
    private Spanned m_detailedText;
    private AlertDialog m_dialog;
    private Spanned m_informativeText;
    private Spanned m_text;
    private Resources.Theme m_theme;
    private Spanned m_title;
    private int m_icon = 0;
    private long m_handler = 0;

    public QtMessageDialogHelper(Activity activity) {
        this.m_activity = activity;
    }

    public void setIcon(int i) {
        this.m_icon = i;
    }

    public Drawable getIconDrawable() {
        if (this.m_icon == 0) {
            return null;
        }
        try {
            TypedValue typedValue = new TypedValue();
            this.m_theme.resolveAttribute(16843605, typedValue, true);
            return this.m_activity.getResources().getDrawable(typedValue.resourceId);
        } catch (Exception e) {
            e.printStackTrace();
            int i = this.m_icon;
            if (i == 1) {
                try {
                    return this.m_activity.getResources().getDrawable(17301659);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return null;
                }
            } else if (i == 2 || i == 3) {
                try {
                    return this.m_activity.getResources().getDrawable(17301543);
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return null;
                }
            } else if (i == 4) {
                try {
                    return this.m_activity.getResources().getDrawable(17301568);
                } catch (Exception e4) {
                    e4.printStackTrace();
                    return null;
                }
            }
            return null;
        }
    }

    public void setTile(String str) {
        this.m_title = Html.fromHtml(str);
    }

    public void setText(String str) {
        this.m_text = Html.fromHtml(str);
    }

    public void setInformativeText(String str) {
        this.m_informativeText = Html.fromHtml(str);
    }

    public void setDetailedText(String str) {
        this.m_detailedText = Html.fromHtml(str);
    }

    public void addButton(int i, String str) {
        if (this.m_buttonsList == null) {
            this.m_buttonsList = new ArrayList<>();
        }
        this.m_buttonsList.add(new ButtonStruct(this, i, str));
    }

    public Drawable getStyledDrawable(String str) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        TypedArray obtainStyledAttributes = this.m_theme.obtainStyledAttributes(new int[]{Class.forName("android.R$attr").getDeclaredField(str).getInt(null)});
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    public void show(long j) {
        this.m_handler = j;
        this.m_activity.runOnUiThread(new Runnable() { // from class: org.qtproject.qt5.android.QtMessageDialogHelper.1
            /* JADX WARN: Removed duplicated region for block: B:308:0x0215  */
            /* JADX WARN: Removed duplicated region for block: B:338:0x0238 A[SYNTHETIC] */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 698
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: org.qtproject.qt5.android.QtMessageDialogHelper.AnonymousClass1.run():void");
            }
        });
    }

    public void hide() {
        this.m_activity.runOnUiThread(new Runnable() { // from class: org.qtproject.qt5.android.QtMessageDialogHelper.2
            @Override // java.lang.Runnable
            public void run() {
                if (QtMessageDialogHelper.this.m_dialog != null && QtMessageDialogHelper.this.m_dialog.isShowing()) {
                    QtMessageDialogHelper.this.m_dialog.dismiss();
                }
                QtMessageDialogHelper.this.reset();
            }
        });
    }

    public long handler() {
        return this.m_handler;
    }

    public void reset() {
        this.m_icon = 0;
        this.m_title = null;
        this.m_text = null;
        this.m_informativeText = null;
        this.m_detailedText = null;
        this.m_buttonsList = null;
        this.m_dialog = null;
        this.m_handler = 0L;
    }
}
