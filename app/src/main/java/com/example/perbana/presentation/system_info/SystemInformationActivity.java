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

import com.example.perbana.BaseActivity;
import com.example.perbana.BuildConfig;
import com.example.perbana.R;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.AppConstants;
import com.example.perbana.util.earthquake.EarthquakeSensorDetector;
import com.example.perbana.util.earthquake.StaLtaDetector;
import com.example.perbana.util.receiver.DismissAlarmReceiver;
import com.example.perbana.util.worker.EarthquakeWorker;
import com.google.android.material.button.MaterialButton;

public class SystemInformationActivity extends BaseActivity {
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

    @Override
    protected void initView() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorDetector != null) {
            sensorDetector.stopListening();
        }
    }
}