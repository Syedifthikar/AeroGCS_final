package org.freedesktop.gstreamer.androidmedia;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/* loaded from: classes2.dex */
public class GstAhsCallback implements SensorEventListener {
    public long mAccuracyCallback;
    public long mSensorCallback;
    public long mUserData;

    public static native void gst_ah_sensor_on_accuracy_changed(Sensor sensor, int i, long j, long j2);

    public static native void gst_ah_sensor_on_sensor_changed(SensorEvent sensorEvent, long j, long j2);

    public GstAhsCallback(long sensor_callback, long accuracy_callback, long user_data) {
        this.mSensorCallback = sensor_callback;
        this.mAccuracyCallback = accuracy_callback;
        this.mUserData = user_data;
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        gst_ah_sensor_on_sensor_changed(event, this.mSensorCallback, this.mUserData);
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        gst_ah_sensor_on_accuracy_changed(sensor, accuracy, this.mAccuracyCallback, this.mUserData);
    }
}
