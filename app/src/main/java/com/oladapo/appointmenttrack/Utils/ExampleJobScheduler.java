package com.oladapo.appointmenttrack.Utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ExampleJobScheduler extends JobService {
    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);

        return true;
    }

    private void doBackgroundWork(final JobParameters parameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);

                    if (jobCancelled) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(parameters, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job canceled before completion");
        jobCancelled = true;
        return true;
    }
}
