package com.example.perbana;

import static android.view.View.VISIBLE;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";
    private File downloadedApkFile;

    //View
    private LinearLayout llSplash;
    private ProgressBar pbDownload;
    private TextView tvProgress;

    //Variabel
    private ScheduledExecutorService progressScheduler;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        autoUpdateCheck();
    }

    private void initView() {
        llSplash = findViewById(R.id.ll_splash);
        pbDownload = findViewById(R.id.pb_download);
        tvProgress = findViewById(R.id.tv_progress);
    }

    private void goToMainActivity(boolean isDelay) {

        if (isDelay) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else {
            runOnUiThread(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            });
        }
    }

    private void autoUpdateCheck() {
        if (!BuildConfig.ENABLE_UPDATE_CHECK) {
            goToMainActivity(true);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(BuildConfig.GITHUB_RELEASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);

                    JSONObject json = new JSONObject(response.toString());
                    String latestTag = json.getString("tag_name").replace("v", "").trim();
                    String releaseNotes = json.optString("body", "Peningkatan performa dan perbaikan bug.");

                    JSONArray assets = json.getJSONArray("assets");
                    String downloadUrl = "";
                    for (int i = 0; i < assets.length(); i++) {
                        JSONObject asset = assets.getJSONObject(i);
                        if (asset.getString("name").endsWith(".apk")) {
                            downloadUrl = asset.getString("browser_download_url");
                            break;
                        }
                    }

                    if (isNewerVersion(latestTag, BuildConfig.VERSION_NAME) && !downloadUrl.isEmpty()) {
                        String finalDownloadUrl = downloadUrl;
                        runOnUiThread(() -> showUpdateDialog(latestTag, releaseNotes, finalDownloadUrl));
                    } else {
                        goToMainActivity(false);
                    }
                } else {
                    goToMainActivity(false);
                }
            } catch (Exception e) {
                Log.e(TAG, "autoUpdateCheck: error: " + e.getMessage());
                goToMainActivity(false);
            }
        });
    }

    private void showUpdateDialog(String latestVersion, String releaseNotes, String downloadUrl) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Pembaruan Tersedia (v" + latestVersion + ")")
                .setMessage("Versi terbaru Perbana telah rilis.\n\nCatatan Rilis:\n" + releaseNotes)
                .setPositiveButton("Perbarui Sekarang", (dialog, which) -> startSilentDownload(downloadUrl))
                .setNegativeButton("Nanti Saja", (dialog, which) -> goToMainActivity(false))
                .setCancelable(false)
                .show();
    }

    private boolean isNewerVersion(String latestVersion, String currentVersion) {
        String[] latestParts = latestVersion.split("\\.");
        String[] currentParts = currentVersion.split("\\.");
        int length = Math.max(latestParts.length, currentParts.length);

        for (int i = 0; i < length; i++) {
            int latestNum = i < latestParts.length ? Integer.parseInt(latestParts[i].replaceAll("[^0-9]", "")) : 0;
            int currentNum = i < currentParts.length ? Integer.parseInt(currentParts[i].replaceAll("[^0-9]", "")) : 0;

            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false;
    }

    private void startSilentDownload(String apkUrl) {
        downloadedApkFile = new File(getExternalCacheDir(), "update.apk");

        if (downloadedApkFile.exists()) {
            downloadedApkFile.delete();
        };

        runOnUiThread(() -> {
                    if (llSplash != null) {
                        llSplash.setVisibility(VISIBLE);
                    }
                    Toast.makeText(this, "Mengunduh pembaruan aplikasi...", Toast.LENGTH_SHORT).show();
                }
        );

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Pembaruan Perbana");
        request.setDescription("Mengunduh versi terbaru...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(downloadedApkFile));

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (manager == null) {
            goToMainActivity(false);
            return;
        }

        long downloadId = manager.enqueue(request);

        startProgressTracking(manager, downloadId);

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    stopProgressTracking();
                    triggerInstall(downloadedApkFile);
                    unregisterDownloadReceiver();
                }
            }
        };

        ContextCompat.registerReceiver(
                this,
                downloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_EXPORTED
        );
    }

    private void startProgressTracking(DownloadManager manager, long downloadId) {
        stopProgressTracking();

        progressScheduler = Executors.newSingleThreadScheduledExecutor();

        progressScheduler.scheduleWithFixedDelay(() -> {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);

            try (Cursor cursor = manager.query(query)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (statusIndex != -1 && cursor.getInt(statusIndex) == DownloadManager.STATUS_FAILED) {
                        stopProgressTracking();
                        goToMainActivity(false);
                        return;
                    }

                    int bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    int bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);

                    if (bytesDownloadedIndex != -1 && bytesTotalIndex != -1) {
                        long bytesDownloaded = cursor.getLong(bytesDownloadedIndex);
                        long bytesTotal = cursor.getLong(bytesTotalIndex);

                        if (bytesTotal > 0) {
                            int progress = (int) ((bytesDownloaded * 100) / bytesTotal);

                            runOnUiThread(() -> {
                                if (pbDownload != null && tvProgress != null) {
                                    pbDownload.setProgress(progress);
                                    tvProgress.setText("Mengunduh pembaruan... " + progress + "%");
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "startProgressTracking error: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void stopProgressTracking() {
        if (progressScheduler != null && !progressScheduler.isShutdown()) {
            progressScheduler.shutdownNow();
            progressScheduler = null;
        }
    }

    private void unregisterDownloadReceiver() {
        if (downloadReceiver != null) {
            try {
                unregisterReceiver(downloadReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Receiver already unregistered: " + e.getMessage());
            }
            downloadReceiver = null;
        }
    }

    private void triggerInstall(File apkFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !getPackageManager().canRequestPackageInstalls()) {
            startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:" + getPackageName())));
            return;
        }

        Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (downloadedApkFile != null && downloadedApkFile.exists()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || getPackageManager().canRequestPackageInstalls()) {
                triggerInstall(downloadedApkFile);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressTracking();
        unregisterDownloadReceiver();
    }
}