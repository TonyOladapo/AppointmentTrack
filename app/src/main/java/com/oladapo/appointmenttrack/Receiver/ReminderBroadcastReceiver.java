package com.oladapo.appointmenttrack.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.oladapo.appointmenttrack.R;
import com.oladapo.appointmenttrack.Util.BaseApp;

import java.util.Objects;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int color = Color.parseColor("#990033");

        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");

        String title = "Appointment reminder";
        String message = "You have an appointment with " + name + " on " + date;

        Notification notification = new NotificationCompat.Builder(context, BaseApp.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setColor(color)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).notify(1, notification);
    }
}
