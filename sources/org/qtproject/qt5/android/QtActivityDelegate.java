package org.qtproject.qt5.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.method.MetaKeyKeyListener;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.pdrl.AeroGCS.BuildConfig;
import org.qtproject.qt5.android.QtLayout;
import org.qtproject.qt5.android.accessibility.QtAccessibilityDelegate;

/* loaded from: classes.dex */
public class QtActivityDelegate {
    private static final String APPLICATION_PARAMETERS_KEY = "application.parameters";
    public static final int ApplicationActive = 4;
    public static final int ApplicationHidden = 1;
    public static final int ApplicationInactive = 2;
    public static final int ApplicationSuspended = 0;
    private static final String BUNDLED_LIBRARIES_KEY = "bundled.libraries";
    private static final int CursorHandleNotShown = 0;
    private static final int CursorHandleShowEdit = 256;
    private static final int CursorHandleShowNormal = 1;
    private static final int CursorHandleShowSelection = 2;
    private static final String ENVIRONMENT_VARIABLES_KEY = "environment.variables";
    private static final String EXTRACT_STYLE_KEY = "extract.android.style";
    private static final String EXTRACT_STYLE_MINIMAL_KEY = "extract.android.style.option";
    private static final String MAIN_LIBRARY_KEY = "main.library";
    private static final String NATIVE_LIBRARIES_KEY = "native.libraries";
    private static final String NECESSITAS_API_LEVEL_KEY = "necessitas.api.level";
    private static final String STATIC_INIT_CLASSES_KEY = "static.init.classes";
    private CursorHandle m_cursorHandle;
    private EditPopupMenu m_editPopupMenu;
    private CursorHandle m_leftSelectionHandle;
    private String m_mainLib;
    private long m_metaState;
    private CursorHandle m_rightSelectionHandle;
    private static String m_environmentVariables = null;
    private static String m_applicationParameters = null;
    private Activity m_activity = null;
    private Method m_super_dispatchKeyEvent = null;
    private Method m_super_onRestoreInstanceState = null;
    private Method m_super_onRetainNonConfigurationInstance = null;
    private Method m_super_onSaveInstanceState = null;
    private Method m_super_onKeyDown = null;
    private Method m_super_onKeyUp = null;
    private Method m_super_onConfigurationChanged = null;
    private Method m_super_onActivityResult = null;
    private Method m_super_dispatchGenericMotionEvent = null;
    private Method m_super_onWindowFocusChanged = null;
    private int m_currentRotation = -1;
    private int m_nativeOrientation = 0;
    private int m_lastChar = 0;
    private int m_softInputMode = 0;
    private boolean m_fullScreen = false;
    private boolean m_started = false;
    private HashMap<Integer, QtSurface> m_surfaces = null;
    private HashMap<Integer, View> m_nativeViews = null;
    private QtLayout m_layout = null;
    private ImageView m_splashScreen = null;
    private boolean m_splashScreenSticky = false;
    private QtEditText m_editText = null;
    private InputMethodManager m_imm = null;
    private boolean m_quitApp = true;
    private View m_dummyView = null;
    private boolean m_keyboardIsVisible = false;
    public boolean m_backKeyPressedSent = false;
    private long m_showHideTimeStamp = System.nanoTime();
    private int m_portraitKeyboardHeight = 0;
    private int m_landscapeKeyboardHeight = 0;
    private int m_probeKeyboardHeightDelay = 50;
    private final int ImhHiddenText = 1;
    private final int ImhSensitiveData = 2;
    private final int ImhNoAutoUppercase = 4;
    private final int ImhPreferNumbers = 8;
    private final int ImhPreferUppercase = 16;
    private final int ImhPreferLowercase = 32;
    private final int ImhNoPredictiveText = 64;
    private final int ImhDate = 128;
    private final int ImhTime = CursorHandleShowEdit;
    private final int ImhPreferLatin = 512;
    private final int ImhMultiLine = 1024;
    private final int ImhDigitsOnly = 65536;
    private final int ImhFormattedNumbersOnly = 131072;
    private final int ImhUppercaseOnly = 262144;
    private final int ImhLowercaseOnly = 524288;
    private final int ImhDialableCharactersOnly = 1048576;
    private final int ImhEmailCharactersOnly = 2097152;
    private final int ImhUrlCharactersOnly = 4194304;
    private final int ImhLatinOnly = 8388608;
    private final int EnterKeyDefault = 0;
    private final int EnterKeyReturn = 1;
    private final int EnterKeyDone = 2;
    private final int EnterKeyGo = 3;
    private final int EnterKeySend = 4;
    private final int EnterKeySearch = 5;
    private final int EnterKeyNext = 6;
    private final int EnterKeyPrevious = 7;
    private boolean m_optionsMenuIsVisible = false;
    private boolean m_contextMenuVisible = false;

