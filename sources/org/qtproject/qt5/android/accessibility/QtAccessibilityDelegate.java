package org.qtproject.qt5.android.accessibility;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import org.qtproject.qt5.android.QtActivityDelegate;

/* loaded from: classes.dex */
public class QtAccessibilityDelegate extends View.AccessibilityDelegate {
    private static final String DEFAULT_CLASS_NAME = "$VirtualChild";
    public static final int INVALID_ID = 333;
    private static final String TAG = "Qt A11Y";
    private Activity m_activity;
    private QtActivityDelegate m_activityDelegate;
    private ViewGroup m_layout;
    private AccessibilityManager m_manager;
    private View m_view = null;
    private int m_focusedVirtualViewId = INVALID_ID;
    private int m_hoveredVirtualViewId = INVALID_ID;
    private final int[] m_globalOffset = new int[2];
    private AccessibilityNodeProvider m_nodeProvider = new AccessibilityNodeProvider() { // from class: org.qtproject.qt5.android.accessibility.QtAccessibilityDelegate.1
        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            return i == -1 ? QtAccessibilityDelegate.this.getNodeForView() : QtAccessibilityDelegate.this.getNodeForVirtualViewId(i);
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int i, int i2, Bundle bundle) {
            boolean z = true;
            if (i2 == 64) {
                if (QtAccessibilityDelegate.this.m_focusedVirtualViewId != i) {
                    QtAccessibilityDelegate.this.m_focusedVirtualViewId = i;
                    QtAccessibilityDelegate.this.m_view.invalidate();
                    QtAccessibilityDelegate.this.sendEventForVirtualViewId(i, 32768);
                    return QtAccessibilityDelegate.this.performActionForVirtualViewId(i, i2, bundle) | z;
                }
                z = false;
                return QtAccessibilityDelegate.this.performActionForVirtualViewId(i, i2, bundle) | z;
            } else if (i2 == 128) {
                if (QtAccessibilityDelegate.this.m_focusedVirtualViewId == i) {
                    QtAccessibilityDelegate.this.m_focusedVirtualViewId = QtAccessibilityDelegate.INVALID_ID;
                }
                QtAccessibilityDelegate.this.m_view.invalidate();
                QtAccessibilityDelegate.this.sendEventForVirtualViewId(i, 65536);
                return QtAccessibilityDelegate.this.performActionForVirtualViewId(i, i2, bundle) | z;
            } else {
                if (i == -1) {
                    return QtAccessibilityDelegate.this.m_view.performAccessibilityAction(i2, bundle);
                }
                z = false;
                return QtAccessibilityDelegate.this.performActionForVirtualViewId(i, i2, bundle) | z;
            }
        }
    };

    /* loaded from: classes.dex */
    public class HoverEventListener implements View.OnHoverListener {
        private HoverEventListener() {
            QtAccessibilityDelegate.this = r1;
        }

        @Override // android.view.View.OnHoverListener
        public boolean onHover(View view, MotionEvent motionEvent) {
            return QtAccessibilityDelegate.this.dispatchHoverEvent(motionEvent);
        }
    }

    public QtAccessibilityDelegate(Activity activity, ViewGroup viewGroup, QtActivityDelegate qtActivityDelegate) {
        this.m_activity = activity;
        this.m_layout = viewGroup;
        this.m_activityDelegate = qtActivityDelegate;
        AccessibilityManager accessibilityManager = (AccessibilityManager) activity.getSystemService("accessibility");
        this.m_manager = accessibilityManager;
        if (accessibilityManager != null) {
            AccessibilityManagerListener accessibilityManagerListener = new AccessibilityManagerListener();
            if (!this.m_manager.addAccessibilityStateChangeListener(accessibilityManagerListener)) {
                Log.w("Qt A11y", "Could not register a11y state change listener");
            }
            if (this.m_manager.isEnabled()) {
                accessibilityManagerListener.onAccessibilityStateChanged(true);
            }
        }
    }

    /* loaded from: classes.dex */
    public class AccessibilityManagerListener implements AccessibilityManager.AccessibilityStateChangeListener {
        private AccessibilityManagerListener() {
            QtAccessibilityDelegate.this = r1;
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean z) {
            if (z) {
                try {
                    View view = QtAccessibilityDelegate.this.m_view;
                    if (view == null) {
                        view = new View(QtAccessibilityDelegate.this.m_activity);
                        view.setId(-1);
                    }
                    view.setAccessibilityDelegate(QtAccessibilityDelegate.this);
                    if (QtAccessibilityDelegate.this.m_view == null) {
                        QtAccessibilityDelegate.this.m_layout.addView(view, QtAccessibilityDelegate.this.m_activityDelegate.getSurfaceCount(), new ViewGroup.LayoutParams(-1, -1));
                    }
                    QtAccessibilityDelegate.this.m_view = view;
                    QtAccessibilityDelegate.this.m_view.setOnHoverListener(new HoverEventListener());
                } catch (Exception e) {
                    Log.w("Qt A11y", "Unknown exception: " + e.toString());
                }
            } else if (QtAccessibilityDelegate.this.m_view != null) {
                QtAccessibilityDelegate.this.m_layout.removeView(QtAccessibilityDelegate.this.m_view);
                QtAccessibilityDelegate.this.m_view = null;
            }
            QtNativeAccessibility.setActive(z);
        }
    }

    @Override // android.view.View.AccessibilityDelegate
    public AccessibilityNodeProvider getAccessibilityNodeProvider(View view) {
        return this.m_nodeProvider;
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        if (!this.m_manager.isTouchExplorationEnabled()) {
            return false;
        }
        int hitTest = QtNativeAccessibility.hitTest(motionEvent.getX(), motionEvent.getY());
        if (hitTest == 333) {
            hitTest = -1;
        }
        int action = motionEvent.getAction();
        if (action == 7 || action == 9) {
            setHoveredVirtualViewId(hitTest);
            return true;
        } else if (action == 10) {
            setHoveredVirtualViewId(hitTest);
            return true;
        } else {
            return true;
        }
    }

    public boolean sendEventForVirtualViewId(int i, int i2) {
        if (i == 333 || !this.m_manager.isEnabled()) {
            Log.w(TAG, "sendEventForVirtualViewId for invalid view");
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) this.m_view.getParent();
        if (viewGroup == null) {
            Log.w(TAG, "Could not send AccessibilityEvent because group was null. This should really not happen.");
            return false;
        }
        return viewGroup.requestSendAccessibilityEvent(this.m_view, getEventForVirtualViewId(i, i2));
    }

    public void invalidateVirtualViewId(int i) {
        sendEventForVirtualViewId(i, 2048);
    }

    private void setHoveredVirtualViewId(int i) {
        int i2 = this.m_hoveredVirtualViewId;
        if (i2 == i) {
            return;
        }
        this.m_hoveredVirtualViewId = i;
        sendEventForVirtualViewId(i, 128);
        sendEventForVirtualViewId(i2, 256);
    }

    private AccessibilityEvent getEventForVirtualViewId(int i, int i2) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
        obtain.setEnabled(true);
        obtain.setClassName(this.m_view.getClass().getName() + DEFAULT_CLASS_NAME);
        obtain.setContentDescription(QtNativeAccessibility.descriptionForAccessibleObject(i));
        if (obtain.getText().isEmpty() && TextUtils.isEmpty(obtain.getContentDescription())) {
            Log.w(TAG, "AccessibilityEvent with empty description");
        }
        obtain.setPackageName(this.m_view.getContext().getPackageName());
        obtain.setSource(this.m_view, i);
        return obtain;
    }

    private void dumpNodes(int i) {
        Log.i(TAG, "A11Y hierarchy: " + i + " parent: " + QtNativeAccessibility.parentId(i));
        Log.i(TAG, "    desc: " + QtNativeAccessibility.descriptionForAccessibleObject(i) + " rect: " + QtNativeAccessibility.screenRect(i));
        StringBuilder sb = new StringBuilder();
        sb.append(" NODE: ");
        sb.append(getNodeForVirtualViewId(i));
        Log.i(TAG, sb.toString());
        int[] childIdListForAccessibleObject = QtNativeAccessibility.childIdListForAccessibleObject(i);
        for (int i2 = 0; i2 < childIdListForAccessibleObject.length; i2++) {
            Log.i(TAG, i + " has child: " + childIdListForAccessibleObject[i2]);
            dumpNodes(childIdListForAccessibleObject[i2]);
        }
    }

    public AccessibilityNodeInfo getNodeForView() {
        AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(this.m_view);
        AccessibilityNodeInfo obtain2 = AccessibilityNodeInfo.obtain(this.m_view);
        this.m_view.onInitializeAccessibilityNodeInfo(obtain2);
        this.m_view.getLocationOnScreen(this.m_globalOffset);
        int[] iArr = this.m_globalOffset;
        int i = iArr[0];
        int i2 = iArr[1];
        Rect rect = new Rect();
        obtain2.getBoundsInParent(rect);
        obtain.setBoundsInParent(rect);
        Rect rect2 = new Rect();
        obtain2.getBoundsInScreen(rect2);
        rect2.offset(i, i2);
        obtain.setBoundsInScreen(rect2);
        ViewParent parent = this.m_view.getParent();
        if (parent instanceof View) {
            obtain.setParent((View) parent);
        }
        obtain.setVisibleToUser(obtain2.isVisibleToUser());
        obtain.setPackageName(obtain2.getPackageName());
        obtain.setClassName(obtain2.getClassName());
        for (int i3 : QtNativeAccessibility.childIdListForAccessibleObject(-1)) {
            obtain.addChild(this.m_view, i3);
        }
        return obtain;
    }

    public AccessibilityNodeInfo getNodeForVirtualViewId(int i) {
        AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain();
        obtain.setClassName(this.m_view.getClass().getName() + DEFAULT_CLASS_NAME);
        obtain.setPackageName(this.m_view.getContext().getPackageName());
        if (!QtNativeAccessibility.populateNode(i, obtain)) {
            return obtain;
        }
        obtain.setSource(this.m_view, i);
        if (TextUtils.isEmpty(obtain.getText()) && TextUtils.isEmpty(obtain.getContentDescription())) {
            Log.w(TAG, "AccessibilityNodeInfo with empty contentDescription: " + i);
        }
        int parentId = QtNativeAccessibility.parentId(i);
        obtain.setParent(this.m_view, parentId);
        Rect screenRect = QtNativeAccessibility.screenRect(i);
        int[] iArr = this.m_globalOffset;
        screenRect.offset(iArr[0], iArr[1]);
        obtain.setBoundsInScreen(screenRect);
        Rect screenRect2 = QtNativeAccessibility.screenRect(parentId);
        screenRect.offset(-screenRect2.left, -screenRect2.top);
        obtain.setBoundsInParent(screenRect);
        if (this.m_focusedVirtualViewId == i) {
            obtain.setAccessibilityFocused(true);
            obtain.addAction(128);
        } else {
            obtain.setAccessibilityFocused(false);
            obtain.addAction(64);
        }
        for (int i2 : QtNativeAccessibility.childIdListForAccessibleObject(i)) {
            obtain.addChild(this.m_view, i2);
        }
        return obtain;
    }

    protected boolean performActionForVirtualViewId(int i, int i2, Bundle bundle) {
        boolean clickAction;
        if (i2 == 16) {
            clickAction = QtNativeAccessibility.clickAction(i);
            if (clickAction) {
                sendEventForVirtualViewId(i, 1);
            }
        } else if (i2 == 4096) {
            clickAction = QtNativeAccessibility.scrollForward(i);
            if (clickAction) {
                sendEventForVirtualViewId(i, 4096);
            }
        } else if (i2 != 8192) {
            return false;
        } else {
            clickAction = QtNativeAccessibility.scrollBackward(i);
            if (clickAction) {
                sendEventForVirtualViewId(i, 4096);
            }
        }
        return clickAction;
    }
}
