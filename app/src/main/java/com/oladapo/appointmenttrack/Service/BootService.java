package com.oladapo.appointmenttrack.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.oladapo.appointmenttrack.R;
import com.oladapo.appointmenttrack.Util.BaseApp;

import java.util.Date;
import java.util.Objects;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int color = Color.parseColor("#990033");

        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        String title = "Appointment reminder";
        String message = "You have an appointment with " + name + " on " + date + " at " + time;

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), BaseApp.CHANNEL_ID)
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

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notification);
    }
}
