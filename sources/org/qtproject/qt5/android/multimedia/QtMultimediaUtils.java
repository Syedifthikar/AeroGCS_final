package org.qtproject.qt5.android.multimedia;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.OrientationEventListener;
import java.io.File;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class QtMultimediaUtils {
    private static Context m_context = null;
    private static OrientationListener m_orientationListener = null;

    /* loaded from: classes.dex */
    private static class OrientationListener extends OrientationEventListener {
        public static int deviceOrientation = 0;

        public OrientationListener(Context context) {
            super(context);
        }

        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int i) {
            if (i == -1) {
                return;
            }
            deviceOrientation = i;
        }
    }

    public static void setContext(Context context) {
        m_context = context;
        m_orientationListener = new OrientationListener(context);
    }

    static void enableOrientationListener(boolean z) {
        if (z) {
            m_orientationListener.enable();
        } else {
            m_orientationListener.disable();
        }
    }

    static int getDeviceOrientation() {
        return OrientationListener.deviceOrientation;
    }

    static String getDefaultMediaDirectory(int i) {
        File externalStoragePublicDirectory;
        String str = new String();
        if (i == 0) {
            str = Environment.DIRECTORY_MUSIC;
        } else if (i == 1) {
            str = Environment.DIRECTORY_MOVIES;
        } else if (i == 2) {
            str = Environment.DIRECTORY_DCIM;
        }
        new File(BuildConfig.FLAVOR);
        if (i == 3) {
            externalStoragePublicDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Sounds");
        } else {
            externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(str);
        }
        externalStoragePublicDirectory.mkdirs();
        return externalStoragePublicDirectory.getAbsolutePath();
    }

    static void registerMediaFile(String str) {
        MediaScannerConnection.scanFile(m_context, new String[]{str}, null, null);
    }
}