    public void setFullScreen(boolean z) {
        if (this.m_fullScreen == z) {
            return;
        }
        this.m_fullScreen = z;
        if (z) {
            this.m_activity.getWindow().addFlags(1024);
            this.m_activity.getWindow().clearFlags(2048);
            try {
                this.m_activity.getWindow().getDecorView().setSystemUiVisibility(1798 | View.class.getDeclaredField("SYSTEM_UI_FLAG_IMMERSIVE_STICKY").getInt(null) | 4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.m_activity.getWindow().addFlags(2048);
            this.m_activity.getWindow().clearFlags(1024);
            this.m_activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
        this.m_layout.requestLayout();
    }

    public void updateFullScreen() {
        if (this.m_fullScreen) {
            this.m_fullScreen = false;
            setFullScreen(true);
        }
    }

    public boolean setKeyboardVisibility(boolean z, long j) {
        if (this.m_showHideTimeStamp > j) {
            return false;
        }
        this.m_showHideTimeStamp = j;
        if (this.m_keyboardIsVisible == z) {
            return false;
        }
        this.m_keyboardIsVisible = z;
        QtNative.keyboardVisibilityChanged(z);
        if (!z) {
            updateFullScreen();
            return true;
        }
        return true;
    }

    public void resetSoftwareKeyboard() {
        if (this.m_imm == null) {
            return;
        }
        this.m_editText.postDelayed(new Runnable() { // from class: org.qtproject.qt5.android.QtActivityDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                QtActivityDelegate.this.m_imm.restartInput(QtActivityDelegate.this.m_editText);
                QtActivityDelegate.this.m_editText.m_optionsChanged = false;
            }
        }, 5L);
    }

    /* JADX WARN: Removed duplicated region for block: B:442:0x011e  */
    /* JADX WARN: Removed duplicated region for block: B:445:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:446:0x012d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void showSoftwareKeyboard(int r16, int r17, int r18, int r19, int r20, int r21) {
        /*
            Method dump skipped, instructions count: 408
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.qtproject.qt5.android.QtActivityDelegate.showSoftwareKeyboard(int, int, int, int, int, int):void");
    }

    /* renamed from: org.qtproject.qt5.android.QtActivityDelegate$2 */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements Runnable {
        final /* synthetic */ int val$enterKeyType;
        final /* synthetic */ int val$height;
        final /* synthetic */ int val$inputHints;
        final /* synthetic */ int val$width;
        final /* synthetic */ int val$x;
        final /* synthetic */ int val$y;

        AnonymousClass2(int i, int i2, int i3, int i4, int i5, int i6) {
            QtActivityDelegate.this = r1;
            this.val$x = i;
            this.val$y = i2;
            this.val$width = i3;
            this.val$height = i4;
            this.val$inputHints = i5;
            this.val$enterKeyType = i6;
        }

        @Override // java.lang.Runnable
        public void run() {
            QtActivityDelegate.this.m_imm.showSoftInput(QtActivityDelegate.this.m_editText, 0, new ResultReceiver(new Handler()) { // from class: org.qtproject.qt5.android.QtActivityDelegate.2.1
                @Override // android.os.ResultReceiver
                protected void onReceiveResult(int i, Bundle bundle) {
                    if (i != 0) {
                        if (i != 1) {
                            if (i == 2) {
                                QtNativeInputConnection.updateCursorPosition();
                            } else if (i != 3) {
                                return;
                            }
                        }
                        QtActivityDelegate.this.setKeyboardVisibility(false, System.nanoTime());
                        return;
                    }
                    QtActivityDelegate.this.setKeyboardVisibility(true, System.nanoTime());
                    if (QtActivityDelegate.this.m_softInputMode == 0) {
                        QtActivityDelegate.this.m_layout.postDelayed(new Runnable() { // from class: org.qtproject.qt5.android.QtActivityDelegate.2.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (!QtActivityDelegate.this.m_keyboardIsVisible) {
                                    return;
                                }
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                QtActivityDelegate.this.m_activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                Rect rect = new Rect();
                                QtActivityDelegate.this.m_activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                                if (displayMetrics.heightPixels == rect.bottom) {
                                    if (QtActivityDelegate.this.m_probeKeyboardHeightDelay < 1000) {
                                        QtActivityDelegate.this.m_probeKeyboardHeightDelay *= 2;
                                    }
                                } else if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                                    if (QtActivityDelegate.this.m_landscapeKeyboardHeight != rect.bottom) {
                                        QtActivityDelegate.this.m_landscapeKeyboardHeight = rect.bottom;
                                        QtActivityDelegate.this.showSoftwareKeyboard(AnonymousClass2.this.val$x, AnonymousClass2.this.val$y, AnonymousClass2.this.val$width, AnonymousClass2.this.val$height, AnonymousClass2.this.val$inputHints, AnonymousClass2.this.val$enterKeyType);
                                    }
                                } else if (QtActivityDelegate.this.m_portraitKeyboardHeight != rect.bottom) {
                                    QtActivityDelegate.this.m_portraitKeyboardHeight = rect.bottom;
                                    QtActivityDelegate.this.showSoftwareKeyboard(AnonymousClass2.this.val$x, AnonymousClass2.this.val$y, AnonymousClass2.this.val$width, AnonymousClass2.this.val$height, AnonymousClass2.this.val$inputHints, AnonymousClass2.this.val$enterKeyType);
                                }
                            }
                        }, QtActivityDelegate.this.m_probeKeyboardHeightDelay);
                    }
                }
            });
            if (QtActivityDelegate.this.m_editText.m_optionsChanged) {
                QtActivityDelegate.this.m_imm.restartInput(QtActivityDelegate.this.m_editText);
                QtActivityDelegate.this.m_editText.m_optionsChanged = false;
            }
        }
    }

    public void hideSoftwareKeyboard() {
        InputMethodManager inputMethodManager = this.m_imm;
        if (inputMethodManager == null) {
            return;
        }
        inputMethodManager.hideSoftInputFromWindow(this.m_editText.getWindowToken(), 0, new ResultReceiver(new Handler()) { // from class: org.qtproject.qt5.android.QtActivityDelegate.3
            @Override // android.os.ResultReceiver
            protected void onReceiveResult(int i, Bundle bundle) {
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            if (i != 3) {
                                return;
                            }
                        }
                    }
                    QtActivityDelegate.this.setKeyboardVisibility(false, System.nanoTime());
                    return;
                }
                QtActivityDelegate.this.setKeyboardVisibility(true, System.nanoTime());
            }
        });
    }

    String getAppIconSize(Activity activity) {
        int dimensionPixelSize = activity.getResources().getDimensionPixelSize(17104896);
        if (dimensionPixelSize < 36 || dimensionPixelSize > 512) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = (displayMetrics.densityDpi / 10) * 3;
            if (i >= 36) {
                dimensionPixelSize = i;
            } else {
                dimensionPixelSize = 36;
            }
            if (dimensionPixelSize > 512) {
                dimensionPixelSize = 512;
            }
        }
        return "\tQT_ANDROID_APP_ICON_SIZE=" + dimensionPixelSize;
    }

    public void updateSelection(int i, int i2, int i3, int i4) {
        InputMethodManager inputMethodManager = this.m_imm;
        if (inputMethodManager == null) {
            return;
        }
        inputMethodManager.updateSelection(this.m_editText, i, i2, i3, i4);
    }

    public void updateHandles(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        int i9;
        int i10 = i;
        int i11 = i10 & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (i11 == 0) {
            CursorHandle cursorHandle = this.m_cursorHandle;
            if (cursorHandle != null) {
                cursorHandle.hide();
                this.m_cursorHandle = null;
            }
            CursorHandle cursorHandle2 = this.m_rightSelectionHandle;
            if (cursorHandle2 != null) {
                cursorHandle2.hide();
                this.m_leftSelectionHandle.hide();
                this.m_rightSelectionHandle = null;
                this.m_leftSelectionHandle = null;
            }
            EditPopupMenu editPopupMenu = this.m_editPopupMenu;
            if (editPopupMenu != null) {
                editPopupMenu.hide();
            }
        } else if (i11 == 1) {
            if (this.m_cursorHandle == null) {
                this.m_cursorHandle = new CursorHandle(this.m_activity, this.m_layout, 1, 16843463, false);
            }
            this.m_cursorHandle.setPosition(i5, i6);
            CursorHandle cursorHandle3 = this.m_rightSelectionHandle;
            if (cursorHandle3 != null) {
                cursorHandle3.hide();
                this.m_leftSelectionHandle.hide();
                this.m_rightSelectionHandle = null;
                this.m_leftSelectionHandle = null;
            }
        } else if (i11 == 2) {
            if (this.m_rightSelectionHandle == null) {
                this.m_leftSelectionHandle = new CursorHandle(this.m_activity, this.m_layout, 2, !z ? 16843461 : 16843462, z);
                this.m_rightSelectionHandle = new CursorHandle(this.m_activity, this.m_layout, 3, !z ? 16843462 : 16843461, z);
            }
            this.m_leftSelectionHandle.setPosition(i5, i6);
            this.m_rightSelectionHandle.setPosition(i7, i8);
            CursorHandle cursorHandle4 = this.m_cursorHandle;
            if (cursorHandle4 != null) {
                cursorHandle4.hide();
                this.m_cursorHandle = null;
            }
            i10 |= CursorHandleShowEdit;
        }
        if (QtNative.hasClipboardText()) {
            i9 = i4 | 4;
        } else {
            i9 = i4 & (-5);
        }
        if ((i10 & CursorHandleShowEdit) == CursorHandleShowEdit && i9 != 0) {
            this.m_editPopupMenu.setPosition(i2, i3, i9, this.m_cursorHandle, this.m_leftSelectionHandle, this.m_rightSelectionHandle);
            return;
        }
        EditPopupMenu editPopupMenu2 = this.m_editPopupMenu;
        if (editPopupMenu2 != null) {
            editPopupMenu2.hide();
        }
    }

    public boolean loadApplication(Activity activity, ClassLoader classLoader, Bundle bundle) {
        int i;
        if (bundle.containsKey("native.libraries") && bundle.containsKey("bundled.libraries") && bundle.containsKey("environment.variables")) {
            this.m_activity = activity;
            setActionBarVisibility(false);
            QtNative.setActivity(this.m_activity, this);
            QtNative.setClassLoader(classLoader);
            int i2 = 2;
            if (bundle.containsKey("static.init.classes")) {
                String[] stringArray = bundle.getStringArray("static.init.classes");
                int length = stringArray.length;
                int i3 = 0;
                while (i3 < length) {
                    String str = stringArray[i3];
                    if (str.length() != 0) {
                        try {
                            Class<?> loadClass = classLoader.loadClass(str);
                            Object newInstance = loadClass.newInstance();
                            try {
                                Class<?>[] clsArr = new Class[i2];
                                clsArr[0] = Activity.class;
                                clsArr[1] = Object.class;
                                Method method = loadClass.getMethod("setActivity", clsArr);
                                Object[] objArr = new Object[i2];
                                objArr[0] = this.m_activity;
                                objArr[1] = this;
                                method.invoke(newInstance, objArr);
                            } catch (Exception e) {
                            }
                            try {
                                loadClass.getMethod("setContext", Context.class).invoke(newInstance, this.m_activity);
                            } catch (Exception e2) {
                            }
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    i3++;
                    i2 = 2;
                }
            }
            QtNative.loadQtLibraries(bundle.getStringArrayList("native.libraries"));
            ArrayList<String> stringArrayList = bundle.getStringArrayList("bundled.libraries");
            String nativeLibrariesDir = QtNativeLibrariesDir.nativeLibrariesDir(this.m_activity);
            QtNative.loadBundledLibraries(stringArrayList, nativeLibrariesDir);
            String string = bundle.getString("main.library");
            this.m_mainLib = string;
            if (string == null && stringArrayList.size() > 0) {
                this.m_mainLib = stringArrayList.get(stringArrayList.size() - 1);
                stringArrayList.remove(stringArrayList.size() - 1);
            }
            if (bundle.containsKey("extract.android.style")) {
                new ExtractStyle(this.m_activity, bundle.getString("extract.android.style"), bundle.containsKey(EXTRACT_STYLE_MINIMAL_KEY) && bundle.getBoolean(EXTRACT_STYLE_MINIMAL_KEY));
            }
            try {
                this.m_super_dispatchKeyEvent = this.m_activity.getClass().getMethod("super_dispatchKeyEvent", KeyEvent.class);
                this.m_super_onRestoreInstanceState = this.m_activity.getClass().getMethod("super_onRestoreInstanceState", Bundle.class);
                this.m_super_onRetainNonConfigurationInstance = this.m_activity.getClass().getMethod("super_onRetainNonConfigurationInstance", new Class[0]);
                this.m_super_onSaveInstanceState = this.m_activity.getClass().getMethod("super_onSaveInstanceState", Bundle.class);
                this.m_super_onKeyDown = this.m_activity.getClass().getMethod("super_onKeyDown", Integer.TYPE, KeyEvent.class);
                this.m_super_onKeyUp = this.m_activity.getClass().getMethod("super_onKeyUp", Integer.TYPE, KeyEvent.class);
                this.m_super_onConfigurationChanged = this.m_activity.getClass().getMethod("super_onConfigurationChanged", Configuration.class);
                this.m_super_onActivityResult = this.m_activity.getClass().getMethod("super_onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);
                this.m_super_onWindowFocusChanged = this.m_activity.getClass().getMethod("super_onWindowFocusChanged", Boolean.TYPE);
                this.m_super_dispatchGenericMotionEvent = this.m_activity.getClass().getMethod("super_dispatchGenericMotionEvent", MotionEvent.class);
                if (!bundle.containsKey("necessitas.api.level")) {
                    i = 1;
                } else {
                    i = bundle.getInt("necessitas.api.level");
                }
                m_environmentVariables = bundle.getString("environment.variables");
                String str2 = (("QT_ANDROID_FONTS_MONOSPACE=Droid Sans Mono;Droid Sans;Droid Sans Fallback\tQT_ANDROID_FONTS_SERIF=Droid Serif\tNECESSITAS_API_LEVEL=" + i + "\tHOME=" + this.m_activity.getFilesDir().getAbsolutePath() + "\tTMPDIR=" + this.m_activity.getFilesDir().getAbsolutePath()) + "\tQT_ANDROID_FONTS=Roboto;Droid Sans;Droid Sans Fallback") + getAppIconSize(activity);
                String str3 = m_environmentVariables;
                if (str3 != null && str3.length() > 0) {
                    m_environmentVariables = str2 + "\t" + m_environmentVariables;
                } else {
                    m_environmentVariables = str2;
                }
                if (bundle.containsKey("application.parameters")) {
                    m_applicationParameters = bundle.getString("application.parameters");
                } else {
                    m_applicationParameters = BuildConfig.FLAVOR;
                }
                try {
                    this.m_softInputMode = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 0).softInputMode;
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
                try {
                    ((DisplayManager) this.m_activity.getSystemService("display")).registerDisplayListener(new DisplayManager.DisplayListener() { // from class: org.qtproject.qt5.android.QtActivityDelegate.4
                        @Override // android.hardware.display.DisplayManager.DisplayListener
                        public void onDisplayAdded(int i4) {
                        }

                        @Override // android.hardware.display.DisplayManager.DisplayListener
                        public void onDisplayChanged(int i4) {
                            QtActivityDelegate qtActivityDelegate = QtActivityDelegate.this;
                            qtActivityDelegate.m_currentRotation = qtActivityDelegate.m_activity.getWindowManager().getDefaultDisplay().getRotation();
                            QtNative.handleOrientationChanged(QtActivityDelegate.this.m_currentRotation, QtActivityDelegate.this.m_nativeOrientation);
                        }

                        @Override // android.hardware.display.DisplayManager.DisplayListener
                        public void onDisplayRemoved(int i4) {
                        }
                    }, null);
                } catch (Exception e5) {
                    e5.printStackTrace();
                }
                String loadMainLibrary = QtNative.loadMainLibrary(this.m_mainLib, nativeLibrariesDir);
                this.m_mainLib = loadMainLibrary;
                return loadMainLibrary != null;
            } catch (Exception e6) {
                e6.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean startApplication() {
        try {
            Bundle extras = this.m_activity.getIntent().getExtras();
            if (extras != null) {
                try {
                    new BufferedReader(new InputStreamReader(this.m_activity.getAssets().open("--Added-by-androiddeployqt--/debugger.command"))).readLine();
                    if (extras.containsKey("extraenvvars")) {
                        try {
                            m_environmentVariables += "\t" + new String(Base64.decode(extras.getString("extraenvvars"), 0), "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (extras.containsKey("extraappparams")) {
                        try {
                            m_applicationParameters += "\t" + new String(Base64.decode(extras.getString("extraappparams"), 0), "UTF-8");
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                }
            }
            if (this.m_surfaces == null) {
                onCreate(null);
                return true;
            }
            return true;
        } catch (Exception e4) {
            e4.printStackTrace();
            return false;
        }
    }

    public void onTerminate() {
        QtNative.terminateQt();
        QtNative.m_qtThread.exit();
    }

    public void onCreate(Bundle bundle) {
        Runnable runnable;
        this.m_quitApp = true;
        if (bundle != null) {
            runnable = null;
        } else {
            runnable = new Runnable() { // from class: org.qtproject.qt5.android.QtActivityDelegate.5
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        QtNative.startApplication(QtActivityDelegate.m_applicationParameters, QtActivityDelegate.m_environmentVariables, QtActivityDelegate.this.m_mainLib);
                        QtActivityDelegate.this.m_started = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        QtActivityDelegate.this.m_activity.finish();
                    }
                }
            };
        }
        this.m_layout = new QtLayout(this.m_activity, runnable);
        int i = this.m_activity.getResources().getConfiguration().orientation;
        try {
            ActivityInfo activityInfo = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128);
            StringBuilder sb = new StringBuilder();
            sb.append("android.app.splash_screen_drawable_");
            sb.append(i == 2 ? "landscape" : "portrait");
            String sb2 = sb.toString();
            if (!activityInfo.metaData.containsKey(sb2)) {
                sb2 = "android.app.splash_screen_drawable";
            }
            if (activityInfo.metaData.containsKey(sb2)) {
                this.m_splashScreenSticky = activityInfo.metaData.containsKey("android.app.splash_screen_sticky") && activityInfo.metaData.getBoolean("android.app.splash_screen_sticky");
                int i2 = activityInfo.metaData.getInt(sb2);
                ImageView imageView = new ImageView(this.m_activity);
                this.m_splashScreen = imageView;
                imageView.setImageDrawable(this.m_activity.getResources().getDrawable(i2));
                this.m_splashScreen.setScaleType(ImageView.ScaleType.FIT_XY);
                this.m_splashScreen.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
                this.m_layout.addView(this.m_splashScreen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.m_editText = new QtEditText(this.m_activity, this);
        this.m_imm = (InputMethodManager) this.m_activity.getSystemService("input_method");
        this.m_surfaces = new HashMap<>();
        this.m_nativeViews = new HashMap<>();
        this.m_activity.registerForContextMenu(this.m_layout);
        this.m_activity.setContentView(this.m_layout, new ViewGroup.LayoutParams(-1, -1));
        int rotation = this.m_activity.getWindowManager().getDefaultDisplay().getRotation();
        boolean z = rotation == 1 || rotation == 3;
        boolean z2 = i == 2;
        if ((z2 && !z) || (!z2 && z)) {
            this.m_nativeOrientation = 2;
        } else {
            this.m_nativeOrientation = 1;
        }
        QtNative.handleOrientationChanged(rotation, this.m_nativeOrientation);
        this.m_currentRotation = rotation;
        this.m_layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.qtproject.qt5.android.QtActivityDelegate.6
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                if (QtActivityDelegate.this.m_keyboardIsVisible) {
                    Rect rect = new Rect();
                    QtActivityDelegate.this.m_activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    QtActivityDelegate.this.m_activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int i3 = displayMetrics.heightPixels - rect.bottom;
                    int[] iArr = new int[2];
                    QtActivityDelegate.this.m_layout.getLocationOnScreen(iArr);
                    QtNative.keyboardGeometryChanged(iArr[0], rect.bottom - iArr[1], rect.width(), i3);
                    return true;
                }
                return true;
            }
        });
        this.m_editPopupMenu = new EditPopupMenu(this.m_activity, this.m_layout);
    }

    public void hideSplashScreen() {
        hideSplashScreen(0);
    }

    public void hideSplashScreen(int i) {
        ImageView imageView = this.m_splashScreen;
        if (imageView == null) {
            return;
        }
        if (i <= 0) {
            this.m_layout.removeView(imageView);
            this.m_splashScreen = null;
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setDuration(i);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: org.qtproject.qt5.android.QtActivityDelegate.7
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                QtActivityDelegate.this.hideSplashScreen(0);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
        this.m_splashScreen.startAnimation(alphaAnimation);
    }

    public void initializeAccessibility() {
        new QtAccessibilityDelegate(this.m_activity, this.m_layout, this);
    }

    public void onWindowFocusChanged(boolean z) {
        try {
            this.m_super_onWindowFocusChanged.invoke(this.m_activity, Boolean.valueOf(z));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (z) {
            updateFullScreen();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        try {
            this.m_super_onConfigurationChanged.invoke(this.m_activity, configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (this.m_quitApp) {
            QtNative.terminateQt();
            QtNative.setActivity(null, null);
            QtNative.m_qtThread.exit();
            System.exit(0);
        }
    }

    public void onPause() {
        QtNative.setApplicationState(2);
    }

    public void onResume() {
        QtNative.setApplicationState(4);
        if (this.m_started) {
            QtNative.updateWindow();
            updateFullScreen();
        }
    }

    public void onNewIntent(Intent intent) {
        QtNative.onNewIntent(intent);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            this.m_super_onActivityResult.invoke(this.m_activity, Integer.valueOf(i), Integer.valueOf(i2), intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        QtNative.onActivityResult(i, i2, intent);
    }

    public void onStop() {
        QtNative.setApplicationState(0);
    }

    public Object onRetainNonConfigurationInstance() {
        try {
            this.m_super_onRetainNonConfigurationInstance.invoke(this.m_activity, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.m_quitApp = false;
        return true;
    }

    public void onSaveInstanceState(Bundle bundle) {
        try {
            this.m_super_onSaveInstanceState.invoke(this.m_activity, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bundle.putBoolean("FullScreen", this.m_fullScreen);
        bundle.putBoolean("Started", this.m_started);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        try {
            this.m_super_onRestoreInstanceState.invoke(this.m_activity, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.m_started = bundle.getBoolean("Started");
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        if (this.m_started) {
            long handleKeyDown = MetaKeyKeyListener.handleKeyDown(this.m_metaState, i, keyEvent);
            this.m_metaState = handleKeyDown;
            int unicodeChar = keyEvent.getUnicodeChar(MetaKeyKeyListener.getMetaState(handleKeyDown) | keyEvent.getMetaState());
            this.m_metaState = MetaKeyKeyListener.adjustMetaAfterKeypress(this.m_metaState);
            if ((Integer.MIN_VALUE & unicodeChar) == 0) {
                i2 = unicodeChar;
            } else {
                i2 = KeyEvent.getDeadChar(this.m_lastChar, Integer.MAX_VALUE & unicodeChar);
            }
            if ((i == 24 || i == 25 || i == 91) && System.getenv("QT_ANDROID_VOLUME_KEYS") == null) {
                return false;
            }
            this.m_lastChar = unicodeChar;
            if (i == 4) {
                boolean z = !this.m_keyboardIsVisible;
                this.m_backKeyPressedSent = z;
                if (!z) {
                    return true;
                }
            }
            QtNative.keyDown(i, i2, keyEvent.getMetaState(), keyEvent.getRepeatCount() > 0);
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (this.m_started) {
            if ((i == 24 || i == 25 || i == 91) && System.getenv("QT_ANDROID_VOLUME_KEYS") == null) {
                return false;
            }
            if (i == 4 && !this.m_backKeyPressedSent) {
                hideSoftwareKeyboard();
                setKeyboardVisibility(false, System.nanoTime());
                return true;
            }
            this.m_metaState = MetaKeyKeyListener.handleKeyUp(this.m_metaState, i, keyEvent);
            QtNative.keyUp(i, keyEvent.getUnicodeChar(), keyEvent.getMetaState(), keyEvent.getRepeatCount() > 0);
            return true;
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.m_started && keyEvent.getAction() == 2 && keyEvent.getCharacters() != null && keyEvent.getCharacters().length() == 1 && keyEvent.getKeyCode() == 0) {
            QtNative.keyDown(0, keyEvent.getCharacters().charAt(0), keyEvent.getMetaState(), keyEvent.getRepeatCount() > 0);
            QtNative.keyUp(0, keyEvent.getCharacters().charAt(0), keyEvent.getMetaState(), keyEvent.getRepeatCount() > 0);
        }
        if (QtNative.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        try {
            return ((Boolean) this.m_super_dispatchKeyEvent.invoke(this.m_activity, keyEvent)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean z = true;
        this.m_optionsMenuIsVisible = true;
        boolean onPrepareOptionsMenu = QtNative.onPrepareOptionsMenu(menu);
        setActionBarVisibility((!onPrepareOptionsMenu || menu.size() <= 0) ? false : false);
        return onPrepareOptionsMenu;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return QtNative.onOptionsItemSelected(menuItem.getItemId(), menuItem.isChecked());
    }

    public void onOptionsMenuClosed(Menu menu) {
        this.m_optionsMenuIsVisible = false;
        QtNative.onOptionsMenuClosed(menu);
    }

    public void resetOptionsMenu() {
        this.m_activity.invalidateOptionsMenu();
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.clearHeader();
        QtNative.onCreateContextMenu(contextMenu);
        this.m_contextMenuVisible = true;
    }

    public void onCreatePopupMenu(Menu menu) {
        QtNative.fillContextMenu(menu);
        this.m_contextMenuVisible = true;
    }

    public void onContextMenuClosed(Menu menu) {
        if (!this.m_contextMenuVisible) {
            return;
        }
        this.m_contextMenuVisible = false;
        QtNative.onContextMenuClosed(menu);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        this.m_contextMenuVisible = false;
        return QtNative.onContextItemSelected(menuItem.getItemId(), menuItem.isChecked());
    }

    public void openContextMenu(final int i, final int i2, final int i3, final int i4) {
        this.m_layout.postDelayed(new Runnable() { // from class: org.qtproject.qt5.android.QtActivityDelegate.8
            @Override // java.lang.Runnable
            public void run() {
                QtActivityDelegate.this.m_layout.setLayoutParams(QtActivityDelegate.this.m_editText, new QtLayout.LayoutParams(i3, i4, i, i2), false);
                PopupMenu popupMenu = new PopupMenu(QtActivityDelegate.this.m_activity, QtActivityDelegate.this.m_editText);
                QtActivityDelegate.this.onCreatePopupMenu(popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: org.qtproject.qt5.android.QtActivityDelegate.8.1
                    @Override // android.widget.PopupMenu.OnMenuItemClickListener
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return QtActivityDelegate.this.onContextItemSelected(menuItem);
                    }
                });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() { // from class: org.qtproject.qt5.android.QtActivityDelegate.8.2
                    @Override // android.widget.PopupMenu.OnDismissListener
                    public void onDismiss(PopupMenu popupMenu2) {
                        QtActivityDelegate.this.onContextMenuClosed(popupMenu2.getMenu());
                    }
                });
                popupMenu.show();
            }
        }, 100L);
    }

    public void closeContextMenu() {
        this.m_activity.closeContextMenu();
    }

    private void setActionBarVisibility(boolean z) {
        if (this.m_activity.getActionBar() == null) {
            return;
        }
        if (ViewConfiguration.get(this.m_activity).hasPermanentMenuKey() || !z) {
            this.m_activity.getActionBar().hide();
        } else {
            this.m_activity.getActionBar().show();
        }
    }

    public void insertNativeView(int i, View view, int i2, int i3, int i4, int i5) {
        View view2 = this.m_dummyView;
        if (view2 != null) {
            this.m_layout.removeView(view2);
            this.m_dummyView = null;
        }
        if (this.m_nativeViews.containsKey(Integer.valueOf(i))) {
            this.m_layout.removeView(this.m_nativeViews.remove(Integer.valueOf(i)));
        }
        if (i4 < 0 || i5 < 0) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        } else {
            view.setLayoutParams(new QtLayout.LayoutParams(i4, i5, i2, i3));
        }
        view.setId(i);
        this.m_layout.addView(view);
        this.m_nativeViews.put(Integer.valueOf(i), view);
    }

    public void createSurface(int i, boolean z, int i2, int i3, int i4, int i5, int i6) {
        if (this.m_surfaces.size() == 0) {
            TypedValue typedValue = new TypedValue();
            this.m_activity.getTheme().resolveAttribute(16842836, typedValue, true);
            if (typedValue.type >= 28 && typedValue.type <= 31) {
                this.m_activity.getWindow().setBackgroundDrawable(new ColorDrawable(typedValue.data));
            } else {
                this.m_activity.getWindow().setBackgroundDrawable(this.m_activity.getResources().getDrawable(typedValue.resourceId));
            }
            View view = this.m_dummyView;
            if (view != null) {
                this.m_layout.removeView(view);
                this.m_dummyView = null;
            }
        }
        if (this.m_surfaces.containsKey(Integer.valueOf(i))) {
            this.m_layout.removeView(this.m_surfaces.remove(Integer.valueOf(i)));
        }
        QtSurface qtSurface = new QtSurface(this.m_activity, i, z, i6);
        if (i4 < 0 || i5 < 0) {
            qtSurface.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        } else {
            qtSurface.setLayoutParams(new QtLayout.LayoutParams(i4, i5, i2, i3));
        }
        this.m_layout.addView(qtSurface, getSurfaceCount());
        this.m_surfaces.put(Integer.valueOf(i), qtSurface);
        if (!this.m_splashScreenSticky) {
            hideSplashScreen();
        }
    }

    public void setSurfaceGeometry(int i, int i2, int i3, int i4, int i5) {
        if (this.m_surfaces.containsKey(Integer.valueOf(i))) {
            this.m_surfaces.get(Integer.valueOf(i)).setLayoutParams(new QtLayout.LayoutParams(i4, i5, i2, i3));
        } else if (this.m_nativeViews.containsKey(Integer.valueOf(i))) {
            this.m_nativeViews.get(Integer.valueOf(i)).setLayoutParams(new QtLayout.LayoutParams(i4, i5, i2, i3));
        } else {
            Log.e(QtNative.QtTAG, "Surface " + i + " not found!");
        }
    }

    public void destroySurface(int i) {
        View view;
        if (this.m_surfaces.containsKey(Integer.valueOf(i))) {
            view = this.m_surfaces.remove(Integer.valueOf(i));
        } else if (this.m_nativeViews.containsKey(Integer.valueOf(i))) {
            view = this.m_nativeViews.remove(Integer.valueOf(i));
        } else {
            Log.e(QtNative.QtTAG, "Surface " + i + " not found!");
            view = null;
        }
        if (view == null) {
            return;
        }
        if (this.m_surfaces.size() == 0 && this.m_nativeViews.size() == 0) {
            this.m_dummyView = view;
        } else {
            this.m_layout.removeView(view);
        }
    }

    public int getSurfaceCount() {
        return this.m_surfaces.size();
    }

    public void bringChildToFront(int i) {
        QtSurface qtSurface = this.m_surfaces.get(Integer.valueOf(i));
        if (qtSurface != null) {
            int surfaceCount = getSurfaceCount();
            if (surfaceCount > 0) {
                this.m_layout.moveChild(qtSurface, surfaceCount - 1);
                return;
            }
            return;
        }
        View view = this.m_nativeViews.get(Integer.valueOf(i));
        if (view != null) {
            this.m_layout.moveChild(view, -1);
        }
    }

    public void bringChildToBack(int i) {
        QtSurface qtSurface = this.m_surfaces.get(Integer.valueOf(i));
        if (qtSurface != null) {
            this.m_layout.moveChild(qtSurface, 0);
            return;
        }
        View view = this.m_nativeViews.get(Integer.valueOf(i));
        if (view != null) {
            this.m_layout.moveChild(view, getSurfaceCount());
        }
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        if (this.m_started && QtNative.dispatchGenericMotionEvent(motionEvent)) {
            return true;
        }
        try {
            return ((Boolean) this.m_super_dispatchGenericMotionEvent.invoke(this.m_activity, motionEvent)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        QtNative.sendRequestPermissionsResult(i, strArr, iArr);
    }
}
