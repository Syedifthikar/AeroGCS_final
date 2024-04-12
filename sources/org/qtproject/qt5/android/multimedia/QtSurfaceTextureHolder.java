package org.qtproject.qt5.android.multimedia;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceHolder;

/* loaded from: classes.dex */
public class QtSurfaceTextureHolder implements SurfaceHolder {
    private Surface surfaceTexture;

    public QtSurfaceTextureHolder(Surface surface) {
        this.surfaceTexture = surface;
    }

    @Override // android.view.SurfaceHolder
    public void addCallback(SurfaceHolder.Callback callback) {
    }

    @Override // android.view.SurfaceHolder
    public Surface getSurface() {
        return this.surfaceTexture;
    }

    @Override // android.view.SurfaceHolder
    public Rect getSurfaceFrame() {
        return new Rect();
    }

    @Override // android.view.SurfaceHolder
    public boolean isCreating() {
        return false;
    }

    @Override // android.view.SurfaceHolder
    public Canvas lockCanvas(Rect rect) {
        return new Canvas();
    }

    @Override // android.view.SurfaceHolder
    public Canvas lockCanvas() {
        return new Canvas();
    }

    @Override // android.view.SurfaceHolder
    public void removeCallback(SurfaceHolder.Callback callback) {
    }

    @Override // android.view.SurfaceHolder
    public void setFixedSize(int i, int i2) {
    }

    @Override // android.view.SurfaceHolder
    public void setFormat(int i) {
    }

    @Override // android.view.SurfaceHolder
    public void setKeepScreenOn(boolean z) {
    }

    @Override // android.view.SurfaceHolder
    public void setSizeFromLayout() {
    }

    @Override // android.view.SurfaceHolder
    public void setType(int i) {
    }

    @Override // android.view.SurfaceHolder
    public void unlockCanvasAndPost(Canvas canvas) {
    }
}
