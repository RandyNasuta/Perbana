package com.example.perbana.util.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.perbana.util.AlarmPlayer;

public class DismissAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmPlayer.stopAlarm();

        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null && notificationId != 0) {
            manager.cancel(notificationId);
        }
    }
}
