package org.qtproject.qt5.android.positioning;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class QtPositioning implements LocationListener {
    public static final int QT_ACCESS_ERROR = 0;
    public static final int QT_CLOSED_ERROR = 1;
    public static final int QT_GPS_PROVIDER = 1;
    public static final int QT_NETWORK_PROVIDER = 2;
    public static final int QT_POSITION_NO_ERROR = 3;
    public static final int QT_POSITION_UNKNOWN_SOURCE_ERROR = 2;
    public static final int QT_SATELLITE_NO_ERROR = 2;
    public static final int QT_SATELLITE_UNKNOWN_SOURCE_ERROR = -1;
    private static final String TAG = "QtPositioning";
    static LocationManager locationManager = null;
    static Object m_syncObject = new Object();
    static HashMap<Integer, QtPositioning> runningListeners = new HashMap<>();
    private int nativeClassReference = 0;
    private int expectedProviders = 0;
    private boolean isSingleUpdate = false;
    private int updateIntervalTime = 0;
    private Location lastGps = null;
    private Location lastNetwork = null;
    private boolean isSatelliteUpdate = false;
    private PositioningLooper looperThread = new PositioningLooper();

    public static native void locationProvidersChanged(int i);

    public static native void locationProvidersDisabled(int i);

    public static native void positionUpdated(Location location, int i, boolean z);

    public static native void satelliteUpdated(GpsSatellite[] gpsSatelliteArr, int i, boolean z);

    public static void setContext(Context context) {
        try {
            locationManager = (LocationManager) context.getSystemService("location");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[] providerList() {
        LocationManager locationManager2 = locationManager;
        if (locationManager2 == null) {
            Log.w(TAG, "No locationManager available in QtPositioning");
            return new int[0];
        }
        List<String> providers = locationManager2.getProviders(true);
        int[] iArr = new int[providers.size()];
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).equals("gps")) {
                iArr[i] = 0;
            } else if (providers.get(i).equals("network")) {
                iArr[i] = 1;
            } else if (providers.get(i).equals("passive")) {
                iArr[i] = 2;
            } else {
                iArr[i] = -1;
            }
        }
        return iArr;
    }

    public static Location lastKnownPosition(boolean z) {
        Location location;
        Location location2;
        try {
            location2 = locationManager.getLastKnownLocation("gps");
            if (z) {
                location = null;
            } else {
                location = locationManager.getLastKnownLocation("network");
            }
        } catch (Exception e) {
            e.printStackTrace();
            location = null;
            location2 = null;
        }
        if (location2 != null && location != null) {
            if (location.getTime() - location2.getTime() < 14400000) {
                return location2;
            }
            return location;
        } else if (location2 != null) {
            return location2;
        } else {
            if (location == null) {
                return null;
            }
            return location;
        }
    }

    private static boolean expectedProvidersAvailable(int i) {
        List<String> providers = locationManager.getProviders(true);
        if ((i & 1) <= 0 || !providers.contains("gps")) {
            return (i & 2) > 0 && providers.contains("network");
        }
        return true;
    }

    private static void addActiveListener(QtPositioning qtPositioning, String str) {
        int i = qtPositioning.nativeClassReference;
        qtPositioning.setActiveLooper(true);
        if (runningListeners.containsKey(Integer.valueOf(i)) && runningListeners.get(Integer.valueOf(i)) != qtPositioning) {
            removeActiveListener(i);
        }
        locationManager.requestSingleUpdate(str, qtPositioning, qtPositioning.looper());
        runningListeners.put(Integer.valueOf(i), qtPositioning);
    }

    private static void addActiveListener(QtPositioning qtPositioning, String str, long j, float f) {
        int i = qtPositioning.nativeClassReference;
        qtPositioning.setActiveLooper(true);
        if (runningListeners.containsKey(Integer.valueOf(i)) && runningListeners.get(Integer.valueOf(i)) != qtPositioning) {
            removeActiveListener(i);
        }
        locationManager.requestLocationUpdates(str, j, f, qtPositioning, qtPositioning.looper());
        runningListeners.put(Integer.valueOf(i), qtPositioning);
    }

    private static void removeActiveListener(QtPositioning qtPositioning) {
        removeActiveListener(qtPositioning.nativeClassReference);
    }

    private static void removeActiveListener(int i) {
        QtPositioning remove = runningListeners.remove(Integer.valueOf(i));
        if (remove != null) {
            locationManager.removeUpdates(remove);
            remove.setActiveLooper(false);
        }
    }

    public static int startUpdates(int i, int i2, int i3) {
        boolean z;
        synchronized (m_syncObject) {
            try {
                try {
                    QtPositioning qtPositioning = new QtPositioning();
                    qtPositioning.nativeClassReference = i;
                    qtPositioning.expectedProviders = i2;
                    qtPositioning.isSatelliteUpdate = false;
                    if (i3 == 0) {
                        i3 = 50;
                    }
                    qtPositioning.updateIntervalTime = i3;
                    if ((i2 & 1) > 0) {
                        Log.d(TAG, "Regular updates using GPS " + i3);
                        try {
                            addActiveListener(qtPositioning, "gps", i3, 0.0f);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                            z = true;
                        }
                    }
                    z = false;
                    if ((i2 & 2) > 0) {
                        Log.d(TAG, "Regular updates using network " + i3);
                        try {
                            addActiveListener(qtPositioning, "network", i3, 0.0f);
                        } catch (SecurityException e2) {
                            e2.printStackTrace();
                            z = true;
                        }
                    }
                    if (!z) {
                        return !expectedProvidersAvailable(i2) ? 1 : 3;
                    }
                    removeActiveListener(qtPositioning);
                    return 0;
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return 2;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void stopUpdates(int i) {
        synchronized (m_syncObject) {
            try {
                try {
                    Log.d(TAG, "Stopping updates");
                    removeActiveListener(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static int requestUpdate(int i, int i2) {
        boolean z;
        synchronized (m_syncObject) {
            try {
                try {
                    QtPositioning qtPositioning = new QtPositioning();
                    qtPositioning.nativeClassReference = i;
                    qtPositioning.isSingleUpdate = true;
                    qtPositioning.expectedProviders = i2;
                    qtPositioning.isSatelliteUpdate = false;
                    if ((i2 & 1) > 0) {
                        Log.d(TAG, "Single update using GPS");
                        try {
                            addActiveListener(qtPositioning, "gps");
                        } catch (SecurityException e) {
                            e.printStackTrace();
                            z = true;
                        }
                    }
                    z = false;
                    if ((i2 & 2) > 0) {
                        Log.d(TAG, "Single update using network");
                        try {
                            addActiveListener(qtPositioning, "network");
                        } catch (SecurityException e2) {
                            e2.printStackTrace();
                            z = true;
                        }
                    }
                    if (z) {
                        removeActiveListener(qtPositioning);
                        return 0;
                    } else if (!expectedProvidersAvailable(i2)) {
                        return 1;
                    } else {
                        return 3;
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                    return 2;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static int startSatelliteUpdates(int i, int i2, boolean z) {
        boolean z2;
        synchronized (m_syncObject) {
            try {
                try {
                    QtPositioning qtPositioning = new QtPositioning();
                    qtPositioning.isSatelliteUpdate = true;
                    qtPositioning.nativeClassReference = i;
                    qtPositioning.expectedProviders = 1;
                    qtPositioning.isSingleUpdate = z;
                    if (i2 == 0) {
                        i2 = 50;
                    }
                    if (z) {
                        Log.d(TAG, "Single update for Satellites " + i2);
                    } else {
                        Log.d(TAG, "Regular updates for Satellites " + i2);
                    }
                    try {
                        addActiveListener(qtPositioning, "gps", i2, 0.0f);
                        z2 = false;
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        z2 = true;
                    }
                    if (z2) {
                        removeActiveListener(qtPositioning);
                        return 0;
                    } else if (!expectedProvidersAvailable(qtPositioning.expectedProviders)) {
                        return 1;
                    } else {
                        return 2;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return -1;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Looper looper() {
        return this.looperThread.looper();
    }

    private void setActiveLooper(boolean z) {
        try {
            if (z) {
                if (this.looperThread.isAlive()) {
                    return;
                }
                if (this.isSatelliteUpdate) {
                    this.looperThread.isSatelliteListener(true);
                }
                long currentTimeMillis = System.currentTimeMillis();
                this.looperThread.start();
                while (!this.looperThread.isReady()) {
                }
                long currentTimeMillis2 = System.currentTimeMillis();
                Log.d(TAG, "Looper Thread startup time in ms: " + (currentTimeMillis2 - currentTimeMillis));
                return;
            }
            this.looperThread.quitLooper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* loaded from: classes.dex */
    public class PositioningLooper extends Thread implements GpsStatus.Listener {
        private boolean isSatelliteLooper;
        private LocationManager locManager;
        private boolean looperRunning;
        private Looper posLooper;

        private PositioningLooper() {
            QtPositioning.this = r2;
            this.isSatelliteLooper = false;
            this.locManager = null;
            this.looperRunning = false;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            new Handler();
            if (this.isSatelliteLooper) {
                try {
                    QtPositioning.locationManager.addGpsStatusListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.posLooper = Looper.myLooper();
            synchronized (this) {
                this.looperRunning = true;
            }
            Looper.loop();
            synchronized (this) {
                this.looperRunning = false;
            }
        }

        public void quitLooper() {
            if (this.isSatelliteLooper) {
                QtPositioning.locationManager.removeGpsStatusListener(this);
            }
            looper().quit();
        }

        public synchronized boolean isReady() {
            return this.looperRunning;
        }

        public void isSatelliteListener(boolean z) {
            this.isSatelliteLooper = z;
        }

        public Looper looper() {
            return this.posLooper;
        }

        @Override // android.location.GpsStatus.Listener
        public void onGpsStatusChanged(int i) {
            if (i == 4) {
                ArrayList arrayList = new ArrayList();
                for (GpsSatellite gpsSatellite : QtPositioning.locationManager.getGpsStatus(null).getSatellites()) {
                    arrayList.add(gpsSatellite);
                }
                QtPositioning.satelliteUpdated((GpsSatellite[]) arrayList.toArray(new GpsSatellite[arrayList.size()]), QtPositioning.this.nativeClassReference, QtPositioning.this.isSingleUpdate);
            }
        }
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location location) {
        if (location == null || this.isSatelliteUpdate) {
            return;
        }
        if (this.isSingleUpdate || this.expectedProviders < 3) {
            positionUpdated(location, this.nativeClassReference, this.isSingleUpdate);
        } else if (location.getProvider().equals("gps")) {
            this.lastGps = location;
            positionUpdated(location, this.nativeClassReference, this.isSingleUpdate);
        } else if (location.getProvider().equals("network")) {
            this.lastNetwork = location;
            if (this.lastGps == null) {
                positionUpdated(location, this.nativeClassReference, this.isSingleUpdate);
            } else if (location.getTime() - this.lastGps.getTime() < this.updateIntervalTime) {
            } else {
                positionUpdated(location, this.nativeClassReference, this.isSingleUpdate);
            }
        }
    }

    @Override // android.location.LocationListener
    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String str) {
        Log.d(TAG, "Enabled provider: " + str);
        locationProvidersChanged(this.nativeClassReference);
    }

    @Override // android.location.LocationListener
    public void onProviderDisabled(String str) {
        Log.d(TAG, "Disabled provider: " + str);
        locationProvidersChanged(this.nativeClassReference);
        if (!expectedProvidersAvailable(this.expectedProviders)) {
            locationProvidersDisabled(this.nativeClassReference);
        }
    }
}
