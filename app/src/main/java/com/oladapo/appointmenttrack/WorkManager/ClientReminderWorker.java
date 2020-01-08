package com.oladapo.appointmenttrack.WorkManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.oladapo.appointmenttrack.Activities.CreateEditAppointmentActivity;

import static com.oladapo.appointmenttrack.App.BaseApp.TAG;

public class ClientReminderWorker extends Worker {

    public ClientReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        sendReminderMessage();
        return Result.success();
    }

    private void sendReminderMessage() {

        Data data = getInputData();

        String message = data.getString(CreateEditAppointmentActivity.DATA_KEY_MESSAGE);

        boolean isSMS = data.getBoolean(CreateEditAppointmentActivity.DATA_KEY_IS_SMS, false);
        boolean isEmail = data.getBoolean(CreateEditAppointmentActivity.DATA_KEY_IS_EMAIL, false);
        boolean isBoth = data.getBoolean(CreateEditAppointmentActivity.DATA_KEY_IS_BOTH, false);

        Log.d(TAG, "sendReminderMessage: " + isSMS);
    }
}
