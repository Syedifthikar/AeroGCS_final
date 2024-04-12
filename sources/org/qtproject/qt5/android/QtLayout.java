package org.qtproject.qt5.android;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class QtLayout extends ViewGroup {
    private Runnable m_startApplicationRunnable;

    public QtLayout(Context context, Runnable runnable) {
        super(context);
        this.m_startApplicationRunnable = runnable;
    }

    public QtLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public QtLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        QtNative.setApplicationDisplayMetrics(displayMetrics.widthPixels, displayMetrics.heightPixels, i, i2, displayMetrics.xdpi, displayMetrics.ydpi, displayMetrics.scaledDensity, displayMetrics.density);
        Runnable runnable = this.m_startApplicationRunnable;
        if (runnable != null) {
            runnable.run();
            this.m_startApplicationRunnable = null;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        measureChildren(i, i2);
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                i4 = Math.max(i4, layoutParams.x + childAt.getMeasuredWidth());
                i3 = Math.max(i3, layoutParams.y + childAt.getMeasuredHeight());
            }
        }
        setMeasuredDimension(resolveSize(Math.max(i4, getSuggestedMinimumWidth()), i), resolveSize(Math.max(i3, getSuggestedMinimumHeight()), i2));
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2, 0, 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int i6 = layoutParams.x;
                int i7 = layoutParams.y;
                childAt.layout(i6, i7, childAt.getMeasuredWidth() + i6, childAt.getMeasuredHeight() + i7);
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;

        public LayoutParams(int i, int i2, int i3, int i4) {
            super(i, i2);
            this.x = i3;
            this.y = i4;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    public void moveChild(View view, int i) {
        if (view == null || indexOfChild(view) == -1) {
            return;
        }
        detachViewFromParent(view);
        requestLayout();
        invalidate();
        attachViewToParent(view, i, view.getLayoutParams());
    }

    public void setLayoutParams(View view, ViewGroup.LayoutParams layoutParams, boolean z) {
        if (view == null || !checkLayoutParams(layoutParams)) {
            return;
        }
        if (this == view.getParent()) {
            view.setLayoutParams(layoutParams);
            if (z) {
                invalidate();
                return;
            }
            return;
        }
        addView(view, layoutParams);
    }
}
