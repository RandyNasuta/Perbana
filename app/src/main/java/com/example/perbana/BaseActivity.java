package com.example.perbana;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.perbana.presentation.system_info.SystemInformationActivity;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.earthquake.EarthquakeSensorDetector;
import com.example.perbana.util.earthquake.StaLtaDetector;
import com.example.perbana.util.receiver.DismissAlarmReceiver;
import com.example.perbana.util.worker.EarthquakeWorker;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    protected EarthquakeSensorDetector sensorDetector;
    protected StaLtaDetector staLtaDetector;
    protected boolean isCooldown = false;
    protected boolean hasShownCalibrationTest = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setupSensoringTesting();
    }

    protected abstract void initView();

    protected void setupSensoringTesting() {
        sensorDetector = new EarthquakeSensorDetector(this);

        //Sensor_DELAY_GAME berjalan -50Hz.
        //STA: 50 sample (1 detik). LTA: 500 sample (10 detik)
        staLtaDetector = new StaLtaDetector(50, 500, 15.0);

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

                if (!hasShownCalibrationTest && staLtaDetector.isCalibrated()) {
                    hasShownCalibrationTest = true;
                    runOnUiThread(() -> {
                        Log.i(TAG, "Kalibrasi selesai! Sensor gempa siap");
                        Toast.makeText(BaseActivity.this, "Kalibrasi selesai! Sensor gempa siap", Toast.LENGTH_SHORT).show();
                    });
                }
                boolean isEarthquake = staLtaDetector.processAccelaration(acceleration);

                if (isEarthquake && !isCooldown) {
                    isCooldown = true;
                    Log.w(TAG, "Potensi gempa terdeteksi!");

                    runOnUiThread(() -> {
                        Toast.makeText(BaseActivity.this, "GEMPA TERDETEKSI!", Toast.LENGTH_SHORT).show();
                        triggerNotification();

                        new android.os.Handler().postDelayed(() -> {
                            isCooldown = false;
                            Toast.makeText(BaseActivity.this, "Sensor siap deteksi kembali", Toast.LENGTH_SHORT).show();
                        }, 15000);
                    });
                }
            }
        });
    }

    protected void triggerNotification() {
        Log.i(TAG, "triggerNotification: Start trigger");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        AlarmPlayer.startAlarm(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(EarthquakeWorker.CHANNEL_ID_EARTHQUAKE);
            NotificationChannel channel = new NotificationChannel(
                    EarthquakeWorker.CHANNEL_ID_EARTHQUAKE,
                    "Peringatan Gempa Terdekat",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.setSound(null, null);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        int notificationId = (int) System.currentTimeMillis();

        Intent dismissIntent = new Intent(this, DismissAlarmReceiver.class);
        dismissIntent.putExtra("NOTIFICATION_ID", notificationId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, notificationId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext() , EarthquakeWorker.CHANNEL_ID_EARTHQUAKE)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle("UJI COBA ALARM GEMPA")
                .setContentText("Pengujian sirine bahaya dan notifikasi berhasil")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setColor(Color.RED)
                .setColorized(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .addAction(R.drawable.warning, "MATIKAN ALARM", dismissPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorDetector != null) {
            sensorDetector.stopListening();
        }
    }
}
