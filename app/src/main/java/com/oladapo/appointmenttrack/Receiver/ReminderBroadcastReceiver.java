package com.oladapo.appointmenttrack.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.oladapo.appointmenttrack.R;
import com.oladapo.appointmenttrack.Service.BootService;
import com.oladapo.appointmenttrack.Util.BaseApp;

import java.util.Date;
import java.util.Objects;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static String TAG = "vkv";

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent i = new Intent(context, BootService.class);
            ComponentName service = context.startService(i);

            if (null == service) {
                Log.e(TAG, "onReceive: Could not start service");
            } else {
                Log.d(TAG, "onReceive: Service successfully started");
            }
        } else {

            int color = Color.parseColor("#990033");

            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");

            String title = "Appointment reminder";
            String message = "You have an appointment with " + name + " on " + date + " at " + time;

            Notification notification = new NotificationCompat.Builder(context, BaseApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_calender_black_24dp)
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
}
