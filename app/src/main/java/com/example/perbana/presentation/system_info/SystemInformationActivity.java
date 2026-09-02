package com.example.perbana.presentation.system_info;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.perbana.BuildConfig;
import com.example.perbana.R;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.AppConstants;
import com.example.perbana.util.earthquake.EarthquakeSensorDetector;
import com.example.perbana.util.earthquake.StaLtaDetector;
import com.example.perbana.util.receiver.DismissAlarmReceiver;
import com.example.perbana.util.worker.EarthquakeWorker;
import com.google.android.material.button.MaterialButton;

public class SystemInformationActivity extends AppCompatActivity {
    private final String TAG = "SystemInformationActivity";

    //View
    private TextView tvAppVersion = null;
    private MaterialButton btnTestAlarm = null;
    private MaterialButton btnForceSync = null;
    private ScrollView main = null;

    //Variabel
    private EarthquakeSensorDetector sensorDetector;
    private StaLtaDetector staLtaDetector;
    private boolean isCooldown = false;
    private boolean hasShownCalibrationTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        setupSensoringTesting();
    }

    private void initView() {
        tvAppVersion = findViewById(R.id.tv_app_version);
        btnTestAlarm = findViewById(R.id.btn_test_alarm);
        btnForceSync = findViewById(R.id.btn_force_sync);
        main = findViewById(R.id.main);
        main.setBackgroundResource(AppConstants.weatherBackgroundResource);

        String appVersion = BuildConfig.VERSION_NAME;
        String environment = BuildConfig.ENVIRONMENT;

        tvAppVersion.setText("Versi Aplikasi: v" + appVersion + " (" + environment + ")");

        btnTestAlarm.setOnClickListener(view -> triggerNotification());

        btnForceSync.setOnClickListener(view -> {
            OneTimeWorkRequest testWork = new OneTimeWorkRequest.Builder(EarthquakeWorker.class).build();
            WorkManager.getInstance(this).enqueue(testWork);
            Toast.makeText(this, "Worker berhasil dipicu!", Toast.LENGTH_SHORT).show();
        });


    }

    private void setupSensoringTesting() {
        sensorDetector = new EarthquakeSensorDetector(this);

        //Sensor_DELAY_GAME berjalan -50Hz.
        //STA: 50 sample (1 detik). LTA: 500 sample (10 detik). Threshold: rasio 4.0x lipat
        staLtaDetector = new StaLtaDetector(50, 500, 8.0);

        sensorDetector.startListening(new EarthquakeSensorDetector.OnVibrationDetectedListener() {
            @Override
            public void onVibrationDetected(double acceleration, float x, float y, float z) {

                boolean isVerticalLift = Math.abs(z) > 1.2;

                //Hindari getaran micro / noise sensor murni di bawah 0.2 m/s2 masuk perhitungan
                if (acceleration < 2.2 || isVerticalLift) {
                    acceleration = 0.0;
                }

                if (!hasShownCalibrationTest && staLtaDetector.isCalibrated()) {
                    hasShownCalibrationTest = true;
                    runOnUiThread(() -> {
                        Toast.makeText(SystemInformationActivity.this, "Kalibrasi selesai! Sensor gempa siap", Toast.LENGTH_SHORT).show();
                    });
                }
                boolean isEarthquake = staLtaDetector.processAccelaration(acceleration);

                if (isEarthquake && !isCooldown) {
                    isCooldown = true;
                    Log.w(TAG, "Potensi gempa terdeteksi!");

                    runOnUiThread(() -> {
                        Toast.makeText(SystemInformationActivity.this, "GEMPA TERDETEKSI!", Toast.LENGTH_SHORT).show();
                        triggerNotification();

                        new android.os.Handler().postDelayed(() -> {
                            isCooldown = false;
                            Toast.makeText(SystemInformationActivity.this, "Sensor siap deteksi kembali", Toast.LENGTH_SHORT).show();
                        }, 15000);
                    });
                }
            }
        });
    }

    private void triggerNotification() {
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