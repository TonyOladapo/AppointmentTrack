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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.Database.ViewModel;
import com.oladapo.appointmenttrack.R;

import java.util.Objects;

public class AppointmentDetailsActivity extends AppCompatActivity implements LifecycleObserver {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLIENT_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_REMINDER_TIME = "reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_DATE = "client_reminder_date";
    public static final String EXTRA_CLIENT_REMINDER_TIME = "client_reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_MESSAGE = "client_reminder_message";
    public static final String EXTRA_REMINDER_STATE = "reminder_state";
    public static final String EXTRA_CLIENT_REMINDER_STATE = "client_reminder_state";
    public static final String EXTRA_SMS_REMINDER = "sms_reminder";
    public static final String EXTRA_EMAIL_REMINDER = "email_reminder";
    public static final String EXTRA_BOTH = "both";
    public static final String EXTRA_DATE_TIME = "dateTime";

    private int id;
    private String clientName;
    private String phone;
    private String email;
    private String desc;
    private String date;
    private String time;
    private int reminderTime;
    private String clientReminderDate;
    private String clientReminderTime;
    private String clientReminderMessage;
    private int reminderState;
    private int clientReminderState;
    private boolean isSms;
    private boolean isEmail;
    private boolean isBoth;
    private String dateTime;

    private static final int RC_EDIT_CLIENT = 4;
    private static final int RESULT_OK = 2;

    TextView phoneTextView;
    TextView emailTextView;
    TextView descTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView clientNameTextView;

    FloatingActionButton fabMain;
    ConstraintLayout constraintLayout;

    ViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        constraintLayout = findViewById(R.id.layoutAppointmentDetails);

        clientNameTextView = findViewById(R.id.clientNameDetails);
        phoneTextView = findViewById(R.id.appointment_details_phone);
        emailTextView = findViewById(R.id.appointment_details_email);
        descTextView = findViewById(R.id.appointment_details_desc);
        dateTextView = findViewById(R.id.appointment_details_date);
        timeTextView = findViewById(R.id.appointment_details_time);

        fabMain = findViewById(R.id.fabDetailsMain);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditAppointment();
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
        reminderTime = intent.getIntExtra(EXTRA_REMINDER_TIME, 0);
        clientReminderDate = intent.getStringExtra(EXTRA_CLIENT_REMINDER_DATE);
        clientReminderTime = intent.getStringExtra(EXTRA_CLIENT_REMINDER_TIME);
        clientReminderMessage = intent.getStringExtra(EXTRA_CLIENT_REMINDER_MESSAGE);
        reminderState = intent.getIntExtra(EXTRA_REMINDER_STATE, 0);
        clientReminderState = intent.getIntExtra(EXTRA_CLIENT_REMINDER_STATE, 0);
        isSms = intent.getBooleanExtra(EXTRA_SMS_REMINDER, false);
        isEmail = intent.getBooleanExtra(EXTRA_EMAIL_REMINDER, false);
        isBoth = intent.getBooleanExtra(EXTRA_BOTH, false);
        dateTime = intent.getStringExtra(EXTRA_DATE_TIME);

        if (!phone.isEmpty()) {
            phoneTextView.setText(phone);
            phoneTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
        }

        if (!email.isEmpty()) {
            emailTextView.setText(email);
            emailTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
        }

        if (!desc.isEmpty()) {
            descTextView.setText(phone);
            descTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
        }

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

    private void startEditAppointment() {

        Intent intent = new Intent(AppointmentDetailsActivity.this, CreateEditAppointmentActivity.class);

        intent.putExtra(CreateEditAppointmentActivity.EXTRA_ID, id);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_NAME, clientName);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_PHONE, phone);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL, email);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE, date);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_TIME, time);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DESC, desc);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_TIME, reminderTime);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_DATE, clientReminderDate);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_TIME, clientReminderTime);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_MESSAGE, clientReminderMessage);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_STATE, reminderState);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_STATE, clientReminderState);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_SMS_REMINDER, isSms);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL_REMINDER, isEmail);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_BOTH, isBoth);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE_TIME, dateTime);

        startActivityForResult(intent, RC_EDIT_CLIENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_EDIT_CLIENT && resultCode == RESULT_OK) {
            int id = Objects.requireNonNull(data).getIntExtra(CreateEditAppointmentActivity.EXTRA_ID, -1);

            if (id == -1) {
                Snackbar.make(constraintLayout, "Something went wrong! Could not update.", Snackbar.LENGTH_LONG).show();
                return;
            }

            clientName = data.getStringExtra("name");
            phone = data.getStringExtra("phone");
            email = data.getStringExtra("email");
            desc = data.getStringExtra("desc");
            date = data.getStringExtra("date");
            time = data.getStringExtra("time");
            reminderTime = data.getIntExtra("reminderTime", 0);
            clientReminderDate = data.getStringExtra("clientReminderDate");
            clientReminderTime = data.getStringExtra("clientReminderTime");
            reminderState = data.getIntExtra("reminderState", 0);
            clientReminderState = data.getIntExtra("clientReminderState", 0);
            String reminderMessage = "message";
            String dateAdded = data.getStringExtra("dateAdded");
            isSms = data.getBooleanExtra("is_sms", false);
            isEmail = data.getBooleanExtra("is_email", false);
            isBoth = data.getBooleanExtra("is_both", false);
            dateTime  = data.getStringExtra("dateTime");

            if (!clientNameTextView.getText().toString().matches(Objects.requireNonNull(clientName))) {
                clientNameTextView.setText(clientName);
            }

            if (phone != null && !phoneTextView.getText().toString().matches(Objects.requireNonNull(phone))) {
                if (!phone.isEmpty()) {
                    phoneTextView.setText(phone);
                    phoneTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
                }
            }

            if (email != null && !emailTextView.getText().toString().matches(Objects.requireNonNull(email))) {
                if (!email.isEmpty()) {
                    emailTextView.setText(email);
                    emailTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
                }
            }

            if (desc != null && !descTextView.getText().toString().matches(Objects.requireNonNull(desc))) {
                if (!desc.isEmpty()) {
                    descTextView.setText(desc);
                    descTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
                }
            }

            if (!dateTextView.getText().toString().matches(Objects.requireNonNull(date))) {
                dateTextView.setText(date);
                dateTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
            }

            if (!timeTextView.getText().toString().matches(Objects.requireNonNull(time))) {
                timeTextView.setText(time);
                timeTextView.setTextColor(getResources().getColor(R.color.md_light_primary_text));
            }

            Appointments appointments = new Appointments(clientName, phone, email, desc, date, time, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, isSms, isEmail, isBoth, dateTime);

            appointments.setId(id);

            viewModel.update(appointments);

            Snackbar.make(constraintLayout, "Appointment updated", Snackbar.LENGTH_LONG).show();
        }
    }
}