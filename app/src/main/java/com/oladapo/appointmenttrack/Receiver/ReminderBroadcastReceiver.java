package com.oladapo.appointmenttrack.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.oladapo.appointmenttrack.R;
import com.oladapo.appointmenttrack.Service.BootService;
import com.oladapo.appointmenttrack.App.BaseApp;

import java.util.Date;
import java.util.Objects;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static String TAG = "vkv";

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, BootService.class);
            intent.setAction("com.oladapo.appointmenttrack.Receiver");
            context.startService(serviceIntent);

            Log.d(TAG, "onReceive: success boot");
        } else {

            scheduleNotification(context, intent);
            Log.d(TAG, "onReceive: success notification");
        }
    }

    private void scheduleNotification(Context context, Intent intent) {

        int color = Color.parseColor("#990033");

        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        String title = "Appointment reminder";
        String message = "You have an appointment with " + name + " on " + date + " at " + time;

        Notification notification = new NotificationCompat.Builder(context, BaseApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notification);
    }
}
