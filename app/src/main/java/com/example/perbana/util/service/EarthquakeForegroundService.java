package com.example.perbana.util.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.perbana.R;
import com.example.perbana.presentation.system_info.SystemInformationActivity;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.earthquake.EarthquakeSensorDetector;
import com.example.perbana.util.earthquake.StaLtaDetector;
import com.example.perbana.util.receiver.DismissAlarmReceiver;
import com.example.perbana.util.worker.EarthquakeWorker;

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
    protected boolean isCalibratrionLogged = false;


    @Override
    public void onCreate() {
        super.onCreate();
        initSensor();
    }

    private void initSensor() {
        sensorDetector = new EarthquakeSensorDetector(this);
        staLtaDetector = new StaLtaDetector(50, 500, 15.0);

        //Atur status awal saat service baru jalan
        updateForegroundNotification("Belum Terkalibrasi");

        sensorDetector.startListening(new EarthquakeSensorDetector.OnVibrationDetectedListener() {
            @Override
            public void onVibrationDetected(double acceleration, float x, float y, float z) {

                //Hindari getaran micro / noise sensor murni di bawah 0.2 m/s2 masuk perhitungan
                if (acceleration > 0.5) {
                    Log.i(TAG, "onVibrationDetected: Raw Accel: " + acceleration + " | x: " + x + " | y: " + y + " | z: " + z);
                }

//                boolean isHandlingAction = (Math.abs(x) > 0.3 || Math.abs(y) > 0.3 || Math.abs(z) < 0.4);
                if (acceleration < 2.0) {
                    acceleration = 0.0;
                } else {
                    Log.i(TAG, "Lolos filter: nilai = " + acceleration);
                }

                //Monitor perubahan status kalibrasi
                StaLtaDetector.CalibrationState currentState = staLtaDetector.getCalibrationState();
                if (currentState != lastState) {
                    lastState = currentState;
                    updateNotificationBasedOnState(currentState);
                }

                if (!isCalibratrionLogged && staLtaDetector.isCalibrated()) {
                    isCalibratrionLogged = true;
                    Log.i(TAG, "Kalibrasi selesai! Sensor gempa siap");
                }

                boolean isEarthquake = staLtaDetector.processAccelaration(acceleration);
                if (isEarthquake && !isCoolDown && staLtaDetector.isCalibrated()) {
                    isCoolDown = true;
                    Log.i(TAG, "Potensi gempa terdeteksi");
                    triggerEarthquakeAlarm();

                    handler.postDelayed(() -> isCoolDown = false, 15000);
                }
            }
        });
    }

    private void updateNotificationBasedOnState(StaLtaDetector.CalibrationState state) {
        String textStatus;
        switch (state) {
            case NOT_CALIBRATED:
                textStatus = "Belum Terkalibrasi";
                break;
            case LOADING:
                textStatus = "Memuat Kalibrasi...";
                break;
            case CALIBRATED:
                textStatus = "Terkalibrasi (SIAGA)";
                break;
            default:
                textStatus = "Tidak Diketahui";
                break;
        }
        updateForegroundNotification(textStatus);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (EarthquakeForegroundService.ACTION_START.equals(action)) {
                startForeground(NOTIFICATION_ID_FOREGROUND, createNotification("Memulai Kalibrasi Sensor..."));
            } else if (EarthquakeForegroundService.ACTION_STOP.equals(action)) {
                if (sensorDetector != null) {
                    sensorDetector.stopListening();
                }

                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    private Notification createNotification(String statusText) {
        createChannelIfNeeded();

        Intent notificationIntent = new Intent(this, SystemInformationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID_STATUS)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle("Status Monitor Gempa")
                .setContentText("Kondisi: " + statusText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void updateForegroundNotification(String statusText) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID_FOREGROUND, createNotification(statusText));
        }
    }

    private void createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null && manager.getNotificationChannel(CHANNEL_ID_STATUS) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID_STATUS,
                        "Status Sensor Gempa",
                        NotificationManager.IMPORTANCE_LOW
                );
                channel.setDescription("Menampilkan status kalibrasi sensor gempa di latar belakang");
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void triggerEarthquakeAlarm() {
        AlarmPlayer.startAlarm(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel alertChannel = new NotificationChannel(
                    EarthquakeWorker.CHANNEL_ID_EARTHQUAKE,
                    "Peringatan Gempa Darurat",
                    NotificationManager.IMPORTANCE_HIGH
            );
            alertChannel.enableVibration(true);
            notificationManager.createNotificationChannel(alertChannel);
        }

        int notifcationId = (int) System.currentTimeMillis();
        Intent dismissIntent = new Intent(this, DismissAlarmReceiver.class);
        dismissIntent.putExtra("NOTIFICATION_ID", notifcationId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, notifcationId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder alertBuilder = new NotificationCompat.Builder(this, EarthquakeWorker.CHANNEL_ID_EARTHQUAKE)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle("GEMPA BUMI TERDETEKSI!")
                .setContentText("Segera cari tempat berlindung yang aman!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(Color.RED)
                .setColorized(true)
                .setOngoing(true)
                .addAction(R.drawable.warning, "MATIKAN ALARM", dismissPendingIntent);

        notificationManager.notify(notifcationId, alertBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorDetector != null) {
            sensorDetector.stopListening();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
