package org.qtproject.qt5.android;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class QtNative {
    public static final int IdCursorHandle = 1;
    public static final int IdLeftHandle = 2;
    public static final int IdRightHandle = 3;
    public static final String QtTAG = "Qt JAVA";
    private static final int m_moveThreshold = 0;
    private static int m_oldx;
    private static int m_oldy;
    private static Activity m_activity = null;
    private static boolean m_activityPaused = false;
    private static Service m_service = null;
    private static QtActivityDelegate m_activityDelegate = null;
    private static QtServiceDelegate m_serviceDelegate = null;
    public static Object m_mainActivityMutex = new Object();
    private static ArrayList<Runnable> m_lostActions = new ArrayList<>();
    private static boolean m_started = false;
    private static int m_displayMetricsScreenWidthPixels = 0;
    private static int m_displayMetricsScreenHeightPixels = 0;
    private static int m_displayMetricsDesktopWidthPixels = 0;
    private static int m_displayMetricsDesktopHeightPixels = 0;
    private static double m_displayMetricsXDpi = 0.0d;
    private static double m_displayMetricsYDpi = 0.0d;
    private static double m_displayMetricsScaledDensity = 1.0d;
    private static double m_displayMetricsDensity = 1.0d;
    private static ClipboardManager m_clipboardManager = null;
    private static Method m_checkSelfPermissionMethod = null;
    private static Boolean m_tabletEventSupported = null;
    private static boolean m_usePrimaryClip = false;
    public static QtThread m_qtThread = new QtThread();
    private static Method m_addItemMethod = null;
    private static final Runnable runPendingCppRunnablesRunnable = new Runnable() { // from class: org.qtproject.qt5.android.QtNative.1
        @Override // java.lang.Runnable
        public void run() {
            QtNative.runPendingCppRunnables();
        }
    };
    private static ClassLoader m_classLoader = null;

    public static native boolean dispatchGenericMotionEvent(MotionEvent motionEvent);

    public static native boolean dispatchKeyEvent(KeyEvent keyEvent);

    public static native void fillContextMenu(Menu menu);

    public static native void handleLocationChanged(int i, int i2, int i3);

    public static native void handleOrientationChanged(int i, int i2);

    public static native boolean isTabletEventSupported();

    public static native void keyDown(int i, int i2, int i3, boolean z);

    public static native void keyUp(int i, int i2, int i3, boolean z);

    public static native void keyboardGeometryChanged(int i, int i2, int i3, int i4);

    public static native void keyboardVisibilityChanged(boolean z);

    public static native void longPress(int i, int i2, int i3);

    public static native void mouseDown(int i, int i2, int i3);

    public static native void mouseMove(int i, int i2, int i3);

    public static native void mouseUp(int i, int i2, int i3);

    public static native void mouseWheel(int i, int i2, int i3, float f, float f2);

    public static native void onActivityResult(int i, int i2, Intent intent);

    public static native IBinder onBind(Intent intent);

    public static native void onClipboardDataChanged();

    public static native boolean onContextItemSelected(int i, boolean z);

    public static native void onContextMenuClosed(Menu menu);

    public static native void onCreateContextMenu(ContextMenu contextMenu);

    public static native void onNewIntent(Intent intent);

    public static native boolean onOptionsItemSelected(int i, boolean z);

    public static native void onOptionsMenuClosed(Menu menu);

    public static native boolean onPrepareOptionsMenu(Menu menu);

    public static native void quitQtAndroidPlugin();

    public static native void quitQtCoreApplication();

    public static native void runPendingCppRunnables();

    public static native void sendRequestPermissionsResult(int i, String[] strArr, int[] iArr);

    public static native void setDisplayMetrics(int i, int i2, int i3, int i4, double d, double d2, double d3, double d4);

    private static native void setNativeActivity(Activity activity);

    private static native void setNativeService(Service service);

    public static native void setSurface(int i, Object obj, int i2, int i3);

    public static native boolean startQtAndroidPlugin(String str, String str2);

    public static native void startQtApplication();

    public static native void tabletEvent(int i, int i2, long j, int i3, int i4, int i5, float f, float f2, float f3);

    public static native void terminateQt();

    public static native void touchAdd(int i, int i2, int i3, boolean z, int i4, int i5, float f, float f2, float f3, float f4);

    public static native void touchBegin(int i);

    public static native void touchEnd(int i, int i2);

    public static native void updateApplicationState(int i);

    public static native void updateWindow();

    public static native void waitForServiceSetup();

    public static ClassLoader classLoader() {
        return m_classLoader;
    }

    public static void setClassLoader(ClassLoader classLoader) {
        m_classLoader = classLoader;
    }

    public static Activity activity() {
        Activity activity;
        synchronized (m_mainActivityMutex) {
            activity = m_activity;
        }
        return activity;
    }

    public static Service service() {
        Service service;
        synchronized (m_mainActivityMutex) {
            service = m_service;
        }
        return service;
    }

    public static QtActivityDelegate activityDelegate() {
        QtActivityDelegate qtActivityDelegate;
        synchronized (m_mainActivityMutex) {
            qtActivityDelegate = m_activityDelegate;
        }
        return qtActivityDelegate;
    }

    public static QtServiceDelegate serviceDelegate() {
        QtServiceDelegate qtServiceDelegate;
        synchronized (m_mainActivityMutex) {
            qtServiceDelegate = m_serviceDelegate;
        }
        return qtServiceDelegate;
    }

    public static boolean openURL(String str, String str2) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.VIEW", parse);
            if (!str2.isEmpty()) {
                intent.setDataAndType(parse, str2);
            }
            activity().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int openFdForContentUrl(Context context, String str, String str2) {
        try {
            return context.getContentResolver().openFileDescriptor(Uri.parse(str), str2).detachFd();
        } catch (FileNotFoundException e) {
            return -1;
        } catch (SecurityException e2) {
            Log.e(QtTAG, "Exception when opening file", e2);
            return -1;
        }
    }

    public static void loadQtLibraries(final ArrayList<String> arrayList) {
        m_qtThread.run(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.2
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList2 = arrayList;
                if (arrayList2 == null) {
                    return;
                }
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    try {
                        if (new File(str).exists()) {
                            System.load(str);
                        } else {
                            Log.i(QtNative.QtTAG, "Can't find '" + str + "'");
                        }
                    } catch (SecurityException e) {
                        Log.i(QtNative.QtTAG, "Can't load '" + str + "'", e);
                    } catch (Exception e2) {
                        Log.i(QtNative.QtTAG, "Can't load '" + str + "'", e2);
                    }
                }
            }
        });
    }

    public static void loadBundledLibraries(final ArrayList<String> arrayList, final String str) {
        m_qtThread.run(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.3
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList2 = arrayList;
                if (arrayList2 == null) {
                    return;
                }
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    try {
                        String str3 = "lib" + str2 + ".so";
                        File file = new File(str + str3);
                        if (!file.exists()) {
                            Log.i(QtNative.QtTAG, "Can't find '" + file.getAbsolutePath());
                            try {
                                ActivityInfo activityInfo = QtNative.m_activity.getPackageManager().getActivityInfo(QtNative.m_activity.getComponentName(), 128);
                                String str4 = "/system/lib/";
                                if (activityInfo.metaData.containsKey("android.app.system_libs_prefix")) {
                                    str4 = activityInfo.metaData.getString("android.app.system_libs_prefix");
                                }
                                file = new File(str4 + str3);
                            } catch (Exception e) {
                            }
                        }
                        if (file.exists()) {
                            System.load(file.getAbsolutePath());
                        } else {
                            Log.i(QtNative.QtTAG, "Can't find '" + file.getAbsolutePath());
                        }
                    } catch (Exception e2) {
                        Log.i(QtNative.QtTAG, "Can't load '" + str2 + "'", e2);
                    }
                }
            }
        });
    }

    public static String loadMainLibrary(final String str, final String str2) {
        final String[] strArr = {null};
        m_qtThread.run(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String str3 = "lib" + str + ".so";
                    File file = new File(str2 + str3);
                    if (!file.exists()) {
                        try {
                            ActivityInfo activityInfo = QtNative.m_activity.getPackageManager().getActivityInfo(QtNative.m_activity.getComponentName(), 128);
                            String str4 = "/system/lib/";
                            if (activityInfo.metaData.containsKey("android.app.system_libs_prefix")) {
                                str4 = activityInfo.metaData.getString("android.app.system_libs_prefix");
                            }
                            file = new File(str4 + str3);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    if (!file.exists()) {
                        return;
                    }
                    System.load(file.getAbsolutePath());
                    strArr[0] = file.getAbsolutePath();
                } catch (Exception e2) {
                    Log.e(QtNative.QtTAG, "Can't load '" + str + "'", e2);
                }
            }
        });
        return strArr[0];
    }

    public static void setActivity(Activity activity, QtActivityDelegate qtActivityDelegate) {
        synchronized (m_mainActivityMutex) {
            m_activity = activity;
            m_activityDelegate = qtActivityDelegate;
        }
    }

    public static void setService(Service service, QtServiceDelegate qtServiceDelegate) {
        synchronized (m_mainActivityMutex) {
            m_service = service;
            m_serviceDelegate = qtServiceDelegate;
        }
    }

    public static void setApplicationState(int i) {
        synchronized (m_mainActivityMutex) {
            if (i == 4) {
                m_activityPaused = false;
                Iterator<Runnable> it = m_lostActions.iterator();
                while (it.hasNext()) {
                    runAction(it.next());
                }
                m_lostActions.clear();
            } else {
                m_activityPaused = true;
            }
        }
        updateApplicationState(i);
    }

    private static void runAction(Runnable runnable) {
        synchronized (m_mainActivityMutex) {
            Looper mainLooper = Looper.getMainLooper();
            if (!((m_activityPaused || m_activity == null || mainLooper == null || !new Handler(mainLooper).post(runnable)) ? false : true)) {
                m_lostActions.add(runnable);
            }
        }
    }

    private static void runPendingCppRunnablesOnAndroidThread() {
        synchronized (m_mainActivityMutex) {
            if (m_activity != null) {
                if (!m_activityPaused) {
                    m_activity.runOnUiThread(runPendingCppRunnablesRunnable);
                } else {
                    runAction(runPendingCppRunnablesRunnable);
                }
            } else {
                Looper mainLooper = Looper.getMainLooper();
                if (mainLooper.getThread().equals(Thread.currentThread())) {
                    runPendingCppRunnablesRunnable.run();
                } else {
                    new Handler(mainLooper).post(runPendingCppRunnablesRunnable);
                }
            }
        }
    }

    private static void setViewVisibility(final View view, final boolean z) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.5
            @Override // java.lang.Runnable
            public void run() {
                view.setVisibility(z ? 0 : 8);
            }
        });
    }

    public static boolean startApplication(String str, final String str2, String str3) throws Exception {
        if (str == null) {
            str = "-platform\tandroid";
        }
        final boolean[] zArr = {false};
        synchronized (m_mainActivityMutex) {
            if (str.length() > 0 && !str.startsWith("\t")) {
                str = "\t" + str;
            }
            final String str4 = str3 + str;
            m_qtThread.run(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.6
                @Override // java.lang.Runnable
                public void run() {
                    zArr[0] = QtNative.startQtAndroidPlugin(str4, str2);
                    QtNative.setDisplayMetrics(QtNative.m_displayMetricsScreenWidthPixels, QtNative.m_displayMetricsScreenHeightPixels, QtNative.m_displayMetricsDesktopWidthPixels, QtNative.m_displayMetricsDesktopHeightPixels, QtNative.m_displayMetricsXDpi, QtNative.m_displayMetricsYDpi, QtNative.m_displayMetricsScaledDensity, QtNative.m_displayMetricsDensity);
                }
            });
            m_qtThread.post(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.7
                @Override // java.lang.Runnable
                public void run() {
                    QtNative.startQtApplication();
                }
            });
            waitForServiceSetup();
            m_started = true;
        }
        return zArr[0];
    }

    public static void setApplicationDisplayMetrics(int i, int i2, int i3, int i4, double d, double d2, double d3, double d4) {
        double d5;
        double d6;
        if (d >= 120.0d) {
            d5 = d;
        } else {
            d5 = 120.0d;
        }
        if (d2 >= 120.0d) {
            d6 = d2;
        } else {
            d6 = 120.0d;
        }
        synchronized (m_mainActivityMutex) {
            if (m_started) {
                setDisplayMetrics(i, i2, i3, i4, d5, d6, d3, d4);
            } else {
                m_displayMetricsScreenWidthPixels = i;
                m_displayMetricsScreenHeightPixels = i2;
                m_displayMetricsDesktopWidthPixels = i3;
                m_displayMetricsDesktopHeightPixels = i4;
                m_displayMetricsXDpi = d5;
                m_displayMetricsYDpi = d6;
                m_displayMetricsScaledDensity = d3;
                m_displayMetricsDensity = d4;
            }
        }
    }

    private static void quitApp() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.8
            @Override // java.lang.Runnable
            public void run() {
                QtNative.quitQtAndroidPlugin();
                if (QtNative.m_activity != null) {
                    QtNative.m_activity.finish();
                }
                if (QtNative.m_service != null) {
                    QtNative.m_service.stopSelf();
                }
            }
        });
    }

    private static int getAction(int i, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2) {
            int historySize = motionEvent.getHistorySize();
            if (historySize > 0) {
                float x = motionEvent.getX(i);
                float y = motionEvent.getY(i);
                for (int i2 = 0; i2 < historySize; i2++) {
                    if (motionEvent.getHistoricalX(i, i2) != x || motionEvent.getHistoricalY(i, i2) != y) {
                        return 1;
                    }
                }
                return 2;
            }
            return 1;
        } else if (actionMasked == 0 || (actionMasked == 5 && i == motionEvent.getActionIndex())) {
            return 0;
        } else {
            if (actionMasked != 1) {
                return (actionMasked == 6 && i == motionEvent.getActionIndex()) ? 3 : 2;
            }
            return 3;
        }
    }

    public static void sendTouchEvent(MotionEvent motionEvent, int i) {
        int i2;
        if (m_tabletEventSupported == null) {
            m_tabletEventSupported = Boolean.valueOf(isTabletEventSupported());
        }
        int toolType = motionEvent.getToolType(0);
        if (toolType == 2) {
            i2 = 1;
        } else {
            i2 = toolType != 4 ? 0 : 3;
        }
        if (motionEvent.getToolType(0) == 3) {
            sendMouseEvent(motionEvent, i);
        } else if (m_tabletEventSupported.booleanValue() && i2 != 0) {
            tabletEvent(i, motionEvent.getDeviceId(), motionEvent.getEventTime(), motionEvent.getAction(), i2, motionEvent.getButtonState(), motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure());
        } else {
            touchBegin(i);
            int i3 = 0;
            while (i3 < motionEvent.getPointerCount()) {
                touchAdd(i, motionEvent.getPointerId(i3), getAction(i3, motionEvent), i3 == 0, (int) motionEvent.getX(i3), (int) motionEvent.getY(i3), motionEvent.getTouchMajor(i3), motionEvent.getTouchMinor(i3), motionEvent.getOrientation(i3), motionEvent.getPressure(i3));
                i3++;
            }
            int action = motionEvent.getAction();
            if (action == 0) {
                touchEnd(i, 0);
            } else if (action == 1) {
                touchEnd(i, 2);
            } else {
                touchEnd(i, 1);
            }
        }
    }

    public static void sendTrackballEvent(MotionEvent motionEvent, int i) {
        sendMouseEvent(motionEvent, i);
    }

    public static boolean sendGenericMotionEvent(MotionEvent motionEvent, int i) {
        if ((motionEvent.getAction() & 15) == 0 || (motionEvent.getSource() & 2) != 2) {
            return false;
        }
        return sendMouseEvent(motionEvent, i);
    }

    public static boolean sendMouseEvent(MotionEvent motionEvent, int i) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            mouseDown(i, (int) motionEvent.getX(), (int) motionEvent.getY());
            m_oldx = (int) motionEvent.getX();
            m_oldy = (int) motionEvent.getY();
        } else if (actionMasked == 1) {
            mouseUp(i, (int) motionEvent.getX(), (int) motionEvent.getY());
        } else if (actionMasked == 2 || actionMasked == 7) {
            if (motionEvent.getToolType(0) == 3) {
                mouseMove(i, (int) motionEvent.getX(), (int) motionEvent.getY());
            } else {
                int y = (int) (motionEvent.getY() - m_oldy);
                if (Math.abs((int) (motionEvent.getX() - m_oldx)) > 5 || Math.abs(y) > 5) {
                    mouseMove(i, (int) motionEvent.getX(), (int) motionEvent.getY());
                    m_oldx = (int) motionEvent.getX();
                    m_oldy = (int) motionEvent.getY();
                }
            }
        } else if (actionMasked != 8) {
            return false;
        } else {
            mouseWheel(i, (int) motionEvent.getX(), (int) motionEvent.getY(), motionEvent.getAxisValue(10), motionEvent.getAxisValue(9));
        }
        return true;
    }

    public static Context getContext() {
        Activity activity = m_activity;
        if (activity != null) {
            return activity;
        }
        return m_service;
    }

    public static int checkSelfPermission(String str) {
        int i;
        synchronized (m_mainActivityMutex) {
            Context context = getContext();
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (m_checkSelfPermissionMethod == null) {
                        m_checkSelfPermissionMethod = Context.class.getMethod("checkSelfPermission", String.class);
                    }
                    i = ((Integer) m_checkSelfPermissionMethod.invoke(context, str)).intValue();
                } else {
                    i = context.getPackageManager().checkPermission(str, context.getApplicationContext().getPackageName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                i = -1;
            }
        }
        return i;
    }

    private static void updateSelection(final int i, final int i2, final int i3, final int i4) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.9
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.updateSelection(i, i2, i3, i4);
                }
            }
        });
    }

    private static void updateHandles(final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final int i8, final boolean z) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.10
            @Override // java.lang.Runnable
            public void run() {
                QtNative.m_activityDelegate.updateHandles(i, i2, i3, i4, i5, i6, i7, i8, z);
            }
        });
    }

    private static void showSoftwareKeyboard(final int i, final int i2, final int i3, final int i4, final int i5, final int i6) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.11
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.showSoftwareKeyboard(i, i2, i3, i4, i5, i6);
                }
            }
        });
    }

    private static void resetSoftwareKeyboard() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.12
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.resetSoftwareKeyboard();
                }
            }
        });
    }

    private static void hideSoftwareKeyboard() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.13
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.hideSoftwareKeyboard();
                }
            }
        });
    }

    private static void setFullScreen(final boolean z) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.14
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.setFullScreen(z);
                }
                QtNative.updateWindow();
            }
        });
    }

    private static void registerClipboardManager() {
        if (m_service == null || m_activity != null) {
            final Semaphore semaphore = new Semaphore(0);
            runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.15
                @Override // java.lang.Runnable
                public void run() {
                    if (QtNative.m_activity != null) {
                        ClipboardManager unused = QtNative.m_clipboardManager = (ClipboardManager) QtNative.m_activity.getSystemService("clipboard");
                    }
                    if (QtNative.m_clipboardManager != null) {
                        QtNative.m_clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() { // from class: org.qtproject.qt5.android.QtNative.15.1
                            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
                            public void onPrimaryClipChanged() {
                                QtNative.onClipboardDataChanged();
                            }
                        });
                    }
                    semaphore.release();
                }
            });
            try {
                semaphore.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearClipData() {
        m_usePrimaryClip = false;
    }

    private static void setClipboardText(String str) {
        if (m_clipboardManager != null) {
            updatePrimaryClip(ClipData.newPlainText("text/plain", str));
        }
    }

    public static boolean hasClipboardText() {
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getText() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getClipboardText() {
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getText() != null) {
                    return primaryClip.getItemAt(i).getText().toString();
                }
            }
            return BuildConfig.FLAVOR;
        }
        return BuildConfig.FLAVOR;
    }

    private static void updatePrimaryClip(ClipData clipData) {
        if (m_usePrimaryClip) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            if (Build.VERSION.SDK_INT >= 26 && m_addItemMethod == null) {
                try {
                    m_addItemMethod = m_clipboardManager.getClass().getMethod("addItem", ContentResolver.class, ClipData.Item.class);
                } catch (Exception e) {
                }
            }
            Method method = m_addItemMethod;
            if (method != null) {
                try {
                    method.invoke(m_activity.getContentResolver(), clipData.getItemAt(0));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else {
                primaryClip.addItem(clipData.getItemAt(0));
            }
            m_clipboardManager.setPrimaryClip(primaryClip);
            return;
        }
        m_clipboardManager.setPrimaryClip(clipData);
        m_usePrimaryClip = true;
    }

    private static void setClipboardHtml(String str, String str2) {
        if (m_clipboardManager != null) {
            updatePrimaryClip(ClipData.newHtmlText("text/html", str, str2));
        }
    }

    public static boolean hasClipboardHtml() {
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getHtmlText() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getClipboardHtml() {
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getHtmlText() != null) {
                    return primaryClip.getItemAt(i).getHtmlText().toString();
                }
            }
            return BuildConfig.FLAVOR;
        }
        return BuildConfig.FLAVOR;
    }

    private static void setClipboardUri(String str) {
        if (m_clipboardManager != null) {
            updatePrimaryClip(ClipData.newUri(m_activity.getContentResolver(), "text/uri-list", Uri.parse(str)));
        }
    }

    public static boolean hasClipboardUri() {
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getUri() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String[] getClipboardUris() {
        ArrayList arrayList = new ArrayList();
        ClipboardManager clipboardManager = m_clipboardManager;
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData primaryClip = m_clipboardManager.getPrimaryClip();
            for (int i = 0; i < primaryClip.getItemCount(); i++) {
                if (primaryClip.getItemAt(i).getUri() != null) {
                    arrayList.add(primaryClip.getItemAt(i).getUri().toString());
                }
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private static void openContextMenu(final int i, final int i2, final int i3, final int i4) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.16
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.openContextMenu(i, i2, i3, i4);
                }
            }
        });
    }

    private static void closeContextMenu() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.17
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.closeContextMenu();
                }
            }
        });
    }

    private static void resetOptionsMenu() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.18
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.resetOptionsMenu();
                }
            }
        });
    }

    private static void openOptionsMenu() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.19
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activity != null) {
                    QtNative.m_activity.openOptionsMenu();
                }
            }
        });
    }

    private static byte[][] getSSLCertificates() {
        TrustManager[] trustManagers;
        ArrayList arrayList = new ArrayList();
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
                if (trustManager instanceof X509TrustManager) {
                    for (X509Certificate x509Certificate : ((X509TrustManager) trustManager).getAcceptedIssuers()) {
                        arrayList.add(x509Certificate.getEncoded());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(QtTAG, "Failed to get certificates", e);
        }
        return (byte[][]) arrayList.toArray(new byte[arrayList.size()]);
    }

    private static void createSurface(final int i, final boolean z, final int i2, final int i3, final int i4, final int i5, final int i6) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.20
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.createSurface(i, z, i2, i3, i4, i5, i6);
                }
            }
        });
    }

    private static void insertNativeView(final int i, final View view, final int i2, final int i3, final int i4, final int i5) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.21
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.insertNativeView(i, view, i2, i3, i4, i5);
                }
            }
        });
    }

    private static void setSurfaceGeometry(final int i, final int i2, final int i3, final int i4, final int i5) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.22
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.setSurfaceGeometry(i, i2, i3, i4, i5);
                }
            }
        });
    }

    private static void bringChildToFront(final int i) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.23
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.bringChildToFront(i);
                }
            }
        });
    }

    private static void bringChildToBack(final int i) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.24
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.bringChildToBack(i);
                }
            }
        });
    }

    private static void destroySurface(final int i) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.25
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.destroySurface(i);
                }
            }
        });
    }

    private static void initializeAccessibility() {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.26
            @Override // java.lang.Runnable
            public void run() {
                QtNative.m_activityDelegate.initializeAccessibility();
            }
        });
    }

    private static void hideSplashScreen(final int i) {
        runAction(new Runnable() { // from class: org.qtproject.qt5.android.QtNative.27
            @Override // java.lang.Runnable
            public void run() {
                if (QtNative.m_activityDelegate != null) {
                    QtNative.m_activityDelegate.hideSplashScreen(i);
                }
            }
        });
    }

    private static String[] listAssetContent(AssetManager assetManager, String str) {
        ArrayList arrayList = new ArrayList();
        try {
            String[] list = assetManager.list(str);
            if (list.length > 0) {
                for (String str2 : list) {
                    try {
                        String[] list2 = assetManager.list(str.length() > 0 ? str + "/" + str2 : str2);
                        if (list2 != null && list2.length > 0) {
                            str2 = str2 + "/";
                        }
                        arrayList.add(str2);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e2) {
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
