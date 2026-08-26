package com.example.perbana.util.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.perbana.R;
import com.example.perbana.db.local.PerbanaPreferences;
import com.example.perbana.util.AlarmPlayer;
import com.example.perbana.util.receiver.DismissAlarmReceiver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EarthquakeWorker extends Worker {
    private final String TAG = "EarthquakeWorker";
    public final static String CHANNEL_ID_EARTHQUAKE = "earthquake_alert_channel";

    public EarthquakeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PerbanaPreferences pref = new PerbanaPreferences(getApplicationContext());
        double userLat = pref.getLatitude();
        double userLon = pref.getLongitude();

        if (userLat == 0.0 && userLon == 0.0) {
            return Result.success();
        }

        try {
            URL url = new URL("https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                JSONObject root = new JSONObject(builder.toString());
                JSONObject earthquake = root.getJSONObject("Infogempa").getJSONObject("gempa");

                String dateTime = earthquake.getString("DateTime");
                String coordinates = earthquake.getString("Coordinates");
                double magnitude = earthquake.getDouble("Magnitude");
                String wilayah = earthquake.getString("Wilayah");

                if (dateTime.equals(pref.getLastEarthquakeDate())) {
                    return Result.success();
                }

                String[] latLng = coordinates.split(",");
                double earthquakeLat = Double.parseDouble(latLng[0]);
                double earthquakeLon = Double.parseDouble(latLng[1]);

                double length = calculateLength(userLat, userLon, earthquakeLat, earthquakeLon);

                if (length <= 150.0 || magnitude >= 6.0) {
                    String title = "Peringatan Gempa (M " + magnitude + ")";
                    String message = "Lokasi: " + wilayah + " (" + Math.round(length) + " km dari lokasi Anda)";

                    showDangerNotification(title, message);
                    pref.setLastEarthquakeDate(dateTime);
                }
            }
            conn.disconnect();
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "doWork: error: " + e.getMessage());
            return Result.retry();
        }
    }

    private void showDangerNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        AlarmPlayer.startAlarm(getApplicationContext());

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

        Intent dismissIntent = new Intent(getApplicationContext(), DismissAlarmReceiver.class);
        dismissIntent.putExtra("NOTIFICATION_ID", notificationId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext() , EarthquakeWorker.CHANNEL_ID_EARTHQUAKE)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(title)
                .setContentText(message)
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

    private double calculateLength(double userLat, double userLon, double earthquakeLat, double earthquakeLon) {
        final int R = 6371; //Earth Radius
        double dLat = Math.toRadians(earthquakeLat - userLat);
        double dLon = Math.toRadians(earthquakeLon - userLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(earthquakeLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
