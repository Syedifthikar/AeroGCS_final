package org.qtproject.qt5.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/* loaded from: classes.dex */
public class QtNativeLibrariesDir {
    public static final String systemLibrariesDir = "/system/lib/";

    public static String nativeLibrariesDir(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return applicationInfo.nativeLibraryDir + "/";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
