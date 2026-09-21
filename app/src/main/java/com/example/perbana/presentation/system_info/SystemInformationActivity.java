package com.example.perbana.presentation.system_info;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.perbana.BaseActivity;
import com.example.perbana.BuildConfig;
import com.example.perbana.MainActivity;
import com.example.perbana.R;
import com.example.perbana.db.local.PerbanaPreferences;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.AppConstants;
import com.example.perbana.util.receiver.DismissAlarmReceiver;
import com.example.perbana.util.service.EarthquakeForegroundService;
import com.example.perbana.util.worker.EarthquakeWorker;
import com.google.android.material.button.MaterialButton;

public class SystemInformationActivity extends BaseActivity {
    private final String TAG = "SystemInformationActivity";

    //View
    private TextView tvAppVersion = null;
    private MaterialButton btnTestAlarm = null;
    private MaterialButton btnForceSync = null;
    private ScrollView main = null;
    private SwitchCompat switchEarthquakeSensor = null;

    //Variabel
    private PerbanaPreferences pref;

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

        pref = new PerbanaPreferences(SystemInformationActivity.this);

        initView();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SystemInformationActivity.this, MainActivity.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void initView() {
        tvAppVersion = findViewById(R.id.tv_app_version);
        btnTestAlarm = findViewById(R.id.btn_test_alarm);
        btnForceSync = findViewById(R.id.btn_force_sync);
        switchEarthquakeSensor = findViewById(R.id.switch_earthquake_sensor);
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

        boolean isSensorActive = pref.getInitiateEarthquakeSensor();
        switchEarthquakeSensor.setChecked(isSensorActive);

        if (isSensorActive) {
            Intent intent = new Intent(SystemInformationActivity.this, EarthquakeForegroundService.class);
            intent.setAction(EarthquakeForegroundService.ACTION_START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }

        switchEarthquakeSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked) {
                pref.setInitiateEarthquakeSensor(isChecked);

                Intent intent = new Intent(SystemInformationActivity.this, EarthquakeForegroundService.class);
                if (isChecked) {
                    intent.setAction(EarthquakeForegroundService.ACTION_START);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                } else {
                    intent.setAction(EarthquakeForegroundService.ACTION_STOP);
                    startService(intent);
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