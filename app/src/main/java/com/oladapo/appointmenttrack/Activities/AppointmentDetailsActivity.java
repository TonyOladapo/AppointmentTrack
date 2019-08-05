package com.oladapo.appointmenttrack.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.Database.ViewModel;
import com.oladapo.appointmenttrack.R;

import java.util.List;
import java.util.Objects;

public class AppointmentDetailsActivity extends AppCompatActivity implements LifecycleObserver {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLIENT_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_REMINDER_DATE = "reminder_date";
    public static final String EXTRA_REMINDER_TIME = "reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_DATE = "client_reminder_date";
    public static final String EXTRA_CLIENT_REMINDER_TIME = "client_reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_MESSAGE = "client_reminder_message";
    public static final String EXTRA_REMINDER_STATE = "reminder_state";
    public static final String EXTRA_CLIENT_REMINDER_STATE = "client_reminder_state";
    public static final String EXTRA_ALL_DAY_STATE = "all_day_state";
    public static final String EXTRA_SMS_REMINDER = "sms_reminder";
    public static final String EXTRA_EMAIL_REMINDER = "email_reminder";
    public static final String EXTRA_BOTH = "both";

    private int id;
    private String clientName;
    private String phone;
    private String email;
    private String desc;
    private String date;
    private String time;
    private String reminderDate;
    private String reminderTime;
    private String clientReminderDate;
    private String clientReminderTime;
    private String clientReminderMessage;
    private int reminderState;
    private int clientReminderState;
    private int allDayState;
    private boolean isSms;
    private boolean isEmail;
    private boolean isBoth;

    private static final int RC_EDIT_CLIENT = 4;
    private static final int RESULT_OK = 2;

    private boolean isFabOpen;

    TextView phoneTextView;
    TextView emailTextView;
    TextView descTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView clientNameTextView;

    FloatingActionButton fabMain;
    FloatingActionButton fabAddEditReminder;
    FloatingActionButton fabAddEditClientReminder;

    CoordinatorLayout coordinatorLayout;

    ViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        coordinatorLayout = findViewById(R.id.coordinatorAppDetails);

        clientNameTextView = findViewById(R.id.clientNameDetails);
        phoneTextView = findViewById(R.id.phoneDetails);
        emailTextView = findViewById(R.id.emailDetails);
        descTextView = findViewById(R.id.descDetails);
        dateTextView = findViewById(R.id.dateDetails);
        timeTextView = findViewById(R.id.timeDetails);

        fabMain = findViewById(R.id.fabDetailsMain);
        fabAddEditReminder = findViewById(R.id.fabAddEditReminder);
        fabAddEditClientReminder = findViewById(R.id.fabAddEditClientReminder);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    showMenu();
                } else {
                    startEditAppointment();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimary);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimary);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        } else {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }

        Toolbar toolbar = findViewById(R.id.appointment_details_toolbar);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        Intent intent = getIntent();

        id = intent.getIntExtra(EXTRA_ID, 0);
        clientName = intent.getStringExtra(EXTRA_CLIENT_NAME);
        phone = intent.getStringExtra(EXTRA_PHONE);
        email = intent.getStringExtra(EXTRA_EMAIL);
        desc = intent.getStringExtra(EXTRA_DESC);
        date = intent.getStringExtra(EXTRA_DATE);
        time = intent.getStringExtra(EXTRA_TIME);
        reminderDate = intent.getStringExtra(EXTRA_REMINDER_DATE);
        reminderTime = intent.getStringExtra(EXTRA_REMINDER_TIME);
        clientReminderDate = intent.getStringExtra(EXTRA_CLIENT_REMINDER_DATE);
        clientReminderTime = intent.getStringExtra(EXTRA_CLIENT_REMINDER_TIME);
        clientReminderMessage = intent.getStringExtra(EXTRA_CLIENT_REMINDER_MESSAGE);
        reminderState = intent.getIntExtra(EXTRA_REMINDER_STATE, 0);
        clientReminderState = intent.getIntExtra(EXTRA_CLIENT_REMINDER_STATE, 0);
        allDayState = intent.getIntExtra(EXTRA_ALL_DAY_STATE, 0);
        isSms = intent.getBooleanExtra(EXTRA_SMS_REMINDER, false);
        isEmail = intent.getBooleanExtra(EXTRA_EMAIL_REMINDER, false);
        isBoth = intent.getBooleanExtra(EXTRA_BOTH, false);

        clientNameTextView.setText(clientName);
        dateTextView.setText(date);
        timeTextView.setText(time);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showMenu() {
        isFabOpen = true;
        fabAddEditReminder.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabAddEditClientReminder.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeMenu() {
        isFabOpen = false;
        fabAddEditReminder.animate().translationY(0);
        fabAddEditClientReminder.animate().translationY(0);
    }

    private void startEditAppointment() {

        Intent intent = new Intent(AppointmentDetailsActivity.this, CreateEditAppointmentActivity.class);

        intent.putExtra(CreateEditAppointmentActivity.EXTRA_ID, id);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_NAME, clientName);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_PHONE, phone);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL, email);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE, date);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_TIME, time);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DESC, desc);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_DATE, reminderDate);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_TIME, reminderTime);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_DATE, clientReminderDate);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_TIME, clientReminderTime);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_MESSAGE, clientReminderMessage);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_STATE, reminderState);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_STATE, clientReminderState);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_ALL_DAY_STATE, allDayState);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_SMS_REMINDER, isSms);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL_REMINDER, isEmail);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_BOTH, isBoth);

        startActivityForResult(intent, RC_EDIT_CLIENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_EDIT_CLIENT && resultCode == RESULT_OK) {
            int id = Objects.requireNonNull(data).getIntExtra(CreateEditAppointmentActivity.EXTRA_ID, -1);

            if (id == -1) {
                Snackbar.make(coordinatorLayout, "Something went wrong! Could not update.", Snackbar.LENGTH_LONG).show();
                return;
            }

            final String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");
            String desc = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String reminderDate = data.getStringExtra("reminderDate");
            String reminderTime = data.getStringExtra("reminderTime");
            String clientReminderDate = data.getStringExtra("clientReminderDate");
            String clientReminderTime = data.getStringExtra("clientReminderTime");
            int reminderState = data.getIntExtra("reminderState", 0);
            int clientReminderState = data.getIntExtra("clientReminderState", 0);
            String reminderMessage = "message";
            String dateAdded = data.getStringExtra("dateAdded");
            int allDayState = data.getIntExtra("allDayState", 0);
            boolean isSms = data.getBooleanExtra("is_sms", false);
            boolean isEmail = data.getBooleanExtra("is_email", false);
            boolean isBoth = data.getBooleanExtra("is_both", false);

            Appointments appointments = new Appointments(name, phone, email, desc, date, time,
                    reminderDate, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, allDayState,
                    isSms, isEmail, isBoth);

            viewModel.getAllAppointments().observe(this, new Observer<List<Appointments>>() {
                @Override
                public void onChanged(List<Appointments> appointments) {
                    clientNameTextView.setText(name);
                }
            });

            appointments.setId(id);

            viewModel.update(appointments);

            Snackbar.make(coordinatorLayout, "Appointment updated", Snackbar.LENGTH_LONG).show();
        }
    }
}
