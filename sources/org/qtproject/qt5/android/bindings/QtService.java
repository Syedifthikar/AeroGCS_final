package org.qtproject.qt5.android.bindings;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import org.qtproject.qt5.android.bindings.QtApplication;

/* loaded from: classes2.dex */
public class QtService extends Service {
    QtServiceLoader m_loader = new QtServiceLoader(this);

    protected void onCreateHook() {
        this.m_loader.onCreate();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        onCreateHook();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        QtApplication.invokeDelegate(new Object[0]);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(intent);
        if (res.invoked) {
            return (IBinder) res.methodReturns;
        }
        return null;
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        if (!QtApplication.invokeDelegate(newConfig).invoked) {
            super.onConfigurationChanged(newConfig);
        }
    }

    public void super_onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        if (!QtApplication.invokeDelegate(new Object[0]).invoked) {
            super.onLowMemory();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(intent, Integer.valueOf(flags), Integer.valueOf(startId));
        if (res.invoked) {
            return ((Integer) res.methodReturns).intValue();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public int super_onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override // android.app.Service
    public void onTaskRemoved(Intent rootIntent) {
        if (!QtApplication.invokeDelegate(rootIntent).invoked) {
            super.onTaskRemoved(rootIntent);
        }
    }

    public void super_onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override // android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        if (!QtApplication.invokeDelegate(Integer.valueOf(level)).invoked) {
            super.onTrimMemory(level);
        }
    }

    public void super_onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        QtApplication.InvokeResult res = QtApplication.invokeDelegate(intent);
        if (res.invoked) {
            return ((Boolean) res.methodReturns).booleanValue();
        }
        return super.onUnbind(intent);
    }

    public boolean super_onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
