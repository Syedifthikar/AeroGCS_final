package org.qtproject.qt5.android.bindings;

import android.app.Service;
import android.content.ComponentName;

/* loaded from: classes2.dex */
public class QtServiceLoader extends QtLoader {
    QtService m_service;

    public QtServiceLoader(QtService service) {
        super(service, QtService.class);
        this.m_service = service;
    }

    public void onCreate() {
        try {
            this.m_contextInfo = this.m_service.getPackageManager().getServiceInfo(new ComponentName(this.m_service, this.m_service.getClass()), 128);
            if (QtApplication.m_delegateObject != null && QtApplication.onCreate != null) {
                QtApplication.invokeDelegateMethod(QtApplication.onCreate, new Object[0]);
            }
            startApp(true);
        } catch (Exception e) {
            e.printStackTrace();
            this.m_service.stopSelf();
        }
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected void finish() {
        this.m_service.stopSelf();
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected String loaderClassName() {
        return "org.qtproject.qt5.android.QtServiceDelegate";
    }

    @Override // org.qtproject.qt5.android.bindings.QtLoader
    protected Class<?> contextClassName() {
        return Service.class;
    }
}
