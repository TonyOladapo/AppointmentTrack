package com.oladapo.appointmenttrack.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.oladapo.appointmenttrack.R;
import com.oladapo.appointmenttrack.App.BaseApp;

import java.util.Date;
import java.util.Objects;

public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {

                Thread.sleep(5000);
                startNotification();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startNotification() {

        int color = Color.parseColor("#990033");

        SharedPreferences preferences = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = preferences.edit();

        String name = preferences.getString("nameNotif", "");
        String date = preferences.getString("dateNotif", "");
        String time = preferences.getString("timeNotif", "");

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

        editor.clear();
        editor.apply();
    }
}
