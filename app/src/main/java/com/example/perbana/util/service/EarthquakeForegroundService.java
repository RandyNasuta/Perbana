package com.example.perbana.util.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.perbana.util.earthquake.EarthquakeSensorDetector;
import com.example.perbana.util.earthquake.StaLtaDetector;

public class EarthquakeForegroundService extends Service {
    private static final String TAG = "EarthquakeForegroundService";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";

    private static final int NOTIFICATION_ID_FOREGROUND = 999;
    private static final String CHANNEL_ID_STATUS = "earthquake_status_channel";

    private EarthquakeSensorDetector sensorDetector;
    private StaLtaDetector staLtaDetector;
    private boolean isCoolDown = false;
    private StaLtaDetector.CalibrationState lastState = null;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        initSensor();
    }

    private void initSensor() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
