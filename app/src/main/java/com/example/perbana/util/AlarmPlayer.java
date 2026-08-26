package com.example.perbana.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.perbana.R;

public class AlarmPlayer {
    private static final String TAG = "AlarmPlayer";
    private static MediaPlayer mediaPlayer;

    public static synchronized void startAlarm(Context context) {
        stopAlarm();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.earthquake_alert_sound, audioAttributes, 0);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public static synchronized void stopAlarm() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "stopAlarm: " + e.getMessage());
            }
            mediaPlayer = null;
        }
    }
}
