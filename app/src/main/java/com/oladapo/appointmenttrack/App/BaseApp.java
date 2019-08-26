package com.oladapo.appointmenttrack.App;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class BaseApp extends Application {

    public static final String CHANNEL_ID = "channel1Id";
    public static final String CHANNEL_NAME = "Reminder notification";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Reminder notification");
        notificationChannel.enableVibration(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notificationChannel.canBubble();
        }

        NotificationManager manager = getSystemService(NotificationManager.class);
        Objects.requireNonNull(manager).createNotificationChannel(notificationChannel);
    }
}
