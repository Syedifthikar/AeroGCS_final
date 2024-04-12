package org.qtproject.qt5.android.bindings;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import org.qtproject.qt5.android.bindings.QtApplication;

/* loaded from: classes2.dex */
public class QtActivity extends Activity {
    public String QT_ANDROID_DEFAULT_THEME;
    public String[] QT_ANDROID_THEMES;
    public String APPLICATION_PARAMETERS = null;
    public String ENVIRONMENT_VARIABLES = "QT_USE_ANDROID_NATIVE_DIALOGS=1";
    private QtActivityLoader m_loader = new QtActivityLoader(this);

    public QtActivity() {
        this.QT_ANDROID_THEMES = null;
        this.QT_ANDROID_DEFAULT_THEME = null;
        if (Build.VERSION.SDK_INT >= 21) {
            this.QT_ANDROID_THEMES = new String[]{"Theme_Holo_Light"};
            this.QT_ANDROID_DEFAULT_THEME = "Theme_Holo_Light";
            return;
        }
        this.QT_ANDROID_THEMES = new String[]{"Theme_DeviceDefault_Light"};
        this.QT_ANDROID_DEFAULT_THEME = "Theme_DeviceDefault_Light";
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchKeyEvent == null) ? super.dispatchKeyEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchKeyEvent, event)).booleanValue();
    }

    public boolean super_dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchPopulateAccessibilityEvent == null) ? super.dispatchPopulateAccessibilityEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchPopulateAccessibilityEvent, event)).booleanValue();
    }

    public boolean super_dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchTouchEvent == null) ? super.dispatchTouchEvent(ev) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchTouchEvent, ev)).booleanValue();
    }

    public boolean super_dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchTrackballEvent == null) ? super.dispatchTrackballEvent(ev) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchTrackballEvent, ev)).booleanValue();
    }

    public boolean super_dispatchTrackballEvent(MotionEvent event) {
        return super.dispatchTrackballEvent(event);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (QtApplication.m_delegateObject != null && QtApplication.onActivityResult != null) {
            QtApplication.invokeDelegateMethod(QtApplication.onActivityResult, Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
            return;
        }
        if (requestCode == 62446) {
            this.m_loader.startApp(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void super_onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        if (!QtApplication.invokeDelegate(theme, Integer.valueOf(resid), Boolean.valueOf(first)).invoked) {
            super.onApplyThemeResource(theme, resid, first);
        }
    }

    public void super_onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override // android.app.Activity
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        if (!QtApplication.invokeDelegate(childActivity, title).invoked) {
            super.onChildTitleChanged(childActivity, title);
        }
    }

    public void super_onChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        if (!QtApplication.invokeDelegate(newConfig).invoked) {
            super.onConfigurationChanged(newConfig);
        }
    }

    public void super_onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onContentChanged();
        }
    }

    public void super_onContentChanged() {
        super.onContentChanged();
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem item) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(item);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onContextItemSelected(item);
    }

    public boolean super_onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override // android.app.Activity
    public void onContextMenuClosed(Menu menu) {
        if (!QtApplication.invokeDelegate(menu).invoked) {
            super.onContextMenuClosed(menu);
        }
    }

    public void super_onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    protected void onCreateHook(Bundle savedInstanceState) {
        this.m_loader.APPLICATION_PARAMETERS = this.APPLICATION_PARAMETERS;
        this.m_loader.ENVIRONMENT_VARIABLES = this.ENVIRONMENT_VARIABLES;
        this.m_loader.QT_ANDROID_THEMES = this.QT_ANDROID_THEMES;
        this.m_loader.QT_ANDROID_DEFAULT_THEME = this.QT_ANDROID_DEFAULT_THEME;
        this.m_loader.onCreate(savedInstanceState);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateHook(savedInstanceState);
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!QtApplication.invokeDelegate(menu, v, menuInfo).invoked) {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public void super_onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override // android.app.Activity
    public CharSequence onCreateDescription() {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(new Object[0]);
        if (res.invoked) {
            return (CharSequence) res.methodReturns;
        }
        return super.onCreateDescription();
    }

    public CharSequence super_onCreateDescription() {
        return super.onCreateDescription();
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(id));
        if (res.invoked) {
            return (Dialog) res.methodReturns;
        }
        return super.onCreateDialog(id);
    }

    public Dialog super_onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(menu);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean super_onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(featureId), menu);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    public boolean super_onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(featureId));
        if (res.invoked) {
            return (View) res.methodReturns;
        }
        return super.onCreatePanelView(featureId);
    }

    public View super_onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    @Override // android.app.Activity
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(outBitmap, canvas);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onCreateThumbnail(outBitmap, canvas);
    }

    public boolean super_onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }

    @Override // android.app.Activity, android.view.LayoutInflater.Factory
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(name, context, attrs);
        if (res.invoked) {
            return (View) res.methodReturns;
        }
        return super.onCreateView(name, context, attrs);
    }

    public View super_onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onKeyDown == null) ? super.onKeyDown(keyCode, event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onKeyDown, Integer.valueOf(keyCode), event)).booleanValue();
    }

    public boolean super_onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onKeyMultiple == null) ? super.onKeyMultiple(keyCode, repeatCount, event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onKeyMultiple, Integer.valueOf(keyCode), Integer.valueOf(repeatCount), event)).booleanValue();
    }

    public boolean super_onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onKeyUp == null) ? super.onKeyUp(keyCode, event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onKeyUp, Integer.valueOf(keyCode), event)).booleanValue();
    }

    public boolean super_onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onLowMemory();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(featureId), item);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public boolean super_onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(featureId), menu);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onMenuOpened(featureId, menu);
    }

    public boolean super_onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        if (!QtApplication.invokeDelegate(intent).invoked) {
            super.onNewIntent(intent);
        }
    }

    public void super_onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(item);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean super_onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    public void onOptionsMenuClosed(Menu menu) {
        if (!QtApplication.invokeDelegate(menu).invoked) {
            super.onOptionsMenuClosed(menu);
        }
    }

    public void super_onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        if (!QtApplication.invokeDelegate(Integer.valueOf(featureId), menu).invoked) {
            super.onPanelClosed(featureId, menu);
        }
    }

    public void super_onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        QtApplication.invokeDelegate(savedInstanceState);
    }

    @Override // android.app.Activity
    protected void onPostResume() {
        super.onPostResume();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (!QtApplication.invokeDelegate(Integer.valueOf(id), dialog).invoked) {
            super.onPrepareDialog(id, dialog);
        }
    }

    public void super_onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(menu);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean super_onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(featureId), view, menu);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    public boolean super_onPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (!QtApplication.invokeDelegate(savedInstanceState).invoked) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    public void super_onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    public Object onRetainNonConfigurationInstance() {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(new Object[0]);
        if (res.invoked) {
            return res.methodReturns;
        }
        return super.onRetainNonConfigurationInstance();
    }

    public Object super_onRetainNonConfigurationInstance() {
        return super.onRetainNonConfigurationInstance();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        if (!QtApplication.invokeDelegate(outState).invoked) {
            super.onSaveInstanceState(outState);
        }
    }

    public void super_onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onSearchRequested() {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(new Object[0]);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onSearchRequested();
    }

    public boolean super_onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Activity
    protected void onTitleChanged(CharSequence title, int color) {
        if (!QtApplication.invokeDelegate(title, Integer.valueOf(color)).invoked) {
            super.onTitleChanged(title, color);
        }
    }

    public void super_onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onTouchEvent == null) ? super.onTouchEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onTouchEvent, event)).booleanValue();
    }

    public boolean super_onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override // android.app.Activity
    public boolean onTrackballEvent(MotionEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onTrackballEvent == null) ? super.onTrackballEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onTrackballEvent, event)).booleanValue();
    }

    public boolean super_onTrackballEvent(MotionEvent event) {
        return super.onTrackballEvent(event);
    }

    @Override // android.app.Activity
    public void onUserInteraction() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onUserInteraction();
        }
    }

    public void super_onUserInteraction() {
        super.onUserInteraction();
    }

    @Override // android.app.Activity
    protected void onUserLeaveHint() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onUserLeaveHint();
        }
    }

    public void super_onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (!QtApplication.invokeDelegate(params).invoked) {
            super.onWindowAttributesChanged(params);
        }
    }

    public void super_onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!QtApplication.invokeDelegate(Boolean.valueOf(hasFocus)).invoked) {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    public void super_onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onAttachedToWindow();
        }
    }

    public void super_onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onBackPressed();
        }
    }

    public void super_onBackPressed() {
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onDetachedFromWindow() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onDetachedFromWindow();
        }
    }

    public void super_onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onKeyLongPress == null) ? super.onKeyLongPress(keyCode, event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onKeyLongPress, Integer.valueOf(keyCode), event)).booleanValue();
    }

    public boolean super_onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle args) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(Integer.valueOf(id), args);
        if (res.invoked) {
            return (Dialog) res.methodReturns;
        }
        return super.onCreateDialog(id, args);
    }

    public Dialog super_onCreateDialog(int id, Bundle args) {
        return super.onCreateDialog(id, args);
    }

    @Override // android.app.Activity
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (!QtApplication.invokeDelegate(Integer.valueOf(id), dialog, args).invoked) {
            super.onPrepareDialog(id, dialog, args);
        }
    }

    public void super_onPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchKeyShortcutEvent == null) ? super.dispatchKeyShortcutEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchKeyShortcutEvent, event)).booleanValue();
    }

    public boolean super_dispatchKeyShortcutEvent(KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
        if (!QtApplication.invokeDelegate(mode).invoked) {
            super.onActionModeFinished(mode);
        }
    }

    public void super_onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
        if (!QtApplication.invokeDelegate(mode).invoked) {
            super.onActionModeStarted(mode);
        }
    }

    public void super_onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
    }

    @Override // android.app.Activity
    public void onAttachFragment(Fragment fragment) {
        if (!QtApplication.invokeDelegate(fragment).invoked) {
            super.onAttachFragment(fragment);
        }
    }

    public void super_onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override // android.app.Activity, android.view.LayoutInflater.Factory2
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(parent, name, context, attrs);
        if (res.invoked) {
            return (View) res.methodReturns;
        }
        return super.onCreateView(parent, name, context, attrs);
    }

    public View super_onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override // android.app.Activity
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onKeyShortcut == null) ? super.onKeyShortcut(keyCode, event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onKeyShortcut, Integer.valueOf(keyCode), event)).booleanValue();
    }

    public boolean super_onKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(callback);
        if (res.invoked) {
            return (ActionMode) res.methodReturns;
        }
        return super.onWindowStartingActionMode(callback);
    }

    public ActionMode super_onWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return (QtApplication.m_delegateObject == null || QtApplication.dispatchGenericMotionEvent == null) ? super.dispatchGenericMotionEvent(ev) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.dispatchGenericMotionEvent, ev)).booleanValue();
    }

    public boolean super_dispatchGenericMotionEvent(MotionEvent event) {
        return super.dispatchGenericMotionEvent(event);
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent event) {
        return (QtApplication.m_delegateObject == null || QtApplication.onGenericMotionEvent == null) ? super.onGenericMotionEvent(event) : ((Boolean) QtApplication.invokeDelegateMethod(QtApplication.onGenericMotionEvent, event)).booleanValue();
    }

    public boolean super_onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (QtApplication.m_delegateObject != null && QtApplication.onRequestPermissionsResult != null) {
            QtApplication.invokeDelegateMethod(QtApplication.onRequestPermissionsResult, Integer.valueOf(requestCode), permissions, grantResults);
        }
    }
}
