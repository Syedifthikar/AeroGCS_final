package org.qtproject.qt5.android;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/* loaded from: classes.dex */
public class QtSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Object m_accessibilityDelegate;
    private GestureDetector m_gestureDetector;

    public QtSurface(Context context, int i, boolean z, int i2) {
        super(context);
        this.m_accessibilityDelegate = null;
        setFocusable(false);
        setFocusableInTouchMode(false);
        setZOrderMediaOverlay(z);
        getHolder().addCallback(this);
        if (i2 != 16) {
            getHolder().setFormat(1);
        } else {
            getHolder().setFormat(4);
        }
        setId(i);
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.qtproject.qt5.android.QtSurface.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                QtNative.longPress(QtSurface.this.getId(), (int) motionEvent.getX(), (int) motionEvent.getY());
            }
        });
        this.m_gestureDetector = gestureDetector;
        gestureDetector.setIsLongpressEnabled(true);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (i2 < 1 || i3 < 1) {
            return;
        }
        QtNative.setSurface(getId(), surfaceHolder.getSurface(), i2, i3);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        QtNative.setSurface(getId(), null, 0, 0);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        QtNative.sendTouchEvent(motionEvent, getId());
        this.m_gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        QtNative.sendTrackballEvent(motionEvent, getId());
        return true;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return QtNative.sendGenericMotionEvent(motionEvent, getId());
    }
}
