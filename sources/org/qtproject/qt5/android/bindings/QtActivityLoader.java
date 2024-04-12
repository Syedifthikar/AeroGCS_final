package org.qtproject.qt5.android.bindings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import java.lang.reflect.Field;

/* loaded from: classes2.dex */
public class QtActivityLoader extends QtLoader {
    QtActivity m_activity;

    public QtActivityLoader(QtActivity activity) {
        super(activity, QtActivity.class);
        this.m_activity = activity;
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected void downloadUpgradeMinistro(String msg) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this.m_activity);
        downloadDialog.setMessage(msg);
        downloadDialog.setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: org.qtproject.qt5.android.bindings.QtActivityLoader.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Uri uri = Uri.parse("market://details?id=org.kde.necessitas.ministro");
                    Intent intent = new Intent("android.intent.action.VIEW", uri);
                    QtActivityLoader.this.m_activity.startActivityForResult(intent, QtLoader.MINISTRO_INSTALL_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    QtActivityLoader.this.ministroNotFound();
                }
            }
        });
        downloadDialog.setNegativeButton(17039369, new DialogInterface.OnClickListener() { // from class: org.qtproject.qt5.android.bindings.QtActivityLoader.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                QtActivityLoader.this.m_activity.finish();
            }
        });
        downloadDialog.show();
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected String loaderClassName() {
        return "org.qtproject.qt5.android.QtActivityDelegate";
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected Class<?> contextClassName() {
        return Activity.class;
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected void finish() {
        this.m_activity.finish();
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected String getTitle() {
        return (String) this.m_activity.getTitle();
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected void runOnUiThread(Runnable run) {
        this.m_activity.runOnUiThread(run);
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    Intent getIntent() {
        return this.m_activity.getIntent();
    }

    public void onCreate(Bundle savedInstanceState) {
        Field[] declaredFields;
        try {
            this.m_contextInfo = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128);
            int theme = ((ActivityInfo) this.m_contextInfo).getThemeResource();
            for (Field f : Class.forName("android.R$style").getDeclaredFields()) {
                if (f.getInt(null) == theme) {
                    this.QT_ANDROID_THEMES = new String[]{f.getName()};
                    this.QT_ANDROID_DEFAULT_THEME = f.getName();
                    break;
                }
            }
            try {
                this.m_activity.setTheme(Class.forName("android.R$style").getDeclaredField(this.QT_ANDROID_DEFAULT_THEME).getInt(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.m_activity.requestWindowFeature(8);
            if (QtApplication.m_delegateObject != null && QtApplication.onCreate != null) {
                QtApplication.invokeDelegateMethod(QtApplication.onCreate, savedInstanceState);
                return;
            }
            this.m_displayDensity = this.m_activity.getResources().getDisplayMetrics().densityDpi;
            this.ENVIRONMENT_VARIABLES += "\tQT_ANDROID_THEME=" + this.QT_ANDROID_DEFAULT_THEME + "/\tQT_ANDROID_THEME_DISPLAY_DPI=" + this.m_displayDensity + "\t";
            if (this.m_activity.getLastNonConfigurationInstance() == null) {
                if (this.m_contextInfo.metaData.containsKey("android.app.background_running") && this.m_contextInfo.metaData.getBoolean("android.app.background_running")) {
                    this.ENVIRONMENT_VARIABLES += "QT_BLOCK_EVENT_LOOPS_WHEN_SUSPENDED=0\t";
                } else {
                    this.ENVIRONMENT_VARIABLES += "QT_BLOCK_EVENT_LOOPS_WHEN_SUSPENDED=1\t";
                }
                if (this.m_contextInfo.metaData.containsKey("android.app.auto_screen_scale_factor") && this.m_contextInfo.metaData.getBoolean("android.app.auto_screen_scale_factor")) {
                    this.ENVIRONMENT_VARIABLES += "QT_AUTO_SCREEN_SCALE_FACTOR=1\t";
                }
                startApp(true);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            finish();
        }
    }
}
