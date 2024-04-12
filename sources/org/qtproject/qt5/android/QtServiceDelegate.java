package org.qtproject.qt5.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import java.util.ArrayList;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class QtServiceDelegate {
    private static final String APPLICATION_PARAMETERS_KEY = "application.parameters";
    private static final String APP_DISPLAY_METRIC_SCREEN_DENSITY_KEY = "display.screen.density";
    private static final String APP_DISPLAY_METRIC_SCREEN_DESKTOP_KEY = "display.screen.desktop";
    private static final String APP_DISPLAY_METRIC_SCREEN_XDPI_KEY = "display.screen.dpi.x";
    private static final String APP_DISPLAY_METRIC_SCREEN_YDPI_KEY = "display.screen.dpi.y";
    private static final String BUNDLED_LIBRARIES_KEY = "bundled.libraries";
    private static final String ENVIRONMENT_VARIABLES_KEY = "environment.variables";
    private static final String MAIN_LIBRARY_KEY = "main.library";
    private static final String NATIVE_LIBRARIES_KEY = "native.libraries";
    private static final String STATIC_INIT_CLASSES_KEY = "static.init.classes";
    private String m_mainLib = null;
    private Service m_service = null;
    private static String m_environmentVariables = null;
    private static String m_applicationParameters = null;

    public boolean loadApplication(Service service, ClassLoader classLoader, Bundle bundle) {
        String[] stringArray;
        if (bundle.containsKey("native.libraries") && bundle.containsKey("bundled.libraries")) {
            this.m_service = service;
            QtNative.setService(service, this);
            QtNative.setClassLoader(classLoader);
            QtNative.setApplicationDisplayMetrics(10, 10, 10, 10, 120.0d, 120.0d, 1.0d, 1.0d);
            if (bundle.containsKey("static.init.classes")) {
                for (String str : bundle.getStringArray("static.init.classes")) {
                    if (str.length() != 0) {
                        try {
                            Class<?> loadClass = classLoader.loadClass(str);
                            Object newInstance = loadClass.newInstance();
                            try {
                                loadClass.getMethod("setService", Service.class, Object.class).invoke(newInstance, this.m_service, this);
                            } catch (Exception e) {
                            }
                            try {
                                loadClass.getMethod("setContext", Context.class).invoke(newInstance, this.m_service);
                            } catch (Exception e2) {
                            }
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }
            QtNative.loadQtLibraries(bundle.getStringArrayList("native.libraries"));
            ArrayList<String> stringArrayList = bundle.getStringArrayList("bundled.libraries");
            String nativeLibrariesDir = QtNativeLibrariesDir.nativeLibrariesDir(this.m_service);
            QtNative.loadBundledLibraries(stringArrayList, nativeLibrariesDir);
            this.m_mainLib = bundle.getString("main.library");
            m_environmentVariables = bundle.getString("environment.variables");
            String str2 = "QT_ANDROID_FONTS_MONOSPACE=Droid Sans Mono;Droid Sans;Droid Sans Fallback\tQT_ANDROID_FONTS_SERIF=Droid Serif\tHOME=" + this.m_service.getFilesDir().getAbsolutePath() + "\tTMPDIR=" + this.m_service.getFilesDir().getAbsolutePath();
            String str3 = Build.VERSION.SDK_INT < 14 ? str2 + "\tQT_ANDROID_FONTS=Droid Sans;Droid Sans Fallback" : str2 + "\tQT_ANDROID_FONTS=Roboto;Droid Sans;Droid Sans Fallback";
            String str4 = m_environmentVariables;
            if (str4 != null && str4.length() > 0) {
                m_environmentVariables = str3 + "\t" + m_environmentVariables;
            } else {
                m_environmentVariables = str3;
            }
            if (bundle.containsKey("application.parameters")) {
                m_applicationParameters = bundle.getString("application.parameters");
            } else {
                m_applicationParameters = BuildConfig.FLAVOR;
            }
            String loadMainLibrary = QtNative.loadMainLibrary(this.m_mainLib, nativeLibrariesDir);
            this.m_mainLib = loadMainLibrary;
            return loadMainLibrary != null;
        }
        return false;
    }

    public boolean startApplication() {
        try {
            QtNativeLibrariesDir.nativeLibrariesDir(this.m_service);
            QtNative.startApplication(m_applicationParameters, m_environmentVariables, this.m_mainLib);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onDestroy() {
        QtNative.quitQtCoreApplication();
        QtNative.terminateQt();
        QtNative.setService(null, null);
        QtNative.m_qtThread.exit();
        System.exit(0);
    }

    public IBinder onBind(Intent intent) {
        IBinder onBind;
        synchronized (this) {
            onBind = QtNative.onBind(intent);
        }
        return onBind;
    }
}
