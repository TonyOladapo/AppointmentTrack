package com.oladapo.appointmenttrack.WorkManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.oladapo.appointmenttrack.Activities.CreateEditAppointmentActivity;
import com.oladapo.appointmenttrack.App.BaseApp;
import com.oladapo.appointmenttrack.R;

import java.util.Date;
import java.util.Objects;

public class ReminderWork extends Worker {

    public ReminderWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        scheduleNotification();
        return Result.success();
    }

    private void scheduleNotification() {

        int color = Color.parseColor("#990033");

        Data data = getInputData();

        String name = data.getString(CreateEditAppointmentActivity.DATA_KEY_NAME);
        String date = data.getString(CreateEditAppointmentActivity.DATA_KEY_DATE);
        String time = data.getString(CreateEditAppointmentActivity.DATA_KEY_TIME);

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
