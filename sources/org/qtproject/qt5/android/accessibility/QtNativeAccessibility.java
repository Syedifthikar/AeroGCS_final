package org.qtproject.qt5.android.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: classes.dex */
public class QtNativeAccessibility {
    public static native int[] childIdListForAccessibleObject(int i);

    public static native boolean clickAction(int i);

    public static native String descriptionForAccessibleObject(int i);

    public static native int hitTest(float f, float f2);

    public static native int parentId(int i);

    public static native boolean populateNode(int i, AccessibilityNodeInfo accessibilityNodeInfo);

    public static native Rect screenRect(int i);

    public static native boolean scrollBackward(int i);

    public static native boolean scrollForward(int i);

    public static native void setActive(boolean z);

    QtNativeAccessibility() {
    }
}
